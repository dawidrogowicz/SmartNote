package com.rogowiczdawid.smartnote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        String title = context.getString(R.string.app_name) + " " + extras.getString("title");
        String text = extras.getString("text");
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sn_icon);
        int notificationId = extras.getInt("id");

        //Create PendingIntent leading to app's main activity
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification
        Notification mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.sn_icon)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(largeIcon)
                        .build();

        //Make notification
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notificationId, mBuilder);
    }
}
