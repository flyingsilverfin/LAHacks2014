package com.JS.meetontime;

import java.util.Calendar;
import java.util.Date;



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
	

	class goingToMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			String[] idSet = res.split("=");
			int eventId = Integer.parseInt(idSet[1]);
			DbBuilder.goingToMeetup(eventId);
		}
	}
	
	class arrivedAtMeetupCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			String[] idSet = res.split("=");
			int eventId = Integer.parseInt(idSet[1]);
			DbBuilder.arrivedAtMeetup(eventId);
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
	
	
	
	
	/*
	 * HERE'S THE PLAN:
	 * A meetup can only be created when there is a network connection (isOnline)
	 * When a meetup is created, the creating device sends the server the number of milliseconds
	 * from the current device time until the date/time of the event. The times will be restricted to 5 minute intervals
	 * The server the receives this millisecond time, adds it to its own current time, and creates a date/time
	 * which is rounded to the down to the next lower 5 minutes (given network latency, we should round down)
	 * This should always work (network latency should never be more than 5 minutes, so we should always get the right server date/time rounded down to closest 5 min)
	 * 
	 * So now the server has the target date/time based on it's own internal time
	 * When a invited person requests an event, we send back the remaining time in milliseconds
	 * That is, current target date/time in millis - current date/time in millis.
	 * The receiving device then add this difference to the current date/time in millis and converts back,
	 * rounding down to the closest 5 minutes!
	 */
	
	class syncTimeCallback implements AsyncResponseInterface {
		public void asyncCallback(String res) {
			
			//what comes back:
			//year=&month=&day=&hour=&min=
			 
			String[] data = res.split("&");
			int serverYear = Integer.parseInt(data[0].split("=")[1]);
			int serverMonth = Integer.parseInt(data[1].split("=")[1]);
			int serverDay = Integer.parseInt(data[2].split("=")[1]);
			int serverHour = Integer.parseInt(data[3].split("=")[1]);
			int serverMin = Integer.parseInt(data[4].split("=")[1]);
			
			Calendar cal = Calendar.getInstance();
			cal.set(serverYear,  serverMonth, serverDay, serverHour, serverMin);
			Date serverDate = cal.getTime();
			
			cal.clear();
			Date localDate = cal.getTime(); //hope this gets current local time
			
			long diff = serverDate.getTime() - localDate.getTime();
		
		}
	}
}

