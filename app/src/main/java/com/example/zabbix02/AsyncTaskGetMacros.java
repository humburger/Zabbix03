package com.example.zabbix02;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class AsyncTaskGetMacros extends AsyncTask<Void, Void, String[]> {
    private String zabbixApiUrl;
    private String auth;
    private String[] testing = {"cat", "dog"};
    private String entityString = null;
    private String[] macroName = null;
    private String[] macroValue = null;
    private String[] globalmacroID = null;
    private String[] alert = null;
    private String[] macroStatus = null;
    private String[] triggerValue = null;
    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog;

    //konstruktors panem padotos datus
    public AsyncTaskGetMacros(Context context, Activity activity, String auth, String zabbixApiUrl) {

        this.context = context;
        this.auth = auth;
        this.activity = activity;
        this.zabbixApiUrl = zabbixApiUrl;
    }

    //Informatiiva dialoga izveide par pieteiksanaas processu
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

    //fona uzdevums, kur norisinaas datu pieprasiisana un sanemsana
    protected String[] doInBackground(Void... arg0) {

        StringBuilder sb = new StringBuilder();
        //peiprasiijums peec resursdatoru nosaukumiem
        sb.append("{\"jsonrpc\":\"2.0\",");

        sb.append("\"method\":\"usermacro.get\",");

        sb.append("\"params\":{");

        sb.append("\"output\":\"extend\"");

        sb.append(",");

        sb.append("\"globalmacro\": true");

        //sb.append("\"globalmacro\": false");

        sb.append("},");

        sb.append("\"auth\":\"").append(auth).append("\",");

        sb.append("\"id\":\"1\"}");

        String sbString = sb.toString();

        try {

// tiek nodibinaati sakari ar serveri, lai dabutu informaciju par resursdatoriem
            HttpResponse response = LogInActivity.makePostRequest(sbString, zabbixApiUrl);

            HttpEntity entity = response.getEntity();

            entityString = EntityUtils.toString(entity);

            Log.e("AsyncTaskGetMacros usermacro.get ", entityString);


//tiek ieguuti visi resursdatora nosaukumi
            macroName = LogInActivity.getJsonParameters(entityString, "macro", null, null);

            macroValue = LogInActivity.getJsonParameters(entityString, "value", null, null);

//tiek ieguuti visi resursdatoru identifikaacijas numuri
            //hostmacroID = LogInActivity.getJsonParameters(entityString, "hostmacroid", null, null);





            return macroName;

//jebkaadu kluudu gadiijumaa tiek atdota tuksa veertiiba, peec kuras tiek izlemta taalaaka darbiiba
        } catch (Exception e) {
            String[] error = {"error"}; //lai dobackground izeja ir string[]
            error[0] = e.toString();
            return error;
        }
    }

    // rezultaatu izvade
    protected void onPostExecute(String[] result) {

//peec darbiibu izpildes informatiivais dialogs tiek izsleegts
        progressDialog.dismiss();

        Log.e("AsyncTaskGetMacros onpostexecute ", result[0]);

//ja veertiiba nav tuksa, seko rezultaatu apstraades turpinaajums
        if (result != null) {

            Intent intent = new Intent(activity, MacroActivity.class);

// tiek padoti mainiigo parametri resursdatoru saraksta atteelosanas modulim
            intent.putExtra("auth", auth);

            intent.putExtra("zabbixApiUrl", zabbixApiUrl);

            intent.putExtra("macroName", macroName);

            intent.putExtra("macroValue", macroValue);

            activity.startActivity(intent);
        }
    }
}
