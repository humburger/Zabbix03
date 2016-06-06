/**
 * Girts Vilumsons
 * 
 * Resursdatoru grafiku saraksta atteelosanas modulis
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

//atteelo grafiku sarakstu
public class GraphListActivity extends ListActivity {
	
	private String auth;
	private String hostName;
	private String graphName;
	private ArrayAdapter<String> graphListAdapter;
	private String[] graphArray;
	private String zabbixApiUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
	
//padoto veertiibu sanemsana		
		Intent intent = getIntent();
		
		auth = intent.getExtras().getString("auth");
		
		zabbixApiUrl = intent.getExtras().getString("zabbixApiUrl");
		
		hostName = intent.getExtras().getString("hostName");
		
//ekraana nosaukuma maina		
		getActionBar().setTitle( hostName + " graphs");
		
		graphArray = intent.getExtras().getStringArray("graphArray");

//saraksta izveide		
		graphListAdapter = new ArrayAdapter<String>(
				
        		this, android.R.layout.simple_list_item_1, graphArray);
		
		setListAdapter(graphListAdapter);
		
	}

	
//izveeloties grafiku, tas tiek uzziimeets peec papildus informaacijas ieguusanas
	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {
		
		super.onListItemClick(l, v, pos, id);

		if (getListAdapter() == graphListAdapter){
			
			graphName = (String) getListView().getItemAtPosition(pos);

//informaacijas ieguusana			
			new AsyncTaskGetChoosenGraph(this, this, auth, zabbixApiUrl, hostName, graphName).execute();
			
		}
    }
	
//tiek izveidots mobilaas ieriices menu pogas izveelne, kur ir tikai iespeeja atteikties no sisteemas
@Override
public boolean onCreateOptionsMenu(Menu menu) {
					
			getMenuInflater().inflate(R.menu.log_out_menu, menu);
					
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
