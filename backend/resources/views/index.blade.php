@extends('include.app')
@section('header')
<script src="{{ asset('asset/script/index.js') }}"></script>
@endsection

@section('content') 
<section class="section dashboard-section">

    <div class="dashboard-cards">
        <!-- <div class="dashboard-blog">
            <div class="dashboard-blog-content-top">
                <p> {{ $allCategories }}</p>
                <h4 class="fw-normal">All Category</h4>
                <a href="{{route('categories')}}">{{ __('viewMore') }}</a>
            </div>
            <div class="dashboard-blog-content">
                <div class="card-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-users"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
                </div>
            </div>
        </div> -->
        <div class="dashboard-blog">
            <div class="dashboard-blog-content-top">
                <p>{{ $category }}</p>
                <h4 class="fw-normal">{{ __('category') }}</h4>
                <a href="{{route('categories')}}">{{ __('viewMore') }}</a>
            </div>
            <div class="dashboard-blog-content">
                <div class="card-icon">
                      <i data-feather="box"></i>
                </div>
            </div>
        </div>
        <div class="dashboard-blog">
            <div class="dashboard-blog-content-top">
                <p>{{ $liveCategory }}</p>
                <h4 class="fw-normal">{{ __('liveCategory') }}</h4>
                <a href="{{route('liveCategories')}}">{{ __('viewMore') }}</a>
            </div>
            <div class="dashboard-blog-content">
                <div class="card-icon">
                    <i data-feather="box"></i>
                </div>
            </div>
        </div>
        <!-- <div class="dashboard-blog">
            <div class="dashboard-blog-content-top">
                <p>{{ $allWallpapers }}</p>
                <h4 class="fw-normal">All Wallpaper</h4>
                <a href="{{ route('wallpaper') }}">View more</a>
            </div>
            <div class="dashboard-blog-content">
                <div class="card-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-check-circle"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                </div>
            </div>
        </div> -->
        <div class="dashboard-blog">
            <div class="dashboard-blog-content-top">
                <p> {{ $wallpapers }} </p>
                <h4 class="fw-normal">{{ __('wallpaper') }}</h4>
                <a href="{{ route('wallpaper') }}">{{ __('viewMore') }}</a>
            </div>
            <div class="dashboard-blog-content">
                <div class="card-icon">
                    <i data-feather="image"></i>
                </div>
            </div>
        </div>
        <div class="dashboard-blog">
            <div class="dashboard-blog-content-top">
                <p> {{ $liveWallpapers }}</p>
                <h4 class="fw-normal">{{ __('liveWallpaper') }}</h4>
                <a href="{{ route('liveWallpaper') }}">{{ __('viewMore') }}</a>
            </div>
            <div class="dashboard-blog-content">
                <div class="card-icon">
                    <i data-feather="image"></i>
                </div>
            </div>
        </div>
    </div>
</section>
@endsection