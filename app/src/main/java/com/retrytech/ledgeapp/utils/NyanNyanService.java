package com.retrytech.ledgeapp.utils;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import java.io.File;
import java.io.IOException;

public class NyanNyanService extends WallpaperService {
    static final String TAG = "NYAN";
    static final Handler mNyanHandler = new Handler();
    private ExoPlayer player;

    /**
     * @see WallpaperService#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();


    }

    /**
     * @see WallpaperService#onCreateEngine()
     */
    @Override
    public Engine onCreateEngine() {
        try {
            return new NyanEngine();
        } catch (IOException e) {
            Log.w(TAG, "Error creating NyanEngine", e);
            stopSelf();
            return null;
        }
    }

    public File getPath() {
        String state = Environment.getExternalStorageState();
        File filesDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            filesDir = getExternalFilesDir(null);
        } else {
            // Load another directory, probably local memory
            filesDir = getFilesDir();
        }
        return filesDir;
    }

    class NyanEngine extends Engine {
        //        private final Movie mNyan;
//        private final int mNyanDuration;
//        private final Runnable mNyanNyan;
        float mScaleX;
        float mScaleY;
        int mWhen;
        long mStart;

        NyanEngine() throws IOException {


//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/demo.gif");
//
//            InputStream is = new FileInputStream(file);
//            if (is != null) {
//                try {
//                    mNyan = Movie.decodeStream(is);
//                    mNyanDuration = mNyan.duration();
//                } finally {
//                    is.close();
//                }
//            } else {
//                throw new IOException("Unable to open R.raw.nyan");
//            }
//
//            mWhen = -1;
//            mNyanNyan = new Runnable() {
//                public void run() {
//                    nyan();
//                }
//            };
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
//            mNyanHandler.removeCallbacks(mNyanNyan);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                nyan();
            } else {
//                mNyanHandler.removeCallbacks(mNyanNyan);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (player != null) {
                player.setVideoSurface(holder.getSurface());
            }
//            mScaleX = width / (1f * mNyan.width());
//            mScaleY = height / (1f * mNyan.height());
            nyan();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                                     float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            nyan();
        }

        void nyan() {
            tick();
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            player = new ExoPlayer.Builder(getApplicationContext())
                    .build();


            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(getPath() + "/" + "video.mp4"));

            player.setMediaItem(mediaItem);

            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.seekTo(0, 0);
            player.setPlayWhenReady(true);
            player.setVolume(0);
            player.setVideoSurface(surfaceHolder.getSurface());
            player.prepare();

//            Canvas canvas = null;
//            try {
//                canvas = surfaceHolder.lockCanvas();
//                if (canvas != null) {
//                    nyanNyan(canvas);
//                }
//            } finally {
//                if (canvas != null) {
//                    surfaceHolder.unlockCanvasAndPost(canvas);
//                }
//            }
//            mNyanHandler.removeCallbacks(mNyanNyan);
            if (isVisible()) {
//                mNyanHandler.postDelayed(mNyanNyan, 1000L/25L);
            }
        }

        void tick() {
            if (mWhen == -1L) {
                mWhen = 0;
                mStart = SystemClock.uptimeMillis();
            } else {
                long mDiff = SystemClock.uptimeMillis() - mStart;
//                mWhen = (int) (mDiff % mNyanDuration);
            }
        }

        void nyanNyan(Canvas canvas) {
//            canvas.save();
//            canvas.scale(mScaleX, mScaleY);
////            mNyan.setTime(mWhen);
////            mNyan.draw(canvas, 0, 0);
//            canvas.restore();
        }
    }
}