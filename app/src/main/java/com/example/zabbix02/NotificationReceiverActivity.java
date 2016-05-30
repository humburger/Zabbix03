package com.example.zabbix02;

import android.app.Activity;
import android.os.Bundle;

//http://www.vogella.com/tutorials/AndroidNotifications/article.html
public class NotificationReceiverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_result);
    }
}
