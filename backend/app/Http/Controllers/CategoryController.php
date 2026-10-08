<?php
namespace App\Http\Controllers;
use App\Models\Category;
use App\Models\GlobalFunction;
use App\Models\Wallpaper;
use Illuminate\Http\Request;

class CategoryController extends Controller
{
    public function categories()
    {
        return view('categories');
    }
    public function liveCategories()
    {
        return view('liveCategories');
    }

    public function categoryList(Request $request)
    {
        $totalData = Category::Where('type', $request->type)->count();
        $rows = Category::Where('type', $request->type)->orderBy('id', 'DESC')->get();
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
            $result = Category::Where('type', $request->type)->offset($start)
                ->limit($limit)
                ->orderBy($order, $dir)
                ->get();
        } else {
            $search = $request->input('search.value');
            $result = Category::Where('type', $request->type)->Where('title', 'LIKE', "%{$search}%")
                ->offset($start)
                ->limit($limit)
                ->orderBy($order, $dir)
                ->get();
            $totalFiltered = Category::Where('type', $request->type)->Where('title', 'LIKE', "%{$search}%")->count();
        }
        $data = [];
        foreach ($result as $item) {
  
            $imageUrl = GlobalFunction::createMediaUrl($item->image);

            if ($item->image != null) {
                $image = "<img src=". $imageUrl ." class='tbl_img'>";
            } else {
                $image = "<img src='./asset/img/placeholder-image.png' class='tbl_img'>";
            }
            if ($item->image == null) {
                $imageUrl = "null";
            }

            $totalWallpaper = Wallpaper::where('category_id', $item->id)->count();
            $wallpaperCount = '<span class="propertyCount">'.$totalWallpaper.'</span>';

            $edit = '<a href="#" data-title="'.$item->title.'" data-image="'.$imageUrl.'"  rel='.$item->id.' class="btn edit btn-success me-3">' . __('edit') . '</a>'; 
            $delete = '<a href="#" class="btn delete btn-danger" rel=' . $item->id . '>' . __('delete') . '</a>';
            $action = '<span class="float-end">'. $edit . $delete .' </span>' ;

            $data[] = [   
                $image,           
                $item->title,
                $wallpaperCount,
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

    public function addCategory(Request $request)
    {
        $category = new Category();
        $category->type = $request->type;
        $category->title = $request->title;
        if ($request->hasFile('image')) {
            $file = $request->file('image');
            $path = GlobalFunction::saveFileAndGivePath($file);
            $category->image = $path;
        }
        $category->save();

        return response()->json([
            'status' => true,
            'message' => 'Category Added Successfully',
            'data' => $category,
        ]);
        
    }

    public function updateCategory(Request $request)
    {
        $category = Category::where('id', $request->category_id)->first();

        if ($category == null) {
            return response()->json([
                'status' => false,
                'message' => 'Category Not Found',
            ]);
        } else {
            $category->title = $request->title;
            
            if ($request->hasFile('image')) {
                GlobalFunction::deleteFile($category->image); 

                $file = $request->file('image');
                $path = GlobalFunction::saveFileAndGivePath($file);
                $category->image = $path;
            }

            $category->save();

            return response()->json([
                'status' => true,
                'message' => 'Category Updated Successfully',
            ]);
        
        }
    }
    
    public function deleteCategory(Request $request)
    {
        $category = Category::where('id', $request->category_id)->first();
        
        if ($category == null) {
            return response()->json([
                'status' => false,
                'message' => 'Category Not Found',
            ]);
        } 
        GlobalFunction::deleteFile($category->image);

        $categoryWallpapers = Wallpaper::where('category_id', $request->category_id)->get();
        foreach ($categoryWallpapers as $categoryWallpaper) {
            GlobalFunction::deleteFile($categoryWallpaper->content);
            GlobalFunction::deleteFile($categoryWallpaper->thumbnail);
            $categoryWallpaper->delete();
        }
        
        $category->delete();

        return response()->json([
            'status' => true,
            'message' => 'Category Delete Successfully',
        ]);
    } 

     
}
