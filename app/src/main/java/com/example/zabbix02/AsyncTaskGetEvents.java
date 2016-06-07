package com.example.zabbix02;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class AsyncTaskGetEvents extends AsyncTask<Void, Void, String[]> {

    private String zabbixApiUrl;
    private String auth;
    private String[] triggerID;
    private String uiMssg;
    private String[] objectIDs = null;
    private String[] objectClock = null;
    private String[] triggerError = null;
    private String[] triggerDescription = {"cat", "dog"};
    private String[] triggerExpression = null;
    private String[] triggerLastTime = null;
    private String[] triggerValue = null;
    private String[] triggerPriority = null;
    private String[] triggerComments = null;
    private Context context;
    private String hostID;
//    private int zero = 0;
    private Activity activity;
    private ProgressDialog progressDialog;


    public AsyncTaskGetEvents(Activity activity, Context context, String auth,

                                String zabbixApiUrl) {

        this.auth = auth;
        this.context = context;
        this.zabbixApiUrl = zabbixApiUrl;
        this.activity = activity;
    }

    //Informatiivs dialogs par informaacijas pieprasiijuma processu
    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Please wait...");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setIndeterminate(true);

        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    protected String[] doInBackground(Void... arg0) {

        StringBuilder sb = new StringBuilder();

        sb.append("{\"jsonrpc\":\"2.0\",");

        sb.append("\"method\":\"event.get\",");

        sb.append("\"params\":{");

        sb.append("\"output\":\"extend\",");

        //vertibas tiek sakartotas dilstosaa seciibaa, jo svariigaaks ir jaunais raadiijums
        sb.append("\"sortfield\": [\"clock\"],").append("\"sortorder\": \"DESC\"");

        sb.append("},");

        sb.append("\"auth\":\"").append(auth).append("\",");

        sb.append("\"id\":\"1\"}");

        String sbString = sb.toString();

        Log.e("AsyncTaskGetTriggers sbString to send", sbString);

        try {

// tiek nodibinaati sakari ar serveri
            HttpResponse response = LogInActivity.makePostRequest(sbString,
                    zabbixApiUrl);

            HttpEntity entity = response.getEntity();

            String entityString = EntityUtils.toString(entity);

            Log.e("AsyncTaskGetEvent get result string", entityString);

            objectIDs = LogInActivity.getJsonParameters(entityString, "objectid", null, null);
            objectClock = LogInActivity.getJsonParameters(entityString, "clock", null, null);

            for (int i = 0; i < objectClock.length; i++) {
                objectClock[ i ] = LogInActivity.convertTimestamp(Long.parseLong(objectClock[ i ]) , "yyyy-MM-dd HH:mm");
            }

            sb = new StringBuilder();

            sb.append("{\"jsonrpc\":\"2.0\",");

            sb.append("\"method\":\"trigger.get\",");

            sb.append("\"params\":{");

            sb.append("\"output\": [\"triggerid\",").append("\"description\",").append("\"priority\"],");

            sb.append("\"sortfield\": \"priority\",");

            sb.append("\"sortorder\": \"DESC\"");

            sb.append("},");

            sb.append("\"auth\":\"").append(auth).append("\",");

            sb.append("\"id\":\"1\"}");

            sbString = sb.toString();
//
//// tiek nodibinaati sakari ar serveri
            response = LogInActivity.makePostRequest(sbString, zabbixApiUrl);

            entity = response.getEntity();

            entityString = EntityUtils.toString(entity);

            Log.e("AsyncTaskGetEvents get triggers result ", entityString);

            triggerID = LogInActivity.getJsonParameters(entityString, "triggerid", null, null);
//
////trigeru apraksts jeb nosaukums
            triggerDescription = LogInActivity.getJsonParameters(entityString,
                    "description", null, null);

//trigeru proritaate peec nopietniibas pakaapes
            triggerPriority = LogInActivity.getJsonParameters(entityString,
                    "priority", null, null);

            triggerExpression = LogInActivity.getJsonParameters(entityString,
                    "expression", null, null);
//

            return triggerDescription;

        } catch (IOException e) {
//neveiksmiigs savienojums
            uiMssg = "Connection lost to the Zabbix server";

            return null;

        } catch (Exception e) {

            return null;

        }
    }

    // rezultaatu izvade
    protected void onPostExecute(String[] result) {

        if (result != null) {

            uiMssg = "Successfully created event list";

//trigeru nopietniibas pakaapes
            for (int i = 0; i < triggerDescription.length; i++) {
//0 -> (default) not classified;
//1 -> information;
//2 -> warning;
//3 -> average;
//4 -> high;
//5 -> disaster.
                triggerPriority[ i ] = triggerPriority[ i ].replace("0", "Not classified");
                triggerPriority[ i ] = triggerPriority[ i ].replace("1", "Information");
                triggerPriority[ i ] = triggerPriority[ i ].replace("2", "Warning");
                triggerPriority[ i ] = triggerPriority[ i ].replace("3", "Average");
                triggerPriority[ i ] = triggerPriority[ i ].replace("4", "Hight");
                triggerPriority[ i ] = triggerPriority[ i ].replace("5", "Disaster");
                //piemēra variants
                triggerDescription[ i ] = triggerDescription[ i ].replace("{HOST.NAME}", "Zabbix");
            }

//trigeru parametru padosana taalaak
            Intent intent = new Intent(activity, EventActivity.class);

            intent.putExtra("auth", auth);

            intent.putExtra("zabbixApiUrl", zabbixApiUrl);

            intent.putExtra("objectIDs", objectIDs);

            intent.putExtra("objectClock", objectClock);

            intent.putExtra("triggerIDs", triggerID);

            intent.putExtra("triggerDescription", triggerDescription); //change to something more understandable

            intent.putExtra("triggerPriority", triggerPriority); //change to something more understandable

            intent.putExtra("triggerExpression", triggerExpression); //change to something more understandable



//dialoga nonemsana
            progressDialog.dismiss();

//pazinojuma izvade
            Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();

//jaunas aktivitaates saakums
            activity.startActivity(intent);

        } else {
//dialoga nonemsana
            progressDialog.dismiss();

            uiMssg = "Unsuccessfully created trigger list";

//pazinojuma izvade
            Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
        }
    }
}
