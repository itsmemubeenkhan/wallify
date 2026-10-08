<?php 

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

use App\Http\Controllers\SettingsController;
use Illuminate\Support\Facades\Route;

Route::post('testSearch', [SettingsController::class, 'testSearch'])->name('testSearch');

Route::post('fetchAllData', [SettingsController::class, 'fetchAllData'])->middleware('checkHeader');

Route::post('fetchWallpaperByCategory', [SettingsController::class, 'fetchWallpaperByCategory'])->middleware('checkHeader');
Route::post('searchWallpaper', [SettingsController::class, 'searchWallpaper'])->middleware('checkHeader');
Route::post('fetchSettings', [SettingsController::class, 'fetchSettings'])->middleware('checkHeader');
Route::post('fetchHomePageData', [SettingsController::class, 'fetchHomePageData'])->middleware('checkHeader');
Route::post('fetchLikedWallpaper', [SettingsController::class, 'fetchLikedWallpaper'])->middleware('checkHeader');
Route::post('trackWallpaperDownload', [SettingsController::class, 'trackWallpaperDownload'])->middleware('checkHeader');


