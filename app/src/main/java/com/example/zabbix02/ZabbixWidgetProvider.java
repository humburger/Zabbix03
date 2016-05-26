package com.example.zabbix02;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */

//http://www.androidauthority.com/create-simple-android-widget-608975/
public class ZabbixWidgetProvider extends AppWidgetProvider {

    // String to be sent on Broadcast as soon as Data is Fetched
    // should be included on WidgetProvider manifest intent action
    // to be recognized by this WidgetProvider to receive broadcast
    public static final String DATA_FETCHED = "com.wordpress.laaptu.DATA_FETCHED";

    /* https://github.com/laaptu/appwidget-listview/blob/appwidget-listview1/src/com/wordpress/laaptu/WidgetProvider.java
      * this method is called every 30 mins as specified on widgetinfo.xml
      * this method is also called on every phone reboot
      */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int count = appWidgetIds.length;
		/*int[] appWidgetIds holds ids of multiple instance of your widget
		 * meaning you are placing more than one widgets on your homescreen*/
        for (int i = 0; i < count; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.zabbix_widget);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, ZabbixWidgetService.class);
        //passing app widget id to that RemoteViews Service

        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //setting adapter to listview of the widget
            remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget,
                    svcIntent);
        }
        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
        return remoteViews;
    }

    /*
	 * It receives the broadcast as per the action set on intent filters on
	 * Manifest.xml once data is fetched from RemotePostService,it sends
	 * broadcast and WidgetProvider notifies to change the data the data change
	 * right now happens on ListProvider as it takes RemoteFetchService
	 * listItemList as data
	 */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(DATA_FETCHED)) {
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }
}

