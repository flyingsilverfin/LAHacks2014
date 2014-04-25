package com.JS.meetontime;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MeetupListAdapter extends ArrayAdapter<Meetup> {

	private static final String TAG = "MeetupListAdapter";
	private Context mContext;
	private int mLayoutResId;
	private ArrayList<Meetup> mElements;
	private Meetup m;
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
		View rowView = inflater.inflate(mLayoutResId, parent, false);
		
		//need to save this in case the mElements updates using an Async
		m = mElements.get(position);
		Gson gson = new Gson();
		final String obj = gson.toJson(m);
		
		
		
		/*
		 * TODO
		 * 
		 * This is in here so the callback can edit the button!!!
		 * 
		 */

		
		class rsvpMeetupCallback implements AsyncResponseInterface {
			public void asyncCallback(String res) {
				res = "eventId=000";
				String[] idSet = res.split("=");
				int eventId = Integer.parseInt(idSet[1]);
				DatabaseBuilder.getDatabaseBuilder(mContext).rsvpMeetup(eventId);
			}
		}
		
		class goingToMeetupCallback implements AsyncResponseInterface {
			public void asyncCallback(String res) {
				res = "eventId=000";
				String[] idSet = res.split("=");
				int eventId = Integer.parseInt(idSet[1]);
				DatabaseBuilder.getDatabaseBuilder(mContext).goingToMeetup(eventId);
			}
		}
		
		String datetime = m.getHumanDate();
		String hostName = m.getHostName();
		String eventName = m.getEventName();
		
		TextView event = (TextView) rowView.findViewById(R.id.meetupListMeetupName);
		TextView host = (TextView) rowView.findViewById(R.id.meetupListHostName);
		TextView time = (TextView) rowView.findViewById(R.id.meetupListTime);
		
		event.setText(eventName);
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
		if (m.isRsvp()) {
			statusButton.setText("Coming");
		}
		
		/*
		 * button colors:
		 * before rsvp'ing: "#55ff0000"
		 * after rsvp'ing but not travelling yet: "#66FF9D00"
		 * when travelling:  "#5500FF00"
		 */
		
		statusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Button b = (Button) v; //same as statusButton

				//if it's the 'on my way' button
				if (m.isRsvp()) {
					m.setGoing();
					b.setText("Going");
					b.setEnabled(false);
					b.setBackgroundColor(Color.argb(0x55, 0x00, 0xFF, 0x00));
					mNetworker.comingToMeeting(m, new goingToMeetupCallback());
					/*
					 * TODO
					 * make the goingToEvent stuff
					 */
				}
				else {
					m.rsvp();
					b.setText("Coming");
					b.setBackgroundColor(Color.argb(0x66, 0xFF, 0x9D, 0x00));
					mNetworker.rsvpMeetup(m, new rsvpMeetupCallback());
				}
			}
			
			
		});
			

		
		return rowView;
	}
	

}
