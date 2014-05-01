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
	private static SimpleDateFormat readableDateFormat  = new SimpleDateFormat("HH:mm, EEEE MMMM dd yyyy");

	private int mEventId;
	private String mEventName;
	private String mHostId;
	private String mHostName;
	private String mLat;
	private String mLong;
	private Date mDate;
	
	private boolean isHost;
	private boolean isTransmitted = false;
	private boolean isRsvp = false;
	private boolean isTravelling = false;
	
	private ArrayList<String> mInviteds;
	private ArrayList<String> mInvitedsNames;
	private ArrayList<String> mInvitedsStatuses;
	private ArrayList<Float> mInvitedsRatings;
	
	
	//currently unimplemented...
	private String mLastUpdated;	//to check for updates on the server
									//IMPORTANT: this time is always based on server... do not use local time otherwise will never match!
	

	//need Context to tell if you're hosting
	public Meetup(int eventId, String eventName, String hostId, String hostName, String lat, String lng, String datetime, 
			ArrayList<String> inviteds, ArrayList<String> invitedsNames, ArrayList<String> invitedsStatuses, ArrayList<Float> invitedsRatings,
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
		mInvitedsNames = invitedsNames;
		mInvitedsStatuses = invitedsStatuses;
		mInvitedsRatings = invitedsRatings;
		
	}

	public String getLat() {
		return mLat;
	}
	
	public String getLong() {
		return mLong;
	}
	
	public String getHumanDate() {
		return readableDateFormat.format(mDate);
	}
	
	public String getFormattedDate() {
		return dateFormat.format(mDate);
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
	
	public String getInvitedsNamesString() {
		StringBuilder names = new StringBuilder();
		for (String s : mInvitedsNames) {
			names.append(s);
			names.append(";");
		}
		
		names.deleteCharAt(names.length()-1);
		return names.toString();
	}
	
	public ArrayList<String[]> getCollatedInvitedsInfo() {
		ArrayList<String[]> info = new ArrayList<String[]>();
		for (int i = 0; i < mInviteds.size(); i++) {
			info.add(new String[] {mInvitedsNames.get(i), mInviteds.get(i),
					String.format("%.1f", mInvitedsRatings.get(i)), mInvitedsStatuses.get(i)});
		}
		return info;
	}
	
	public boolean isTransmitted() {
		return isTransmitted;
	}
	
	public boolean isRsvp() {
		return isRsvp;
	}
	
	public boolean isGoing() {
		return isTravelling;
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
	
	public void setInvitedsNames(ArrayList<String> names) {
		mInvitedsNames = names;
	}
	
	public void setInvitedsStatus(ArrayList<String> invitedsStatus) {
		mInvitedsStatuses = invitedsStatus;
	}
	
	public void setInvitedsRatings(ArrayList<Float> invitedsRatings) {
		mInvitedsRatings = invitedsRatings;
	}
	
	public void rsvp() {
		isRsvp = true;
	}
	
	public void setGoing() {
		isTravelling = true;
	}
	
}
