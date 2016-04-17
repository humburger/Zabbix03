package com.example.zabbix02;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

//ieguust vajadziigo informaaciju izveeleetaa grafika uzziimeesanai
public class AsyncTaskGetChoosenGraph extends AsyncTask<Void, Void, ItemHistory[]> {

	private String zabbixApiUrl;
	private String auth;
	private String uiMssg;
	private String[] parameterArray = null;
	private String hostName;
	private String graphName;
	private String hostID;
	private String label;
	private String graphID;
	private String[] itemID = null;
	private String[] itemColor = null;
	private String[] itemDelay = null;
	private String[] itemName = null;
	private String[] itemHistory = null;
	private int[] itemLimitInt = null; 
	private int[] itemDelayInt = null;
	private Activity activity;
	private String[] itemClock = null;
	private Context context;
	private ProgressDialog progressDialog;
	private int zero = 0;
	private int itemCount;
	private int valueCount;
	private ItemHistory[] history = null;
	private Bundle bundle = new Bundle();

//konstruktors ieguust padotaas veertiibas	
	public AsyncTaskGetChoosenGraph(Activity activity, Context context, String auth, String zabbixApiUrl,
			String hostName, String graphName) {
		
		this.auth = auth;
		this.zabbixApiUrl = zabbixApiUrl;
		this.hostName = hostName;
		this.graphName = graphName;
		this.activity = activity;
		this.context = context;
	}
	
//Informatiivs dialogs par lejupielaades processu	
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

//fona darbiiba datu ieguvei no servera	
	@Override
	protected ItemHistory[] doInBackground(Void... arg0) {

//host.get, resursdatoru informaacijas ieguve		
		StringBuilder sb0 = new StringBuilder();

		sb0.append("{\"jsonrpc\":\"2.0\",");

		sb0.append("\"method\":\"host.get\",");

		sb0.append("\"params\":{");

		sb0.append("\"output\":\"extend\"");

		sb0.append("},");

		sb0.append("\"auth\":\"").append(auth).append("\",");

		sb0.append("\"id\":\"1\"}");

		String sb0String = sb0.toString();

		try {
// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoriem
			HttpResponse response = LogInActivity.makePostRequest(sb0String, zabbixApiUrl);

			HttpEntity entity = response.getEntity();

			String entityString = EntityUtils.toString(entity);

			parameterArray = LogInActivity.getJsonParameters(entityString, "hostid", "host", hostName);
			
			hostID = parameterArray[zero];

			
//graph.get, grafiku informaacijas ieguve konkreetam resursdatoram
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

			parameterArray = LogInActivity.getJsonParameters(entityString, "graphid", "name", graphName);
			
				graphID = parameterArray[zero];
			
			parameterArray = LogInActivity.getJsonParameters(entityString, "name", "name", graphName);
			
				label = parameterArray[zero];
				
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

//tiek nodibinaati sakari ar serveri un ieguuti dati resursdatoru grafiem
			response = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);

			entity = response.getEntity();

			entityString = EntityUtils.toString(entity);
			
			itemID = LogInActivity.getJsonParameters(entityString, "itemid", "graphid", null);
			
//grafika vienumu skaits			
			itemCount = itemID.length;

//vienuma kraasa grafika ziimeejumaa			
			itemColor = LogInActivity.getJsonParameters(entityString, "color", "graphid", null);
			
//item.get ieguust vieniibas nosaukumu, datu atjaunosanas ciklu
			
			itemDelay = new String [itemCount];
			itemName = new String [itemCount];
			itemHistory = new String [itemCount];
			
				sb1 = new StringBuilder();

				sb1.append("{\"jsonrpc\":\"2.0\",");

				sb1.append("\"method\":\"item.get\",");

				sb1.append("\"params\":{");

				sb1.append("\"output\":\"extend\",");
				
				sb1.append("\"hostids\":\"").append(hostID).append("\",");
				
				sb1.append("\"search\":{ \"itemids\" : ["); 
				
					for (int i = 0; i < itemCount; i ++) {
						
						sb1.append("\"").append(itemID[i]).append("\"");
						
						if ( i != itemCount - 1 ) {
							
							sb1.append(",");
						} else {
							
							sb1.append("] },");
						}
							
						
					}
				
				sb1.append("\"sortfield\": \"name\"");

				sb1.append("},");

				sb1.append("\"auth\":\"").append(auth).append("\",");

				sb1.append("\"id\":\"1\"}");

				sb1String = sb1.toString();

// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoru grafikiem
				response = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);

				entity = response.getEntity();

				entityString = EntityUtils.toString(entity);
				
				for (int i = 0; i < itemCount; i ++) {

//vienuma nosaukums					
				parameterArray = LogInActivity.getJsonParameters(entityString, "name", "itemid", itemID[i]);
					
					itemName[i] = parameterArray[zero];
		
//vienuma atjaunosanas periods					
				parameterArray = LogInActivity.getJsonParameters(entityString, "delay", "itemid", itemID[i]);
				
					itemDelay[i] = parameterArray[zero];
	
//vienuma uzglabaajamo veertiibu veestures laiks					
				parameterArray = LogInActivity.getJsonParameters(entityString, "history", "itemid", itemID[i]);
			
					itemHistory[i] = parameterArray[zero];
					
				}
				
