package com.JS.meetontime;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

public class MeetupListActivity extends Activity {

	private static final String TAG = "MeetupListActivity";
	
	private ListView mListView;
	private MeetupListAdapter mAdapter;
	private ArrayList<Meetup> mMeetups;
	private Networking mNetworker;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meetup_list);
	
		mMeetups = DatabaseBuilder.getDatabaseBuilder(this).retrieveMeetups();
		if (mMeetups.size() != 0) {
			Meetup m = mMeetups.get(0);
			mMeetups.add(new Meetup(m.getEventId()+1,m.getEventName()+"1", m.getHostId(), m.getHostName(), m.getLat(), m.getLong(), "2014-04-2020:20:20", null, null, null, null, this));
			mMeetups.add(new Meetup(m.getEventId()+2,m.getEventName()+"2", m.getHostId(), m.getHostName(), m.getLat(), m.getLong(), "2014-04-2020:20:20", null, null, null, null, this));
			mMeetups.add(new Meetup(m.getEventId()+3,m.getEventName()+"3", m.getHostId(), m.getHostName(), m.getLat(), m.getLong(), "2014-04-2020:20:20", null, null, null, null, this));
			mMeetups.add(new Meetup(m.getEventId()+4,m.getEventName()+"4", m.getHostId(), m.getHostName(), m.getLat(), m.getLong(), "2014-04-2020:20:20", null, null, null, null, this));
			mMeetups.add(new Meetup(m.getEventId()+5,m.getEventName()+"5", m.getHostId(), m.getHostName(), m.getLat(), m.getLong(), "2014-04-2020:20:20", null, null, null, null, this));
		}
				
		mNetworker = new Networking(this);
		
		mAdapter = new MeetupListAdapter(this, R.layout.meetup_list_element, mMeetups, mNetworker);
		mListView = (ListView) findViewById(R.id.eventsListView);
		mListView.setAdapter(mAdapter);	
		
		
	}
	
	protected void onResume() {
		super.onResume();
		//update the checker as to where we are
		ContinuousNetworkChecker.getInstance(this).setStatusView(findViewById(R.id.networkStatusBar));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meetup_list, menu);
		return true;
	}

}
