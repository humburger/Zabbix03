package com.example.zabbix02;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

//saraksta skata adaptera definesana
//https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
public class ZabbixWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new ZabbixListProvider(this.getApplicationContext(), intent));
    }
}
