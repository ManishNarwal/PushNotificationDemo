package com.devtech.pushnotificationdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("FCM", "Message received from: " + remoteMessage.getFrom());
        super.onMessageReceived(remoteMessage);
        // Handle the received message here
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + Objects.requireNonNull(remoteMessage.getNotification()).getBody());
        if (remoteMessage.getNotification() != null) {

            Log.d("FCM", "Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d("FCM", "Notification Custom Title: " + remoteMessage.getData().get("title"));
            Log.d("FCM", "Notification Custom Url : " + remoteMessage.getData().get("url"));
            Log.d("FCM", "Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            // Handle the notification content
            showNotification(title, body);
        }
    }
    private void showNotification(String title, String body) {
        String NOTIFICATION_CHANNEL_ID = "default_channel_id";

        Intent actionIntent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
        actionIntent.setAction("ccms_action");
// Add extras if needed
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 101, actionIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background);
        notification.addAction(R.drawable.ic_launcher_background,"ccms_action",pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            notification.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        mNotificationManager.notify(/*notification id*/0, notification.build());
    }

}
