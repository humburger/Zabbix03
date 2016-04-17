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

//klase veic fona uzdevumu, kas veic resursdatoru trigeru datu pieprasiijumu serverim un sanemto datu padosanu resursadatoru trigeru saraksta atteelosanas modulim
public class AsyncTaskGetTriggers extends AsyncTask<Void, Void, String[]> {

	private String zabbixApiUrl;
	private String auth;
	private String uiMssg;
	private String[] parameterArray = null;
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
	

	public AsyncTaskGetTriggers(Activity activity, Context context, String auth,
			
			String zabbixApiUrl, String hostName) {
		
		this.auth = auth;
		this.context = context;
		this.zabbixApiUrl = zabbixApiUrl;
		this.hostName = hostName;
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

		sb.append("\"method\":\"host.get\",");

		sb.append("\"params\":{");

		sb.append("\"output\":\"extend\"");

		sb.append("},");

		sb.append("\"auth\":\"").append(auth).append("\",");

		sb.append("\"id\":\"1\"}");

		String sbString = sb.toString();

		Log.e("AsyncTaskGetTriggers sbString", sbString);

		try {

// tiek nodibinaati sakari ar serveri
			HttpResponse response = LogInActivity.makePostRequest(sbString,
					zabbixApiUrl);

			HttpEntity entity = response.getEntity();

			String entityString = EntityUtils.toString(entity);

			parameterArray = LogInActivity.getJsonParameters(entityString, "hostid", "host", hostName);
			
			hostID = parameterArray[zero];

			sb = new StringBuilder();

			sb.append("{\"jsonrpc\":\"2.0\",");

			sb.append("\"method\":\"trigger.get\",");

			sb.append("\"params\":{");

			sb.append("\"hostids\": \"").append(hostID).append("\",");
			
			sb.append("\"output\":\"extend\",");

			sb.append("\"selectFunctions\": \"extend\",");

			sb.append("\"sortfield\": \"priority\",");

			sb.append("\"sortorder\": \"DESC\"");

			sb.append("},");

			sb.append("\"auth\":\"").append(auth).append("\",");

			sb.append("\"id\":\"1\"}");

			sbString = sb.toString();

// tiek nodibinaati sakari ar serveri
			response = LogInActivity.makePostRequest(sbString, zabbixApiUrl);

			entity = response.getEntity();

			entityString = EntityUtils.toString(entity);
			
//trigeru apraksts jeb nosaukums
			triggerDescription = LogInActivity.getJsonParameters(entityString,
					"description", null, null);
	
//trigeru proritaate peec nopietniibas pakaapes			
			triggerPriority = LogInActivity.getJsonParameters(entityString,
					"priority", null, null);

//trigeru kludas pazinojums			
			triggerError = LogInActivity.getJsonParameters(entityString,
					"error", null, null);
	
//trigera peedaajais laiks, kad notika izmainas			
			triggerLastTime = LogInActivity.getJsonParameters(entityString,
					"lastchange", null, null);

//trigera funkcija peec kuras noasaka vai ir probleema vai nav			
			triggerExpression = LogInActivity.getJsonParameters(entityString,
					"expression", null, null);
			
//trigera nolasiitaa veertiiba			
			triggerValue = LogInActivity.getJsonParameters(entityString,
					"value", null, null);

//trigera izskaidrojosi komentaari			
			triggerComments = LogInActivity.getJsonParameters(entityString,
					"comments", null, null);

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

			uiMssg = "Successfully created trigger list";

//{HOST.NAME} tiek aizvietots ar esosaa resursdatora nosaukumu			
			for (int i = 0; i < triggerDescription.length; i++) {
				
				triggerDescription[ i ] = triggerDescription[ i ].replace("{HOST.NAME}", hostName);
				
				Log.e("AsyncTaskGetTriggers triggerDescription [" + i + "] = ", triggerDescription[i]);
			}

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
			}
			
//0 -> OK; 1 -> PROBLEM				
			for (int i = 0; i < triggerDescription.length; i++) {
				
				triggerValue[ i ] = triggerValue[ i ].replace("0", "OK"); 
				triggerValue[ i ] = triggerValue[ i ].replace("1", "PROBLEM"); 
			}
//trigeru parametru padosana taalaak			
			Intent intent = new Intent(activity, TriggerActivity.class);
			
			intent.putExtra("auth", auth);
			
			intent.putExtra("zabbixApiUrl", zabbixApiUrl);
			
			intent.putExtra("hostName", hostName);
			
			intent.putExtra("triggerDescription", triggerDescription); //change to something more understandable
			
			intent.putExtra("triggerPriority", triggerPriority); //change to something more understandable
			
			intent.putExtra("triggerError", triggerError); //change to something more understandable
			
			intent.putExtra("triggerExpression", triggerExpression); //change to something more understandable
			
			intent.putExtra("triggerLastTime", triggerLastTime); //change to something more understandable
			
			intent.putExtra("triggerValue", triggerValue); //change to something more understandable
			
			intent.putExtra("triggerComments", triggerComments);

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
