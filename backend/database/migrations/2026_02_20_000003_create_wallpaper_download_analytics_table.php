<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('wallpaper_download_analytics', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('wallpaper_id');
            $table->string('device_id', 191)->nullable();
            $table->string('device_brand', 100)->nullable();
            $table->string('device_model', 120)->nullable();
            $table->string('device_manufacturer', 120)->nullable();
            $table->string('os_version', 60)->nullable();
            $table->string('app_version', 60)->nullable();
            $table->string('ip_address', 64)->nullable();
            $table->timestamps();

            $table->index('wallpaper_id');
            $table->index('device_id');
            $table->foreign('wallpaper_id')
                ->references('id')
                ->on('wallpapers')
                ->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('wallpaper_download_analytics');
    }
};

