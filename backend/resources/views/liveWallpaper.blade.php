@extends('include.app')
@section('header')
    <script src="{{ asset('asset/script/wallpaper.js') }}"></script>
@endsection

@section('content')

<section class="section">
    <nav class="card-tab">
        <div class="nav nav-tabs" id="nav-tab" role="tablist">
            <a href="{{ route('wallpaper') }}" class="nav-link">
                 {{ __('wallpaper') }}
            </a>
            <a href="{{ route('liveWallpaper') }}" class="nav-link active">
                {{ __('liveWallpaper') }}
            </a>
        </div>
    </nav>
    
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
</section>

 

    <!-- Live WallpaperModal Modal -->
    <div class="modal fade" id="liveWallpaperModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('addLiveWallpaper') }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="addLiveWallpaperForm" method="post">
                    <div class="modal-body">
                        <input type="hidden" class="form-control" name="wallpaper_type" value="1">
                        <div class="form-group">
                            <label for="tags" class="form-label">{{ __('tags') }}</label>
                            <input type="text" class="form-control" name="tags" id="liveWallpaperTags" required="">
                        </div>
                         <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectCategory') }}</label>
                                    <select name="category_id" id="liveWallpaperCategory" class="form-control">
                                        <option disabled selected> {{ __('selectCategory') }} </option>
                                        @foreach($liveCategories as $liveCategory)
                                        <option value="{{$liveCategory->id}}"> {{$liveCategory->title}} </option>
                                        @endforeach
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectType') }}</label>
                                    <select name="access_type" id="liveWallpaperType" class="form-control">
                                        <option disabled selected> {{ __('selectType') }} </option>
                                        <option value="0"> {{ __('premium') }} </option>
                                        <option value="1"> {{ __('locked') }} </option>
                                        <option value="2"> {{ __('none') }} </option>
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                         </div>
                          <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group m-0">
                                    <label for="image" class="form-label">{{ __('thumbnail') }}</label>
                                    <div class="posterImg editContent">
                                        <img id="loadthumbnailImage" class="custom_img_portrait img-fluid" src="{{ asset('asset/img/placeholder-image-portrait.png')}}">
                                        <div class="upload-options upload-options-portrait btn-add-img">
                                            <label for="loadthumbnail1"> 
                                                <input type="file" accept=".png, .jpg, .jpeg, .webp, image/gif" onchange="loadthumbnail(event)" name="thumbnail" id="loadthumbnail1">
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group m-0">
                                    <label for="liveWallpaper" class="form-label">{{ __('liveWallpaper') }}</label>
                                    <video width="100%" class="live-video-tag" type="video/ogg" id="video" controls="" data-video="0"></video>
                                    <div class="upload-options upload-options-portrait btn-add-video">
                                        <label for="file-input"> 
                                            <input type="file" accept="video/*" name="content" id="file-input">
                                        </label>
                                    </div> 
                                </div>
                            </div>
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

    <!-- Edit live WallpaperModal Modal -->
    <div class="modal fade" id="editLiveWallpaperModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('editLiveWallpaper') }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="editLiveWallpaperForm" method="post">
                    <div class="modal-body">
                        <input type="hidden" class="form-control" name="wallpaper_type" value="1">
                        <input type="hidden" id="liveWallpaperID">
                        <div class="form-group">
                            <label for="tags" class="form-label">{{ __('tags') }}</label>
                            <input type="text" class="form-control" name="tags" id="editLiveWallpaperTags" required="">
                        </div>
                         <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectCategory') }}</label>
                                    <select name="category_id" id="editLiveWallpaperCategory" class="form-control">
                                        <option disabled selected> {{ __('selectCategory') }} </option>
                                        @foreach($liveCategories as $liveCategory)
                                        <option value="{{$liveCategory->id}}"> {{$liveCategory->title}} </option>
                                        @endforeach
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group select-option-arrow">
                                    <label for="category" class="form-label">{{ __('selectType') }}</label>
                                    <select name="access_type" id="editLiveWallpaperType" class="form-control">
                                        <option disabled selected> {{ __('selectType') }} </option>
                                        <option value="0"> {{ __('premium') }} </option>
                                        <option value="1"> {{ __('locked') }} </option>
                                        <option value="2"> {{ __('none') }} </option>
                                    </select>
                                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="6 9 12 15 18 9"></polyline></svg>
                                </div>
                            </div>
                         </div>
                          <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group m-0">
                                    <label for="image" class="form-label">{{ __('thumbnail') }}</label>
                                    <div class="posterImg editContent">
                                        <img id="editLoadthumbnailImage" class="custom_img_portrait img-fluid" src="{{ asset('asset/img/placeholder-image-portrait.png')}}">
                                        <div class="upload-options upload-options-portrait btn-add-img">
                                            <label for="editLoadthumbnail1"> 
                                                <input type="file" accept="image/*, image/gif" onchange="editLoadthumbnail(event)" name="thumbnail" id="editLoadthumbnail1">
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <div class="form-group m-0">
                                    <label for="editLiveWallpaper" class="form-label">{{ __('liveWallpaper') }}</label>
                                    <video width="100%" class="live-video-tag" type="video/ogg" id="editVideo" controls="" data-video="0"></video>
                                    <div class="upload-options upload-options-portrait btn-add-video">
                                        <label for="editFile-input"> 
                                            <input type="file" accept="video/*" name="content" id="editFile-input">
                                        </label>
                                    </div> 
                                </div>
                            </div>
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

    <!-- Live wallpaer preview modal -->
    <div class="modal fade" id="LiveWallpaperPreviewModal" tabindex="-1" aria-labelledby="videoPreviewModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <button type="button" class="btn-close preview-btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                <div class="videoPreview">
                    <video src="" id="showWallpaperUrl" playsinline autoplay controls type="video/mp4"></video>
                </div>
            </div>
        </div>
    </div>


    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <script>

$(document).on("change", "#file-input", function(evt) {
  var $source = $('#video');
  $source[0].src = URL.createObjectURL(this.files[0]);
  $source.parent()[0].load();
});

$(document).on("change", "#editFile-input", function(evt) {
  var $source = $('#editVideo');
  $source[0].src = URL.createObjectURL(this.files[0]);
  $source.parent()[0].load();
});

 
var loadthumbnail = function(event) {
    var output = document.getElementById('loadthumbnailImage');
    output.src = URL.createObjectURL(event.target.files[0]); 
    output.onload = function() {
        URL.revokeObjectURL(output.src)
    }
};
var editLoadthumbnail = function(event) {
    var output = document.getElementById('editLoadthumbnailImage');
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
