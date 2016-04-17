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

//klase veic fona uzdevumu, kas veic resursdatoru vienumu datu pieprasiijumu serverim un sanemto datu padosanu resursadatoru vienumu saraksta atteelosanas modulim
public class AsyncTaskGetItems extends AsyncTask<Void, Void, String[]> {

	private String zabbixApiUrl;
	private String auth;
	private String uiMssg;
	private String[] parameterArray = null;
	private String hostName;
	private String hostID;
	private Activity activity;
	private String[] itemArray = null;
	private String[] itemKey = null;
	private String[] itemLastCheck = null;
	private String[] itemLastValue = null;
	private String[] itemDescription = null;
	private String[] itemError = null;
	private String[] itemUnits = null;
	private String[] itemNameSplit = null;
	private String[] itemKeySplit01 = null;
	private String[] itemKeySplit02 = null;
	private String dollarSymbolString;
	private int dollarSymbolInt;
	private int itemNameSplitNum;
	private int zero = 0;
	private ProgressDialog progressDialog;
	private Context context;

//konstruktors ieguust padotaas veertiibas	
	public AsyncTaskGetItems(Activity activity, Context context, String auth, String hostName, String zabbixApiUrl) {
		
		this.auth = auth;
		this.zabbixApiUrl = zabbixApiUrl;
		this.hostName = hostName;
		this.activity = activity;
		this.context = context;
	}
	
//Informatiivs dialogs par pieprasiijuma processa izpildi	
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
	@Override
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

		try {
// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoriem

			HttpResponse response = LogInActivity.makePostRequest(sb0String, zabbixApiUrl);

			HttpEntity entity = response.getEntity();

			String entityString = EntityUtils.toString(entity);

//tiek ieguuti parametri tika par izveeleeto resursdatoru			
			parameterArray = LogInActivity.getJsonParameters(entityString, "hostid", "host", hostName);
	
//atseviskajam mainiigajam tiek padots resursdatora id numurs
//no masiiva uz atsevisku string veertiibu, jo pati izsaucamaa funkcija izdod tikai masiivus, pat ja masiiva izmeers ir "1"			
			hostID = parameterArray[zero];

//item.get ieguust vieniibas nosaukumu, aizkavi
			sb0 = new StringBuilder();

			sb0.append("{\"jsonrpc\":\"2.0\",");

			sb0.append("\"method\":\"item.get\",");

			sb0.append("\"params\":{");

			sb0.append("\"output\":\"extend\",");
			
			sb0.append("\"hostids\":\"").append(hostID).append("\",");
			
			sb0.append("\"search\":{"); //LIKE "%...%"
			
			sb0.append("\"sortfield\": \"name\"}");

			sb0.append("},");

			sb0.append("\"auth\":\"").append(auth).append("\",");

			sb0.append("\"id\":\"1\"}");

			sb0String = sb0.toString();

// tiek nodibinaati sakari ar serveri un ieguuti dati par resursdatoru vienumiem
			response = LogInActivity.makePostRequest(sb0String, zabbixApiUrl);

			entity = response.getEntity();

			entityString = EntityUtils.toString(entity);
			
			//log			
			Log.e("json", entityString);
//

//visu vienumu nosaukumi			
			itemArray = LogInActivity.getJsonParameters(entityString, "name", null, null);
	
//visu vienumu "atsleegas", kuras satur mainiigos, peec kuriem tiek veidoti vienumu pilniigie nosaukumi
//ja vienuma nosaukums satur kaadu mainiigo veertiibu, tad tas tiek aizvietots ar kaadu no "atsleegas" veertiibaam 			
			itemKey = LogInActivity.getJsonParameters(entityString, "key_", null, null);

//visu vienumu peedeejais laiks, kad tika atjaunota veertiiba			
			itemLastCheck = LogInActivity.getJsonParameters(entityString, "lastclock", null, null);

//visu vienumu peedeejaa sanemtaa veertiiba			
			itemLastValue = LogInActivity.getJsonParameters(entityString, "lastvalue", null, null);

//visu vienumu apraksts			
			itemDescription = LogInActivity.getJsonParameters(entityString, "description", null, null);

//visu vienumu kluudu pazinojumi			
			itemError = LogInActivity.getJsonParameters(entityString, "error", null, null);

//visu vienumu meeraamaas veertiibas			
			itemUnits = LogInActivity.getJsonParameters(entityString, "units", null, null);
			
			return itemArray;
			
//peeksns savienojuma paarraavums ar serveri			
		} catch (IOException e) {
			
			uiMssg = "Connection lost to the Zabbix server";
			
			return null;
			
//citas kluudas			
		} catch (Exception e) {

			return null;
		}
	}

