package com.example.zabbix02;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//resursdatoru vienumu un trigeru siikaakas informaacijas atteelosanai

//adapteris paarvalda datu modeli un to pielaago saraksta skata rindinaam
public class DetailsAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private ArrayList<Details> data;
    
//konstruktors ieguust vajadziigos parametrus   	
	public DetailsAdapter(Context context, ArrayList<Details> data) {
		
		inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.data = data;
	}

//saraksta garums	
	@Override
	public int getCount() {
		
		return data.size();
	}

//saraksta vienuma poziicija	
	@Override
	public Details getItem(int position) {
		
		return data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		
		return 0;
	}

//saraksata skata sagatavosana		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//dialoga saraksta skata rindas struktuuras ievietosana		
		View view = convertView;
		
	        if ( convertView == null ) {
	        	
	        	view = inflater.inflate( R.layout.list_view_details_dialog_row, null );
	        }
//vienuma poziicija        
        Details item = data.get(position);
  
//detalas        
		TextView detail = (TextView)view.findViewById(R.id.detail);
		
		detail.setText( item.getDetails() );

//informaacija		
		TextView triggerData = (TextView)view.findViewById(R.id.data);
		
		triggerData.setText( item.getData() );
		
		return view;
	}
}
