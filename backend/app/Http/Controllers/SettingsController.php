<?php

namespace App\Http\Controllers;

use App\Models\Admin;
use App\Models\Admob;
use App\Models\Category;
use App\Models\GlobalFunction;
use App\Models\GlobalSettings;
use App\Models\SubscriptionPackage;
use App\Models\Wallpaper;
use App\Models\WallpaperDownloadAnalytics;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class SettingsController extends Controller
{
    function index()
    {
        $allCategories = Category::count();
        $category = Category::where('type', '0')->count();
        $liveCategory = Category::where('type', '1')->count();
        $allWallpapers = Wallpaper::count();
        $wallpapers = Wallpaper::where('wallpaper_type', '0')->count();
        $liveWallpapers = Wallpaper::where('wallpaper_type', '1')->count();
        return view('index', [
            'allCategories' => $allCategories, 
            'category' => $category, 
            'liveCategory' => $liveCategory, 
            'allWallpapers' => $allWallpapers, 
            'wallpapers' => $wallpapers, 
            'liveWallpapers' => $liveWallpapers, 
        ]);
    }

    public function setting()
    {
        $setting = GlobalSettings::first();
        return view('setting', [
            'setting' => $setting
        ]);
    }

    public function fetchSettings()
    {
        $data = GlobalSettings::first();
        if ($data) {
            $data->splash_media_url = GlobalFunction::createMediaUrl($data->splash_media);
            $data->featured_media_url = GlobalFunction::createMediaUrl($data->featured_media);
        }
        $categories = Category::orderBy('id', 'DESC')->withCount('wallpapers')->get();
        $admob = Admob::get();
        $subscriptionPackages = SubscriptionPackage::get();

        return response()->json([
            'status' => true,
            'message' => 'Fetch Setting Successfully',
            'data' => $data,
            'categories' => $categories,
            'admob' => $admob,
            'subscriptionPackages' => $subscriptionPackages,
        ]);
    }

    public function fetchHomePageData()
    {
        $featuredWallpapers = Wallpaper::where('is_featured','1')
                                        ->withCount('downloads as download_count')
                                        ->take(5)
                                        ->get();
        $latestWallpapers = $wallpapers = Wallpaper::where('category_id', '!=', 22)
                                        ->withCount('downloads as download_count')
                                        ->orderByDesc('is_featured') // Featured ones first
                                        ->inRandomOrder()            // Then random order within each group
                                        ->take(50)
                                        ->get();

        return response()->json([
            'status' => true,
            'message' => 'Fetch Featured and Latest Wallpapers Successfully',
            'featured_wallpapers' => $featuredWallpapers,
            'latest_wallpapers' => $latestWallpapers,
        ]);
    }

    public function fetchLikedWallpaper(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'wallpaper_ids' => 'required',
        ]);

        if ($validator->fails()) {
            $messages = $validator->errors()->all();
            $msg = $messages[0];
            return response()->json(['status' => false, 'message' => $msg]);
        }

        $wallpaper_ids = explode(',', $request->wallpaper_ids);

        $fetchLikedWallpapers = Wallpaper::whereIn('id', $wallpaper_ids)
                                        ->withCount('downloads as download_count')
                                        ->get();
        
        return response()->json([
            'status' => true,
            'message' => 'Fetch Liked Wallpapers Successfully',
            'data' => $fetchLikedWallpapers,
        ]);
    }

    public function saveSettings(Request $request)
    {
        $wantsJson = $request->ajax() || $request->wantsJson();

        $validator = Validator::make($request->all(), [
            'splash_media' => 'nullable|file|mimes:jpg,jpeg,png,webp,mp4,mov,m4v,3gp,webm|max:51200',
            'splash_media_link' => 'nullable|url|max:2048',
            'remove_splash_media' => 'nullable|in:0,1',
            'splash_media_path' => 'nullable|string|max:255',
            'splash_media_type' => 'nullable|in:image,video',
            'splash_media_base64' => 'nullable|string',
            'splash_media_mime' => 'nullable|string|max:100',
            'featured_media' => 'nullable|file|mimes:jpg,jpeg,png,webp,mp4,mov,m4v,3gp,webm|max:51200',
            'featured_media_link' => 'nullable|url|max:2048',
            'remove_featured_media' => 'nullable|in:0,1',
            'featured_media_path' => 'nullable|string|max:255',
            'featured_media_type' => 'nullable|in:image,video',
            'featured_media_base64' => 'nullable|string',
            'featured_media_mime' => 'nullable|string|max:100',
        ]);

        if ($validator->fails()) {
            $messages = $validator->errors()->all();
            $msg = $messages[0] ?? 'Invalid input';
            if ($wantsJson) {
                return response()->json(['status' => false, 'message' => $msg]);
            }
            return redirect()->route('setting')->with('error', $msg);
        }

        $setting = GlobalSettings::first();

        if ($setting == null) {
            if ($wantsJson) {
                return response()->json([
                    'status' => false,
                    'message' => 'setting Not Found',
                ]);
            }
            return redirect()->route('setting')->with('error', 'Setting not found');
        } 
        if ($request->has('app_name')) {
            $setting->app_name = $request->app_name;
            $request->session()->put('app_name', $setting['app_name']);
        }
        if ($request->has('announcement_text')) {
            $setting->announcement_text = $request->announcement_text;
        }
        if ($request->has('currency')) {
            $setting->currency = $request->currency;
            $request->session()->put('currency', $setting['currency']);
        }

        if ($request->input('remove_splash_media') === '1') {
            GlobalFunction::deleteFile($setting->splash_media);
            $setting->splash_media = null;
            $setting->splash_media_type = null;
        }
        if ($request->input('remove_featured_media') === '1') {
            GlobalFunction::deleteFile($setting->featured_media);
            $setting->featured_media = null;
            $setting->featured_media_type = null;
        }

        if ($request->hasFile('splash_media')) {
            GlobalFunction::deleteFile($setting->splash_media);

            $uploadedPath = GlobalFunction::saveFileAndGivePath($request->file('splash_media'));
            $mimeType = (string) $request->file('splash_media')->getMimeType();
            $mediaType = str_starts_with($mimeType, 'video/') ? 'video' : 'image';

            $setting->splash_media = $uploadedPath;
            $setting->splash_media_type = $mediaType;
        }

        if ($request->filled('splash_media_path') && $request->filled('splash_media_type')) {
            GlobalFunction::deleteFile($setting->splash_media);
            $setting->splash_media = (string) $request->input('splash_media_path');
            $setting->splash_media_type = (string) $request->input('splash_media_type');
        }
        if ($request->filled('splash_media_link')) {
            GlobalFunction::deleteFile($setting->splash_media);
            $splashLink = (string) $request->input('splash_media_link');
            $setting->splash_media = $splashLink;
            $setting->splash_media_type = $this->inferMediaTypeFromUrl($splashLink);
        }

        if ($request->hasFile('featured_media')) {
            GlobalFunction::deleteFile($setting->featured_media);

            $uploadedPath = GlobalFunction::saveFileAndGivePath($request->file('featured_media'));
            $mimeType = (string) $request->file('featured_media')->getMimeType();
            $mediaType = str_starts_with($mimeType, 'video/') ? 'video' : 'image';

            $setting->featured_media = $uploadedPath;
            $setting->featured_media_type = $mediaType;
        }

        if ($request->filled('featured_media_path') && $request->filled('featured_media_type')) {
            GlobalFunction::deleteFile($setting->featured_media);
            $setting->featured_media = (string) $request->input('featured_media_path');
            $setting->featured_media_type = (string) $request->input('featured_media_type');
        }
        if ($request->filled('featured_media_link')) {
            GlobalFunction::deleteFile($setting->featured_media);
            $featuredLink = (string) $request->input('featured_media_link');
            $setting->featured_media = $featuredLink;
            $setting->featured_media_type = $this->inferMediaTypeFromUrl($featuredLink);
        }

        // Fallback path for hosts that block multipart binary uploads with 403.
        if (!$request->hasFile('splash_media') && $request->filled('splash_media_base64')) {
            $base64Input = (string) $request->input('splash_media_base64');
            $providedMime = strtolower((string) $request->input('splash_media_mime', ''));
            $base64Data = $base64Input;
            $detectedMime = $providedMime;

            if (preg_match('/^data:([^;]+);base64,(.*)$/', $base64Input, $matches)) {
                $detectedMime = strtolower((string) $matches[1]);
                $base64Data = (string) $matches[2];
            }

            $binary = base64_decode($base64Data, true);
            if ($binary === false) {
                if ($wantsJson) {
                    return response()->json([
                        'status' => false,
                        'message' => 'Invalid splash media payload',
                    ]);
                }
                return redirect()->route('setting')->with('error', 'Invalid splash media payload');
            }

            if (strlen($binary) > 50 * 1024 * 1024) {
                if ($wantsJson) {
                    return response()->json([
                        'status' => false,
                        'message' => 'Max file size is 50MB',
                    ]);
                }
                return redirect()->route('setting')->with('error', 'Max file size is 50MB');
            }

            $mimeToExt = [
                'image/jpeg' => 'jpg',
                'image/jpg' => 'jpg',
                'image/png' => 'png',
                'image/webp' => 'webp',
                'video/mp4' => 'mp4',
                'video/quicktime' => 'mov',
                'video/x-m4v' => 'm4v',
                'video/3gpp' => '3gp',
                'video/webm' => 'webm',
            ];

            if (!isset($mimeToExt[$detectedMime])) {
                if ($wantsJson) {
                    return response()->json([
                        'status' => false,
                        'message' => 'Unsupported splash media type',
                    ]);
                }
                return redirect()->route('setting')->with('error', 'Unsupported splash media type');
            }

            $extension = $mimeToExt[$detectedMime];
            $filename = time() . '_splash.' . $extension;
            $relativePath = 'uploads/' . $filename;

            GlobalFunction::deleteFile($setting->splash_media);
            \Storage::disk('public')->put($relativePath, $binary);

            $setting->splash_media = $relativePath;
            $setting->splash_media_type = str_starts_with($detectedMime, 'video/') ? 'video' : 'image';
        }

        // Fallback path for featured media upload on hosts that block multipart binary uploads with 403.
        if (!$request->hasFile('featured_media') && $request->filled('featured_media_base64')) {
            $base64Input = (string) $request->input('featured_media_base64');
            $providedMime = strtolower((string) $request->input('featured_media_mime', ''));
            $base64Data = $base64Input;
            $detectedMime = $providedMime;

            if (preg_match('/^data:([^;]+);base64,(.*)$/', $base64Input, $matches)) {
                $detectedMime = strtolower((string) $matches[1]);
                $base64Data = (string) $matches[2];
            }

            $binary = base64_decode($base64Data, true);
            if ($binary === false) {
                if ($wantsJson) {
                    return response()->json([
                        'status' => false,
                        'message' => 'Invalid featured media payload',
                    ]);
                }
                return redirect()->route('setting')->with('error', 'Invalid featured media payload');
            }

            if (strlen($binary) > 50 * 1024 * 1024) {
                if ($wantsJson) {
                    return response()->json([
                        'status' => false,
                        'message' => 'Max file size is 50MB',
                    ]);
                }
                return redirect()->route('setting')->with('error', 'Max file size is 50MB');
            }

            $mimeToExt = [
                'image/jpeg' => 'jpg',
                'image/jpg' => 'jpg',
                'image/png' => 'png',
                'image/webp' => 'webp',
                'video/mp4' => 'mp4',
                'video/quicktime' => 'mov',
                'video/x-m4v' => 'm4v',
                'video/3gpp' => '3gp',
                'video/webm' => 'webm',
            ];

            if (!isset($mimeToExt[$detectedMime])) {
                if ($wantsJson) {
                    return response()->json([
                        'status' => false,
                        'message' => 'Unsupported featured media type',
                    ]);
                }
                return redirect()->route('setting')->with('error', 'Unsupported featured media type');
            }

            $extension = $mimeToExt[$detectedMime];
            $filename = time() . '_featured.' . $extension;
            $relativePath = 'uploads/' . $filename;

            GlobalFunction::deleteFile($setting->featured_media);
            \Storage::disk('public')->put($relativePath, $binary);

            $setting->featured_media = $relativePath;
            $setting->featured_media_type = str_starts_with($detectedMime, 'video/') ? 'video' : 'image';
        }

        $setting->save();
            
        if ($wantsJson) {
            return response()->json([
                'status' => true,
                'message' => 'Setting Updated Successfully',
            ]);
        }

        return redirect()->route('setting')->with('success', 'Setting Updated Successfully');

    }

    public function uploadSplashMedia(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'file' => 'required|file|mimes:jpg,jpeg,png,webp,mp4,mov,m4v,3gp,webm|max:51200',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => false,
                'message' => $validator->errors()->first(),
            ]);
        }

        $file = $request->file('file');
        $mimeType = (string) $file->getMimeType();
        $mediaType = str_starts_with($mimeType, 'video/') ? 'video' : 'image';
        $path = GlobalFunction::saveFileAndGivePath($file);

        return response()->json([
            'status' => true,
            'message' => 'Splash media uploaded successfully',
            'data' => [
                'path' => $path,
                'type' => $mediaType,
                'url' => GlobalFunction::createMediaUrl($path),
            ],
        ]);
    }

    public function changePassword(Request $request)
    {
        $admin = Admin::where('user_type', 1)->first();
        if ($admin) {
            if ($request->has('user_password')) {
                if ($admin->user_password == $request->user_password) {
                    $admin->user_password = $request->new_password;
                    $admin->save();
                    return response()->json([
                        'status' => true,
                        'message' => 'Change Password',
                    ]);
                } else {
                    return response()->json([
                        'status' => false,
                        'message' => 'Old Password does not match',
                    ]);
                }
            }
        } else {
            return response()->json([
                'status' => false,
                'message' => 'Admin not found',
            ]);
        }
    }

    public function subscription()
    {
        $monthlySubscription = SubscriptionPackage::where('package_id', 1)->first();
        $yearlySubscription = SubscriptionPackage::where('package_id', 2)->first();
        return view('subscription', [
            'monthlySubscription' => $monthlySubscription,
            'yearlySubscription' => $yearlySubscription,
        ]);
    }

    public function monthlySubscription(Request $request)
    {
        $monthlySubscription = SubscriptionPackage::where('package_id', $request->package_id)->first();

        if ($monthlySubscription == null) {
            return response()->json([
                'status' => false,
                'message' => 'Subscription Not Found',
            ]);
        }

        $monthlySubscription->android_product_id = $request->android_product_id; 
        $monthlySubscription->ios_product_id = $request->ios_product_id; 
        $monthlySubscription->save();
            
        return response()->json([
            'status' => true,
            'message' => 'Subscription Updated Successfully',
        ]);

    }

    public function yearlySubscription(Request $request)
    {
        $yearlySubscriptionForm = SubscriptionPackage::where('package_id', $request->package_id)->first();

        if ($yearlySubscriptionForm == null) {
            return response()->json([
                'status' => false,
                'message' => 'Subscription Not Found',
            ]);
        }
        
        $yearlySubscriptionForm->android_product_id = $request->android_product_id; 
        $yearlySubscriptionForm->ios_product_id = $request->ios_product_id; 
        $yearlySubscriptionForm->save();
            
        return response()->json([
            'status' => true,
            'message' => 'Subscription Updated Successfully',
        ]);

    }

    function admob()
    {
        $admobAndroid = Admob::where('type', 1)->first();
        $admobiOS = Admob::where('type', 2)->first();
        return view('admob', [
            'admobAndroid' => $admobAndroid,
            'admobiOS' => $admobiOS,
        ]);
    }

    public function admobAndroid(Request $request)
    {
        $admobAndroid = Admob::where('type', $request->type)->first();

        if ($admobAndroid == null) {
            return response()->json([
                'status' => false,
                'message' => 'Subscription Not Found',
            ]);
        } 
        
        $admobAndroid->banner_id = $request->banner_id; 
        $admobAndroid->intersial_id = $request->intersial_id; 
        $admobAndroid->rewarded_id = $request->rewarded_id; 
        $admobAndroid->save();
            
        return response()->json([
            'status' => true,
            'message' => 'Admob Updated Successfully',
        ]);

    }

    public function admobiOS(Request $request)
    {
        $admobAndroid = Admob::where('type', $request->type)->first();

        if ($admobAndroid == null) {
            return response()->json([
                'status' => false,
                'message' => 'Subscription Not Found',
            ]);
        } 
        
      
        $admobAndroid->banner_id = $request->banner_id; 
        $admobAndroid->intersial_id = $request->intersial_id; 
        $admobAndroid->rewarded_id = $request->rewarded_id; 
        $admobAndroid->save();
            
        return response()->json([
            'status' => true,
            'message' => 'Admob Updated Successfully',
        ]);

    }

   
    // API

    public function fetchAllData(Request $request)
    {
        $categories = Category::get();
        $wallpapers = Wallpaper::get();
        $subscriptionPackages = SubscriptionPackage::get();
        $admob = Admob::get();

        return response()->json([
            'status' => true,
            'message' => 'Fetch home page data successfully',
            'wallpapers' => $wallpapers, 
            'categories' => $categories,
            'subscriptionPackages' => $subscriptionPackages, 
            'admob' => $admob, 
        ]);
    }

    public function fetchWallpaperByCategory(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'category_id' => 'required',
            'start' => 'required',
            'limit' => 'required',
        ]);

        if ($validator->fails()) {
            $messages = $validator->errors()->all();
            $msg = $messages[0];
            return response()->json(['status' => false, 'message' => $msg]);
        }

        $category = Category::inRandomOrder()->where('id', $request->category_id)->first();
        if ($category == null) {
            return response()->json([
                'status' => false,
                'message' => 'Category Not Found',
            ]);
        }

        $wallpapers = Wallpaper::where('category_id', $request->category_id)
                                ->withCount('downloads as download_count')
                                ->orderBy('id', 'DESC')
                                ->offset($request->start)
                                ->limit($request->limit)
                                ->get();

        return response()->json([
            'status' => true,
            'message' => 'Fetch wallpaper by category',
            'data' => $wallpapers
        ]);
    }

    public function searchWallpaper(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'start' => 'required',
            'limit' => 'required',
        ]);

        if ($validator->fails()) {
            $messages = $validator->errors()->all();
            $msg = $messages[0];
            return response()->json(['status' => false, 'message' => $msg]);
        }

        $result = Wallpaper::query();

        if ($request->has('access_type')) {
            $result->where('access_type', $request->access_type);
        }
        if($request->has('tags')) {
           $result->where('tags', 'like', '%' . $request->tags . '%');
        }



        $result->offset($request->start)->limit($request->limit);

        $resultsWallpapers = $result->withCount('downloads as download_count')->get();

        return response()->json([
            'status' => true,
            'message' => 'Search result successfully',
            'data' => $resultsWallpapers
        ]);

    }

    public function trackWallpaperDownload(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'wallpaper_id' => 'required|exists:wallpapers,id',
            'device_id' => 'nullable|string|max:191',
            'device_brand' => 'nullable|string|max:100',
            'device_model' => 'nullable|string|max:120',
            'device_manufacturer' => 'nullable|string|max:120',
            'os_version' => 'nullable|string|max:60',
            'app_version' => 'nullable|string|max:60',
        ]);

        if ($validator->fails()) {
            $messages = $validator->errors()->all();
            $msg = $messages[0] ?? 'Invalid input';
            return response()->json(['status' => false, 'message' => $msg]);
        }

        WallpaperDownloadAnalytics::create([
            'wallpaper_id' => (int) $request->wallpaper_id,
            'device_id' => $request->device_id,
            'device_brand' => $request->device_brand,
            'device_model' => $request->device_model,
            'device_manufacturer' => $request->device_manufacturer,
            'os_version' => $request->os_version,
            'app_version' => $request->app_version,
            'ip_address' => (string) $request->ip(),
        ]);

        return response()->json([
            'status' => true,
            'message' => 'Wallpaper download tracked successfully',
        ]);
    }

    public function analytics()
    {
        $totalDownloads = WallpaperDownloadAnalytics::count();
        $uniqueDevices = WallpaperDownloadAnalytics::whereNotNull('device_id')
            ->distinct('device_id')
            ->count('device_id');

        $topWallpapers = Wallpaper::withCount('downloads as download_count')
            ->orderByDesc('download_count')
            ->limit(15)
            ->get();

        $recentLogs = WallpaperDownloadAnalytics::with('wallpaper')
            ->orderByDesc('id')
            ->limit(100)
            ->get();

        return view('analytics', [
            'totalDownloads' => $totalDownloads,
            'uniqueDevices' => $uniqueDevices,
            'topWallpapers' => $topWallpapers,
            'recentLogs' => $recentLogs,
        ]);
    }


 
    private function inferMediaTypeFromUrl(string $url): string
    {
        $path = strtolower((string) parse_url($url, PHP_URL_PATH));
        $videoExt = ['mp4', 'mov', 'm4v', '3gp', 'webm'];
        $ext = pathinfo($path, PATHINFO_EXTENSION);
        return in_array($ext, $videoExt, true) ? 'video' : 'image';
    }

}
