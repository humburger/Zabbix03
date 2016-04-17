package com.example.zabbix02;

//datu modelis prieks adaptera
public class Details {

//detalas	
	private String details;

//informaacija	
	private String data;

//konstruktors parametru ieguusanai	
	public Details(String details, String data) {
		
		this.details = details;
		
		this.data = data;
	}

//attieciiga veertiibu izvade	
	public String getDetails() {
		return details;
	}
	public String getData() {
		return data;
	}
}
