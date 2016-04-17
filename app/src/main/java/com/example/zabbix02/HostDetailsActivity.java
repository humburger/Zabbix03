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

//izveido izveeleetaa resursdatora izveelni: "Items", "Triggers", "Graphs".
public class HostDetailsActivity extends ListActivity {
	
	private String auth;
	private String hostName;
	private ArrayAdapter<String> hostDetailsAdapter;
	private String[] hostDetailsString = { "Items", "Triggers", "Graphs"};
	private String zabbixApiUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.list );

//ieguust padotos datus		
		Intent intent = getIntent();
		
		auth = intent.getExtras().getString("auth");
		
		zabbixApiUrl = intent.getExtras().getString("zabbixApiUrl");
		
		hostName = intent.getExtras().getString("hostName");
		
//nomaina ekraana nosaukumu			
		getActionBar().setTitle( hostName );

//adapteris datu ievietosanai saraksta skataa		
        hostDetailsAdapter = new ArrayAdapter<String>(
				
        		this, android.R.layout.simple_list_item_1, hostDetailsString);
		
		setListAdapter(hostDetailsAdapter);
	}

//atkariibaa peec nospiestaas izveelnes iedalas, tiek pieprasiiti dati no servera
	@Override
	protected void onListItemClick( ListView l, View v, int pos, long id ) {
		
		super.onListItemClick( l, v, pos, id );

		if ((getListView().getItemAtPosition(pos)).equals(getString(R.string.graphs))) {
			
			new AsyncTaskGetGraphs(this, this, auth, hostName, zabbixApiUrl).execute();
				
		} else if ((getListView().getItemAtPosition(pos)).equals(getString(R.string.triggers))) {
			
			new AsyncTaskGetTriggers(this, this, auth, zabbixApiUrl, hostName).execute();
			
		} else if ((getListView().getItemAtPosition(pos)).equals(getString(R.string.items))) {
			
			new AsyncTaskGetItems(this, this, auth, hostName, zabbixApiUrl).execute();
			
		} 
    }
	
	//tiek izveidots mobilaas ieriices menu pogas izveelne, kur ir tikai iespeeja atteikties no sisteemas
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
