<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, shrink-to-fit=no" name="viewport">
    <title>{!! Session::get('app_name') !!}</title>
    <link rel='shortcut icon' type='image/x-icon' href="{{ asset('asset/img/favicon.png') }}" style="width: 2px !important;" />

    <link href="{{ asset('asset/css/bootstrap.min.css') }}" rel="stylesheet">
    <link href="{{ asset('asset/cdncss/iziToast.css') }}" rel="stylesheet" /> 
    <link href="{{ asset('asset/css/style.css') }}" rel="stylesheet">
</head>

<body>
<div class="loader-bg">
    <div class="loader"></div>
</div>

@if (Session::has('message'))
<div class="session-message">
     <div class="iziToast-body" style="padding-left: 33px;">
        <img class="iziToast-icon" src="{{asset('asset/img/x.svg')}}">
        <div class="iziToast-texts">
            <strong class="iziToast-title" style="margin-right: 10px;">{{ __('error') }}</strong>
            <p class="iziToast-message">{!! Session::get('message') !!}</p>
        </div>
    </div>
</div>
@endif

<div class="login-page">
    <div class="container">
        <div class="row align-items-center justify-content-center">
            <div class="col-lg-5 col-md-5 col-sm-12">
                <div class="login-box">
                    <div class="text-center mb-4">
                        <h3> {{ Session::get('app_name') }} </h3>
                    </div>
                    <div class="card login-card">
                        <div class="card-header">
                            <h4>{{ __('logIn')}}</h4>
                        </div>
                        <div class="card-body">
                            <form method="POST" action="login">
                            @csrf
                                <div class="login-form">
                                    <div class="">
                                        <div class="mb-4 form-group">
                                            <label for="user_name" class="form-label">{{ __('username')}}</label>
                                            <input name="user_name" type="text" class="form-control" required id="user_name">
                                        </div>
                                        <div class="mb-4 form-group">
                                            <label for="user_password" class="form-label">{{ __('password')}}</label>
                                            <input name="user_password" type="password" class="form-control" required id="user_password">
                                        </div>
                                    </div>
                                    <button type="submit" class="btn theme-btn text-white mt-4">
                                        {{ __('logIn')}}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
    <div class="main-login-row">
        <div class="width-50">
            <div class="main-login-two-box">
               
            </div>
        </div>
        <div class="width-50 ">
            <div class="main-login-one-box  center">
                <div class="center container ">
                 
                </div>
            </div>
        </div>
    </div>
 
    <script src="asset/js/app.min.js"></script>
    <script src="asset/js/scripts.js"></script>
    <script src="asset/js/custom.js"></script>
    
</body>

</html>
