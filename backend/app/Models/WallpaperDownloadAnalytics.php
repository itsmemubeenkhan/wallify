<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class WallpaperDownloadAnalytics extends Model
{
    use HasFactory;

    protected $table = 'wallpaper_download_analytics';

    protected $fillable = [
        'wallpaper_id',
        'device_id',
        'device_brand',
        'device_model',
        'device_manufacturer',
        'os_version',
        'app_version',
        'ip_address',
    ];

    public function wallpaper()
    {
        return $this->belongsTo(Wallpaper::class, 'wallpaper_id');
    }
}

