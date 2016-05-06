/**
 * Girts Vilumsons
 * 
 * Lietotaaja pieteiksanaas modulis
 * 
 * */
package com.example.zabbix02;

//tiek importeetas vajadziigaas biblioteekas

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

//tiek izmantota atveertaas programmatuuras biblioteeka JSON datu parseesanai//

//http://www.java2s.com/Code/Jar/j/Downloadjacksoncore223jar.htm
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends Activity implements OnClickListener {

//lietotaajvaarda aizpildiisanas lauks
	private EditText usrName;
	
//paroles aizpildiisanas lauks	
	private EditText pssw;
	
//servera adreses aizpildiisanas lauks
	private EditText zabbixUrl;

//pieteiksanaas poga	
	private Button LogIn;

	private String username = "";
	private String password = "";
	private String url = "";
	private String zabbixApiUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
//tiek izveidota pieteiksanaas forma		
		setContentView(R.layout.activity_login);
		
//mainiigajaam veertiibaam piesaista aizpildiisanas lauku veertiibas
		usrName = (EditText) findViewById(R.id.usrNameEdit);

		pssw = (EditText) findViewById(R.id.psswEdit);

		zabbixUrl = (EditText) findViewById(R.id.urlEdit);

		LogIn = (Button) findViewById(R.id.LogIn);
		
//uzstaada funkciju, kura tiek palaista nospiezot pieteiksanaas poga
		LogIn.setOnClickListener(this);

		/*final String[] COUNTRIES = new String[] {
				"Belgium", "France", "Italy", "Germany", "Spain"
		};

		// teksta redigesanas lauks ar vesturi
		//http://stackoverflow.com/questions/14978676/edittext-with-history
		ArrayAdapter<String> adapterUsr = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
		ArrayAdapter<String> adapterZabbixUrl = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
		usrName.setAdapter(adapterUsr);
		zabbixUrl.setAdapter(adapterZabbixUrl); */
	}

	// pieteiksanaas sisteemai
	@Override
	public void onClick(View view) {
		
//katram mainiigajam pieskir aizpildaamo lauku veertiibas
		 username = usrName.getText().toString();

		 password = pssw.getText().toString();

		 url = zabbixUrl.getText().toString();

		 zabbixApiUrl = "http://" + url + "/zabbix/api_jsonrpc.php";
		
// ja visi lauki ir aizpildiiti, tad to veertiibas padod asinhronaa uzdevuma klasei, kas izpilda pieprasiijuma izpildi Zabbix serverim 
		if (!username.equals("") && !password.equals("") && !url.equals(""))
		 {
			if (view.getId() == R.id.LogIn) {
				
				new AsyncTaskLogIn(this, this).execute(username, password, url,
						zabbixApiUrl);
			}
			
// ja kaads no laukiem nav aizpildiits tiek izdots attieciigs pazinojums
		 } else {
			 
//toast klases izveidoais objekts uz kaadu briidi izdod pazinojumu 			 
		 Toast.makeText(this, "Please fill all field values.", Toast.LENGTH_LONG).show();
		 }
	}

// POST sazinas funkcija ar serveri, kas piegaadaa attiieciigo JSON formaataa veidoto pieprasiijumu
//	http://mytechattempts.wordpress.com/tag/zabbix-server/
	static HttpResponse makePostRequest(String request, String zabbixApiUrl)
			throws IOException {

		HttpClient client = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost(zabbixApiUrl);

		httpPost.setEntity( new StringEntity( request ) );

		httpPost.addHeader("Content-Type", "application/json-rpc");

		return client.execute(httpPost);
	}

//laika ziimogu konvertaacija uz datumu un laiku; 
//	http://sluse.com/view/20654967 			
		public static String convertTimestamp(long epochSeconds, String formatString) {
			
		    Date date = new Date(epochSeconds * 1000);

		    SimpleDateFormat format = new SimpleDateFormat(formatString);
		    
		    return format.format(date);
		}
		
