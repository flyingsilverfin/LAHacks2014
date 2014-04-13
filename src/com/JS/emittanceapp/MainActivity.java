package com.JS.emittanceapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private LocatorClass locator;
	private double[] location;
	private Button newMeetingButton;
	private Button gpsCheckButton;
	private Button joinMeetingButton;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	public String userName;
	public String userNumber;
	final Context context = this;
	private EditText nameInput;
	private EditText phoneInput;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		if(sharedPref.getBoolean("firstTime", true)){
			promptUser();
		} else {
			userName = sharedPref.getString("userName", "userName");
			userNumber = sharedPref.getString("userNumber", "userNumber");
			editor.commit();
			Log.d(TAG, "Stored: " + userName + " " + userNumber);
		}
		
		locator = new LocatorClass(getApplicationContext());
		getLocation();
		
		gpsCheckButton = (Button) findViewById(R.id.gpsCheckButton);
		gpsCheckButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				getLocation();
		
			}
		});
		
		newMeetingButton = (Button) findViewById(R.id.newMeetingButton);
		newMeetingButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CreateMeetingActivity.class);
				startActivity(intent);
			}
		});
		
		joinMeetingButton = (Button) findViewById(R.id.joinMeetingButton);
		joinMeetingButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, JoinMeeting.class);
				startActivity(intent);
			}});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void getLocation(){
		try{
			if(locator.getLocation() != null){
				location = locator.getLocation();
				Log.d(TAG, "Latitude: " + location[0]);
				Log.d(TAG, "Longitude: " + location[1]);
			} else {
				Log.d(TAG, "Location null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void promptUser(){
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.prompt_layout, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		alertDialogBuilder.setView(promptView);
		
		nameInput = (EditText) promptView.findViewById(R.id.nameValue);
		phoneInput = (EditText) promptView.findViewById(R.id.phoneNumValue);
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					userName = nameInput.getText().toString();
					editor.putString("userName", userName);
					Log.d(TAG, "Name stored as: " + userName);
					
					userNumber = phoneInput.getText().toString();
					editor.putString("userNumber", userNumber);
					Log.d(TAG, "Phone Number stored as: " + userNumber);
					editor.putBoolean("firstTime", false);
					editor.commit();
					Log.d(TAG, "Commited userName, userNumber, and firstTime");
					
				}
			});
		
		AlertDialog alertD = alertDialogBuilder.create();
		
		alertD.show();
		
	}
	


}
