package com.JS.emittanceapp;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	LocatorClass locator;
	double[] location;
	Button gpsCheck;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locator = new LocatorClass(getApplicationContext());
		getLocation();
		
		gpsCheck = (Button) findViewById(R.id.gpsCheckButton);
		
		gpsCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getLocation();
		
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void getLocation(){
		try{
			location = locator.getLocation();
			Log.d(TAG, "Latitude: " + location[0]);
			Log.d(TAG, "Longitude: " + location[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
