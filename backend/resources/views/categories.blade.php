@extends('include.app')
@section('header')
    <script src="{{ asset('asset/script/category.js') }}"></script>
@endsection

@section('content')

<section class="section">
    <nav class="card-tab">
        <div class="nav nav-tabs" id="nav-tab" role="tablist">
            <a href="{{ route('categories') }}" class="nav-link active">
                {{ __('category') }}
            </a>
            <a href="{{ route('liveCategories') }}" class="nav-link" >
                {{ __('liveCategory') }}
            </a>
        </div>
    </nav>
    <div class="card">
        <div class="card-header">
            <div class="page-title w-100">
                <div class="d-flex align-items-center justify-content-between">
                    <h4 class="mb-0 fw-semibold">{{ __('category') }}</h4>
                    <button type="button" class="btn theme-bg theme-btn text-white" data-bs-toggle="modal"
                        data-bs-target="#categoryModal" id="addCategoryType">
                        {{ __('addCategory') }}
                    </button>
                </div>
            </div>
        </div>
        <div class="card-body">
              <table class="table table-striped w-100" id="categoryTable">
                <thead>
                    <tr>
                        <th width="150px">{{ __('image') }}</th>
                        <th>{{ __('title') }}</th>
                        <th>{{ __('wallpaperCount') }}</th>
                        <th width="200px" style="text-align: right">{{ __('action') }}</th>
                    </tr>
                </thead>
            </table>
            <!-- <table class="table table-striped w-100 dataTable no-footer" id="categoryTable">
                <thead>
                    <tr>
                        <th width="100px">{{ __('image') }}</th>
                        <th width="100%">{{ __('title') }}</th>
                        <th width="100%">{{ __('wallpaperCount') }}</th>
                        <th width="200px" style="text-align: right">{{ __('action') }}</th>
                    </tr>
                </thead>
            </table> -->
        </div>
    </div>
   
</section>

 
    <!-- Category Modal -->
    <div class="modal fade" id="categoryModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('addCategory') }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="addCategoryForm" method="post">
                    <div class="modal-body">
                        <input type="hidden" class="form-control" name="type" id="categoryType" value="0" >
                        <div class="form-group">
                            <label for="title" class="form-label">{{ __('title') }}</label>
                            <input type="text" class="form-control" name="title" id="category" required="">
                        </div>
                        <div class="form-group">
                            <label for="image" class="form-label">{{ __('image') }}</label>
                            <div class="posterImg">
                                <img id="posterImg" class="custom_img img-fluid" src="{{ asset('asset/img/placeholder-image.png')}}">
                                <div class="upload-options">
                                    <label for="poster"> 
                                        <input type="file" accept=".png, .jpg, .jpeg, .webp" onchange="loadFile(event)" name="image" id="poster" required>
                                    </label>
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

    <!-- Edit Category Modal -->
    <div class="modal fade" id="editCategoryModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('editCategory') }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="editCategoryForm" method="post">
                    <input type="hidden" id="categoryID">
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="title" class="form-label">{{ __('title') }}</label>
                            <input type="text" class="form-control" name="title" id="editCategory" required="">
                        </div>
                        <div class="form-group">
                            <label for="image" class="form-label">{{ __('image') }}</label>
                            <div class="posterImg editContent">
                                <img id="posterImgEdit" class="custom_img img-fluid" src="{{ asset('asset/img/placeholder-image.png')}}">
                                <div class="upload-options">
                                    <label for="poster1"> 
                                        <input type="file" accept=".png, .jpg, .jpeg, .webp" onchange="loadFileEdit(event)" name="image" id="poster1">
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

    <script>


 var loadFile = function(event) {
        var output = document.getElementById('posterImg');
        if (event.target.files.length > 0) {
            output.src = URL.createObjectURL(event.target.files[0]);
            output.onload = function() {
                URL.revokeObjectURL(output.src);
            };
        } else {
            // If no file is selected, set the src attribute to the placeholder image
            output.src = "{{ asset('asset/img/placeholder-image.png') }}";
        }
    };

var loadFileEdit = function(event) {
    var output = document.getElementById('posterImgEdit');
    output.src = URL.createObjectURL(event.target.files[0]); 
    output.onload = function() {
        URL.revokeObjectURL(output.src)
    }
};
 

    </script>
@endsection