// rezultaatu izvade
	protected void onPostExecute(String[] result) {
		
//ja viss ir bijis veiksmiigi, tad tiek padotas nolasiitaas veertiibas 
		if (result != null) {

			uiMssg = "Successfully created item list";
			
			Intent intent = new Intent(activity, ItemListActivity.class);
			
			intent.putExtra("auth", auth);
			
			intent.putExtra("zabbixApiUrl", zabbixApiUrl);
			
			intent.putExtra("hostName", hostName);
			
			intent.putExtra("itemArray", itemArray);
			
			intent.putExtra("itemLastCheck", itemLastCheck);
			
			intent.putExtra("itemLastValue", itemLastValue);
			
			intent.putExtra("itemDescription", itemDescription);
			
			intent.putExtra("itemError", itemError);
			
			intent.putExtra("itemUnits", itemUnits);
			
 			
			for (int i = 0; i < itemArray.length; i++) {
				
//katram vienuma nosaukumam, kurs satur mainiigaas veertiibas, piem. "$1", so veertiibu vietaa tiek ievietotas atbilsosaas veertiibas				
				if ( itemArray[ i ].contains( "$" ) ) {
					
//vienuma nosaukuma sadaliisana pa dalaam					
					itemNameSplit = itemArray[ i ].split( "\\$" );
					
					itemNameSplitNum = itemNameSplit.length - 1;
					
//bezparametru gadiijums					
					if ( itemKey[ i ].contains("[]") ) { 

//vienuma nosaukums bez parametriem						
						itemArray [ i ] = itemNameSplit[ 0 ];
					}
					
//vairaaku parametru gadiijums					
						else if ( itemKey[ i ].contains( "," ) ) {
							
//"atleega" tiek sadaliita "," vietaas							
							itemKeySplit01 = itemKey[ i ].split( "," );
							
//peedeejo simbolu, kas ir "]", aizvieto ar tuksumu							
							itemKeySplit01[ itemKeySplit01.length - 1 ] = itemKeySplit01[ itemKeySplit01.length - 1 ].replace( "]", "" );
							
//pirmais parametrs ir ""							
								if ( Character.toString( itemKeySplit01[ 0 ].charAt( ( itemKeySplit01[ 0 ].length() - 1 ) ) ).equals("[")
										
										&& ( itemKeySplit01.length == 2 ) ) {
								
//nonem "[" simbolu									
									itemNameSplit[ itemNameSplitNum ] = itemNameSplit[ itemNameSplitNum ].substring( 1, itemNameSplit[ itemNameSplitNum ].length() );
									
//no jaunizveidotajiem teksta gabaliniem saliek vienuma nosaukumu									
									itemArray[ i ] = itemNameSplit[ 0 ] + itemKeySplit01[ 1 ] + itemNameSplit[ itemNameSplitNum ];
									
//pirmais parametrs nav ""								
								} else {
									
									itemArray[ i ] = itemNameSplit[ 0 ];
									
//darbiiba ar vairaakiem mainiigajiem, kuri veido vienuma nosaukumu									
									for ( int j = 1; j < itemNameSplit.length; j++ ) { 
										
										dollarSymbolString = Character.toString( ( itemNameSplit[ j ].charAt( 0 ) ) );
										
										dollarSymbolInt = Integer.parseInt( dollarSymbolString ) ;
//darbiiba ar pirmo parametru									
										if ( dollarSymbolInt == 1 ) {
											
											itemKeySplit02 = itemKeySplit01[ 0 ].split( "\\[" );
											
											itemArray[ i ] = itemArray[ i ] + " " + itemKeySplit02[ dollarSymbolInt ];
											
											if ( j == itemNameSplit.length - 1 ) {
												
												if ( itemNameSplit[ itemNameSplitNum ].length() > 1 ) { 
												
													itemNameSplit[ itemNameSplitNum ] = itemNameSplit[ itemNameSplitNum ].substring( 1, itemNameSplit[ itemNameSplitNum ].length() );
													
													itemArray[ i ] = itemArray[ i ] + itemNameSplit[ itemNameSplitNum ];
												} 
											} 
//darbiiba ar paareejiem parametriem											
										} else {

											itemArray[ i ] = itemArray[ i ] + " " + itemKeySplit01[ dollarSymbolInt - 1 ];
											
											if ( j == itemNameSplit.length - 1 ) {
												
												if ( itemNameSplit[ itemNameSplitNum ].length() > 1 ) { 
												
													itemNameSplit[ itemNameSplitNum ] = itemNameSplit[ itemNameSplitNum ].substring( 1, itemNameSplit[ itemNameSplitNum ].length() );
													
													itemArray[ i ] = itemArray[ i ] + itemNameSplit[ itemNameSplitNum ];
												}
											}  
										}
									}
								}
//viena parametra gadiijums				
						} else {
							
							itemKeySplit01 = itemKey[ i ].split( "\\[" );
							
							itemKeySplit01[ itemKeySplit01.length - 1 ] = itemKeySplit01[ itemKeySplit01.length - 1 ].replace( "]", "" );
							
							itemArray[ i ] = itemNameSplit[ 0 ] + itemKeySplit01[ itemKeySplit01.length - 1 ];
						} 
					} 
				}

//dialoga izsleegsana			
			progressDialog.dismiss();
	
//pazinojums			
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();

//naakosaas aktivitaates palaisana			
			 activity.startActivity(intent);

		} else {
			
//dialoga izsleegsana			
			progressDialog.dismiss();

			uiMssg = "Unsuccessfully created item list";
			
			Toast.makeText(context, uiMssg, Toast.LENGTH_LONG).show();
		}
	}
}
