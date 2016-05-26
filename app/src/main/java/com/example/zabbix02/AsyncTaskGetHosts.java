package com.example.zabbix02;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

//klase veic fona uzdevumu, kas veic resursdatoru datu pieprasiijumu serverim un sanemto datu padosanu resursadatoru saraksta atteelosanas modulim
public class AsyncTaskGetHosts extends AsyncTask<Void, Void, String[]> {

	private String zabbixApiUrl;
	private String auth;
	private String[] hostName = null;
	private String[] hostID = null;
	private String[] alert = null;
	private String[] hostStatus = null;
	private String[] triggerValue = null;
	private Context context;
	private Activity activity;
	private ProgressDialog progressDialog;

//konstruktors panem padotos datus	
	public AsyncTaskGetHosts(Context context, Activity activity, String auth, String zabbixApiUrl) {
		this.auth = auth;
		this.context = context;
		this.zabbixApiUrl = zabbixApiUrl;
		this.activity = activity;
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
		
		sb.append("\"method\":\"host.get\",");
		
		sb.append("\"params\":{");
		
		sb.append("\"output\":\"extend\"");

		sb.append("},");
		
		sb.append("\"auth\":\"").append(auth).append("\",");
		
		sb.append("\"id\":\"1\"}");
		
		String sbString = sb.toString();

		/*StringBuilder sb1 = new StringBuilder();

		sb1.append("{\"jsonrpc\":\"2.0\",");

		sb1.append("\"method\":\"alert.get\",");

		sb1.append("\"params\":{");

		sb1.append("\"output\":\"extend\"");

		sb1.append("},");

		sb1.append("\"auth\":\"").append(auth).append("\",");

		sb1.append("\"id\":\"1\"}");

		String sb1String = sb1.toString();
*/
		try {
			
// tiek nodibinaati sakari ar serveri, lai dabutu informaciju par resursdatoriem
			HttpResponse response = LogInActivity.makePostRequest(sbString, zabbixApiUrl);
			
			HttpEntity entity = response.getEntity();
			
			String entityString = EntityUtils.toString(entity);

			/*// tiek nodibinaati sakari ar serveri, lai dabutu informaciju par briidinaajumiem
			HttpResponse response_alert = LogInActivity.makePostRequest(sb1String, zabbixApiUrl);

			HttpEntity entity_alert = response.getEntity();

			String entityString_alert = EntityUtils.toString(entity_alert);*/

//tiek ieguuti visi resursdatora nosaukumi			
			hostName = LogInActivity.getJsonParameters(entityString, "host", null, null);
			
//tiek ieguuti visi resursdatoru identifikaacijas numuri			
			hostID = LogInActivity.getJsonParameters(entityString, "hostid", null, null);


			
			hostStatus = new String[ hostName.length ];
			
//resursdatora saakuma statusa veertiiba ir pozitiiva			
			for ( int i = 0; i < hostName.length; i++ ) {
				
				hostStatus [ i ] = "OK";
			}
			
//katram resursdatoram tiek veikts pieprasiijums ieguut datus par to, vai eksistee kaads problemaatisks trigeris			
			for ( int i = 0; i < hostName.length; i++ ) {
				
				sb = new StringBuilder();

				sb.append("{\"jsonrpc\":\"2.0\",");

				sb.append("\"method\":\"trigger.get\",");

				sb.append("\"params\":{");

				sb.append("\"hostids\": \"").append( hostID[ i ] ).append("\",");
				
				sb.append("\"output\":\"extend\",");

				sb.append("\"selectFunctions\": \"extend\",");

				sb.append("\"sortfield\": \"priority\",");

				sb.append("\"sortorder\": \"DESC\"");

				sb.append("},");

				sb.append("\"auth\":\"").append( auth ).append("\",");

				sb.append("\"id\":\"1\"}");

				sbString = sb.toString();

// tiek nodibinaati sakari ar serveri
				response = LogInActivity.makePostRequest(sbString, zabbixApiUrl);

				entity = response.getEntity();

				entityString = EntityUtils.toString(entity);
				
//tiek ieguuti trigeru staavokli				
				triggerValue = LogInActivity.getJsonParameters(entityString, "value", null, null);

//tiek samekleets problemaatisks trigeris un pie konkreetaa resursdatora tas tiek atziimeets,
//				un tiek turpinaats mekleet problemaatiskos trigerus naakosajam resursdatoram				
//
					for (String trigger : triggerValue) {
						if ( trigger.equals("1") ) {
							hostStatus [ i ] = "PROBLEM";
							break;
						}
					}
			}
			return hostName;

//jebkaadu kluudu gadiijumaa tiek atdota tuksa veertiiba, peec kuras tiek izlemta taalaaka darbiiba			
		} catch (Exception e) {
			
			return null;
		}
	}

// rezultaatu izvade
	protected void onPostExecute(String[] result) {
		
//peec darbiibu izpildes informatiivais dialogs tiek izsleegts			
		progressDialog.dismiss();

//ja veertiiba nav tuksa, seko rezultaatu apstraades turpinaajums		
		if (result != null) {

			Intent intent = new Intent(activity, MainListActivity.class);
			
// tiek padoti mainiigo parametri resursdatoru saraksta atteelosanas modulim
			intent.putExtra("auth", auth);
						
			intent.putExtra("zabbixApiUrl", zabbixApiUrl);
			
			intent.putExtra("hostListArray", hostName);
			
			intent.putExtra("hostStatus", hostStatus);
			
			activity.startActivity(intent);
		}
	}
}
