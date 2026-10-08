@extends('include.app')
@section('header')
    <script src="{{ asset('asset/script/wallpaper.js') }}"></script>
@endsection

@section('content')

<section class="section">
    <nav class="card-tab">
        <div class="nav nav-tabs" id="nav-tab" role="tablist">
            <a href="{{ route('wallpaper') }}" class="nav-link active">
               {{ __('wallpaper') }} 
            </a>
            <a href="{{ route('liveWallpaper') }}" class="nav-link">
                {{ __('liveWallpaper') }} 
            </a>
        </div>
    </nav>
    <div class="card">
        <div class="card-header">
            <div class="page-title w-100">
                <div class="d-flex align-items-center justify-content-between">
                    <h4 class="mb-0 fw-semibold">{{ __('wallpaper') }}</h4>
                    <button type="button" class="btn theme-bg theme-btn text-white" data-bs-toggle="modal"
                        data-bs-target="#wallpaperModal">
                        {{ __('addWallpaper') }}
                    </button>
                </div>
            </div>
        </div>
        <div class="card-body">
            <table class="table table-striped w-100" id="wallpaperTable">
                <thead>
                    <tr>
                        <th width="100px">{{ __('image') }}</th>
                        <th>{{ __('category') }}</th>
                        <th>{{ __('tags') }}</th>
                        <th>{{ __('type') }}</th>
                        <th>{{ __('featured') }}</th>
                        <th width="200px" style="text-align: right">{{ __('action') }}</th>
                    </tr>
                </thead>
            </table>
        </div>
    </div>
    <div class="tab-content" id="nav-tabContent">
        <div class="tab-pane" id="nav-live-wallpaper" role="tabpanel" aria-labelledby="nav-live-wallpaper-tab" tabindex="0">
            <div class="card">
                <div class="card-header">
                    <div class="page-title w-100">
                        <div class="d-flex align-items-center justify-content-between">
                            <h4 class="mb-0 fw-semibold">{{ __('liveWallpaper') }}</h4>
                            <button type="button" class="btn theme-bg theme-btn text-white" data-bs-toggle="modal"
                                data-bs-target="#liveWallpaperModal">
                                {{ __('addLiveWallpaper') }}
                            </button>
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <table class="table table-striped w-100" id="liveWallpaperTable">
                        <thead>
                            <tr>
                                <th width="100px">{{ __('thumbnail') }}</th>
                                <th>{{ __('liveWallpaper') }}</th>
                                <th>{{ __('category') }}</th>
                                <th>{{ __('tags') }}</th>
                                <th>{{ __('type') }}</th>
                                <th>{{ __('featured') }}</th>
                                <th width="200px" style="text-align: right">{{ __('action') }}</th>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

 
    <!-- Wallpaper Modal -->
    <div class="modal fade" id="wallpaperModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('addWallpaper') }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="addWallpaperForm" method="post">
                    <div class="modal-body">
                        <input type="hidden" class="form-control" name="wallpaper_type" id="wallpaper_type" value="0" >
                        <div class="form-group">
                            <label for="title" class="form-label">{{ __('tags') }}</label>
                            <input type="text" class="form-control" name="tags" id="tags" required="">
                        </div>
                         <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectCategory') }}</label>
                                    <select name="category_id" id="category" class="form-control" required="">
                                        <option value="" disabled selected>{{ __('selectCategory') }} </option>
                                        @foreach($categories as $category)
                                        <option value="{{$category->id}}"> {{$category->title}} </option>
                                        @endforeach
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectType') }}</label>
                                    <select name="access_type" id="type" class="form-control" required="">
                                        <option value="" disabled selected> {{ __('selectType') }} </option>
                                        <option value="0"> {{ __('premium') }} </option>
                                        <option value="1"> {{ __('locked') }} </option>
                                        <option value="2"> {{ __('none') }} </option>
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                         </div>
                        <div class="form-group m-0">
                            <label for="image" class="form-label">{{ __('image') }}</label>
                            <div class="upload__box">
                                <label class="upload__btn btn text-white theme-btn">
                                    <p class="m-0 ">{{ __('uploadImages') }}</p>
                                    <input type="file" accept="image/*"  multiple="" id="images" data-max_length="20" class="upload__inputfile" name="content[]">
                                </label>
                            </div>
                        </div> 
                        <div class="form-group mt-3">
                            <label for="content_urls" class="form-label">Or Direct Image Link(s)</label>
                            <textarea class="form-control" id="content_urls" name="content_urls" rows="3" placeholder="https://example.com/image1.jpg&#10;https://example.com/image2.png"></textarea>
                            <small class="text-muted">One URL per line or comma separated.</small>
                        </div>
                        <div id="image_preview" class="upload__img-wrap" style="width:100%;">
                        
                        </div> 
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn modal-cancel-btn"
                            data-bs-dismiss="modal">{{ __('cancel') }}</button>
                        <button type="submit" class="btn theme-btn text-white saveButton">{{ __('save') }}</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit Wallpaper Modal -->
    <div class="modal fade" id="editWallpaperModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('editWallpaper') }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="editWallpaperForm" method="post">
                    <div class="modal-body">
                        <input type="hidden" id="wallpaperID">
                        <div class="form-group">
                            <label for="title" class="form-label">{{ __('tags') }}</label>
                            <input type="text" class="form-control" name="tags" id="editTags" required="">
                        </div>
                         <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectCategory') }}</label>
                                    <select name="category_id" id="editCategory" class="form-control">
                                        <option disabled selected> {{ __('selectCategory') }} </option>
                                        @foreach($categories as $category)
                                        <option value="{{$category->id}}"> {{$category->title}} </option>
                                        @endforeach
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectType') }}</label>
                                    <select name="access_type" id="editType" class="form-control">
                                        <option disabled selected> {{ __('selectType') }} </option>
                                        <option value="0"> {{ __('premium') }} </option>
                                        <option value="1"> {{ __('locked') }} </option>
                                        <option value="2"> {{ __('none') }} </option>
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                         </div>
                         <div class="form-group m-0">
                            <label for="image" class="form-label">{{ __('image') }}</label>
                            <div class="posterImg editContent">
                                <img id="posterImgEdit" class="custom_img_portrait img-fluid" src="{{ asset('asset/img/placeholder-image-portrait.png')}}">
                                <div class="upload-options upload-options-portrait">
                                    <label for="poster1"> 
                                        <input type="file" accept="image/*" onchange="loadFileEdit(event)" name="content" id="poster1">
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn modal-cancel-btn"
                            data-bs-dismiss="modal">{{ __('cancel') }}</button>
                        <button type="submit" class="btn theme-btn text-white saveButton1">{{ __('save') }}</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
 
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <script>



