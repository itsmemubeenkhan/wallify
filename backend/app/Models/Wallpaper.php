<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Wallpaper extends Model
{
    use HasFactory;

    public function category() {
        return $this->hasOne(Category::class, 'id', 'category_id');
    }

    public function downloads()
    {
        return $this->hasMany(WallpaperDownloadAnalytics::class, 'wallpaper_id');
    }
}
