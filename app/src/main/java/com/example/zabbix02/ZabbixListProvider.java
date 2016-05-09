package com.example.zabbix02;

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

//https://github.com/laaptu/appwidget-listview/blob/appwidget-listview1/src/com/wordpress/laaptu/ListProvider.java
//darbojas ta pat, ka saraksta skata adapteris aktivitates logam
public class ZabbixListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<ZabbixListItem> listItemList = new ArrayList<ZabbixListItem>();
    private Context context = null;
    private int appWidgetId;

    public ZabbixListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        for (int i = 0; i < 10; i++) {
            ZabbixListItem listItem = new ZabbixListItem();
            listItem.heading = "Heading" + i;
            listItem.content = i
                    + " This is the content of the app widget listview.Nice content though";
            listItemList.add(listItem);
        }

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        ZabbixListItem listItem = (ZabbixListItem) listItemList.get(position);
        remoteView.setTextViewText(R.id.name, listItem.heading);
        remoteView.setTextViewText(R.id.image, listItem.content);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }
}
