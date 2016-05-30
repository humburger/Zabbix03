package com.example.zabbix02;

import android.annotation.TargetApi;
import android.support.v4.app.NotificationCompat;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//http://www.vogella.com/tutorials/AndroidNotifications/article.html
public class CreateNotificationActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zabbix_notification);
    }

    //@TargetApi(16) //mērķa API ir 16, par 11 vēl tiks padomāts ...
    public void createNotification(View view) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        // API 11 and lower notification feature
        //http://stackoverflow.com/a/13717518
        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle("Alert by some triggers.")
                .setContentText("Some information about triggers.").setSmallIcon(R.drawable.walle)
                .setContentIntent(pIntent).build();
                /*.addAction(R.drawable.arrow, "Call", pIntent)
                .addAction(R.drawable.walle, "More", pIntent)
                .addAction(R.drawable.ic_launcher, "And more", pIntent).build();*/
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }
}
