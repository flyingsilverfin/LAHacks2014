package com.JS.meetontime;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class MeetupDetailActivity extends Activity {

	private Meetup mMeetup;
	private PeopleListAdapter mAdapter;
	private ArrayList<String[]> mPeopleInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meetup_detail);
		String jsonObj = getIntent().getStringExtra("JsonObj");
		Gson gson = new Gson();
		mMeetup = gson.fromJson(jsonObj, Meetup.class);	
		
		mPeopleInfo = mMeetup.getCollatedInvitedsInfo();
		mPeopleInfo.add(new String[] {"Drake Levy", "8589119111", "3.7", "rsvp"});
		mPeopleInfo.add(new String[] {"Drake Levy", "8589119111", "3.7", "rsvp"});
		mPeopleInfo.add(new String[] {"Drake Levy", "8589119111", "3.7", "rsvp"});
		mPeopleInfo.add(new String[] {"Drake Levy", "8589119111", "3.7", "rsvp"});
		mPeopleInfo.add(new String[] {"Drake Levy", "8589119111", "3.7", "rsvp"});

		mAdapter = new PeopleListAdapter(this, R.layout.person_list_element, mPeopleInfo);
		
		ListView peopleList = (ListView) findViewById(R.id.detail_activity_inviteds_list);
		peopleList.setAdapter(mAdapter);
		
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.detail_activity_map_fragment)).getMap();
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				Double.parseDouble(mMeetup.getLat()), Double.parseDouble(mMeetup.getLong())), (float) 13));
		
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meetup_detail, menu);
		return true;
	}

}
