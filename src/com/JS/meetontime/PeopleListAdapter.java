package com.JS.meetontime;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PeopleListAdapter extends ArrayAdapter<String[]> {

	private static final String TAG = "PeopleListAdapter";

	private Context mContext;
	private int mLayoutResId;
	private ArrayList<String[]> mPeople;

	public PeopleListAdapter(Context context, int layoutResId,
			ArrayList<String[]> people) {
		super(context, layoutResId, people);

		mContext = context;
		mPeople = people;
		mLayoutResId = layoutResId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) { // need final int for mElements.get(position)
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = convertView;

		Log.i(TAG, "in getView!" + position);
		if (convertView == null) {
			rowView = inflater.inflate(mLayoutResId, parent, false);
		}

		String[] person = mPeople.get(position);

		TextView name = (TextView) rowView
				.findViewById(R.id.list_element_person_name);
		TextView number = (TextView) rowView
				.findViewById(R.id.list_element_person_number);
		TextView rating = (TextView) rowView
				.findViewById(R.id.list_element_person_rating_text);

		name.setText(Helper.capsToSpaces(person[0]));
		number.setText(Helper.formatPhoneNumber(person[1]));
		rating.setText(person[2]);

		// if (person[3] == "going") {
		// rowView.setBackgroundColor(Color.argb(0x33, 0x00, 0xFF, 0x00));
		// }
		// else if (person[3] == "rsvpd") {
		// rowView.setBackgroundColor(Color.argb(0x44, 0xFF, 0x9D, 0x00));
		// }
		return rowView;
	}

}
