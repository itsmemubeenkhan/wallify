@extends('include.app')
@section('header')
    <script src="{{ asset('asset/script/setting.js') }}"></script>
@endsection

@section('content')
    <div class="row same-height-card">
        <div class="col-lg-6 col-md-6 col-sm-12">
            <div class="card">
                <div class="card-header">
                    <div class="page-title w-100">
                        <div class="d-flex align-items-center justify-content-between">
                            <h4 class="mb-0 fw-semibold">{{ __('settings') }}</h4>
                        </div>
                    </div>
                </div>
                <div class="card-body px-4">
                    <form id="settingsForm" method="post" action="{{ route('saveSettings') }}" enctype="multipart/form-data" class="form-border" autocomplete="off">
                        @csrf
                        @php
                            $splashUrl = \App\Models\GlobalFunction::createMediaUrl($setting->splash_media ?? null);
                            $splashType = $setting->splash_media_type ?? '';
                            $featuredUrl = \App\Models\GlobalFunction::createMediaUrl($setting->featured_media ?? null);
                            $featuredType = $setting->featured_media_type ?? '';
                        @endphp
                        <div class="row">
                            <div class="col-md-12">
                                <div class="form-group">
                                    <label for="currency" class="form-label">{{ __('appNameTitle') }}</label>
                                    <input value="{{ $setting->app_name }}" type="text" name="app_name" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-md-12 mt-2">
                                <div class="form-group">
                                    <label class="form-label">Splash Media (Image/Video)</label>
                                    <input type="file" name="splash_media" class="form-control" accept="image/*,video/*">
                                    <small class="text-muted">Supported: JPG, PNG, WEBP, MP4, MOV, M4V, 3GP, WEBM (Max 50MB)</small>
                                </div>
                            </div>
                            <div class="col-md-12 mt-2">
                                <div class="form-group">
                                    <label class="form-label">Splash Media Direct Link</label>
                                    <input
                                        type="url"
                                        name="splash_media_link"
                                        class="form-control"
                                        placeholder="https://example.com/video.mp4 or image.jpg">
                                    <small class="text-muted">If provided, this link will be used for splash media.</small>
                                </div>
                            </div>
                            <div class="col-md-12 mt-2">
                                <div class="form-group">
                                    <label for="announcement_text" class="form-label">Announcement</label>
                                    <textarea
                                        id="announcement_text"
                                        name="announcement_text"
                                        class="form-control"
                                        rows="3"
                                        placeholder="Write announcement for app home screen...">{{ $setting->announcement_text ?? '' }}</textarea>
                                    <small class="text-muted">If empty, announcement bar will stay hidden in app.</small>
                                </div>
                            </div>
                            <div class="col-md-12 mt-2">
                                <div class="form-group">
                                    <label class="form-label">Featured Section Override (Image/Video)</label>
                                    <input type="file" name="featured_media" class="form-control" accept="image/*,video/*">
                                    <small class="text-muted">When uploaded, this media will replace featured carousel in app home.</small>
                                </div>
                            </div>
                            <div class="col-md-12 mt-2">
                                <div class="form-group">
                                    <label class="form-label">Featured Override Direct Link</label>
                                    <input
                                        type="url"
                                        name="featured_media_link"
                                        class="form-control"
                                        placeholder="https://example.com/video.mp4 or image.jpg">
                                    <small class="text-muted">If provided, this link will be used for featured override.</small>
                                </div>
                            </div>
                            @if(!empty($splashUrl))
                                <div class="col-md-12 mt-2">
                                    <label class="form-label">Current Splash Preview</label>
                                    <div>
                                        @if($splashType === 'video')
                                            <video src="{{ $splashUrl }}" controls style="max-width: 220px; border-radius: 10px;"></video>
                                        @else
                                            <img src="{{ $splashUrl }}" alt="Splash Preview" style="max-width: 220px; border-radius: 10px;">
                                        @endif
                                    </div>
                                    <div class="form-check mt-2">
                                        <input class="form-check-input" type="checkbox" value="1" name="remove_splash_media" id="removeSplashMedia">
                                        <label class="form-check-label" for="removeSplashMedia">
                                            Remove current splash media
                                        </label>
                                    </div>
                                </div>
                            @endif
                            @if(!empty($setting->featured_media))
                                <div class="col-md-12 mt-2">
                                    <label class="form-label">Current Featured Override Preview</label>
                                    <div>
                                        @if($featuredType === 'video')
                                            <video src="{{ $featuredUrl }}" controls style="max-width: 220px; border-radius: 10px;"></video>
                                        @else
                                            <img src="{{ $featuredUrl }}" alt="Featured Preview" style="max-width: 220px; border-radius: 10px;">
                                        @endif
                                    </div>
                                    <div class="form-check mt-2">
                                        <input class="form-check-input" type="checkbox" value="1" name="remove_featured_media" id="removeFeaturedMedia">
                                        <label class="form-check-label" for="removeFeaturedMedia">
                                            Remove featured override media (show default featured)
                                        </label>
                                    </div>
                                </div>
                            @endif
                        </div>
                        <div class="mt-2 float-end">
                            <button type="submit" class="btn theme-btn text-white saveButton2">{{ __('save') }}</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-lg-6 col-md-8 col-sm-12">
            <div class="card">
                <div class="card-header">
                    <div class="page-title w-100">
                        <div class="d-flex align-items-center justify-content-between">
                            <h4 class="mb-0 fw-normal">{{ __('changePassword') }}</h4>
                        </div>
                    </div>
                </div>
                <div class="card-body px-4">
                    <form id="changePasswordForm" method="POST" action="{{ route('changePassword') }}">
                        <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12 position-relative">
                                <div class="form-group">
                                    <label for="appName" class="form-label">{{ __('oldPassword') }}</label>
                                    <input type="password" class="form-control" name="user_password" id="userPassword" required="">
                                    <div class="password-icon">
                                        <i data-feather="eye"></i>
                                        <i data-feather="eye-off"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12 position-relative">
                                <div class="form-group">
                                    <label for="appName" class="form-label">{{ __('newPassword') }}</label>
                                    <input type="password" class="form-control" name="new_password" id="newPassword" required="">
                                    <div class="password-icon">
                                        <i data-feather="eye" class="eye1"></i>
                                        <i data-feather="eye-off" class="eye-off1"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer p-0">
                            <button type="button" class="btn"></button>
                            <button type="submit" class="btn theme-btn text-white saveButton3">{{ __('changePassword') }}</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
@endsection
