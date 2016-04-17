package com.example.zabbix02;

import java.io.Serializable;

//datu struktuura vienuma veestures laikam un veertiibaam
public class ItemHistory implements Serializable {
	
		private String[] clock;
		
		private String[] value;

//ar "set" uzstaada veertiibas un "get" atdod saglabaataas veertiibas
		
		public String getClock( int location ) {
			
			return clock[ location ];
		}
		public void setClock(String clock, int location) {
			
			this.clock[ location ] = clock;
		}
		public String getValue( int location ) {
			
			return value[ location ];
		}
		public void setValue(String value, int location) {
			
			this.value[ location ] = value;
		}
		
//izveido masiivus laikam un informaacijai		
		public void makeArray(int size){
			
			clock = new String[size];
			
			value = new String[size];
		}
}
