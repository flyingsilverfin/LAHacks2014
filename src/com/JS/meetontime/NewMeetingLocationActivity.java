package com.JS.meetontime;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NewMeetingLocationActivity extends Activity {
	protected static final String TAG = "MeetingLocation";
	private GoogleMap map;
	private LatLng location;
	private Marker currentLatLng;
	private Button createMeetingButton;
	private ArrayList<String[]> checkedContacts = new ArrayList<String[]>();
	private String meetingName;
	private String meetingDateTime;
	private LocatorClass locator;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_meeting_location);
		Log.d(TAG, "Reached location onCreate");
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		checkedContacts = (ArrayList<String[]>) extras.get("checkedContacts");
		meetingName = (String) extras.getString("meetingName");
		meetingDateTime = (String) extras.getString("meetingDateTime");
		
		Log.d(TAG, "Meeting Name: " + meetingName + " Meeting Time: " + meetingDateTime);
		try{
			for(String[] a: checkedContacts){
				Log.d(TAG, a[0].toString() + " " + a[1].toString()); //check all checkedContacts got brought over to the new activity
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.locationMap)).getMap();
		
		locator = new LocatorClass(NewMeetingLocationActivity.this);
		if(locator.getLocation() != null && (locator.getLocation()[0] != 0.0 && locator.getLocation()[1] != 0.0)){
			double[] location = locator.getLocation();
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location[0], location[1]), (float) 10));
		} else {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.0704835, -118.4466106), (float) 10));
		}
		
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				map.clear();
				location = arg0;
				currentLatLng = map.addMarker(new MarkerOptions()
												.position(arg0)
												.draggable(true)
												.title("Lat: " + Double.toString(arg0.latitude) + " Lng: " + Double.toString(arg0.longitude)));
				Log.d(TAG, "Location: " + location.toString());
			}
			
		});
		
		map.setOnMarkerDragListener(new OnMarkerDragListener() 
		{

			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
				location = arg0.getPosition();
				Log.d(TAG, "Location: " + location.toString());
				
			}

			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub
				
			}});
		
		createMeetingButton = (Button) findViewById(R.id.createMeetingButton);
		createMeetingButton.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				
				String[] contacts = new String[checkedContacts.size()];
				for (int i = 0; i < checkedContacts.size(); i++) {
					contacts[i] = checkedContacts.get(i)[1];
				}
				
				Date d;
				try {
					d = HostMeetup.dateFormat.parse(meetingDateTime);
				} catch (Exception e) {
					d = new Date();
					return;
				}
				Log.i(TAG,meetingName + ", " + contacts + ", " + d + ",");
				
				//LatLng location = new LatLng(45.8757878, 67.87665);
				HostMeetup meet = new HostMeetup(String.format("%11.8f",45.8757878),
						String.format("%11.8f", 67.87665), meetingName, 
						contacts, d, getApplicationContext());
				
				meet.prepForAsync();
				/*
				Intent intent = new Intent(NewMeetingLocationActivity.this, MainActivity.class);
				intent.putExtra("checkedContacts", checkedContacts); 	//send checkedContacs to new intent
				intent.putExtra("latLng", location);					//send location to new intent
				intent.putExtra("meetingName", meetingName);
				intent.putExtra("meetingTime", meetingDateTime);
				startActivity(intent);
				*/
				
			}}
		);
		
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_meeting_location, menu);
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
			View rootView = inflater.inflate(
					R.layout.fragment_new_meeting_location, container, false);
			return rootView;
		}
	}
	
	
	

}
