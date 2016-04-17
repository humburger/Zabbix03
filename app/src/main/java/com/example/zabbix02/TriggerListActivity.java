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

//klase izveido un atteelo trigeru sarakstu, nodrosina siikaakas informaacijas apskati
//dialoga izveide ir liidziiga kaa ar vienumu dialogu izveidi
public class TriggerListActivity extends ListActivity {

	private String auth;
	private String hostName;
	private String lastChange;
	private ArrayAdapter<String> triggerListAdapter;
	private String[] triggerError = null;
	private String[] triggerDescription = null;
//	private String[] triggerExpression = null;
	private String[] triggerLastTime = null;
	private String[] triggerValue = null;
	private String[] triggerPriority = null;
	private String[] triggerComments = null;
	private String[] triggerProblemArray = null;
	private String[] triggerProblemError = null;
//	private String[] triggerProblemExpression = null;
	private String[] triggerProblemLastTime = null;
	private String[] triggerProblemValue = null;
	private String[] triggerProblemPriority = null;
	private String[] triggerProblemComments = null;
	private int triggerProblem;
	private int num;
	private String triggerStatus;
	private String zabbixApiUrl;
	private String triggerName;
	private AlertDialog.Builder triggerDetails;
	private ListView listView;
	private DetailsAdapter DetailsAdapter;
	private ArrayList<Details> triggerArrayList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list);
	
//padoto veertiibu sanemsana		
		Intent intent = getIntent();
		
		auth = intent.getExtras().getString("auth");
		
		zabbixApiUrl = intent.getExtras().getString("zabbixApiUrl");
		
		hostName = intent.getExtras().getString("hostName");
		
		triggerDescription = intent.getExtras().getStringArray(
				
				"triggerDescription");
		
		triggerPriority = intent.getExtras().getStringArray("triggerPriority"); //change to something more understandable
		
		triggerError = intent.getExtras().getStringArray("triggerError"); //change to something more understandable
		
//		triggerExpression = intent.getExtras().getStringArray("triggerExpression"); //change to something more understandable
		
		triggerLastTime = intent.getExtras().getStringArray("triggerLastTime"); //change to something more understandable
		
		triggerValue = intent.getExtras().getStringArray("triggerValue");
		
		triggerComments = intent.getExtras().getStringArray("triggerComments");
		
		triggerStatus = intent.getExtras().getString("triggerStatus");

		triggerProblem = intent.getExtras().getInt("triggerProblem");

//problemaatisko trigeru gadiijums		
		if ( triggerStatus.contains("PROBLEM") ) {
	
//ekraana nosaukuma maina			
			getActionBar().setTitle( hostName + " problems");

//problemaatisko trigeru un to datu atlasiisana no visiem paareejiem trigeriem
			triggerProblemArray = new String[ triggerProblem ];
//			triggerProblemExpression = new String[ triggerProblem ];
			triggerProblemLastTime = new String[ triggerProblem ];
			triggerProblemValue = new String[ triggerProblem ];
			triggerProblemPriority = new String[ triggerProblem ];
			triggerProblemComments = new String[ triggerProblem ];
			triggerProblemError = new String[ triggerProblem ];
				
				for ( int i = 0; i < triggerDescription.length; i++ ) {
				
					if ( triggerValue[ i ].equals("PROBLEM") ) {
						
						for ( int j = 0; j < triggerProblem; j++ ) {
							
							if ( triggerProblemArray[ j ] == null ) {
							
								triggerProblemArray[ j ] = triggerDescription[ i ];
	
//								triggerProblemExpression[ j ] = triggerExpression[ i ];
								
								triggerProblemLastTime[ j ] = triggerLastTime[ i ];
								
								triggerProblemValue[ j ] = triggerValue[ i ];
								
								triggerProblemPriority[ j ] = triggerPriority[ i ];
								
								triggerProblemComments[ j ] = triggerComments[ i ];
								
								triggerProblemError[ j ] = triggerError[ i ];
								
								break;
							}
						}
					}
				}
			
		triggerError = null;
//		triggerExpression = null;
		triggerLastTime = null;
		triggerValue = null;
		triggerPriority = null;
		triggerComments = null;
		
		triggerError = triggerProblemError;
//		triggerExpression = triggerProblemExpression;
		triggerLastTime = triggerProblemLastTime;
		triggerValue = triggerProblemValue;
		triggerPriority = triggerProblemPriority;
		triggerComments = triggerProblemComments;
		
		triggerDescription = null;
		triggerDescription = triggerProblemArray;
		
		num = triggerProblem;
	
//visu trigeru gadiijums		
		} else if ( triggerStatus.contains("ALL") ) {
	
//ekraana nosaukuma nomaina			
			getActionBar().setTitle( hostName + " all triggers");
			
			num = triggerDescription.length;
		}

//trigeru saraksta izveide		
		triggerListAdapter = new ArrayAdapter<String> (

				this, android.R.layout.simple_list_item_1, triggerDescription);

		setListAdapter(triggerListAdapter);
	}

//izveeloties trigeru tiek izveidots informatiivs dialogs ar taa parametriem
	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {

		super.onListItemClick(l, v, pos, id);

		if (getListAdapter() == triggerListAdapter) {

//trigera nosaukums
			triggerName = (String) getListView().getItemAtPosition(pos);

			for (int i = 0; i < num; i++) {
				
//izveeleetaa trigera dialoga izveide
				if (triggerDescription[i].equals(triggerName)) {

					triggerDetails = new AlertDialog.Builder(
							this );
					
					triggerDetails.setCancelable( true );
					
					triggerDetails.setPositiveButton("OK", 
							
							new DialogInterface.OnClickListener() {
						
		                        public void onClick( DialogInterface triggerDetailsDialog, int id ) {
		                        	
									triggerDetailsDialog.dismiss();
	                        }
                        }
					);

					LayoutInflater inflater = getLayoutInflater();

					View convertView = inflater.inflate(R.layout.list_view,
							null);

					triggerDetails.setView(convertView);

					triggerDetails.setTitle( triggerPriority[ i ] );

					listView = (ListView) convertView
							
							.findViewById( R.id.listView );

					triggerArrayList = new ArrayList<Details>();

//dialoga parametru savietosana					
					lastChange = LogInActivity.convertTimestamp( Long.parseLong( triggerLastTime[ i ] ), "yyyy-MM-dd HH:mm" );
						
						triggerArrayList.add( new Details( "Description: " , triggerDescription[ i ] ) );
						
//						triggerArrayList.add( new Details( "Expression: " , triggerExpression[ i ] ) );
						
						triggerArrayList.add( new Details( "Status: " , triggerValue[ i ] ) );
						
						triggerArrayList.add( new Details( "Last change: " , lastChange ) );
						
						triggerArrayList.add( new Details( "Error: " , triggerError[ i ] ) );
						
						triggerArrayList.add( new Details( "Comments: " , triggerComments[ i ] ) );
						
					DetailsAdapter = new DetailsAdapter( this, triggerArrayList );
						
			        listView.setAdapter( DetailsAdapter );
			        
//dialoga paraadiisana
					triggerDetails.show();

					break;
				}
			}
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
