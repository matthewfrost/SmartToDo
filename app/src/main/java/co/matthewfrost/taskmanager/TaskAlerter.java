package co.matthewfrost.taskmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

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
                .setContentText("notification")
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setAutoCancel(true).build();

        manager.notify(0, notification);

    }
}
