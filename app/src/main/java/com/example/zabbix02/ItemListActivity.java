/**
 * Girts Vilumsons
 * 
 * Resursdatora vienumu saraksta atteelosanas modulis
 * 
 * */

package com.example.zabbix02;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//
public class ItemListActivity extends ListActivity {

	private String auth;
	private String hostName;
	private ArrayAdapter<String> listAdapter;
	private String[] itemArray = null;
	private String[] itemLastCheck = null;
	private String[] itemLastValue = null;
	private String[] itemError = null;
	private String[] itemDescription = null;
	private String[] itemUnits = null;
	private String lastCheck;
	private String zabbixApiUrl;
	private String itemName;
	private AlertDialog.Builder itemDetails;
	private DetailsAdapter DetailsAdapter;
	private ArrayList<Details> itemArrayList; 
	private ListView listView;
	private int num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list);

//padoto veertiibu ieguusana		
		Intent intent = getIntent();
		
		auth = intent.getExtras().getString("auth");
		
		zabbixApiUrl = intent.getExtras().getString("zabbixApiUrl");
		
		hostName = intent.getExtras().getString("hostName");

//ekraana nosaukuma nomaina atkariibaa no izveeleetaas resursdatora izveelnes iedalas		
		getActionBar().setTitle( hostName  + " items");
		
		itemArray = intent.getExtras().getStringArray("itemArray");
		
		itemLastCheck = intent.getExtras().getStringArray("itemLastCheck");
		
		itemLastValue = intent.getExtras().getStringArray("itemLastValue");
		
		itemDescription = intent.getExtras().getStringArray("itemDescription");
		
		itemError = intent.getExtras().getStringArray("itemError");
		
		itemUnits = intent.getExtras().getStringArray("itemUnits");
		
		num = itemArray.length;

//adapteris datu ievietosanai saraksta skataa			
		listAdapter = new ArrayAdapter<String>(
				
        		this, android.R.layout.simple_list_item_1, itemArray);
		
		setListAdapter(listAdapter);
	}

//nospiezot uz izveeleetaa vienuma nosaukuma tiek izveidots informatiiva dialoga logs
	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {
		
		super.onListItemClick(l, v, pos, id);

//nosaka nosaukumu, uz kura uzspieda		
			itemName = (String) getListView().getItemAtPosition(pos);

//paarksata visu vienumu sarakstu			
			for (int i = 0; i < num; i++) {

//atrod izveeleeto vienumu				
				if ( itemArray[i].equals(itemName) ) {
		
//tiek izveidots dialogs					
					itemDetails = new AlertDialog.Builder( this );

//peec pogas "OK" nospiesanas dialogs tiek nonemts					
					itemDetails.setPositiveButton("OK", 
							
							new DialogInterface.OnClickListener() {
						
		                        public void onClick( DialogInterface itemDetailsDialog, int id ) {
		                        	
		                        	itemDetailsDialog.dismiss();
	                        }
                        }
					);

					LayoutInflater inflater = getLayoutInflater();
					
//saraksta skats, kuru ievietots dialogaa
					View convertView = inflater.inflate(R.layout.list_view, null);

//saraksta skata ievietotosana dialogaa					
					itemDetails.setView(convertView);

//dialoga nosaukums					
					itemDetails.setTitle( itemName ); 

//saraksta skats dialogaa					
					listView = (ListView) convertView.findViewById( R.id.listView );

//dialoga atteelotaa informaacija					
					itemArrayList = new ArrayList<Details>();

//peedeejais laiks, kad vienums saneema informaaciju no aGenta					
						lastCheck = LogInActivity.convertTimestamp( Long.parseLong(itemLastCheck[ i ]), "yyyy-MM-dd HH:mm");

						itemArrayList.add( new Details( "Last check: " , lastCheck ) );
						
//vienuma meeraamaas vieniibas						
						itemArrayList.add( new Details( "Last value: " , itemLastValue[ i ] + " " + itemUnits[ i ] ) );
						
//vienuma apraksts						
						itemArrayList.add( new Details( "Description: " , itemDescription [ i ]) );
						
//vienuma kluudas pazinojums						
						itemArrayList.add( new Details( "Error: " , itemError[ i ] ) );
		
//detalu adapteris			
					DetailsAdapter = new DetailsAdapter( this, itemArrayList );
		
//adaptera ievietosana dialoga skataa					
			        listView.setAdapter( DetailsAdapter );       

//dialoga paraadiisana			        
					itemDetails.show();

//cikla paartraukums					
					break;
			}
		}
	}
	
//tiek izveidots mobilaas ieriices menu pogas izveelne, kur ir tikai iespeeja atteikties no sisteemas@Override
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
