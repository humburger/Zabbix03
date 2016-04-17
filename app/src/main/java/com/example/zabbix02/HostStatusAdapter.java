package com.example.zabbix02;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//resursdatoru saraksta atteelosanai

//adapteris paarvalda datu modeli un to pielaago saraksta skata rindinaam
public class HostStatusAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private ArrayList<HostStatus> data;
	
//konstruktors ieguust vajadziigos parametrus    
	public HostStatusAdapter(Context context, ArrayList<HostStatus> data) {
		
		inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.data = data;
	}

//saraksta garums	
	@Override
	public int getCount() {
		
		return data.size();
	}
 
//iguust saraksta vienuma poziiciju	
	@Override
	public HostStatus getItem(int position) {
		
		return data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		
		return 0;
	}
	
//saraksata skata sagatavosana	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//sarakasta skata rindas struktuuras ievietosana 		
		View view = convertView;
		
	        if ( convertView == null ) {
	        	
	        	view = inflater.inflate(R.layout.list_row, null);
	        }
  
//saraksta vienuma poziicija	        
        HostStatus item = data.get(position);

//ievieto bildiiti: krustins vai keksiitis        
		ImageView image = (ImageView)view.findViewById(R.id.image);
		
		image.setImageResource(item.getStatus());

//resursdatora nosaukums		
		TextView name = (TextView)view.findViewById(R.id.name);
		
		name.setText(item.getName());
		
		return view;
	}
}
