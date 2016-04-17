/**
 * Girts Vilumsons
 * 
 * Resursdatora trigeru saraksta atteelosanas modulis
 * 
 * */

package com.example.zabbix02;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//atteelo trigeru ieddaliijumu sarakstu
public class TriggerActivity extends ListActivity { 

	private String auth;
	private String hostName;
	private ArrayAdapter<String> triggerListAdapter;
	private String[] triggers = new String[ 2 ];
	private String[] triggerError = null;
	private String[] triggerDescription = null;
	private String[] triggerExpression = null;
	private String[] triggerLastTime = null;
	private String[] triggerValue = null;
	private String[] triggerPriority = null;
	private String[] triggerComments = null;
	private int triggerAll = 0;
	private int triggerProblem = 0;
	private String zabbixApiUrl;
	private String triggerStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list);

//padoto parametru ieguusana		
		Intent intent = getIntent();
		
		auth = intent.getExtras().getString("auth");
		
		zabbixApiUrl = intent.getExtras().getString("zabbixApiUrl");
		
		hostName = intent.getExtras().getString("hostName");

//aktivitaates nosaukuma nomaina		
		getActionBar().setTitle( hostName + " triggers");
		
		triggerDescription = intent.getExtras().getStringArray(
				
				"triggerDescription");
		
		triggerPriority = intent.getExtras().getStringArray("triggerPriority"); 
		
		triggerError = intent.getExtras().getStringArray("triggerError"); 
		
		triggerExpression = intent.getExtras().getStringArray("triggerExpression"); 
		
		triggerLastTime = intent.getExtras().getStringArray("triggerLastTime"); 
		
		triggerValue = intent.getExtras().getStringArray("triggerValue");
		
		triggerComments = intent.getExtras().getStringArray("triggerComments");

//tiek noskaidrots problemaatisko trigeru skaits	
		for (String trigger : triggerValue) {
			if (trigger.equals("PROBLEM")) {
				triggerProblem++;
			}
		}

//visu trigeru skaits		
		triggerAll = triggerValue.length;
		
		triggers[ 0 ] = "PROBLEM (" + triggerProblem + ")";
		triggers[ 1 ] = "ALL (" + triggerAll + ")";

//saraksta izveide		
		triggerListAdapter = new ArrayAdapter<String>(

		this, android.R.layout.simple_list_item_1, triggers);

			setListAdapter(triggerListAdapter);
	}

//kad uzspiez uz izveeleetaa trigera iedaliijuma, tad tiek izveidots to saraksts
	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {

		super.onListItemClick(l, v, pos, id);

//izveeleetais trigera veids		
		triggerStatus = (String) getListView().getItemAtPosition(pos);
		
//gadiijumaa, ja nav probleemu tiek padods attieciigs pazinojums
		if ( triggerStatus.equals( "PROBLEM (0)" ) ) { 
			
			Toast.makeText( this, "No problem", Toast.LENGTH_LONG ).show();
		} 

//citaadi tiek veertiibas padotas taalaak trigeru saraksta izveidei		
		else {
			
			Intent intent = new Intent(this, TriggerListActivity.class);
			
			intent.putExtra("auth", auth);
			
			intent.putExtra("zabbixApiUrl", zabbixApiUrl);
			
			intent.putExtra("hostName", hostName);
			
			intent.putExtra("triggerDescription", triggerDescription); 
			
			intent.putExtra("triggerPriority", triggerPriority); 
			
			intent.putExtra("triggerError", triggerError); 
			
			intent.putExtra("triggerExpression", triggerExpression);
			
			intent.putExtra("triggerLastTime", triggerLastTime); 
			
			intent.putExtra("triggerValue", triggerValue);
			
			intent.putExtra("triggerComments", triggerComments);
			
			intent.putExtra("triggerStatus", triggerStatus);
			
			intent.putExtra("triggerProblem", triggerProblem);
			
			intent.putExtra("triggerAll", triggerAll);

//tiek uzsaakta jauna aktivitaate			
			this.startActivity(intent); 
		}
	}
	
//tiek izveidots mobilaas ieriices menu pogas izveelne, kur ir tikai iespeeja atteekties no sisteemas
@Override
public boolean onCreateOptionsMenu(Menu menu) {
				
			getMenuInflater().inflate(R.menu.logout, menu);
				
			return true;
	}	
		
//atteiksanaas no sisteemas	
@Override
public boolean onOptionsItemSelected(MenuItem item) {
	         
        if (item.getItemId() == R.id.logout) {
	        	
        	new AsyncTaskLogOut(this, this).execute(auth, zabbixApiUrl);
	        	
        }
		return false;
    } 
}
