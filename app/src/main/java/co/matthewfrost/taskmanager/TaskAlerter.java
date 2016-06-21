package co.matthewfrost.taskmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by matth on 19/06/2016.
 */
public class TaskAlerter extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("desc"))
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setVibrate(new long[]{500, 500})
                .setLights(Color.GREEN, 1, 1)
                .setAutoCancel(true).build();

        manager.notify((int)System.currentTimeMillis(), notification);

    }
}
