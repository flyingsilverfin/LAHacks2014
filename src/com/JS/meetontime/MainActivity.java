package com.JS.meetontime;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private LocatorClass locator;
	private double[] location;
	private Button newMeetingButton;
	private Button meetingCheckButton;
	private Button joinMeetingButton;
	private Button userSettingsButton;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	public String userName;
	public String userNumber;
	final Context context = this;
	private EditText nameInput;
	private EditText phoneInput;
	private TextView loggedInText;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedPref = getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		if(sharedPref.getBoolean("firstTime", true)){
			editor.putBoolean("isRegisteredOnServer", false);
			Helper.writeFile(getApplicationContext(), Helper.meetupFile, new ArrayList<String>());
			promptUser();
		} else {
			userName = sharedPref.getString("userName", "userName");
			userNumber = sharedPref.getString("userNumber", "userNumber");
			editor.commit();
			Log.d(TAG, "Stored: " + userName + " " + userNumber);
			logInText();
		}
		
		locator = new LocatorClass(getApplicationContext());
		getLocation();
		
		meetingCheckButton = (Button) findViewById(R.id.meetingCheckButton);
		meetingCheckButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
				startActivity(intent);
				Log.d(TAG, "Opening User profile activity");
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
		
		userSettingsButton = (Button) findViewById(R.id.userSettings);
		userSettingsButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				promptUser();
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
					userName = nameInput.getText().toString().replace(" ", ""); //remove spaces
					editor.putString("userName", userName);
					Log.d(TAG, "Name stored as: " + userName);
					
					userNumber = Helper.removeNonDigits(phoneInput.getText().toString());
					editor.putString("userNumber", userNumber);
					Log.d(TAG, "Phone Number stored as: " + userNumber);
					editor.putBoolean("firstTime", false);
					editor.commit();
					Log.d(TAG, "Commited userName, userNumber, and firstTime");
					logInText();
					
					Networking network = new Networking(getApplicationContext());
					network.registerUser(new registerUserCallback());
					
				}
			});
		
		AlertDialog alertD = alertDialogBuilder.create();
		
		alertD.show();
		
	}
	
	public void logInText(){
		loggedInText = (TextView)findViewById(R.id.loggedInText);
		loggedInText.setText(Helper.capsToSpaces(userName));
	}
	

	/*
	 * --------------------callbacks-----------------------
	 */
	
	class registerUserCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			Toast.makeText(getApplicationContext(), "Registered on server!", Toast.LENGTH_SHORT).show();
		}
	}

}
