package com.example.zabbix02;

import android.app.LauncherActivity;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//attalinato datu nolasisanas serviss
public class RemoteFetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private AQuery aquery;
    private String remoteJsonUrl = "https://laaptu.files.wordpress.com/2013/07/widgetlist.key";

    public static ArrayList<ZabbixListItem> listItemList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Retrieve appwidget id from intent it is needed to update widget later
     * initialize our AQuery class
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        aquery = new AQuery(getBaseContext());
        fetchDataFromWeb();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * method which fetches data(json) from web aquery takes params
     * remoteJsonUrl = from where data to be fetched String.class = return
     * format of data once fetched i.e. in which format the fetched data be
     * returned AjaxCallback = class to notify with data once it is fetched
     */
    private void fetchDataFromWeb() {
        aquery.ajax(remoteJsonUrl, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                processResult(result);
                super.callback(url, result, status);
            }
        });
    }

    /**
     * Json parsing of result and populating ArrayList<ListItem> as
     * per json
     * data retrieved from the string
     */
    private void processResult(String result) {
        listItemList = new ArrayList<ZabbixListItem>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ZabbixListItem listItem = new ZabbixListItem();
                listItem.heading = jsonObject.getString("heading");
                listItem.content = jsonObject.getString("content");
                listItem.imageUrl = jsonObject.getString("imageUrl");
                listItemList.add(listItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        populateWidget();
    }

    /**
     * Method which sends broadcast to WidgetProvider
     * so that widget is notified to do necessary action
     * and here action == WidgetProvider.DATA_FETCHED
     */
    private void populateWidget() {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(ZabbixWidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }
}
