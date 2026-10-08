package com.retrytech.ledgeapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.retrytech.ledgeapp.R;
import com.retrytech.ledgeapp.activity.MainActivity;
import com.retrytech.ledgeapp.activity.PreviewActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    public static int NOTIFICATION_ID = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Log.i("TAG", "onMessageReceived: data=" + data);

        if (data != null && !data.isEmpty()) {
            sendNotificationFromData(data);
            return;
        }

        if (notification != null) {
            sendNotificationFromNotification(notification);
        }
    }

    private void sendNotificationFromNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "01";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        showNotification(channelId, notificationBuilder);
    }

    private void sendNotificationFromData(Map<String, String> data) {
        String title = getValue(data, "title", getString(R.string.app_name));
        String body = getValue(data, "body", "");
        String imageUrl = getValue(data, "image", "");
        String wallpaperJson = getValue(data, "wallpaper_json", "");

        Intent intent;
        if (!TextUtils.isEmpty(wallpaperJson)) {
            intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(Const.Key.wallpaper, wallpaperJson);
            intent.putExtra(Const.Key.notification_image, imageUrl);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "01";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Bitmap bitmap = getBitmapFromUrl(imageUrl);
        if (bitmap != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon((Bitmap) null));
            notificationBuilder.setLargeIcon(bitmap);
        } else {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        }

        showNotification(channelId, notificationBuilder);
    }

    private String getValue(Map<String, String> data, String key, String defaultValue) {
        String value = data.get(key);
        return value == null ? defaultValue : value;
    }

    private Bitmap getBitmapFromUrl(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.i("TAG", "getBitmapFromUrl error: " + e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void showNotification(String channelId, NotificationCompat.Builder notificationBuilder) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "Channel_001";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }

        NOTIFICATION_ID = NOTIFICATION_ID + 1;
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
