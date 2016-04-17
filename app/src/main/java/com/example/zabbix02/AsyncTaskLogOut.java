package com.example.zabbix02;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

//klase veic lieotaaja atteiksanos no sisteemas
public class AsyncTaskLogOut extends AsyncTask<String, Void, String> {

	private Activity activity;
	private String entityString;
	private Context context;
	private String zabbixApiUrl;
	private String auth;
	private String result;
	private String uiMssg;
	private ObjectMapper mapper = new ObjectMapper();
	private StringBuilder uiConnectMessage = new StringBuilder();
	private ProgressDialog progressDialog;

	public AsyncTaskLogOut(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}
	
//Informatiivs dialogs par atteiksanaas processu	
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			
	 		progressDialog = new ProgressDialog(context);
	 		
	 		progressDialog.setMessage("Logging out...");
	 		
	 		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	 		
	 		progressDialog.setIndeterminate(true);
	 		
	 		progressDialog.setCancelable(false);
	 		
	 		progressDialog.show();
		}

//atteikuma pieprasiijums		
	protected String doInBackground(String... arg0) {
		
		auth = arg0[0];
		
		zabbixApiUrl = arg0[1];
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\"jsonrpc\":\"2.0\",")
		
				.append("\"method\":\"user.logout\",")
				
				.append("\"params\":[],").append("\"id\":\"1\",")
				
				.append("\"auth\":\"").append(auth).append("\"}");
		try {
			
	// tiek nodibinaati sakari ar serveri
			HttpResponse response = LogInActivity.makePostRequest(sb.toString(),
					zabbixApiUrl);
			
			HttpEntity entity = response.getEntity();
			
			entityString = EntityUtils.toString(entity);
			
//lietotaaju atteiksnaas datu struktuura			
			UserLogout untyped = mapper.readValue(entityString,
					UserLogout.class);
			
			result = untyped.getResult();

// veiksmiigs savienojums
			if (result.equals("true")) {
				
				uiConnectMessage.append("Successful logout");
				uiMssg = uiConnectMessage.toString();
			}
			
			return uiMssg;
			
		} catch (IOException e) {
			
// neveiksmiigs savienojums
			uiMssg = "Connection lost to the Zabbix server";
			
			return uiMssg;
		}

		catch (Exception e) {

			uiMssg = "Unknown error in logout";
			
			return uiMssg;
		}
	}

// rezultaatu izvade
	protected void onPostExecute(String result) {
	
//dialoga izsleegsana		
		progressDialog.dismiss();
		
		if (result.equals("Successful logout")) {
			
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(activity, LogInActivity.class);
			
//peec atteiksanaas tiek iztiiriits aktivitaasu steks	
//http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-l/9580057#9580057
			 intent.putExtra("finish", true);
			 
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
		        
		        activity.startActivity(intent);
		        
		        activity.finish();
		} else { 
			
		Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		
		}
	}
}
