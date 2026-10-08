<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Google\Client;
use Illuminate\Support\Facades\File;
use Illuminate\Support\Facades\Storage;

class GlobalFunction extends Model
{
    use HasFactory;

    private static function baseUrl(): string
    {
        try {
            $request = request();
            if ($request && $request->getHost()) {
                return rtrim($request->getSchemeAndHttpHost(), '/');
            }
        } catch (\Throwable $e) {
        }

        $appUrl = config('app.url');
        if (!empty($appUrl)) {
            return rtrim($appUrl, '/');
        }

        return '';
    }

    private static function ensureHttpsUrl(string $url): string
    {
        return preg_replace('/^http:\/\//i', 'https://', $url) ?? $url;
    }

    private static function toAbsoluteUrl(string $pathOrUrl): string
    {
        if (filter_var($pathOrUrl, FILTER_VALIDATE_URL)) {
            return self::ensureHttpsUrl($pathOrUrl);
        }

        $base = self::baseUrl();
        $cleanPath = '/' . ltrim($pathOrUrl, '/');
        $built = $base !== '' ? ($base . $cleanPath) : url($pathOrUrl);
        return self::ensureHttpsUrl($built);
    }

    public static function sendSimpleResponse($status, $msg)
    {
        return response()->json(['status' => $status, 'message' => $msg]);
    }
    public static function sendDataResponse($status, $msg, $data)
    {
        return response()->json(['status' => $status, 'message' => $msg, 'data' => $data]);
    }

    public static function sendPushNotificationToAllUsers($title, $description, $image = null, $wallpaper = null)
    {
        $client = new Client();
        $client->setAuthConfig('googleCredentials.json');
        $client->addScope('https://www.googleapis.com/auth/firebase.messaging');
        $client->fetchAccessTokenWithAssertion();
        $accessToken = $client->getAccessToken();
        $accessToken = $accessToken['access_token'];
    
        $contents = File::get(base_path('googleCredentials.json'));
        $json = json_decode($contents, true);
    
        $url = 'https://fcm.googleapis.com/v1/projects/'.$json['project_id'].'/messages:send';
    
        $wallpaperPayload = null;
        if ($wallpaper) {
            $wallpaperPayload = [
                'id' => (int) $wallpaper->id,
                'access_type' => (int) $wallpaper->access_type,
                'thumbnail' => (string) ($wallpaper->thumbnail ?? ''),
                'category_id' => (int) $wallpaper->category_id,
                'updated_at' => (string) ($wallpaper->updated_at ?? ''),
                'created_at' => (string) ($wallpaper->created_at ?? ''),
                'wallpaper_type' => (int) $wallpaper->wallpaper_type,
                'content' => (string) ($wallpaper->content ?? ''),
                'is_featured' => (int) ($wallpaper->is_featured ?? 0),
                'tags' => (string) ($wallpaper->tags ?? ''),
            ];
        }

        $notificationImage = '';
        if (!empty($image)) {
            $notificationImage = self::toAbsoluteUrl((string) $image);
        } elseif ($wallpaper) {
            $wallpaperImage = $wallpaper->content;
            if ((int) $wallpaper->wallpaper_type === 1 && !empty($wallpaper->thumbnail)) {
                $wallpaperImage = $wallpaper->thumbnail;
            }
            $notificationImage = self::createMediaUrl($wallpaperImage) ?? '';
        }

        $dataPayload = [
            'title' => (string) $title,
            'body' => (string) $description,
            'image' => (string) $notificationImage,
            'has_wallpaper' => $wallpaperPayload ? '1' : '0',
            'wallpaper_json' => $wallpaperPayload ? json_encode($wallpaperPayload) : '',
        ];

        $notificationPayload = [
            'title' => (string) $title,
            'body' => (string) $description,
        ];
        if (!empty($notificationImage)) {
            $notificationPayload['image'] = (string) $notificationImage;
        }

        $androidPayload = [
            'priority' => 'high',
            'notification' => [
                'title' => (string) $title,
                'body' => (string) $description,
                'sound' => 'default',
                'channel_id' => '01',
            ],
        ];
        if (!empty($notificationImage)) {
            $androidPayload['notification']['image'] = (string) $notificationImage;
        }

        $apnsPayload = [
            'payload' => [
                'aps' => [
                    'sound' => 'default',
                    'alert' => [
                        'title' => (string) $title,
                        'body' => (string) $description,
                    ],
                ]
            ]
        ];
        if (!empty($notificationImage)) {
            $apnsPayload['fcm_options'] = ['image' => (string) $notificationImage];
        }

        $fields = [
            'message'=> [
                'topic'=> 'sphere',
                'notification' => $notificationPayload,
                'data' => $dataPayload,
                'android' => $androidPayload,
                'apns' => $apnsPayload,
            ],
        ];
    
        $headers = [
            'Content-Type:application/json',
            'Authorization:Bearer ' . $accessToken
        ];
    
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    
        $result = curl_exec($ch);
    
        if ($result === FALSE) {
            die('FCM Send Error: ' . curl_error($ch));
        }
        curl_close($ch);
    
        $decodedResult = json_decode($result, true);
        if (isset($decodedResult['error'])) {
            return json_encode([
                'status' => false,
                'message' => $decodedResult['error']['message'] ?? 'FCM error',
                'raw' => $result,
            ]);
        }

        if ($result) {
            return json_encode(['status' => true, 'message' => 'Notification sent successfully']);
        } else {
            return json_encode(['status' => false, 'message ' => 'Not sent!']);
        }
    }

    public static function createMediaUrl($media)
    {
        if ($media == null || $media === '') {
            return null;
        }

        if (filter_var($media, FILTER_VALIDATE_URL)) {
            return self::ensureHttpsUrl($media);
        }

        return self::toAbsoluteUrl('public/storage/' . ltrim($media, '/'));
    }

    public static function uploadFilToS3($request, $key)
    {
        $s3 = Storage::disk('s3');
        $file = $request->file($key);
        $fileName = time() . $file->getClientOriginalName();
        $fileName = str_replace(" ", "_", $fileName);
        $filePath = 'uploads/' . $fileName;
        $result =  $s3->put($filePath, file_get_contents($file), 'public-read');
        return $filePath;
    }

    public static function deleteFile($filename)
    {
        if ($filename != null && file_exists(storage_path('app/public/' . $filename))) {
            unlink(storage_path('app/public/' . $filename));
        }
    }

    public static function saveFileAndGivePath($file)
    {
        if ($file != null) {
            $path = $file->store('uploads');
            return $path;
        } else {
            return null;
        }
    }
}
