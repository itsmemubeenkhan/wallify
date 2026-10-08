@extends('include.app')
@section('header')
    <script src="{{ asset('asset/script/subscription.js') }}"></script>
@endsection

@section('content')

<section class="section">
    <div class="container-fluid">
        <div class="row">
            <div class="col-lg-6 col-md-6 col-sm-12">
                <div class="card">
                    <div class="card-header">
                        <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('monthlySubscription') }}</h1>
                    </div>
                    <div class="card-body px-4">
                        <form id="monthlySubscriptionForm" method="post">
                            <input type="hidden" name="package_id" value="{{$monthlySubscription->package_id}}">
                            <div class="form-group">
                                <label for="title" class="form-label">{{ __('androidProductID') }}</label>
                                <input type="text" class="form-control" name="android_product_id" id="android_product_id" required="" value="{{$monthlySubscription->android_product_id}}">
                            </div>
                            <div class="form-group">
                                <label for="title" class="form-label">{{ __('iOSProductID') }}</label>
                                <input type="text" class="form-control" name="ios_product_id" id="ios_product_id" required="" value="{{$monthlySubscription->ios_product_id}}">
                            </div>
                            <div class="modal-footer px-0">
                                <button type="submit" class="btn theme-btn text-white">{{ __('save') }}</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div class="col-lg-6 col-md-6 col-sm-12">
                <div class="card">
                    <div class="card-header">
                        <h1 class="modal-title fs-5 fw-semibold" id="exampleModalLabel">{{ __('yearlySubscription') }}</h1>
                    </div>                    
                    <div class="card-body px-4">
                        <form id="yearlySubscriptionForm" method="post">
                            <input type="hidden" name="package_id" value="{{$yearlySubscription->package_id}}">
                            <div class="form-group">
                                <label for="title" class="form-label">{{ __('androidProductID') }}</label>
                                <input type="text" class="form-control" name="android_product_id" id="android_product_id" required="" value="{{$yearlySubscription->android_product_id}}">
                            </div>
                            <div class="form-group">
                                <label for="title" class="form-label">{{ __('iOSProductID') }}</label>
                                <input type="text" class="form-control" name="ios_product_id" id="ios_product_id" required="" value="{{$yearlySubscription->ios_product_id}}">
                            </div>
                            <div class="modal-footer px-0">
                                <button type="submit" class="btn theme-btn text-white">{{ __('save') }}</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
</section>
 
@endsection