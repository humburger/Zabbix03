package com.example.zabbix02;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncTaskGetGraphs extends AsyncTask<Void, Void, String[]> {

	private String zabbixApiUrl;
	private String auth;
	private String hostName;
	private String hostID;
	private String uiMssg;
	private String[] parameterArray = null;
	private Context context;
	private String[] graphName = null;
	private Activity activity;
	private ProgressDialog progressDialog;

	public AsyncTaskGetGraphs(Context context, Activity activity, String auth, String hostName, String zabbixApiUrl) {
		this.auth = auth;
		this.hostName = hostName;
		this.context = context;
		this.zabbixApiUrl = zabbixApiUrl;
		this.activity = activity;
	}
	
//Informatiivs dialogs par ielaades processu	
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
	
//fona uzdevums, kas nodorsina pieprasiijuma nosuutiisanu un atbildes sanemsanu no servera
	protected String[] doInBackground(Void... arg0) {
		
		StringBuilder sb0 = new StringBuilder();

		sb0.append("{\"jsonrpc\":\"2.0\",");
		
		sb0.append("\"method\":\"host.get\",");
		
		sb0.append("\"params\":{");
		
		sb0.append("\"output\":\"extend\"");
		
		sb0.append("},");
		
		sb0.append("\"auth\":\"").append(auth).append("\",");
		
		sb0.append("\"id\":\"1\"}");

		String sb0String = sb0.toString();
		
		Log.e("host.get", sb0String);

			try {
// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoriem
				
				HttpResponse response = LogInActivity.makePostRequest(sb0String, zabbixApiUrl);
				
				HttpEntity entity = response.getEntity();
				
				String entityString = EntityUtils.toString(entity);
				
				parameterArray = LogInActivity.getJsonParameters(entityString, "hostid", "host", hostName);
				
				hostID = parameterArray[0];
				
// grafiku pieprasiijuma json
				StringBuilder sb1 = new StringBuilder();
				
				sb1.append("{\"jsonrpc\":\"2.0\",");
				
				sb1.append("\"method\":\"graph.get\",");
				
				sb1.append("\"params\":{");
				
				sb1.append("\"output\":\"extend\",");
				
				sb1.append("\"hostids\":").append(hostID).append(",");
				
				sb1.append("\"sortfield\":\"name\"");
				
				sb1.append("},");
				
				sb1.append("\"auth\":\"").append(auth).append("\",");
				
				sb1.append("\"id\":\"1\"}");
				
				String sb1String = sb1.toString();
				
// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoru grafikiem
				response = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);
				
				entity = response.getEntity();
				
				entityString = EntityUtils.toString(entity);
				
				graphName = LogInActivity.getJsonParameters(entityString, "name", null, null);
				
					
				return graphName;
					
			} catch (IOException e) {
				
//neveiksmiigs savienojums				
				uiMssg = "Connection lost to the Zabbix server";
				
				return null;
				
			} catch (Exception e) {

				uiMssg = "Unknown error in graph list";
				
				return null;
			}
		}

// rezultaatu izvade
	protected void onPostExecute(String[] result) {
	
//dialoga nobeigsana		
		progressDialog.dismiss();
		

		if (result != null) {
			
			uiMssg = "Successfully created graph list";
			
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(activity, GraphListActivity.class);

// tiek padoti mainiigo parametri sekojosajai aktivitaatei
			intent.putExtra("auth", auth);
			
			intent.putExtra("zabbixApiUrl", zabbixApiUrl);
			
			intent.putExtra("hostName", hostName);
			
			intent.putExtra("graphArray", graphName);

			activity.startActivity(intent);
			
		} else {
			
			uiMssg = "Unsuccessfully created graph list";
			
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
		}
	}
}