var loadFileEdit = function(event) {
    var output = document.getElementById('posterImgEdit');
    output.src = URL.createObjectURL(event.target.files[0]); 
    output.onload = function() {
        URL.revokeObjectURL(output.src)
    }
}; 

// $(document).ready(function() {
    var fileArr = [];
    $("#images").change(function(){
 
        // check if fileArr length is greater than 0
        if (fileArr.length > 0) fileArr = [];
        
        $('#image_preview').html("");
        var total_file = document.getElementById("images").files;
        if (!total_file.length) return;
        for (var i = 0; i < total_file.length; i++) {
            if (total_file[i].size > 1048576) {
            return false;
            } else {
            fileArr.push(total_file[i]);
            $('#image_preview').append("<div class='img-div upload__img-box' id='img-div"+i+"'><img src='"+URL.createObjectURL(event.target.files[i])+"' class='img-responsive image img-bg' title='"+total_file[i].name+"'><button id='action-icon' value='img-div"+i+"' class='upload__img-close' role='"+total_file[i].name+"'></button></div>");
            }
        }
   });
  
    $('body').on('click', '#action-icon', function(evt){
        var divName = this.value;
        var fileName = $(this).attr('role');
        $(`#${divName}`).remove();
        
        for (var i = 0; i < fileArr.length; i++) {
            if (fileArr[i].name === fileName) {
            fileArr.splice(i, 1);
            }
        }
        document.getElementById('images').files = FileListItem(fileArr);
        evt.preventDefault();
    });
  
   function FileListItem(file) {
        file = [].slice.call(Array.isArray(file) ? file : arguments)
        for (var c, b = c = file.length, d = !0; b-- && d;) d = file[b] instanceof File
        if (!d) throw new TypeError("expected argument to FileList is File or array of File objects")
        for (b = (new ClipboardEvent("")).clipboardData || new DataTransfer; c--;) b.items.add(file[c])
        return b.files
    }
// });


    </script>
@endsection
