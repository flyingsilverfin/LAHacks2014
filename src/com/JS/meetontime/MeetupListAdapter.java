package com.JS.meetontime;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

public class MeetupListAdapter extends ArrayAdapter<Meetup> {

	private static final String TAG = "MeetupListAdapter";
	private Context mContext;
	private int mLayoutResId;
	private ArrayList<Meetup> mElements;
	private Networking mNetworker;

	public MeetupListAdapter(Context context, int layoutResId, ArrayList<Meetup> elements, Networking networker) {
		super(context, layoutResId, elements);
		mContext = context;
		mLayoutResId = layoutResId;
		mElements = elements;
		mNetworker = networker;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) { //need final int for mElements.get(position)
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = convertView;
		
		Log.i(TAG, "in getView!" + position);
		if (convertView == null) {
			rowView = inflater.inflate(mLayoutResId, parent, false);
		}
		
		//need to save this in case the mElements updates using an Async
		final Meetup m = mElements.get(position);
		Gson gson = new Gson();
		final String obj = gson.toJson(m);
		
		String datetime = m.getHumanDate();
		String hostName = m.getHostName();
		String eventName = m.getEventName();
		
		TextView event = (TextView) rowView.findViewById(R.id.meetupListMeetupName);
		TextView host = (TextView) rowView.findViewById(R.id.meetupListHostName);
		TextView time = (TextView) rowView.findViewById(R.id.meetupListTime);
		
		event.setText(position + ". " + eventName);
		host.setText(hostName);
		time.setText(datetime);
				
		rowView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MeetupDetailActivity.class);
				intent.putExtra("JsonObj", obj);
				mContext.startActivity(intent);
			}
		});
		
		Button statusButton = (Button) rowView.findViewById(R.id.meetupListStatusButton);
		if (m.isGoing()){
			Log.i(TAG, "\t is going");
			statusButton.setText("On my way");
			statusButton.setEnabled(false);
			statusButton.setBackgroundColor(Color.argb(0x33, 0x00, 0xFF, 0x00));
		}
		else if (m.isRsvp() && !m.isGoing()) {
			Log.i(TAG, "\t is rsvp'd");
			statusButton.setText("RSVP'd");
			statusButton.setBackgroundColor(Color.argb(0x44, 0xFF, 0x9D, 0x00));
		}
		//else just use the default
		
		/*
		 * button colors:
		 * before rsvp'ing: "#3ff0000"
		 * after rsvp'ing but not travelling yet: "#44FF9D00"
		 * when travelling:  "#3300FF00"
		 */
		
		statusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "clicked on position: " + position);

				
				Button b = (Button) v; //same as statusButton

				//if it's the 'on my way' button
				if (m.isRsvp()) {
					m.setGoing();
					b.setText("On my way");
					b.setEnabled(false);
					b.setBackgroundColor(Color.argb(0x33, 0x00, 0xFF, 0x00));
					mNetworker.comingToMeeting(m, new goingToMeetupCallback());
				}
				else {
					m.rsvp();
					b.setText("RSVP'd");
					b.setBackgroundColor(Color.argb(0x44, 0xFF, 0x9D, 0x00));
					mNetworker.rsvpMeetup(m, new rsvpMeetupCallback());
				}
			}
			
		});
			
	
		return rowView;
	}
	
	class rsvpMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
		//	res = "eventId=000";
			String[] idSet = res.split("=");
			int eventId = Integer.parseInt(idSet[1]);
			DatabaseBuilder.getDatabaseBuilder(mContext).rsvpMeetup(eventId);
		}
	}
	
	class goingToMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
		//	res = "eventId=000";
			String[] idSet = res.split("=");
			int eventId = Integer.parseInt(idSet[1]);
			DatabaseBuilder.getDatabaseBuilder(mContext).goingToMeetup(eventId);
		}
	}
}
