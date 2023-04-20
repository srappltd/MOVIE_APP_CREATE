package com.sandhya.movieappcreate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification()!=null){
            pushNotification(message.getNotification().getTitle(),message.getNotification().getBody());
        }
    }

    private void pushNotification(String title, String body) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        final String CHANNEL_ID = "push_notification";
        Intent intent = new Intent(this,VideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        CharSequence name = "Notification";
        String description = "Channel push notification";

    }
}
