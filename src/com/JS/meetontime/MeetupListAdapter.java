package com.JS.meetontime;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

public class MeetupListAdapter extends ArrayAdapter<Meetup> {

	private static final String TAG = "MeetupListAdapter";
	private Context mContext;
	private int mLayoutResId;
	private ArrayList<Meetup> mElements;


	public MeetupListAdapter(Context context, int layoutResId, ArrayList<Meetup> elements) {
		super(context, layoutResId, elements);
		mContext = context;
		mLayoutResId = layoutResId;
		mElements = elements;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) { //need final int for mElements.get(position)
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mLayoutResId, parent, false);		
		
		//need to save this in case the mElements 
		Meetup m = mElements.get(position);
		Gson gson = new Gson();
		final String obj = gson.toJson(m);
		
		String datetime = m.getHumanDate();
		String hostName = m.getHostName();
		String eventName = m.getHostName();
		
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
		
		
		
		return rowView;
	}
	

}
