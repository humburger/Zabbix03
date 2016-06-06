package com.example.zabbix02;

import java.util.concurrent.ExecutionException;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

//uzziimee izveeleeto grafiku
public class GraphActivity extends Activity {

	private String[] itemColor = null;
	private int num = 0;

	//tiek jau uzreiz padots bez veertiibaam grafika ziimeesanai
	private GraphViewData[] data = null;

	private GraphViewDataInterface value = null;
	private GraphViewSeries[] GraphViewSeries = null;
	private GraphView graphView = null;
	private Handler handler;
	private String label;
	private String[] itemName = null;
	private String[] itemClock = null; //veertiiba kaa taa ir vajadziiga
	private int[] itemLimitInt = null;
	private String auth;
	private String zabbixApiUrl;
	private String hostName;
	private Context context;
	private Activity activity;
	private String[] newData = null;
	private AsyncTask<Void, Void, ItemHistory[]> getHistory;
	private int[] itemDelayInt;
	private int itemCount;
	private Object[] objectArray;
	private int valueCount;
	private int zero = 0;
	private int valueInt;
	private String valueString;
	private int itemDelayMin;
	private ItemHistory[] history = null;
	private ItemHistory[] appendHistory = null;

	//vieta, no kurienes saak raadiit grafiku
	private int graphStart;

	//veertiibu skaits, ko uzraada grafikaa skataa
	private int graphSize = 150;

	//grafika atjaunosanas uzdevums	(pavediens)
	private Thread drawGraphTask;

	//karodzins(flag) fona uzdevuma paartrauksanai
	private int flag = 0;
	private int one = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_graph);

//veertiibu sanemsana no fona uzdevuma

		Intent intent = getIntent();

		auth = intent.getExtras().getString("auth");

		zabbixApiUrl = intent.getExtras().getString("zabbixApiUrl");

		hostName = intent.getExtras().getString("hostName");

		itemCount = intent.getExtras().getInt("itemCount");

		valueCount = intent.getExtras().getInt("valueCount");

		label = intent.getExtras().getString("label");

//aktivitaates loga nosaukuma maina		
		getActionBar().setTitle(hostName);

		itemColor = intent.getExtras().getStringArray("itemColor");

		itemName = intent.getExtras().getStringArray("itemName");

		itemClock = intent.getExtras().getStringArray("itemClock");

		itemLimitInt = intent.getExtras().getIntArray("itemLimitInt");

		itemDelayInt = intent.getExtras().getIntArray("itemDelayInt");

//objekta deserializeesana		
		objectArray = (Object[]) intent.getExtras().getSerializable("history");

		if (objectArray != null) {

			history = new ItemHistory[objectArray.length];

			for (int i = 0; i < objectArray.length; i++) {

				history[i] = (ItemHistory) objectArray[i];
			}
		}

//atrod mazaako laika intervaalu datu ievaaksanai, lai var preciizaak ieguut datus grafiku vienumiem, kuru datu atjaunosana ir aatraaka par paareejiem vienumiem		
		itemDelayMin = itemDelayInt[zero];

		for (int i = zero + 1; i < itemDelayInt.length; i++) {

			if (itemDelayMin > itemDelayInt[i])

				itemDelayMin = itemDelayInt[i];
		}


		for (int i = 0; i < itemCount; i++) {

			itemColor[i] = "#" + itemColor[i];

		}

		num = valueCount;

		graphView = new LineGraphView(this, label); //conteksts un grafika nosaukums

		GraphViewSeries = new GraphViewSeries[itemCount];

//uzziimee grafiku ar visaam esosajaam veertiibaam

		history = LogInActivity.nullToZero(history, valueCount);

		for (int i = 0; i < itemCount; i++) {

			data = new GraphViewData[num];

			for (int j = 0; j < valueCount; j++) {

				if (history != null) {
					data[j] = new GraphViewData(

//buutu jaaliek laiku, bet ne taa notiek datu atteelosana
							j

//							Double.parseDouble( itemClock [ j ] )

							, Double.parseDouble(history[i].getValue(j)));
				} else {
					//ja nav vertibas
					data[j] = new GraphViewData(

//buutu jaaliek laiku, bet ne taa notiek datu atteelosana
							j

//							Double.parseDouble( itemClock [ j ] )

							, Double.parseDouble(String.valueOf(zero)));
				}


				GraphViewSeries[i] = new GraphViewSeries(itemName[i],

						new GraphViewSeriesStyle(Color.parseColor(itemColor[i]), 3), data);
			}
		}

