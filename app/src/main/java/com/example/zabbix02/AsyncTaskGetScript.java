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

public class AsyncTaskGetScript extends AsyncTask<Void, Void, String[]> {

    private String zabbixApiUrl;
    private String auth;
    private String uiMssg;
    private String[] parameterArray = null;
    private String[] command = null;
    private String[] triggerError = null;
    private String[] triggerDescription = null;
    private String[] triggerExpression = null;
    private String[] triggerLastTime = null;
    private String[] triggerValue = null;
    private String[] triggerPriority = null;
    private String[] triggerComments = null;
    private Context context;
    private String hostName;
    private String hostID;
    private int zero = 0;
    private Activity activity;
    private ProgressDialog progressDialog;


    public AsyncTaskGetScript(Activity activity, Context context, String auth,

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
        //
        StringBuilder sb = new StringBuilder();

        sb.append("{\"jsonrpc\":\"2.0\",");

        sb.append("\"method\":\"script.get\",");

        sb.append("\"params\":{");

        sb.append("\"output\":\"extend\"");

        sb.append("},");

        sb.append("\"auth\":\"").append(auth).append("\",");

        sb.append("\"id\":\"1\"}");

        String sbString = sb.toString();

        Log.e("AsyncTaskGetScript sbString", sbString);

        try {

// tiek nodibinaati sakari ar serveri
            HttpResponse response = LogInActivity.makePostRequest(sbString,
                    zabbixApiUrl);

            HttpEntity entity = response.getEntity();

            String entityString = EntityUtils.toString(entity);

            parameterArray = LogInActivity.getJsonParameters(entityString, "name", null, null);

            command = LogInActivity.getJsonParameters(entityString, "command", null, null);



            return parameterArray;

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

            uiMssg = "Successfully created script list";


//trigeru parametru padosana taalaak
            Intent intent = new Intent(activity, ScriptActivity.class);

            intent.putExtra("auth", auth);

            intent.putExtra("zabbixApiUrl", zabbixApiUrl);

            intent.putExtra("scriptName", parameterArray);

            intent.putExtra("command", command);

//dialoga nonemsana
            progressDialog.dismiss();

//pazinojuma izvade
            Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();

//jaunas aktivitaates saakums
            activity.startActivity(intent);

        } else {
//dialoga nonemsana
            progressDialog.dismiss();

            uiMssg = "Unsuccessfully created script list";

//pazinojuma izvade
            Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
        }
    }
}
