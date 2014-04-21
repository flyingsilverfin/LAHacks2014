package com.JS.meetontime;

import java.util.ArrayList;

import android.widget.Toast;

public class tmp {
	/*
	class registerUserCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			Toast.makeText(getApplicationContext(), "Registered on server!", Toast.LENGTH_SHORT).show();
		}
	}

	class updateUserRegistrationCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			Toast.makeText(getApplicationContext(), "Updated registration", Toast.LENGTH_SHORT).show();
		}
	}
	class newMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			//eventId=...&eventname=&hostId=...&hostName=...&lat=&long=&datetime=&inviteds=...&invitedsNames=...&invitedsStatuses=...&invitedsRatings=...
			String[] data = res.split("&");
			int eventId = Integer.parseInt(data[0].split("=")[1]);
			String eventName = data[1].split("=")[1];
			String hostId = data[2].split("=")[1];	//this is the difference from above!
			String hostName = data[3].split("=")[1];	//and this
			String lat = data[4].split("=")[1];
			String lng = data[5].split("=")[1];
			String datetime = data[6].split("=")[1];
			ArrayList<String> inviteds = Helper.parseToStringList(data[7].split("=")[1], ";");
			ArrayList<String> invitedsNames = Helper.parseToStringList(data[8].split("=")[1], ";");
			ArrayList<String> invitedsStatuses = Helper.parseToStringList(data[9].split("=")[1], ";");
			ArrayList<Integer> invitedsRatings = Helper.parseToIntegerList(data[10].split("=")[1], ";");
			
			Meetup m = new Meetup(eventId, eventName, hostId, hostName, lat, lng, 
					datetime, inviteds, invitedsNames, invitedsNames, invitedsStatuses, invitedsRatings);
			
			DbBuilder.addMeetup(m);
		}
	}

	class joinMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			//eventId=...&eventname=&hostId=...&hostName=...&lat=&long=&datetime=&inviteds=...&invitedsNames=...&invitedsStatuses=...&invitedsRatings=...
			String[] data = res.split("&");
			int eventId = Integer.parseInt(data[0].split("=")[1]);
			String eventName = data[1].split("=")[1];
			String hostId = data[2].split("=")[1];	//this is the difference from above!
			String hostName = data[3].split("=")[1];	//and this
			String lat = data[4].split("=")[1];
			String lng = data[5].split("=")[1];
			String datetime = data[6].split("=")[1];
			ArrayList<String> inviteds = Helper.parseToStringList(data[7].split("=")[1], ";");
			ArrayList<String> invitedsNames = Helper.parseToStringList(data[8].split("=")[1], ";");
			ArrayList<String> invitedsStatuses = Helper.parseToStringList(data[9].split("=")[1], ";");
			ArrayList<Integer> invitedsRatings = Helper.parseToIntegerList(data[10].split("=")[1], ";");

			Meetup m = new Meetup(eventId, eventName, hostId, hostName, lat, lng, 
					datetime, inviteds, invitedsNames, invitedsStatuses, invitedsRatings, getApplicationContext());	
			
			DbBuilder.addMeetup(m);
		}
	}

	class rsvpMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			//res = "eventId=000"
			String[] idSet = res.split("=");
			int eventId = Integer.parseInt(idSet[1]);
			DbBuilder.rsvpMeetup(eventId);
		}
	}

	class updateMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			//eventId=...&eventname=&hostId=...&hostName=...&lat=&long=&datetime=&inviteds=...&invitedsNames=...&invitedsStatuses=...&invitedsRatings=...
			String[] data = res.split("&");
			int eventId = Integer.parseInt(data[0].split("=")[1]);
			String eventName = data[1].split("=")[1];
			String hostId = data[2].split("=")[1];	//this is the difference from above!
			String hostName = data[3].split("=")[1];	//and this
			String lat = data[4].split("=")[1];
			String lng = data[5].split("=")[1];
			String datetime = data[6].split("=")[1];
			ArrayList<String> inviteds = Helper.parseToStringList(data[7].split("=")[1], ";");
			ArrayList<String> invitedsNames = Helper.parseToStringList(data[8].split("=")[1], ";");
			ArrayList<String> invitedsStatuses = Helper.parseToStringList(data[9].split("=")[1], ";");
			ArrayList<Integer> invitedsRatings = Helper.parseToIntegerList(data[10].split("=")[1], ";");

			Meetup m = new Meetup(eventId, eventName, hostId, hostName, lat, lng, 
					datetime, inviteds, invitedsNames, invitedsStatuses, invitedsRatings, getApplicationContext());	
			
			DbBuilder.updateMeetup(m);

		}
	}
	*/
}
