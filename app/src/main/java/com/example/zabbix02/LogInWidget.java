package com.example.zabbix02;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Implementation of App Widget functionality.
 */
public class LogInWidget extends AppWidgetProvider {

    private Activity activity;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int count = appWidgetIds.length;

        //we iterate through all of our widgets (in case the user has placed multiple widgets)
        for (int i = 0; i < count; i++) {

            //lietojumprogrammas palaisana no logrika
            //http://stackoverflow.com/questions/11554085/start-activity-by-clicking-on-widget
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.log_in_widget);
            Intent intent = new Intent(context, LogInActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);

            remoteViews.setOnClickPendingIntent(R.id.LogIn, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created


        /*RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.log_in_widget);
        Intent intent = new Intent(String.valueOf(LogInActivity.class));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntentNetworkInfo = PendingIntent.getActivity(context, 2,
                intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.LogIn, pIntentNetworkInfo);
       // appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);*/
    }

    @Override
    public void onDisabled(Context context) {
        /*// Enter relevant functionality for when the last widget is disabled
        final String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[] { appWidgetId });
            }
        } else {
            super.onReceive(context, intent);
        }
*/
    }


}

