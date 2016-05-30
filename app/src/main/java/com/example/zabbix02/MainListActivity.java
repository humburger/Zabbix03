/**
 * Girts Vilumsons
 * 
 * Resursdatoru saraksta atteelosanas modulis
 * 
 * */
package com.example.zabbix02;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

//izveido un atteelo resursdatoru sarakstu
public class MainListActivity extends Activity implements OnItemClickListener{

	private String auth;
	private String zabbixApiUrl;
	private String choosenListItemName;
	private String[] hostName;
	private String[] hostStatus;
	private ListView listView;
	private ArrayList<HostStatus> arrayList;
	private HostStatusAdapter adapter;
	private Context context;
	private AlertDialog.Builder itemDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
//peec izrakstiisanaas spiezot "Back" pogu tiek iziets no programmas
// http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-l/9580057#9580057		
		boolean finish = getIntent().getBooleanExtra("finish", false);
		
	        if (finish) {
	        	
	            startActivity(new Intent(this, LogInActivity.class));
	            
	            finish();
	            
	            return;
	        }

//uzstaada aktivitaates skatu, izmantojot doto xml failu		
	    setContentView(R.layout.list_view);
	        
//nomaina ekraana nosaukumu	        
	    getActionBar().setTitle("Main list");
	    
		listView = (ListView) findViewById(R.id.listView);
		
//ieguust padotos parametrus
		Intent intent = getIntent();
		
		auth = intent.getExtras().getString("auth");
		
		zabbixApiUrl = intent.getExtras().getString("zabbixApiUrl");
		
		hostName = intent.getExtras().getStringArray("hostListArray");
		
		hostStatus = intent.getExtras().getStringArray("hostStatus");

//resursdatoru saraksts ar to staavokliem	
		setMainList();
		
        adapter = new HostStatusAdapter(this, arrayList);
        
        listView.setAdapter( adapter );       
        
        listView.setOnItemClickListener(this);
	}

//katram resursdatoram tiek izveeleeta bildiite atkariibaa no taa staavokla	
	private void setMainList() {
		
		arrayList = new ArrayList<HostStatus>();
		boolean OK = true;
		
		for (int i = 0; i < hostName.length; i++) {
			if ( hostStatus[ i ].equals("PROBLEM") ) {
				OK = false;
				break;
			} else {
				continue;
			}
		}
//ja kaut vienam resursdatoram ir probleema, tad kopiigajam sarakstam noraada, ka jaapieveers uzmaniiba
		if ( OK == false ) {
				
				arrayList.add( new HostStatus(getString(R.string.all_hosts), R.drawable.x_mark));
				
			} else {
				
				arrayList.add( new HostStatus(getString(R.string.all_hosts), R.drawable.arrow));
			}
        arrayList.add( new HostStatus(getString(R.string.all_events), R.drawable.walle));
	}

//kad uzspiez uz izveeleetaa resursdatora noasaukuma, tad tiek atveerta taa izveelne
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		HostStatus item = adapter.getItem(position);

		choosenListItemName = item.getName();

        if (choosenListItemName.equals(getString(R.string.all_hosts))) {
            Intent intent = new Intent(this, HostListActivity.class);

            // tiek padoti mainiigo parametri resursdatoru saraksta atteelosanas modulim
            intent.putExtra("auth", auth);

            intent.putExtra("zabbixApiUrl", zabbixApiUrl);

            intent.putExtra("hostListArray", hostName);

            intent.putExtra("hostStatus", hostStatus);

            this.startActivity(intent);
        } else if (choosenListItemName.equals(getString(R.string.all_events))) {
            new AsyncTaskGetEvents(this, this, auth, zabbixApiUrl).execute();
        }



        /*//testing notifications
        Intent intent = new Intent(this, CreateNotificationActivity.class);
        this.startActivity(intent);*/
			}

//tiek izveidots mobilaas ieriices menu pogas izveelne, kur ir tikai iespeeja atteekties no sisteemas
@Override
public boolean onCreateOptionsMenu(Menu menu) {
			
			getMenuInflater().inflate(R.menu.settings_menu, menu);
			
			return true;
}	
	
//atteiksanaas no sisteemas	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {

            new AsyncTaskLogOut(this, this).execute(auth, zabbixApiUrl);

        } else if (item.getItemId() == R.id.usrMacros) {

            new AsyncTaskGetMacros(this, this, auth, zabbixApiUrl).execute();

        } else if (item.getItemId() == R.id.maintenance) {

            new AsyncTaskGetMaintenance(this, this, auth, zabbixApiUrl).execute();

		} else if (item.getItemId() == R.id.scripts) {

            new AsyncTaskGetScript(this, this, auth, zabbixApiUrl).execute();

        } else if (item.getItemId() == R.id.usrMedia) {

            new AsyncTaskGetMedia(this, this, auth, zabbixApiUrl).execute();

        }
		return false;
    }    
}
