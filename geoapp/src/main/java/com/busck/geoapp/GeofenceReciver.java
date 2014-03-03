package com.busck.geoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;

public class GeofenceReciver extends BroadcastReceiver {
    public static final String ACTION_GEOFENCE_RECIVED = "com.busck.geoapp.GEOFENCE_RECIVED";
    public GeofenceReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(ACTION_GEOFENCE_RECIVED.equals(action)) {
                Toast.makeText(context, "Your geofence was triggered!", Toast.LENGTH_SHORT).show();
                showNotification(context);
                Log.e("working", "working in place");
            }
        }
    }

    private void showNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Geo App")
                        .setContentText("Geofence was triggered!");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());

    }
}
