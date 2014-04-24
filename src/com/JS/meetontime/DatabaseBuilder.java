package com.JS.meetontime;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

/*
 * Just discovered singletons!!! Pretty awesome...
 */


public class DatabaseBuilder {
	public static final String TAG = "MeetupBuilder";

	private static final String mMeetupsFile = "meetupsFile";
	
	private Gson gson = new Gson();
	private Context mContext;
	
	private ArrayList<Meetup> mMeetups;
	
	private static DatabaseBuilder DbBuilderSingleton; 
	
	//SINGLETON
	private DatabaseBuilder(Context context) {
		mContext = context.getApplicationContext();
		mMeetups = getMeetups();		
	}
	
	//read online stuff to explain why need synchronized (tldr: totally prevents multiple objects, is thread safe)
	//could possibly be a lot slower... whatever
	public static synchronized DatabaseBuilder getDatabaseBuilder(Context context) {
		if (DbBuilderSingleton == null) {
			DbBuilderSingleton = new DatabaseBuilder(context);
		}
		return DbBuilderSingleton;
	}
	

	//add new meetup to database
	//either one you're hosting or one you're joining
	public synchronized boolean addMeetup(Meetup meetup) {
		//since a new meetup we just made won't have the unique Id yet
		//if it's not transmitted then check by hostId, latitude, longitude, and datetime
		if (meetup.isTransmitted()) {
			if (haveTransmittedMeetupWithId(meetup.getEventId())) {
				Log.e(TAG, "Event with Id " + meetup.getEventId() + " already exists in database!");
				return false;
			}
		}
		else {
			//here this means this is an unsubmitted new event without an unique id
			//modifications being made are locally only
			//check event id that is assigned locally
			if (haveUntransmittedMeetupWithid(meetup.getEventId())) {
				Log.e(TAG, "Untransmitted event with Id by " + meetup.getEventId() + " already exists in database!");
				return false;
			}
		}
		mMeetups.add(meetup);	
		return true;
	}
	
	
	
	//this technically just replaces the right meetup with the updated object's JSON
	//might updating: isTransmitted, RSVP'd, statuses, inviteds, etc
	//to save internet connection data, should save a timestamp of last time it was updated here
	//compare to timestamp of updates on server
	//if different, call this function with updated meetup
	public synchronized boolean updateMeetup(Meetup meetup) {
		int index;
		
		if (meetup.isTransmitted()) {
			index = findTransmittedMeetupWithId(meetup.getEventId());
			if (index == -1) {
				Log.e(TAG, "Event with Id " + meetup.getEventId() + " doesn't exist in database yet, cannot update it!");
				return false;
			}
		}
		else {
			index = findUntransmittedMeetupWithId(meetup.getEventId());
			//here this means this is an unsubmitted new event without an unique id
			//modifications being made are locally only
			//check event id that is assigned locally
			if (index == -1) {
				Log.e(TAG, "Untransmitted Event with Id " + meetup.getEventId() + " doesn't exist in database yet, cannot update it!");
				return false;
			}
		}
	
		mMeetups.add(index, meetup); //overwrite that meetup
		return true;
	}
	
	
	/*
	 * calling this means the meetup is already transmitted yay
	 */
	public synchronized boolean rsvpMeetup(int meetupId) {
		int index = findTransmittedMeetupWithId(meetupId);
		if (index == -1) {
			Log.e(TAG, "Event with Id " + meetupId  + "doesn't exist in database, cannot rsvp!");
			return false;
		}
		
		mMeetups.get(index).rsvp();
		return true;
	}
	
	
	
	//another part of the program will check each meetup to see if it still exists on server
	//if not, pass here to remove from database
	public synchronized boolean removeMeetup(Meetup meetup) {
		
		int index;
		
		if (meetup.isTransmitted()) {
			index = findTransmittedMeetupWithId(meetup.getEventId());
			if (index == -1) {
				Log.e(TAG, "Event with Id " + meetup.getEventId() + " doesn't exist in database yet, cannot delete it!");
				return false;
			}
		}
		else {
			index = findUntransmittedMeetupWithId(meetup.getEventId());
			//here this means this is an unsubmitted new event without an unique id
			//modifications being made are locally only
			//check event id that is assigned locally
			if (index == -1) {
				Log.e(TAG, "Untransmitted Event with Id " + meetup.getEventId() + " doesn't exist in database yet, cannot delete it!");
				return false;
			}
		}
		
		mMeetups.remove(index);
		return true;
	}
	
	
	/*
	 * This function is to get the copy of the meetups off the disk
	 * note: PRIVATE!!!
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
	 * Then actually have to delete the meetup using this copy 
	 * then re-add using addMeetup()
	 * because the eventId switches from a local to a server one
	 * the updateMeetup() function fails (ID has changed...)
	 */
	public synchronized ArrayList<Meetup> getUnsubmittedMeetups() {
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
	
	private boolean haveTransmittedMeetupWithId(int id) {
		for (Meetup m : mMeetups) {
			if (m.isTransmitted() && m.getEventId() == id) {
				return true;
			}
		}
		return false;
	}
	
	//check by the local temproary id's assigned
	private boolean haveUntransmittedMeetupWithid(int id) {
		for (Meetup m: mMeetups) {
			if (!m.isTransmitted() && m.getEventId() == id) {
				return true;
			}
		}
		return false;
	}
	
	private int findTransmittedMeetupWithId(int id) {
		for (int i = 0; i < mMeetups.size(); i++) {
			if (mMeetups.get(i).isTransmitted() && mMeetups.get(i).getEventId() == id) {
				return i;
			}
		}
		return -1;
	}
	
	private int findUntransmittedMeetupWithId(int id) {
		for (int i = 0; i < mMeetups.size(); i++) {
			if (!mMeetups.get(i).isTransmitted() && mMeetups.get(i).getEventId() == id) {
				return i;
			}
		}
		return -1;
	}

	
	public void write() {
		ArrayList<String> meetupJson = new ArrayList<String>();
		for (Meetup m : mMeetups) {
			meetupJson.add(gson.toJson(m));
		}
		
		Helper.writeFile(mContext, Helper.meetupFile, meetupJson);		
		Helper.printArrayList(meetupJson);
	}
	
	protected void finalize() {
		try{
			write();
			super.finalize();
		} catch(Throwable t){
		}
	}
}