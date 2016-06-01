/**
 * Girts Vilumsons
 *
 * Informaacijas liizeklu informaacijas ieguusanai no servera
 *
 * */
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

public class AsyncTaskGetMedia extends AsyncTask<Void, Void, String[]> {

    private String zabbixApiUrl;
    private String auth;
    private String uiMssg;
    private Context context;
    private Activity activity;
    private String[] mediaType = null;
    private String[] sendTo = null;
    private String[] period = null;
    private String[] userID = null;
    private ProgressDialog progressDialog;


    public AsyncTaskGetMedia(Activity activity, Context context, String auth,

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

        sb.append("\"method\":\"usermedia.get\",");

        sb.append("\"params\":{");

        sb.append("\"output\":\"extend\",");

        sb.append("\"userids\": \"1\"");

        sb.append("},");

        sb.append("\"auth\":\"").append(auth).append("\",");

        sb.append("\"id\":\"1\"}");

        String sbString = sb.toString();

        Log.e("AsyncTaskGetmedia sbString to send", sbString);

        try {

// tiek nodibinaati sakari ar serveri
            HttpResponse response = LogInActivity.makePostRequest(sbString,
                    zabbixApiUrl);

            HttpEntity entity = response.getEntity();

            String entityString = EntityUtils.toString(entity);

            Log.e("AsyncTaskGetmedia get result string", entityString);

            mediaType = LogInActivity.getJsonParameters(entityString, "mediatypeid", null, null);
            userID = LogInActivity.getJsonParameters(entityString, "userid", null, null);
            sendTo = LogInActivity.getJsonParameters(entityString, "sendto", null, null);
            period = LogInActivity.getJsonParameters(entityString, "period", null, null);
//no skaitliem atkodee, kada informacijas lidzekla veids ir
            for (int i = 0; i < mediaType.length; i++) {
                if (mediaType[ i ].equals("3")) {
                    mediaType[ i ] = "SMS";
                } else if (mediaType[ i ].equals("1")) {
                    mediaType[ i ] = "Email";
                }
            }
//lietotaaja atkodeesana
            for (int i = 0; i < userID.length; i++) {
                if (userID[ i ].equals("1")) {
                    userID[ i ] = "Administrator";
                }
            }

            return mediaType;

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

        Log.e("AsyncTaskGetEvent ", result[0]);

        if (result != null) {

            uiMssg = "Successfully created media list";

////sazinas liidzeklu parametru padosana taalaak
            Intent intent = new Intent(activity, MediaActivity.class);

            intent.putExtra("auth", auth);

            intent.putExtra("zabbixApiUrl", zabbixApiUrl);

            intent.putExtra("mediaType", mediaType);

            intent.putExtra("period", period);

            intent.putExtra("userID", userID);

            intent.putExtra("sendTo", sendTo);

//dialoga nonemsana
            progressDialog.dismiss();

//pazinojuma izvade
            Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();

//jaunas aktivitaates saakums
            activity.startActivity(intent);

        } else {
//dialoga nonemsana
            progressDialog.dismiss();

            uiMssg = "Unsuccessfully created media list";

//pazinojuma izvade
            Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
        }
    }
}