//prieks veestures atjaunosanas		
		valueCount = 1;

//grafiku liiknu pieskirsana grafiskajam skatam		
		for (int i = 0; i < itemCount; i++) {

			graphView.addSeries(GraphViewSeries[i]);
		}

//!!!unix timestamp convertation to time!!!	
//		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
//			  @Override
//			  public String formatLabel(double value, boolean isValueX) {
//
//				  if ( isValueX ) {
//
//					  	valueInt = (int) value; //double -> int
//
////					  	Log.e("valueInt = ", valueInt + "");
//
//					  	valueString = MainActivity.convertTimestamp( valueInt, "yyyy-MM-dd HH:mm" );
//
//			            return valueString;
//
//			        } else {
//
//			            return null;
//			        }
//			  }
//			});

		graphStart = data.length - graphSize;

		graphView.setViewPort(graphStart, graphSize);

//ir iespeejams paarskatiit vecaakas veertiibas, kaa arii mainiit grafika skata izmeerus		
		graphView.setScrollable(true);
		graphView.setScalable(true);

//raada apziimeejumus, bet telefonam ir par mazu ekraans
//		graphView.setShowLegend(true);

//apziimeejumu izvietojums	
		graphView.setLegendAlign(LegendAlign.BOTTOM);

//apziimeejumu platiibas izmeers		
		graphView.getGraphViewStyle().setLegendWidth(500);

		graphView.getGraphViewStyle().setVerticalLabelsWidth(150);

//x ass prezenteejoso veertiibu daudzums		
		graphView.getGraphViewStyle().setNumHorizontalLabels(5);

//laiku veertiibu ass		
//		graphView.setHorizontalLabels(itemClock);

//skata izkaartojuma sagatavosana grafika ziimeesanai
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);

//grafika ievietosana skata izkaartojumaa		
		layout.addView(graphView);

//tiek sagatavots manipulators darbam ar jaunu fona processu
		handler = new Handler();

		drawGraph(graphView);
	}

	public void drawGraph(View view) {

// jauns process galvenajaa processaa	
//		Task drawGraphTask = new Task();

		Thread drawGraphTask = new Thread(new Task());

		drawGraphTask.start();
		//drawGraphTask.interrupt();    //https://www.securecoding.cert.org/confluence/display/java/THI05-J.+Do+not+use+Thread.stop()+to+terminate+threads
	}

	//
// jauna processa klase
	class Task implements Runnable { //Runnable ir izpildaamaa komanda

		@Override
		public void run() {

			int i = num;

			while (true) {

				if (flag == 0) {

					i++;

					final int ii = i;

					try {

						// ik peec noteikta laika tiek pieskirta jauna veetiiba
						Thread.sleep(itemDelayMin * 1000);

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					handler.post(new Runnable() {

						//tiek uzsaakta reaalaa laika grafika ziimeesana
						@Override
						public void run() {

							//ik peec noteikta laika grafiks tiek papildinaats ar jaunaam veertiibaam
							getHistory = new AsyncTaskGetHistory(activity, context, auth, zabbixApiUrl,

									hostName, label).execute();
							try {

								history = new ItemHistory[itemCount];

								history = getHistory.get();

							} catch (InterruptedException e) {

								e.printStackTrace();

							} catch (ExecutionException e) {

								e.printStackTrace();
							}

//noveers null veertiibu probleemu									
							history = LogInActivity.nullToZero(history, valueCount);

							for (int i = 0; i < itemCount; i++) {

								data = new GraphViewData[num];

								if (history != null) {
									value = new GraphViewData(ii, Double.parseDouble(history[i].getValue(zero)));
								} else {
									//ja nav vertibas
									value = new GraphViewData(ii, Double.parseDouble(String.valueOf(zero)));
								}

								GraphViewSeries[i].appendData(value, true, num);
							}
						}
					});

				} else {
					//stopThread(getHistory);
					stopThread(drawGraphTask);
					break;
				}
			}
		}
	}

	//uzdevuma apstaadinaasana
	private synchronized void stopThread(Thread theThread) {
		//theThread.cancel(true);
		if (theThread != null) {
			theThread = null;
		}
	}

	//back poga
	@Override
	public void onBackPressed() {

//indikators fona uzdevuma cikla paartrauksanai		
		flag = 1;

		super.onBackPressed();

		this.finish();
	}

}
