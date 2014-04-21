package com.JS.meetontime;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

public class UserProfileActivity extends Activity {
	
	public static final String TAG = "UserProfileActivity";
	
	
	private TextView userName;
	SharedPreferences sharedPref;
	
	private DatabaseBuilder mDatabaseBuilder;
	
	private ArrayList<Meetup> mMeetupList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		sharedPref = getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		String username = sharedPref.getString("userName", "NA");
		userName = (TextView) findViewById(R.id.userNameProfile);
		userName.setText(username);
		
		Log.i(TAG,"In userprofileActivity");
		
		//mDatabaseBuilder = new DatabaseBuilder(mMeetupList);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		buildAllMeetups();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_user_profile,
					container, false);
			return rootView;
		}
	}

	
	
	public void buildAllMeetups() {
		String primKeys = sharedPref.getString("meetups", "");
		Log.i(TAG, "primeKeys: " + primKeys);
		String userId = sharedPref.getString("userNumber", "");
		String[] primaryKeys = primKeys.split(",");
		for (String s : primaryKeys) {
			System.out.println(s);
		}
		
		//passed in in case the one you get is one you're hosting. Used to make you host
		String userName = sharedPref.getString("userName", "");
		
		for (String primaryKey : primaryKeys) {
			Log.i(TAG, "primaryKey " + primaryKey);
			//mDatabaseBuilder.build(Integer.parseInt(primaryKey), userId,  userName);
		}
		Log.i(TAG,"Completed submitting rebuilding tasks to Async");
	}
}
