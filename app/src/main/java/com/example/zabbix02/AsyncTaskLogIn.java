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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

// koda fragmenti nemti no http://mytechattempts.wordpress.com/tag/zabbix-server/	
public class AsyncTaskLogIn extends AsyncTask<String, Void, String> {

	private Context context;
	private Activity activity;
	private String password;
	private String username;
	private String url;
	private String zabbixApiUrl;
	private Object result = null;
	private String auth;
	private String uiMssg = "";
	private ObjectMapper mapper = new ObjectMapper();
	private ProgressDialog progressDialog; 

	public AsyncTaskLogIn(Context context, Activity activity) {

		this.context = context;
		this.activity = activity;
	}
	
//Informatiivs dialogs par pieteiksanaas processu	
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			
	 		progressDialog = new ProgressDialog(context);
	 		
	 		progressDialog.setMessage("Connecting to server, please wait...");
	 		
	 		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	 		
	 		progressDialog.setIndeterminate(true);
	 		
	 		progressDialog.setCancelable(false);
	 		
	 		progressDialog.show();
		}

	protected String doInBackground(String... arg0) {

		username = arg0[0];
		password = arg0[1];
		url = arg0[2];
		zabbixApiUrl = arg0[3];

//json faila sagatave lieotoaaja pieteiksanaas pieprasiijumam		
		StringBuilder sb = new StringBuilder();

		sb.append("{\"jsonrpc\":\"2.0\",")

		.append("\"method\":\"user.login\",")

		.append("\"params\":{")

		.append("\"user\":\"").append(username).append("\",")

		.append("\"password\":\"").append(password).append("\"},")

		.append("\"id\":\"1\"}");

		try {

//tiek nodibinaati sakari ar serveri
			HttpResponse response = LogInActivity.makePostRequest(sb.toString(),
					zabbixApiUrl);

			HttpEntity entity = response.getEntity();

			String entityString = EntityUtils.toString(entity);
			
//log			
			Log.e("AsyncTaskLogIn: ", entityString);
//
			
//tiek sanemts rezultaats
			
			HashMap logIn = mapper.readValue(entityString, HashMap.class);

			result = logIn.get("result");

// neveiksmiigs pieteiksanaas vaiants serverim
			if (result == null) {

				uiMssg = "Wrong username or password";
				
// tiek izdota savienojuma informaacija
				return uiMssg;
			} else {
				
				auth = result.toString();
				
				uiMssg = "Successfully connected to the server";

// tiek izdota savienojuma informaacija
				return uiMssg;
			}
		} 
		catch (IOException e) {
			
// neveiksmiigs savienojums
				uiMssg = "Could not connect to the Zabbix server at : http://"
						+ url;
//atgriez kluudas rezultaatu
			return null;
		}
		catch (Exception e) {
			
			return null;
		}
	}

	// rezultaatu izvade
	protected void onPostExecute(String result) {
		
//peec darbiibu izpildes informatiivais dialogs tiek izsleegts		
		this.progressDialog.dismiss();

// veiksmiiga pieteiksanaas sisteemai
		if ( uiMssg.equals("Successfully connected to the server") ) {

			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
//uzsaak pieprasiijumu peec resursdatoru datiem			
			new AsyncTaskGetHosts(context, activity, auth, zabbixApiUrl).execute();
			
// neveiksmiiga pieteiksanaas sisteemai
		} else {
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
		}

	}
}
