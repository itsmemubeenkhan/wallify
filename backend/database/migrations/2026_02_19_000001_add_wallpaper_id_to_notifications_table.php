<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (!Schema::hasTable('notifications')) {
            return;
        }

        if (!Schema::hasColumn('notifications', 'wallpaper_id')) {
            Schema::table('notifications', function (Blueprint $table) {
                $table->unsignedBigInteger('wallpaper_id')->nullable()->after('description');
            });
        }
    }

    public function down(): void
    {
        if (!Schema::hasTable('notifications')) {
            return;
        }

        if (Schema::hasColumn('notifications', 'wallpaper_id')) {
            Schema::table('notifications', function (Blueprint $table) {
                $table->dropColumn('wallpaper_id');
            });
        }
    }
};

