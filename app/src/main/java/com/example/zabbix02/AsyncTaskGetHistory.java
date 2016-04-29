package com.example.zabbix02;

import java.io.*;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

//grafikam dinamiski ieguust datus
public class AsyncTaskGetHistory extends AsyncTask<Void, Void, ItemHistory[]> {

	private String zabbixApiUrl;
	private String auth;
	private String[] parameterArray = null;
	private String hostName;
	private String graphName;
	private String hostID;
	private String graphID;
	private String uiMssg = "";
	private String[] itemID = null;
	private String[] graphData = null;
	private String[] itemClock = null;
	private ItemHistory[] history = null;
	private Context context;
	private int count = 1;
	private int zero = 0;
	private int itemCount;

//iguust padotaas veertiibas	
	public AsyncTaskGetHistory(Activity activity, Context context, String auth, String zabbixApiUrl,
			String hostName, String graphName) {
		
		this.auth = auth;
		this.zabbixApiUrl = zabbixApiUrl;
		this.hostName = hostName;
		this.graphName = graphName;
		this.context = activity;
	}

//fona uzdevums ieguust datus no servera	
	@Override
	protected ItemHistory[] doInBackground(Void... arg0) {

//host.get, ieguust resursdatora identifikaatoru	
		StringBuilder sb0 = new StringBuilder();

		sb0.append("{\"jsonrpc\":\"2.0\",");

		sb0.append("\"method\":\"host.get\",");

		sb0.append("\"params\":{");

		sb0.append("\"output\":\"extend\"");

		sb0.append("},");

		sb0.append("\"auth\":\"").append(auth).append("\",");

		sb0.append("\"id\":\"1\"}");

		String sb0String = sb0.toString();
		// http://www.tutorialspoint.com/java/java_string_split.htm
		for (String retval: sb0String.split("; ", 2)){
			Log.e("AsyncTaskGetHistory host.get", retval);
			Log.e("", "\n");
		}


		try {
// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoriem
			HttpResponse response = LogInActivity.makePostRequest(sb0String, zabbixApiUrl);

			HttpEntity entity = response.getEntity();

			String entityString = EntityUtils.toString(entity);

			parameterArray = LogInActivity.getJsonParameters(entityString, "hostid", "host", hostName);
			
			hostID = parameterArray[zero];


//graph.get ieguust iedentifikaatoru
			
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
		
//grafika identifikators peec taa vaarda			
			parameterArray = LogInActivity.getJsonParameters(entityString, "graphid", "name", graphName);
			
				graphID = parameterArray[zero];
			
//graphitem.get ieguust vieniibas identifikaatoru, kraasu un seciibu
				
			sb1 = new StringBuilder();

			sb1.append("{\"jsonrpc\":\"2.0\",");

			sb1.append("\"method\":\"graphitem.get\",");

			sb1.append("\"params\":{");

			sb1.append("\"output\":\"extend\",");
			
			sb1.append("\"expandData\":\"1\",");

			sb1.append("\"graphids\":\"").append(graphID).append("\"");

			sb1.append("},");

			sb1.append("\"auth\":\"").append(auth).append("\",");

			sb1.append("\"id\":\"1\"}");

			sb1String = sb1.toString();

// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoru grafiem
			response = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);

			entity = response.getEntity();

			entityString = EntityUtils.toString(entity);

//grafika vienumu identifikatori			
			parameterArray = LogInActivity.getJsonParameters(entityString, "itemid", null, null);
			
				itemID = parameterArray;

//vienumu skaits 				
				itemCount = itemID.length;
			
//history.get ieguust vecos datus
				
		history = new ItemHistory[itemCount];
				
			sb1 = new StringBuilder();

			sb1.append("{\"jsonrpc\":\"2.0\",");

			sb1.append("\"method\":\"history.get\",");

			sb1.append("\"params\":{");

			sb1.append("\"output\":\"extend\",");
			
			sb1.append("\"history\": 0,");	//float values
			
			sb1.append("\"itemids\": [");

//vienumi, kuriem jaaieguust datus			
			for (int i = 0; i < itemCount; i ++) {
				
				sb1.append("\"").append( itemID[i] ).append("\"");
				
				if ( i != itemCount - 1 ) {
					
					sb1.append(",");
				} else {
					
					sb1.append("] ,");	
				}
			}
			
			sb1.append("\"sortfield\": \"clock\",");
			
			sb1.append("\"sortorder\": \"DESC\","); //augosaa seciibaa
			
			sb1.append("\"limit\": ").append(itemCount); //lielaakais limits

			sb1.append("},");

			sb1.append("\"auth\":\"").append(auth).append("\",");

			sb1.append("\"id\":\"1\"}");

			sb1String = sb1.toString();

			// // tiek nodibinaati sakari ar serveri un ieguuti dati par
			// resursdatoru grafiem
			response = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);

			entity = response.getEntity();

			entityString = EntityUtils.toString(entity);

			for (int i = 0; i < itemCount; i++ ) {
				
				history[i] = LogInActivity.getItemHistory(entityString, count, itemID[i]);
				
				
			}
			
			return history;
			
		} catch (IOException e) {

//neveiksmiigs savienojums				
			uiMssg = "Connection lost to the Zabbix server";
			
			return null;
		} catch (Exception e) {

			uiMssg = "Unknown error in history";

			return null;
		}
	}

// rezultaatu izvade tiek izsaukta no aktivitaates
	protected void onPostExecute(ItemHistory[] result) {

//kluudu gadiijumaa tiek izdots pazinojums		
		if (result == null) {
			
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
		}
	}
}
