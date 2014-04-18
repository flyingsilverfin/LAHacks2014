package com.JS.meetontime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class MeetupBuilder implements AsyncResponseInterface {

	
	
	public static final String TAG = "MeetupBuilder";
	static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-ddHH:mm:ss");
	public static final String baseUrl = "http://172.4.32.172:8001/";

	public static String[] getStrings = { "phoneId", "phoneName", "meetupName",
			"hostId", "meetupId", "isRegistering", "isNewMeetup", "isRsvp",
			"isLocCheckIn", "isInfoRequest", "peopleToInvite", "meetupLat",
			"meetupLong", "dateTime" };

	
	
	
	private String mHostId;
	private String mLat;
	private String mLong;
	private String mName;
	private Date mDate;
	
	private NetworkHandler netHandler;
	
	ArrayList<Meetup> mMeetups;
	
	public MeetupBuilder(ArrayList<Meetup> meetups) {
		mMeetups = meetups; 
	}
	
	private NetworkHandler newNetworkHandler(String hostId) {
		NetworkHandler nethandler = new NetworkHandler(hostId);
		nethandler.response = this;
		return nethandler;
	}
	
	public void build(int primaryKey, String userId, String hostId) {
		
		//the idea is that we only have your ID and the primary keys and you generate the objects back again
		String get = "?";
		get = get + getStrings[0] + "=" + userId + "&";
		get = get + getStrings[1] + "=" + "-1&";
		get = get + getStrings[2] + "=" + "-1&";
		get = get + getStrings[3] + "=" + "-1&";
		get = get + getStrings[4] + "=" + primaryKey + "&";	//primary key!
		get = get + getStrings[5] + "=" + "false&"; //isRegistering
		get = get + getStrings[6] + "=" + "false&";	//isNewMeetup
		get = get + getStrings[7] + "=" + "false&";	//isRsvp
		get = get + getStrings[8] + "=" + "false&";	//isLocCheckIn
		get = get + getStrings[9] + "=" + "true&";	//isInfoRequest
		get = get + getStrings[10] + "=" + "-1&";
		get = get + getStrings[11] + "=" + "-1&";
		get = get + getStrings[12] + "=" + "-1&";
		get = get + getStrings[13] + "=" + "-1";
	
		netHandler = newNetworkHandler(hostId);
		netHandler.execute(get);
	}
	
	
	private class NetworkHandler extends AsyncTask<String, String, String> {

		public AsyncResponseInterface response = null;
		private String mHostId;
		
		public NetworkHandler(String hostId) {
			mHostId = hostId;
		}
		@Override
		protected String doInBackground(String... urls) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

			String url = urls[0];

			Log.i(TAG, "url: " + baseUrl + url);
			url = baseUrl + url;

			HttpGet httpGet = new HttpGet(url);
			String responseString = "";

			try {
				HttpResponse response = client.execute(httpGet);
				Log.i(TAG, "SUCCESS");
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					responseString = builder.toString();
				} else {
					Log.e("Getter", "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.i(TAG, result);
			
			response.addNewMeetupCallback(result, mHostId);

		}
	}

	//this runs at the end of the asynctask
	//not sure why i didn't just do this in the onPostExecute
	//must have been confused...
	public void addNewMeetupCallback(String res, String hostId) {
		Log.i(TAG, "ProcessResult has received the string!");
		Log.i(TAG, "This string is: " + res);
		
		String[] pairs = res.split(";");
		
		ArrayList<String> inviteds = new ArrayList<String>();
		ArrayList<String> invitedsConditions = new ArrayList<String>();
		
		//not hosted by you
		if (pairs[0].startsWith("hostId")) {
			String[] splitHostId = pairs[0].split(":"); //gives splits "hostId:8582019190", need second part
			String[] splitLat = pairs[1].split(":");
			String[] splitLong = pairs[2].split(":");
			String[] splitMeetName = pairs[3].split(":");
			
			int dateTimeStart = findLocOfChar(pairs[4], ':');
			
			String datetimeString = pairs[4].substring(dateTimeStart);
			
			Meetup meet = new Meetup(splitMeetName[1],splitHostId[1], splitLat[1], splitLong[1],datetimeString);
			mMeetups.add(meet);
		}
		//this is something hosted by you
		else {
			String[] splitLat = pairs[0].split(":");
			String[] splitLong = pairs[1].split(":");
			String[] splitMeetName = pairs[2].split(":");
			
			int dateTimeStart = findLocOfChar(pairs[3], ':');
			String datetimeString = pairs[3].substring(dateTimeStart);
			
			for (int i = 4; i < pairs.length; i++) {
				String[] p = pairs[i].split(":"); //get pairs of attendees and status
				inviteds.add(p[0]);
				invitedsConditions.add(p[1]);
			}
			
			Meetup meet = new Meetup(splitMeetName[1], hostId, splitLat[1], splitLong[1], datetimeString, inviteds, invitedsConditions);
			
			mMeetups.add(meet);
			
		}
		
		
		
	}
	
	public int findLocOfChar(String s, char charToFind ) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == charToFind) {
				return i;
			}
		}
		return -1;
		
	}

}