			itemLimitInt = new int [itemCount];
			itemDelayInt = new int [itemCount];

//peec veestures maksimaalaa laika un datu atjaunosanas cikliskuma  tiek apreekinaats vienuma veertiibu daudzums, kuru var atteelot grafikaa
			for (int i = 0; i < itemCount; i++){
			
				itemLimitInt[i] = Integer.parseInt(itemHistory[i]); //String -> int
					
				itemDelayInt[i] = Integer.parseInt(itemDelay[i]);
					
				itemLimitInt[i] = itemLimitInt[i] * 24 * 60 * 60; // dienas -> sekundes
					
				itemLimitInt[i] = itemLimitInt[i] / itemDelayInt[i]; //veertiibu skaits, limits
			}
			
//history.get visus esosos datus
			
				sb1 = new StringBuilder();

				sb1.append("{\"jsonrpc\":\"2.0\",");

				sb1.append("\"method\":\"history.get\",");

				sb1.append("\"params\":{");

				sb1.append("\"output\":\"extend\",");
				
				sb1.append("\"history\": 0,");
				
				sb1.append("\"itemids\": \"").append( itemID [zero] ).append("\",");
				
				sb1.append("\"sortfield\": \"clock\",");
				
				sb1.append("\"sortorder\": \"ASC\""); //augosaa seciibaa
				
				sb1.append("},");

				sb1.append("\"auth\":\"").append(auth).append("\",");

				sb1.append("\"id\":\"1\"}");

				sb1String = sb1.toString();

// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoru grafiem
				response = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);

				entity = response.getEntity();

				entityString = EntityUtils.toString(entity);
			
//vienumu uznemto veertiibu laiks				
				itemClock = LogInActivity.getJsonParameters(entityString, "clock", null, null);
				
				valueCount = itemClock.length;
				
				history = new ItemHistory[itemCount];
						
						sb1 = new StringBuilder();

						sb1.append("{\"jsonrpc\":\"2.0\",");

						sb1.append("\"method\":\"history.get\",");

						sb1.append("\"params\":{");

						sb1.append("\"output\":\"extend\",");
						
						sb1.append("\"history\": 0,");
						
						sb1.append("\"itemids\": [");
						
//konkreetaa grafika vienumi						
							for (int i = 0; i < itemCount; i ++) {
								
								sb1.append("\"").append( itemID[i] ).append("\""); 
							
								if ( i != itemCount - 1 ) {
									
									sb1.append(",");
								} else {
									
									sb1.append("] ,");	
								}
							}
						
						sb1.append("\"sortfield\": \"clock\",");
						
						sb1.append("\"sortorder\": \"ASC\""); //augosaa seciibaa
						
						sb1.append("},");

						sb1.append("\"auth\":\"").append(auth).append("\",");

						sb1.append("\"id\":\"1\"}");

						sb1String = sb1.toString();

// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoru grafikiem
						response = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);

						entity = response.getEntity();

						entityString = EntityUtils.toString(entity);
						
						for (int i = 0; i < itemCount; i++) {
							
							history[i] = LogInActivity.getItemHistory(entityString, valueCount, itemID[i]);
						}
						
						return history;
						
		} catch (IOException e) {

			Log.e("IOException", e.toString());
			
// neveiksmiigs savienojums
			uiMssg = "Connection lost to the Zabbix server";
			
			return null;
			
		} catch (Exception e) {

			uiMssg = "Unknown error in choosen graph";

			return null;
		}
	}

	// rezultaatu izvade
	protected void onPostExecute(ItemHistory[] result) {
		
//peec darbiibu izpildes informatiivais dialogs tiek izsleegts		
		this.progressDialog.dismiss();
		
//ja viss ir bijis veiksmiigi, tad tiek padotas nolasiitaas veertiibas 
		if (result != null) {

			uiMssg = "Successfully created graph";
//Rezultaata pazinojums			
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(activity, GraphActivity.class);
			
			intent.putExtra("auth", auth);
			
			intent.putExtra("zabbixApiUrl", zabbixApiUrl);
			
			intent.putExtra("hostName", hostName);
			 
			intent.putExtra("label", label);
			
			intent.putExtra("itemName", itemName);
			
			intent.putExtra("itemColor", itemColor);
			
			intent.putExtra("itemClock", itemClock);
			
			intent.putExtra("itemLimitInt", itemLimitInt);
			
			intent.putExtra("itemDelayInt", itemDelayInt);	
			
			intent.putExtra("itemCount", itemCount);
			
			intent.putExtra("valueCount", valueCount);
				
//objekta padosana grafiku ziimeesanai		
				
			bundle.putSerializable("history", history);
			intent.putExtras(bundle);
			
//tiek uzsaakta jauna aktivitaate			
			 activity.startActivity(intent);
		} else {

			uiMssg = "No history data";
			
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
		}
	}
}
