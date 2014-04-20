package com.JS.meetontime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;


/*
 * 
 * Updated
 * 
 */
public class Meetup{

	public static final String TAG = "Meetup"; 
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
	private static SimpleDateFormat readableDateFormat  = new SimpleDateFormat("HH:mm, MM/dd/yyyy");

	private int mEventId;
	private String mEventName;
	private String mHostId;
	private String mHostName;
	private String mLat;
	private String mLong;
	private Date mDate;
	
	private boolean isHost;
	private boolean isTransmitted = false;
	private boolean hasRsvp = false;
	
	private ArrayList<String> mInviteds;
	private ArrayList<String> mInvitedsStatuses;
	private ArrayList<Integer> mInvitedsRatings;
	
	
	//also currently unused...
	private String mLastUpdated;	//to check for updates on the server
									//IMPORTANT: this time is always based on server... do not use local time otherwise will never match!
	

	//need Context to tell if you're hosting
	public Meetup(int eventId, String eventName, String hostId, String hostName, String lat, String lng, String datetime, 
			ArrayList<String> inviteds, ArrayList<String> invitedsStatuses, ArrayList<Integer> invitedsRatings,
			Context context) {
		
		if (Helper.getUserNumber(context).equals(hostId)) {
			isHost = true;
		}
		else {
			isHost = false;
		}
		
		mEventId = eventId;
		mEventName = eventName;
		mHostId = hostId;
		mHostName = hostName;
		mLat = lat;
		mLong = lng;

		try{
			mDate = dateFormat.parse(datetime);
		} catch (Exception e) {
			Log.i(TAG, "could not parse date from datetime string");
		}
		
		mInviteds = inviteds;
		mInvitedsStatuses = invitedsStatuses;
		mInvitedsRatings = invitedsRatings;
		
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
	
	public String getHostName() {
		return mHostName;
	}
	
	public String getHostId() {
		return mHostId;
	}
	
	public String getEventName() {
		return mEventName;
	}
	
	public int getEventId() {
		return mEventId;
	}
	
	public String getInvitedsString() {
		StringBuilder inviteds = new StringBuilder();
		
		for (String s : mInviteds) {
			inviteds.append(s);
			inviteds.append(";");
		}
		
		//remove the last comma we added but don't need
		inviteds.deleteCharAt(inviteds.length()-1);
		return inviteds.toString();			
	}
	
	public boolean isTransmitted() {
		return isTransmitted;
	}
	
	public Date getDate() {
		return mDate;
	}
	
	public String getLastUpdated() {
		return mLastUpdated;
	}
	
	
	
	public void setTransmitted(int eventId) {
		isTransmitted = true;
		mEventId = eventId;
	}
	
	public void setInviteds(ArrayList<String> inviteds) {
		mInviteds = inviteds;
	}
	
	public void setInvitedsStatus(ArrayList<String> invitedsStatus) {
		mInvitedsStatuses = invitedsStatus;
	}
	
	public void setInvitedsRatings(ArrayList<Integer> invitedsRatings) {
		mInvitedsRatings = invitedsRatings;
	}
	
	public void rsvp() {
		hasRsvp = true;
	}
	
	
	
}
