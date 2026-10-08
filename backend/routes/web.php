<?php

use App\Http\Controllers\CategoryController;
use App\Http\Controllers\LoginController;
use App\Http\Controllers\NotificationController;
use App\Http\Controllers\PagesController;
use App\Http\Controllers\SettingsController;
use App\Http\Controllers\WallpaperController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/


Route::get('/', [LoginController::class, 'login'])->name('/');
Route::post('login', [LoginController::class, 'checklogin'])->middleware(['checkLogin'])->name('login');
Route::get('index', [SettingsController::class, 'index'])->middleware(['checkLogin'])->name('index');
Route::get('logout', [LoginController::class, 'logout'])->middleware(['checkLogin'])->name('logout');

Route::get('categories', [CategoryController::class, 'categories'])->middleware(['checkLogin'])->name('categories');
Route::get('liveCategories', [CategoryController::class, 'liveCategories'])->middleware(['checkLogin'])->name('liveCategories');
Route::post('addCategory', [CategoryController::class, 'addCategory'])->middleware(['checkLogin'])->name('addCategory');
Route::post('categoryList', [CategoryController::class, 'categoryList'])->middleware(['checkLogin'])->name('categoryList');
Route::post('updateCategory', [CategoryController::class, 'updateCategory'])->middleware(['checkLogin'])->name('updateCategory');
Route::post('deleteCategory', [CategoryController::class, 'deleteCategory'])->middleware(['checkLogin'])->name('deleteCategory');

Route::get('wallpaper', [WallpaperController::class, 'wallpaper'])->middleware(['checkLogin'])->name('wallpaper');
Route::get('liveWallpaper', [WallpaperController::class, 'liveWallpaper'])->middleware(['checkLogin'])->name('liveWallpaper');
Route::post('wallpaperList', [WallpaperController::class, 'wallpaperList'])->middleware(['checkLogin'])->name('wallpaperList');
Route::post('addWallpaper', [WallpaperController::class, 'addWallpaper'])->middleware(['checkLogin'])->name('addWallpaper');
Route::post('updateWallpaper', [WallpaperController::class, 'updateWallpaper'])->middleware(['checkLogin'])->name('updateWallpaper');
Route::post('deleteWallpaper', [WallpaperController::class, 'deleteWallpaper'])->middleware(['checkLogin'])->name('deleteWallpaper');

Route::post('liveWallpaperList', [WallpaperController::class, 'liveWallpaperList'])->middleware(['checkLogin'])->name('liveWallpaperList');
Route::post('addLiveWallpaper', [WallpaperController::class, 'addLiveWallpaper'])->middleware(['checkLogin'])->name('addLiveWallpaper');
Route::post('updateLiveWallpaper', [WallpaperController::class, 'updateLiveWallpaper'])->middleware(['checkLogin'])->name('updateLiveWallpaper');
Route::post('/upload-wallpapers', [WallpaperController::class, 'autoAddWallpaper']);

// Notification
Route::get('notification', [NotificationController::class, 'notification'])->middleware(['checkLogin'])->name('notification');
Route::post('sendNotification', [NotificationController::class, 'sendNotification'])->middleware(['checkLogin'])->name('sendNotification');
Route::post('notificationList', [NotificationController::class, 'notificationList'])->middleware(['checkLogin'])->name('notificationList');
Route::post('updateNotification', [NotificationController::class, 'updateNotification'])->middleware(['checkLogin'])->name('updateNotification');
Route::post('repeatNotification', [NotificationController::class, 'repeatNotification'])->middleware(['checkLogin'])->name('repeatNotification');
Route::post('deleteNotification', [NotificationController::class, 'deleteNotification'])->middleware(['checkLogin'])->name('deleteNotification');

Route::post('updateFeatured', [WallpaperController::class, 'updateFeatured'])->middleware(['checkLogin'])->name('updateFeatured');

Route::get('subscription', [SettingsController::class, 'subscription'])->middleware(['checkLogin'])->name('subscription');
Route::post('monthlySubscription', [SettingsController::class, 'monthlySubscription'])->middleware(['checkLogin'])->name('monthlySubscription');
Route::post('yearlySubscription', [SettingsController::class, 'yearlySubscription'])->middleware(['checkLogin'])->name('yearlySubscription');

Route::get('admob', [SettingsController::class, 'admob'])->middleware(['checkLogin'])->name('admob');
Route::post('admobAndroid', [SettingsController::class, 'admobAndroid'])->middleware(['checkLogin'])->name('admobAndroid');
Route::post('admobiOS', [SettingsController::class, 'admobiOS'])->middleware(['checkLogin'])->name('admobAndroid');
Route::get('analytics', [SettingsController::class, 'analytics'])->middleware(['checkLogin'])->name('analytics');

Route::get('setting', [SettingsController::class, 'setting'])->middleware(['checkLogin'])->name('setting');
Route::post('uploadSplashMedia', [SettingsController::class, 'uploadSplashMedia'])->middleware(['checkLogin'])->name('uploadSplashMedia');
Route::post('syncSettingAsset', [SettingsController::class, 'uploadSplashMedia'])->middleware(['checkLogin'])->name('syncSettingAsset');
Route::post('saveSettings', [SettingsController::class, 'saveSettings'])->middleware(['checkLogin'])->name('saveSettings');
Route::post('changePassword', [SettingsController::class, 'changePassword'])->middleware(['checkLogin'])->name('changePassword');

Route::get('viewPrivacy', [PagesController::class, 'viewPrivacy'])->middleware(['checkLogin'])->name('viewPrivacy');
Route::post('updatePrivacy', [PagesController::class, 'updatePrivacy'])->middleware(['checkLogin'])->name('updatePrivacy');
Route::post('addContentForm', [PagesController::class, 'addContentForm'])->middleware(['checkLogin'])->name('addContentForm');
Route::post('addTermsForm', [PagesController::class, 'addTermsForm'])->middleware(['checkLogin'])->name('addTermsForm');
Route::post('updateTerms', [PagesController::class, 'updateTerms'])->middleware(['checkLogin'])->name('updateTerms');
Route::get('viewTerms', [PagesController::class, 'viewTerms'])->middleware(['checkLogin'])->name('viewTerms');
Route::get('privacyPolicy', [PagesController::class, 'privacyPolicy'])->name('privacyPolicy');
Route::get('termsOfUse', [PagesController::class, 'termsOfUse'])->name('termsOfUse');
