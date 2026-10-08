<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        if (!Schema::hasTable('global_settings')) {
            return;
        }

        Schema::table('global_settings', function (Blueprint $table) {
            if (!Schema::hasColumn('global_settings', 'splash_media')) {
                $table->string('splash_media')->nullable()->after('app_name');
            }
            if (!Schema::hasColumn('global_settings', 'splash_media_type')) {
                $table->string('splash_media_type', 20)->nullable()->after('splash_media');
            }
        });
    }

    public function down(): void
    {
        if (!Schema::hasTable('global_settings')) {
            return;
        }

        Schema::table('global_settings', function (Blueprint $table) {
            if (Schema::hasColumn('global_settings', 'splash_media_type')) {
                $table->dropColumn('splash_media_type');
            }
            if (Schema::hasColumn('global_settings', 'splash_media')) {
                $table->dropColumn('splash_media');
            }
        });
    }
};
