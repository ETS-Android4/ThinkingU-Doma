/**
 * FirebaseMessagingService
 * Responsible for handling the incoming notifications.
 */

package com.example.doma.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.doma.Activities.ContactProfile;
import com.example.doma.Activities.MainActivity;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        /*
            Handle the user's device notification ringtone and phone vibration duration
            upon a notification received.
         */
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }

        // Vibrate the phone twice upon notification received.
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        vibrator.vibrate(pattern, -1);

        // Show the icon of application with notification.
        int resourceImage = getResources().getIdentifier(remoteMessage.getNotification().getIcon(), "drawable", getPackageName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(resourceImage);
        } else {
            builder.setSmallIcon(resourceImage);
        }

        Intent intent = new Intent(this, ContactProfile.class);
        // Open the ContactProfile activity upon tap on the notification.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Show received notification's metadata.
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "com.example.doma";
            NotificationChannel channel = new NotificationChannel(channelId,"New notification from Doma",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(100, builder.build());

    }
}
