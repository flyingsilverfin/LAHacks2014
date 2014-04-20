package com.JS.meetontime;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;


/*
 * THERE SHOULD ONLY EVERY BE 1 INSTANCE OF THIS!!!
 */
public class DatabaseBuilder {
	public static final String TAG = "MeetupBuilder";

	private static final String mMeetupsFile = "meetupsFile";
	
	private Gson gson;
	private Context mContext;
	
	private ArrayList<Meetup> mMeetups;
	
	public DatabaseBuilder(Context context) {
		mContext = context;
		
		mMeetups = getMeetups();		
	}
	
	

	//add new meetup to database
	//either one you're hosting or one you're joining
	public synchronized void addMeetup(Meetup meetup) {
		ArrayList<String> meetups = Helper.readFile(mContext, Helper.meetupFile);
		
		//since a new meetup we just made won't have the unique Id yet
		//if it's not transmitted then check by hostId, latitude, longitude, and datetime
		
		if (meetup.isTransmitted()) {
			if (jsonIndexWithIntValue(meetups, "mEventId", meetup.getEventId()) != -1) {
				Log.e(TAG, "Event with Id " + meetup.getEventId() + " already exists in database!");
				return;
			}
		}
		else {
			
			//Check if there's a json string with all of these properties and all those values
			if ( jsonIndexWithStringValues(meetups, new String[] {"mHostId", "mLat", "mLong", "mDate"},
					new String[] {meetup.getHostId(),  meetup.getLat(), meetup.getLong(), gson.toJson(meetup.getDate())}) != -1
					) {
				Log.e(TAG, "Untransmitted event hosted by " + meetup.getHostId() + " already exists in database!");
				return;
			}
		}
		
		String jsonifiedMeetup = gson.toJson(meetup);
		Helper.appendToFile(mContext, Helper.meetupFile, jsonifiedMeetup);		
	}
	
	
	
	//this technically just replaces the right meetup with the updated object's JSON
	//might updating: isTransmitted, RSVP'd, statuses, inviteds, etc
	//to save data, should save a timestamp of last time it was updated here
	//compare to timestamp of updates on server
	//if different, call this function
	public synchronized void updateMeetup(Meetup meetup) {
		ArrayList<String> meetups = Helper.readFile(mContext, Helper.meetupFile);
		
		int index;
		
		if (meetup.isTransmitted()) {
			index = jsonIndexWithIntValue(meetups, "mEventId", meetup.getEventId());
			if (index == -1) {
				Log.e(TAG, "This event doesn't exist yet... aborting update");
				return;
			}
		}
		else {
			index = jsonIndexWithStringValues(meetups, new String[] {"mHostId", "mLat", "mLong", "mDate"},
					new String[] {meetup.getHostId(),  meetup.getLat(), meetup.getLong(), gson.toJson(meetup.getDate())});
			if (index == -1) {
				Log.e(TAG, "This event doesn't exist yet... aborting update");
				return;
			}
		}
		String updatedJson = gson.toJson(meetup);
	
		meetups.add(index, updatedJson); //overwrite that meetup
		
		Helper.writeFile(mContext, Helper.meetupFile, meetups);
	}
	
	
	
	//another part of the program will check each meetup to see if it still exists on server
	//if not, pass here to remove from database
	public synchronized void removeMeetup(Meetup meetup) {
		ArrayList<String> meetups = Helper.readFile(mContext, Helper.meetupFile);
		
		int index;
		
		if (meetup.isTransmitted()) {
			index = jsonIndexWithIntValue(meetups, "mEventId", meetup.getEventId());
			if (index == -1) {
				Log.e(TAG, "This event doesn't exist... aborting update");
				return;
			}
		}
		else {
			index = jsonIndexWithStringValues(meetups, new String[] {"mHostId", "mLat", "mLong", "mDate"},
					new String[] {meetup.getHostId(),  meetup.getLat(), meetup.getLong(), gson.toJson(meetup.getDate())});
			if (index == -1) {
				Log.e(TAG, "This event doesn't exist... aborting update");
				return;
			}
		}
		
		meetups.remove(index);
		Helper.writeFile(mContext, Helper.meetupFile, meetups);		
	}
	
	
	/*
	 * This function is to get the copy of the meetups off the disk
	 */
	private synchronized ArrayList<Meetup> getMeetups() {
		ArrayList<String> meetups = Helper.readFile(mContext, Helper.meetupFile);
		ArrayList<Meetup> meetupList = new ArrayList<Meetup>();
		
		for(String s: meetups) {
			meetupList.add(gson.fromJson(s, Meetup.class));
		}
		
		return meetupList;
	}
	
	/*
	 * retrieve ArrayList of meetups that you have stored in your database
	 * must call this manually every time using one of above functions to update database
	 */
	public synchronized ArrayList<Meetup> retrieveMeetups() {
		return mMeetups;
	}
	
	
	/*
	 * simplifies code needed later
	 * just call these and submit them through networking class
	 */
	public synchronized ArrayList<Meetup> retrieveUnsubmittedMeetups() {
		ArrayList<Meetup> unsubmitted = new ArrayList<Meetup>();
		for (int i = mMeetups.size() -1; i >= 0; i--) {
			if (!mMeetups.get(i).isTransmitted()) {
				unsubmitted.add(mMeetups.get(i));
			}
		}
		
		return unsubmitted;
	}
	
	
	
	/*
	 * Meetup searchers
	 */
	
	private boolean has
	
	/*
	 * 
	 * Json Helpers
	 * 
	 * do these need to be synchronized???
	 * 
	 */
	private boolean jsonHasStringValue(String json, String property, String value) {
		int loc = json.indexOf(property);
		int start = Helper.findNextIndexOf(json, loc, ":");
		int end = Helper.findNextIndexOf(json, start, ","); //since we're looking for a string, this includes something like "hello" with quotes
		String val = json.substring(start+2, end-1); //+'s and -'s cut off quotes
		if (val.equals(value)) {
			return true; //found it!
		}
		return false;
	}
	
	private boolean jsonHasIntValue(String json, String property, int value) {
		int loc = json.indexOf(property);
		int start = Helper.findNextIndexOf(json, loc, ":");
		int end = Helper.findNextIndexOf(json, start, ","); //since we're looking for a string, this includes something like "hello" with quotes
		String val = json.substring(start+1, end); //+'s and -'s cut off quotes
		if (val.equals(value)) {
			return true; //found it!
		}
		return false;
	}
	
	
	private int jsonIndexWithStringValue(ArrayList<String> meetupsJson, String property, String value) {
		for (int i = 0; i < meetupsJson.size(); i++) {
			if (jsonHasStringValue(meetupsJson.get(i), property, value)) {
				return i;
			}
		}
		return -1;
	}
	
	private int jsonIndexWithStringValues(ArrayList<String> meetupsJson, String[] properties, String[] values) {
		boolean hasAll = true;
		for (int i = 0; i < meetupsJson.size(); i++) {
			//to test for all the properties, assume all are there, if one isn't, check next json string
			hasAll = true;
			for (int j = 0; j < properties.length; j++) {
				if (!jsonHasStringValue(meetupsJson.get(i), properties[j], values[j])) {
					hasAll = false;
					break;
				}
			}
			if (hasAll) {
				return i;
			}
		}
		return -1;
	}
	
	private int jsonIndexWithIntValue(ArrayList<String> meetupsJson, String property, int value) {
		for (int i = 0; i < meetupsJson.size(); i++) {
			if (jsonHasIntValue(meetupsJson.get(i), property, value)) {
				return i;
			}
		}
		return -1;
	}
}