//Json datu parseesana - JSON teksts ar datiem, mekleejamaa informaacija, saliidzinaamais parametrs, indikators datu saliidzinaasanas vajadziibai
		public static String[] getJsonParameters(String entityString, String parameter, String equalParameter, String equal)

				throws IOException{ //izdodamaas kluudas un izneemumi
			
			String[] value;
			String path = "result";
			
//tiek sagatavots masiivs datu saglabaasanai, vispirms nosakot masiiva izmeerus
			int count = LogInActivity.getCount(entityString);
			value = new String[count];

//tiek veikta JSON datu paarveidosana uz klases struktuuru, kuru peec tam apstaigaas un nolasiis vajadziigos datus 
			ObjectMapper mapper = new ObjectMapper();

			JsonNode rootNode = mapper.readValue(entityString, JsonNode.class);

//atzarojums ar resursdatoru sarakstu
			JsonNode resultNode = rootNode.path(path);

// paarstaigaasana ar iteratoru
			Iterator<JsonNode> list = resultNode.elements();
			
			int i = 0;
			
//ja tiek mekleetas konkreetas parametra veertiibas	
			if (equal != null) {
				
// ar iteratoru tiek apstaigaata visa kopa atrodot veertiibu, peec parametra nosaukuma
				do {
					JsonNode element = list.next();
					
//ja tiek atrasts parametrs, tad taa veertiiba tiek paarveidota par string veertiibu bez peedinaam
					if ( element.path( equalParameter ).toString().replaceAll("\"", "")
							
							.equals(equal) ) {

//ja nav veertiibas, tad to aizvieto ar 0, lai var siis veertiibas izmantot grafika ziimeesanaa 						
						if ( element.path( parameter ).toString().replaceAll("\"", "") == null 
								
								|| element.path( parameter ).toString().equals("") ) {
							
							value[i] = "0";
							
						} else {
							
							value[i] = element.path( parameter ).toString()
									.replaceAll("\"", "");
						}
						break;
					}
					
//kameer visi parametri nav apstaigaati tiek turpinaata datu mekleesana un nolasiisana		
					
				} while (list.hasNext());
				
//ja ir vajadziigi visi parametri ar vienu nosaukumu, tiek veiktas taas pasas darbiibas, tikai bez saliidzinaasanas ar konkreetu parametru				
			} else {
				
// ar iteratoru tiek apstaigaata visa parametru kopa
				do {
					JsonNode element = list.next();
			
//kaa jau bija iepreiks, visus rezultaatus nolasa un paarveido par string veertiibu bez peedinaam					
						value[i] = element.path(parameter).toString()
								.replaceAll("\"", "");
						
						i++;

					} while (list.hasNext());
			}
			
//tiek atgriezts veertiibu masiivs			
			return value;
		}
		
//nosaka Json parametru skaitu
		public static int getCount(String entityString)
		
				throws IOException{
			
			int count = 0;
			
			String path = "result";
			
			ObjectMapper mapper = new ObjectMapper();

			JsonNode rootNode = mapper.readValue(entityString, JsonNode.class);

// kopas atzarojums ar parametriem
			JsonNode resultNode = rootNode.path(path);

// paarstaigaasana ar iteratoru
			Iterator<JsonNode> list = resultNode.elements(); // getElements()

// ar iteratoru tiek apstaigaata visa tikai noskaidrojot visu paramteru kopiigo skaitu
			do {
				
				JsonNode element = list.next();
				
				 count++;
				
				} while (list.hasNext());
	
//tiek izdots parametru skaits			
			return count;
		} 
		
//ieguust vienumu veestures datus atbilstosi laika pluusmai
		public static ItemHistory getItemHistory( String entityString, int valueCount, String equal)
		
				throws IOException{

			ItemHistory history = new ItemHistory();//= new ItemHistory[ itemCount ];
			
			history.makeArray( valueCount );
			
			String path = "result";
//parametri			
			String itemID = "itemid";
			String itemData = "value";
			String itemClock = "clock";
			
//tiek sagatavota klase datu un laika glabaasanai
			
				ObjectMapper mapper = new ObjectMapper();

				JsonNode rootNode = mapper.readValue(entityString, JsonNode.class);

// kopas atzarojums ar resursdatoru sarakstu
				JsonNode resultNode = rootNode.path(path);

// paarstaigaasana ar iteratoru
				Iterator<JsonNode> list = resultNode.elements(); 

				int i = 0;
				
// ar iteratoru tiek apstaigaata visa mekleejot parametrus
				do {
					JsonNode element = list.next();
			
//paarbaude, lai nav iecikleesanaas parametru mekleesanaa					
					if ( i >= valueCount) {
						
						break;
						
//atbilstosajam resursdatora vienumam nolasa laiku un tajaa briidii nolasiito veertiibu						
					} else if ( element.path( itemID ).toString()
//							
							.replaceAll("\"", "").equals(equal) ) {
						
						history.setValue( element.path( itemData ).toString()
//								
								.replaceAll("\"", ""), i );
						
						history.setClock( element.path( itemClock ).toString()
//								
								.replaceAll("\"", ""), i );
						
						i++;
					} else continue; //paarlec paari citu vieniibu datiem
					
					} while (list.hasNext());

//tiek atdota resursdatoru vienumu dati, kuri ir saglabaati ItemHistory objektos				
			return history;
		}
		
//ja gadaas tuksaas veertiibas, tad taas tiek aizpildiitas ar "0", lai var sekmiigi uzziimeet grafiku		
	public static ItemHistory[] nullToZero(ItemHistory[] history, int valueCount) {
		
		if (history == null) {
			return history;
		}

		for (int i = 0; i < history.length; i++) {
			for (int j = 0; j < valueCount; j++) {

				if ( history[i].getClock(j) == null ) {

					history[i].setClock( "0" , j);
					history[i].setValue( "0" , j);
				}
			}
		}
		return history;
	}

//izveelne pie kuras tiek nospiezot telefona izveelnes pogu
//sai lietotnei nav lielas noziimes gar izveelni, taapeec taa paliek ar tuksam veertiibaam	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);

		return true;
	}
}
