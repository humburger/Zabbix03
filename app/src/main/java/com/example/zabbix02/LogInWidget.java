package com.example.zabbix02;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Implementation of App Widget functionality.
 */
public class LogInWidget extends AppWidgetProvider {

    private Activity activity;
    private Button login_button;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active on your homescreen, so update all of them
        final int count = appWidgetIds.length;

            //we iterate through all of our widgets (in case the user has placed multiple widgets)
            for (int i = 0; i < count; i++) {

                int widgetId = appWidgetIds[i];
                //lietojumprogrammas palaisana no logrika
                //http://stackoverflow.com/questions/11554085/start-activity-by-clicking-on-widget
                RemoteViews remoteView_login = new RemoteViews(context.getPackageName(), R.layout.log_in_widget);

                Intent intent = new Intent(context, LogInActivity.class);
                //update the RemoteView’s textview with a new random number between 100 and 999
                remoteView_login.setTextViewText(R.id.usrName, String.valueOf(456));

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, 0);

                remoteView_login.setOnClickPendingIntent(R.id.LogIn, pendingIntent);

                //http://stackoverflow.com/questions/7509830/how-to-hide-show-button-in-android-home-screen-widget
                //pogas paslepsana
                remoteView_login.setViewVisibility(R.id.LogIn, View.INVISIBLE);

                ComponentName watchWidget = new ComponentName( context, LogInWidget.class );

                (AppWidgetManager.getInstance(context)).updateAppWidget( watchWidget, remoteView_login );
                //appWidgetManager.updateAppWidget(watchWidget, remoteView_login);
                //appWidgetManager.updateAppWidget(appWidgetIds, remoteView_login);
            }
        }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        RemoteViews remoteView_login = new RemoteViews(context.getPackageName(), R.layout.log_in_widget);

        Intent intent = new Intent(context, LogInActivity.class);
        //update the RemoteView’s textview with a new random number between 100 and 999
        remoteView_login.setTextViewText(R.id.usrName, String.valueOf(123));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        remoteView_login.setOnClickPendingIntent(R.id.LogIn, pendingIntent);

        ComponentName watchWidget = new ComponentName( context, LogInWidget.class );

        (AppWidgetManager.getInstance(context)).updateAppWidget( watchWidget, remoteView_login );
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

