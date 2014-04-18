package com.JS.meetontime;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<Meetup> {

	private static final String TAG = "ListElementAdapter";
	private Context mContext;
	private int mLayoutResId;
	private ArrayList<Meetup> mElements;


	public MyArrayAdapter(Context context, int layoutResId, ArrayList<Meetup> elements) {
		super(context, layoutResId, elements);
		mContext = context;
		mLayoutResId = layoutResId;
		mElements = elements;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) { //need final int for mElements.get(position)
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mLayoutResId, parent, false);
		
		//
		// TODO
		//
		
		return rowView;
	}
	

}
