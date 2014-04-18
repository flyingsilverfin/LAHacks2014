package com.JS.meetontime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Meetup{

	public static final String TAG = "Meetup"; 
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
	static SimpleDateFormat readableDateFormat  = new SimpleDateFormat("HH:mm, MM/dd/yyyy");

	private String mHostId;
	private String mLat;
	private String mLong;
	private String mName;
	private Date mDate;
	
	private boolean isHost;
	
	private ArrayList<String> mInviteds;
	private ArrayList<String> mInvitedsStatuses;
	

	public Meetup(String name, String host, String lat, String lng, String date) {
		isHost = false;
		mHostId = host;
		mName = name;
		mLat = lat;
		mLong = lng;
		try{
			mDate = dateFormat.parse(date);
		} catch (Exception e) {}
		
	}
	
	public Meetup(String name, String host, String lat, String lng, String date, ArrayList<String> inviteds, ArrayList<String> invitedsStatuses){
		isHost = true;
		mHostId = host;
		mName = name;
		mLat = lat;
		mLong = lng;
		mInviteds = inviteds;
		mInvitedsStatuses = invitedsStatuses;
		try {
			mDate = dateFormat.parse(date);
		} catch (Exception e) {}		
	}
	
	public String getLat() {
		return mLat;
	}
	
	public String getLong() {
		return mLong;
	}
	
	public String getFormattedDate() {
		return readableDateFormat.format(mDate);
	}
	
	public boolean youAreHost() {
		return isHost;
	}
	
}
