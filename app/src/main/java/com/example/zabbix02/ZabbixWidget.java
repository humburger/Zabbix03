package com.example.zabbix02;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */

//http://www.androidauthority.com/create-simple-android-widget-608975/
public class ZabbixWidget extends AppWidgetProvider {

   /* static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.zabbix_widget);
        views.setTextViewText(16, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        //we iterate through all of our widgets (in case the user has placed multiple widgets)
        for (int i = 0; i < count; i++) {

            //get a RemoteViews object
            int widgetId = appWidgetIds[i];
            int number = (new Random().nextInt(900) + 100);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.zabbix_widget);
            //update the RemoteView’s textview with a new random number between 100 and 999
            remoteViews.setTextViewText(R.id.textView, String.valueOf(number));

            Intent intent = new Intent(context, ZabbixWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            //We also indicate the widgets that should be updated (all of the app widgets)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            //To update the current widget only
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            //To request a manual update when the update button is clicked
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        /*// There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }*/
    }

  /*  @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }*/
}

