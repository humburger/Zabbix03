package com.example.zabbix02;


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
import android.widget.Toast;

import java.util.ArrayList;

public class MacroActivity
        extends ListActivity {

    private String auth;
    private String hostName;
    private String lastChange;
    private ArrayAdapter<String> macroListAdapter;
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
    private String[] macroValue;
    private String zabbixApiUrl;
    private String[] macroName;
    private String name;
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

        macroName = intent.getExtras().getStringArray("macroName");

        macroValue = intent.getExtras().getStringArray("macroValue");

        //masiiva izmeera noteiksana
        //http://alvinalexander.com/java/java-array-length-example-length-method
        num = macroName.length;

//trigeru saraksta izveide
        macroListAdapter = new ArrayAdapter<String>(

                this, android.R.layout.simple_list_item_1, macroName);

        setListAdapter(macroListAdapter);
    }

    //izveeloties trigeru tiek izveidots informatiivs dialogs ar taa parametriem
    @Override
    protected void onListItemClick(ListView l, View v, int pos, long id) {

        super.onListItemClick(l, v, pos, id);

        if (getListAdapter() == macroListAdapter) {

//trigera nosaukums
            name = (String) getListView().getItemAtPosition(pos);

            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();

            for (int i = 0; i < num; i++) {

//izveeleetaa trigera dialoga izveide
                if (macroName[i].equals(name)) {

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

                    triggerDetails.setTitle( "User macro" );

                    listView = (ListView) convertView

                            .findViewById( R.id.listView );

                    triggerArrayList = new ArrayList<Details>();


                    triggerArrayList.add( new Details( "Name: " , macroName[ i ] ) );

                    triggerArrayList.add( new Details( "Value: " , macroValue[ i ] ) );


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
