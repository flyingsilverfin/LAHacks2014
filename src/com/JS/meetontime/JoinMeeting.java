package com.JS.meetontime;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinMeeting extends Activity {
	
	private static final String TAG = "JoinMeeting";

	private Button mMeetingConnectButton;
	private EditText mMeetingIdInput;
	
	private Networking mNetworking;
	private DatabaseBuilder mDbBuilder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_meetup);

		mDbBuilder = DatabaseBuilder.getDatabaseBuilder(getApplicationContext());
		mNetworking = new Networking(getApplicationContext());
		
		mMeetingConnectButton = (Button) findViewById(R.id.meetingConnectButton);
		mMeetingIdInput = (EditText) findViewById(R.id.meetingIdValue);
		
		mMeetingConnectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int meetingId;
				try {
					meetingId = Integer.parseInt(mMeetingIdInput.getText().toString());
				} catch (NumberFormatException e) {
					Toast.makeText(getApplicationContext(), "Invalid number", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (mNetworking.isOnline()) {
					mNetworking.joinMeetup(meetingId, new joinMeetupCallback());
				} else {
					//for now, intending to make offline save-able
					Toast.makeText(getApplicationContext(), "No networking connection...", Toast.LENGTH_SHORT).show();
				}
								
			}
		});
		
		
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_meeting, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_join_meetup,
					container, false);
			return rootView;
		}
	}

	
	protected void onDestroy() {
		mDbBuilder.write();
		super.onDestroy();

	}
	
	/*
	 * -----------------callbacks-----------------
	 */
	
	class joinMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			//eventId=...&eventname=&hostId=...&hostName=...&lat=&long=&datetime=&inviteds=...&invitedsNames=...&invitedsStatuses=...&invitedsRatings=...
			
			Log.i(TAG, "in join meetup callback");
			
			String[] data = res.split("&");
			int eventId = Integer.parseInt(data[0].split("=")[1]);
			String eventName = data[1].split("=")[1];
			String hostId = data[2].split("=")[1];	//this is the difference from above!
			String hostName = data[3].split("=")[1];	//and this
			String lat = data[4].split("=")[1];
			String lng = data[5].split("=")[1];
			String datetime = data[6].split("=")[1];
			ArrayList<String> inviteds = Helper.parseToStringList(data[7].split("=")[1], ";");
			ArrayList<String> invitedsNames = Helper.parseToStringList(data[8].split("=")[1], ";");
			ArrayList<String> invitedsStatuses = Helper.parseToStringList(data[9].split("=")[1], ";");
			ArrayList<Float> invitedsRatings = Helper.parseToFloatList(data[10].split("=")[1], ";");

			Meetup m = new Meetup(eventId, eventName, hostId, hostName, lat, lng, 
					datetime, inviteds, invitedsNames, invitedsStatuses, invitedsRatings, getApplicationContext());	
			m.setTransmitted(eventId);
			
			boolean goodResult = mDbBuilder.addMeetup(m);
			if (goodResult){
				Toast.makeText(getApplicationContext(), "Added to your meetups", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(getApplicationContext(), "Meetup " + eventId + " already exists", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
}

