<?php

namespace App\Http\Controllers;

use App\Models\GlobalFunction;
use App\Models\Notification;
use App\Models\Wallpaper;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

class NotificationController extends Controller
{
    public function notificationList(Request $request)
    {
        $totalData = Notification::count();
        $rows = Notification::orderBy('id', 'DESC')->get();
        $result = $rows;

        $columns = [
            0 => 'id',
            1 => 'title',
            2 => 'description',
        ];

        $limit = $request->input('length');
        $start = $request->input('start');
        $order = $columns[$request->input('order.0.column')];
        $dir = $request->input('order.0.dir');

        $totalFiltered = $totalData;
        if (empty($request->input('search.value'))) {
            $result = Notification::offset($start)
                ->limit($limit)
                ->orderBy($order, $dir)
                ->get();
        } else {
            $search = $request->input('search.value');
            $result = Notification::Where('title', 'LIKE', "%{$search}%")->orWhere('description', 'LIKE', "%{$search}%")
                ->offset($start)
                ->limit($limit)
                ->orderBy($order, $dir)
                ->get();
            $totalFiltered = Notification::Where('title', 'LIKE', "%{$search}%")->orWhere('description', 'LIKE', "%{$search}%")->count();
        }
        $data = [];
        foreach ($result as $item) {
            $repeat = '<a href="#" data-title="' . $item->title . '" data-description="' . $item->description . '" data-wallpaper_id="' . ($item->wallpaper_id ?? '') . '" class="me-3 btn btn-info px-4 text-white repeat" rel=' . $item->id . '>' . __('repeat') . '</a>';
            $edit = '<a href="#" data-title="' . $item->title . '" data-description="' . $item->description . '" data-wallpaper_id="' . ($item->wallpaper_id ?? '') . '" rel='.$item->id.' class="btn edit btn-success me-3" >' . __('edit') . '</a>'; 
            $delete = '<a href="#" class="btn delete btn-danger text-white" rel=' . $item->id . '>' . __('delete') . '</a>';
            $action = '<span class="float-end">'. $repeat . $edit . $delete .' </span>' ;

            $data[] = [
                $item->title,
                Str::limit($item->description, 105),
                $action
            ];
        }
        $json_data = [
            'draw' => intval($request->input('draw')),
            'recordsTotal' => intval($totalData),
            'recordsFiltered' => $totalFiltered,
            'data' => $data,
        ];
        echo json_encode($json_data);
        exit();
    }

    public function sendNotification(Request $request)
    {
        $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'wallpaper_id' => 'nullable|exists:wallpapers,id',
            'image' => 'nullable|image|mimes:jpg,jpeg,png|max:2048'
        ]);
    
        $notification = new Notification;
        $notification->title = $request->title;
        $notification->description = $request->description;
        $notification->wallpaper_id = $request->wallpaper_id;
    
        $imagePath = null;
        if ($request->hasFile('image')) {
            $imageName = time().'_'.$request->file('image')->getClientOriginalName();
            $request->file('image')->move(public_path('uploads/notifications'), $imageName);
            $imagePath = 'uploads/notifications/' . $imageName;
        }
    
        $notification->save();
    
        $wallpaper = null;
        if (!empty($request->wallpaper_id)) {
            $wallpaper = Wallpaper::where('id', $request->wallpaper_id)->first();
        }

        // Send Push Notification with image
        $pushResponse = GlobalFunction::sendPushNotificationToAllUsers(
            $request->title,
            $request->description,
            $imagePath,
            $wallpaper
        );

        $pushDecoded = json_decode($pushResponse, true);
        if (!$pushDecoded || empty($pushDecoded['status'])) {
            return response()->json([
                'status' => false,
                'message' => $pushDecoded['message'] ?? 'Notification send failed',
                'raw' => $pushDecoded['raw'] ?? $pushResponse,
            ]);
        }
    
        return response()->json([
            'status' => true,
            'message' => 'Notification Send Successfully',
        ]);
    }

    public function updateNotification(Request $request)
    {
        $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'wallpaper_id' => 'nullable|exists:wallpapers,id',
        ]);

        $notification = Notification::where('id', $request->notificationID)->first();
        
        if ($notification) {
            $notification->title = $request->title;
            $notification->description = $request->description;
            $notification->wallpaper_id = $request->wallpaper_id;
            $notification->save();
 
            return response()->json([
                'status' => true,
                'message' => 'Notification Updated Successfully',
            ]);
        } else {
            return response()->json([
                'status' => false,
                'message' => 'Notification Not Found',
            ]);
        }

    }

    public function repeatNotification(Request $request)
    {
        $title = $request->title;
        $description  = $request->description;
        $wallpaper = null;
        if (!empty($request->wallpaper_id)) {
            $wallpaper = Wallpaper::where('id', $request->wallpaper_id)->first();
        }

        $pushResponse = GlobalFunction::sendPushNotificationToAllUsers($title, $description, null, $wallpaper);
        $pushDecoded = json_decode($pushResponse, true);
        if (!$pushDecoded || empty($pushDecoded['status'])) {
            return response()->json([
                'status' => false,
                'message' => $pushDecoded['message'] ?? 'Notification send failed',
                'raw' => $pushDecoded['raw'] ?? $pushResponse,
            ]);
        }

        return response()->json([
            'status' => true,
            'message' => 'Notification Send Successfully',
        ]);
    }

    public function deleteNotification(Request $request)
    {
        $notification = Notification::where('id', $request->notification_id)->first();
 
        if ($notification) {
            $notification->delete();
            return response()->json([
                'status' => true,
                'message' => 'Notification Delete Successfully',
            ]);
        } else {
            return response()->json([
                'status' => false,
                'message' => 'Notification Not Found',
            ]);
        }
    }

    public function notification()
    {
        $wallpapers = Wallpaper::orderBy('id', 'DESC')->get();
        return view('notification', [
            'wallpapers' => $wallpapers,
        ]);
    }

}
