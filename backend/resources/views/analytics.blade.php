@extends('include.app')

@section('content')
<section class="section">
    <div class="section-body">
        <div class="row">
            <div class="col-md-6 col-lg-3">
                <div class="dashboard-blog">
                    <div class="dashboard-blog-content-top">
                        <p>{{ $totalDownloads }}</p>
                        <h4 class="fw-normal">Total Downloads</h4>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-lg-3">
                <div class="dashboard-blog">
                    <div class="dashboard-blog-content-top">
                        <p>{{ $uniqueDevices }}</p>
                        <h4 class="fw-normal">Unique Devices</h4>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h4>Top Downloaded Wallpapers</h4>
            </div>
            <div class="card-body table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Wallpaper ID</th>
                            <th>Downloads</th>
                            <th>Content</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse ($topWallpapers as $item)
                            <tr>
                                <td>{{ $loop->iteration }}</td>
                                <td>{{ $item->id }}</td>
                                <td>{{ $item->download_count }}</td>
                                <td>{{ $item->content }}</td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="4">No download analytics yet.</td>
                            </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h4>Recent Download Logs</h4>
            </div>
            <div class="card-body table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Time</th>
                            <th>Wallpaper</th>
                            <th>Device</th>
                            <th>OS</th>
                            <th>App</th>
                            <th>IP</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse ($recentLogs as $log)
                            <tr>
                                <td>{{ $loop->iteration }}</td>
                                <td>{{ $log->created_at }}</td>
                                <td>#{{ $log->wallpaper_id }}</td>
                                <td>
                                    {{ $log->device_brand ?? '-' }}
                                    {{ $log->device_model ?? '' }}
                                    @if (!empty($log->device_manufacturer))
                                        <small>({{ $log->device_manufacturer }})</small>
                                    @endif
                                </td>
                                <td>{{ $log->os_version ?? '-' }}</td>
                                <td>{{ $log->app_version ?? '-' }}</td>
                                <td>{{ $log->ip_address ?? '-' }}</td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="7">No logs found.</td>
                            </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>
@endsection

@section('header')
<script>
    $(function () {
        $('.sideBarli').removeClass('active');
        $('.analyticsSideA').addClass('active');
    });
</script>
@endsection

