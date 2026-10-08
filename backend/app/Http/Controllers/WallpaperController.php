<?php

namespace App\Http\Controllers;

use App\Models\Category;
use App\Models\Constants;
use App\Models\GlobalFunction;
use App\Models\Wallpaper;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\File;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Validator;

class WallpaperController extends Controller
{
    public function wallpaper()
    {
        $categories = Category::where('type', 0)->get();
        return view('wallpaper', [
            'categories' => $categories,
        ]);
    }

    public function liveWallpaper()
    {
        $liveCategories = Category::where('type', 1)->get();
        return view('liveWallpaper', [
            'liveCategories' => $liveCategories,
        ]);
    }

    public function wallpaperList(Request $request)
    {
        $totalData = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)->count();
        $rows = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)->orderBy('id', 'DESC')->get();
        $result = $rows;
        $columns = [
            0 => 'id',
            1 => 'image',
        ];
        $limit = $request->input('length');
        $start = $request->input('start');
        $order = $columns[$request->input('order.0.column')];
        $dir = $request->input('order.0.dir');
        $totalFiltered = $totalData;
        if (empty($request->input('search.value'))) {
            $result = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)->offset($start)
                ->limit($limit)
                ->orderByDesc('is_featured')
                ->get();
        } else {

            $search = $request->input('search.value');

            $query = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)
                ->where(function ($query) use ($search) {
                    $query->whereHas('category', function ($query) use ($search) {
                        $query->where('title', 'LIKE', "%{$search}%");
                    })
                        ->orWhere('tags', 'LIKE', "%{$search}%");
                });

            $totalFiltered = $query->count();

            $result = $query->offset($start)
                ->limit($limit)
                ->orderBy($order, $dir)
                ->get();
        }
        $data = [];
        foreach ($result as $item) {

            $imageUrl = GlobalFunction::createMediaUrl($item->content);

            if ($item->content != null) {
                $image = "<img src=" . $imageUrl . " class='tbl_portrait_image'>";
            } else {
                $image = "<img src='./asset/img/placeholder-image.png' class='tbl_portrait_image'>";
            }
            if ($item->content == null) {
                $imageUrl = "null";
            }

            if ($item->access_type == Constants::Premium) {
                $type = '<span class="badge badge-success border-radius-5"> Premium </span>';
            } elseif ($item->access_type == Constants::Locked) {
                $type = '<span class="badge badge-primary border-radius-5"> Locked </span>';
            } else {
                $type = '<span class="badge badge-info border-radius-5"> None </span>';
            }

            if ($item->is_featured == 1) {
                $featured = '<div class="checkbox-slider">
                <label>
                    <input type="checkbox" class="d-none Featured"  checked rel="' . $item->id . '" value="' . $item->is_featured . '" >
                    <span class="toggle_background">
                        <div class="circle-icon"></div>
                        <div class="vertical_line"></div>
                    </span>
                </label>
            </div>';
            } else {
                $featured = '<div class="checkbox-slider">
                <label>
                    <input type="checkbox" class="d-none Featured"  rel="' . $item->id . '" value="' . $item->is_featured . '" >
                    <span class="toggle_background">
                        <div class="circle-icon"></div>
                        <div class="vertical_line"></div>
                    </span>
                </label>
            </div>';
            }


            $edit = '<a href="#" data-image="' . $imageUrl . '" data-category_id="' . $item->category->id . '" data-tags="' . $item->tags . '" data-access_type="' . $item->access_type . '"   rel=' . $item->id . ' class="btn edit btn-success me-3">' . __('edit') . '</a>';
            $delete = '<a href="#" class="btn delete btn-danger" rel=' . $item->id . '>' . __('delete') . '</a>';
            $action = '<span class="float-end">' . $edit . $delete . ' </span>';

            $data[] = [
                $image,
                $item->category->title,
                $item->tags,
                $type,
                $featured,
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

    public function addWallpaper(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'wallpaper_type' => 'required',
            'category_id' => 'required|exists:categories,id',
            'access_type' => 'required',
            'tags' => 'required|string',
            'content.*' => 'nullable|image',
            'content_urls' => 'nullable|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => false,
                'message' => $validator->errors()->first(),
            ]);
        }

        $createdCount = 0;

        if ($request->hasFile('content')) {
            foreach ($request->file('content') as $thisImage) {
                $wallpaper = new Wallpaper();
                $wallpaper->wallpaper_type = $request->wallpaper_type;
                $wallpaper->category_id = $request->category_id;
                $wallpaper->access_type = $request->access_type;
                $wallpaper->tags = $request->tags;

                $path = GlobalFunction::saveFileAndGivePath($thisImage);
                $wallpaper->content = $path;
                $wallpaper->save();
                $createdCount++;
            }
        }

        $rawUrls = (string) $request->input('content_urls', '');
        $urls = preg_split('/[\r\n,]+/', $rawUrls);
        $urls = array_filter(array_map('trim', $urls), function ($url) {
            return !empty($url);
        });

        foreach ($urls as $url) {
            if (!filter_var($url, FILTER_VALIDATE_URL)) {
                continue;
            }

            $wallpaper = new Wallpaper();
            $wallpaper->wallpaper_type = $request->wallpaper_type;
            $wallpaper->category_id = $request->category_id;
            $wallpaper->access_type = $request->access_type;
            $wallpaper->tags = $request->tags;
            $wallpaper->content = $url;
            $wallpaper->save();
            $createdCount++;
        }

        if ($createdCount === 0) {
            return response()->json([
                'status' => false,
                'message' => 'Please upload at least one image or provide valid URL(s)',
            ]);
        }

        return response()->json([
            'status' => true,
            'message' => 'Wallpaper Added Successfully',

        ]);
    }
    
    public function autoAddWallpaper(Request $request)
    {
        foreach ($request->content as $imageUrl) {
            try {
                // Download the image content from the URL
                $imageContent = file_get_contents($imageUrl);
    
                if ($imageContent === false) {
                    continue; // skip if download fails
                }
    
                // Generate a temporary filename with correct extension
                $extension = pathinfo(parse_url($imageUrl, PHP_URL_PATH), PATHINFO_EXTENSION) ?: 'jpg';
                $tempFile = tempnam(sys_get_temp_dir(), 'wallpaper_');
                file_put_contents($tempFile, $imageContent);
    
                // Convert temp file into UploadedFile so it works with existing function
                $uploadedFile = new \Illuminate\Http\UploadedFile(
                    $tempFile,
                    Str::uuid() . '.' . $extension,
                    null,
                    null,
                    true // $testMode = true to skip file validation
                );
    
                // Save using your working helper
                $path = GlobalFunction::saveFileAndGivePath($uploadedFile);
    
                // Save wallpaper
                $wallpaper = new Wallpaper();
                $wallpaper->wallpaper_type = $request->wallpaper_type;
                $wallpaper->category_id = $request->category_id;
                $wallpaper->access_type = $request->access_type;
                $wallpaper->tags = $request->tags;
                $wallpaper->content = $path;
                $wallpaper->save();
    
            } catch (\Exception $e) {
                \Log::error("Failed to save image from URL: $imageUrl. Error: " . $e->getMessage());
                continue;
            }
        }
    
        return response()->json([
            'status' => true,
            'message' => 'Wallpapers added successfully from URLs.'
        ]);
    }

    public function updateWallpaper(Request $request)
    {
        $wallpaper = Wallpaper::where('id', $request->wallpaper_id)->first();

        if ($wallpaper == null) {
            return response()->json([
                'status' => false,
                'message' => 'Wallpaper Not Found',
            ]);
        } else {
            $wallpaper->tags = $request->tags;
            $wallpaper->category_id = $request->category_id;
            $wallpaper->access_type = $request->access_type;

            if ($request->hasFile('content')) {
                $path = "./public/storage/" . $wallpaper->content;
                if (File::exists($path)) {
                    File::delete($path);
                }
                $file = $request->file('content');
                $filePath = GlobalFunction::saveFileAndGivePath($file);
                $wallpaper->content = $filePath;
            }

            $wallpaper->save();

            return response()->json([
                'status' => true,
                'message' => 'Wallpaper Updated Successfully',
            ]);
        }
    }

    public function deleteWallpaper(Request $request)
    {
        $wallpaper = Wallpaper::where('id', $request->wallpaper_id)->first();

        if ($wallpaper == null) {
            return response()->json([
                'status' => false,
                'message' => 'Wallapaper Not Found',
            ]);
        }
        GlobalFunction::deleteFile($wallpaper->content);
        GlobalFunction::deleteFile($wallpaper->thumbnail);
        $wallpaper->delete();

        return response()->json([
            'status' => true,
            'message' => 'Category Delete Successfully',
        ]);
    }

    public function addLiveWallpaper(Request $request)
    {
        $liveWallpaper = new Wallpaper();
        $liveWallpaper->wallpaper_type = $request->wallpaper_type;
        $liveWallpaper->category_id = $request->category_id;
        $liveWallpaper->access_type = $request->access_type;
        $liveWallpaper->tags = $request->tags;

        $thumbnailPath = GlobalFunction::saveFileAndGivePath($request->file('thumbnail'));
        $liveWallpaper->thumbnail = $thumbnailPath;

        $path = GlobalFunction::saveFileAndGivePath($request->file('content'));
        $liveWallpaper->content = $path;

        $liveWallpaper->save();

        return response()->json([
            'status' => true,
            'message' => 'Live wallpaper added Successfully',
            'data' => $liveWallpaper,
        ]);
    }

    public function liveWallpaperList(Request $request)
    {
        $totalData = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)->count();
        $rows = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)->orderBy('id', 'DESC')->get();
        $result = $rows;
        $columns = [
            0 => 'id',
            1 => 'image',
        ];
        $limit = $request->input('length');
        $start = $request->input('start');
        $order = $columns[$request->input('order.0.column')];
        $dir = $request->input('order.0.dir');
        $totalFiltered = $totalData;
        if (empty($request->input('search.value'))) {
            $result = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)->offset($start)
                ->limit($limit)
                ->orderBy($order, $dir)
                ->get();
        } else {
            $search = $request->input('search.value');

            $query = Wallpaper::Where('wallpaper_type', $request->wallpaper_type)
                ->where(function ($query) use ($search) {
                    $query->whereHas('category', function ($query) use ($search) {
                        $query->where('title', 'LIKE', "%{$search}%");
                    })
                        ->orWhere('tags', 'LIKE', "%{$search}%");
                });

            $totalFiltered = $query->count();

            $result = $query->offset($start)
                ->limit($limit)
                ->orderBy($order, $dir)
                ->get();
        }
        $data = [];
        foreach ($result as $item) {

            $thumbnailUrl = GlobalFunction::createMediaUrl($item->thumbnail);

            if ($item->thumbnail != null) {
                $thumbnail = "<img src=" . $thumbnailUrl . " class='tbl_portrait_image'>";
            } else {
                $thumbnail = "<img src='./asset/img/placeholder-image-portrait.png' class='tbl_portrait_image'>";
            }
            if ($item->thumbnail == null) {
                $thumbnailUrl = "null";
            }

            if ($item->access_type == Constants::Premium) {
                $type = '<span class="badge badge-success border-radius-5"> Premium </span>';
            } elseif ($item->access_type == Constants::Locked) {
                $type = '<span class="badge badge-primary border-radius-5"> Locked </span>';
            } else {
                $type = '<span class="badge badge-info border-radius-5"> None </span>';
            }

            if ($item->is_featured == 1) {
                $featured = '<div class="checkbox-slider">
                <label>
                    <input type="checkbox" class="d-none Featured"  checked rel="' . $item->id . '" value="' . $item->is_featured . '" >
                    <span class="toggle_background">
                        <div class="circle-icon"></div>
                        <div class="vertical_line"></div>
                    </span>
                </label>
            </div>';
            } else {
                $featured = '<div class="checkbox-slider">
                <label>
                    <input type="checkbox" class="d-none Featured"  rel="' . $item->id . '" value="' . $item->is_featured . '" >
                    <span class="toggle_background">
                        <div class="circle-icon"></div>
                        <div class="vertical_line"></div>
                    </span>
                </label>
            </div>';
            }

            $liveWallpaperUrl = GlobalFunction::createMediaUrl($item->content);

            $liveWallpaper = '<a href="javascript:;" rel="' . $item->id . '" data-live_wallpaper="' . $liveWallpaperUrl . '" class="me-2  btn-primary text-white liveWallpaperUrl px-3 py-3 border-radius">
            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1 me-1 "><circle cx="12" cy="12" r="10"></circle><polygon points="10 8 16 12 10 16 10 8"></polygon></svg>
            Preview</a>';

            $edit = '<a href="#" data-content="' . $liveWallpaperUrl . '" data-thumbnail="' . $thumbnailUrl . '" data-category_id="' . $item->category->id . '" data-tags="' . $item->tags . '" data-access_type="' . $item->access_type . '"   rel=' . $item->id . ' class="btn edit btn-success me-3">' . __('edit') . '</a>';
            $delete = '<a href="#" class="btn delete btn-danger" rel=' . $item->id . '>' . __('delete') . '</a>';
            $action = '<span class="float-end">' . $edit . $delete . ' </span>';

            $data[] = [
                $thumbnail,
                $liveWallpaper,
                $item->category->title,
                $item->tags,
                $type,
                $featured,
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

    public function updateLiveWallpaper(Request $request)
    {
        $wallpaper = Wallpaper::where('id', $request->wallpaper_id)->first();

        if ($wallpaper == null) {
            return response()->json([
                'status' => false,
                'message' => 'Wallpaper Not Found',
            ]);
        } else {
            $wallpaper->tags = $request->tags;
            $wallpaper->category_id = $request->category_id;
            $wallpaper->access_type = $request->access_type;

            if ($request->hasFile('thumbnail')) {
                GlobalFunction::deleteFile($wallpaper->thumbnail);
                $file = $request->file('thumbnail');
                $thumbnailPath = GlobalFunction::saveFileAndGivePath($file);
                $wallpaper->thumbnail = $thumbnailPath;
            }

            if ($request->hasFile('content')) {
                GlobalFunction::deleteFile($wallpaper->content);
                $file = $request->file('content');
                $filePath = GlobalFunction::saveFileAndGivePath($file);
                $wallpaper->content = $filePath;
            }

            $wallpaper->save();

            return response()->json([
                'status' => true,
                'message' => 'Live wallpaper Updated Successfully',
            ]);
        }
    }

    public function updateFeatured(Request $request)
    {
        $wallpaper = Wallpaper::where('id', $request->id)->first();
        if ($wallpaper == null) {
            return response()->json([
                'status' => false,
                'message' => 'Wallpaper not found',
            ]);
        }

        $wallpaper->is_featured = $request->is_featured;
        $wallpaper->save();

        return response()->json([
            'status' => true,
            'message' => 'Wallpaper Updated Successfully',
        ]);
    }
}
