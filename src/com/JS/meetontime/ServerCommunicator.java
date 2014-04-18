package com.JS.meetontime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class ServerCommunicator {

	public static final String TAG = "ServerCommunication";

	static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-ddHH:mm:ss");

	private static final String IP = "http://172.4.32.172";
	private static final String PORT = "8001";

	private static final String baseUrl = IP + ":" + PORT + "/";

	private String globalResponse;

	private Context mContext;
	private SharedPreferences prefs;
	// private NetworkHandler mNetworkHandler;

	private String mUserName;
	private String mUserNumber;

	// can only be one of the following
	private boolean isRegistering, isNewMeetup, isRsvp, isLocCheckIn,
			isInfoRequest;

	private HashMap<String, Object> mGETVarMap;

	private String[] mGETStrings;

	NetworkHandler mNetworkHandler;

	// hacks
	// private ArrayList<Meetup> mMeetups;
	// private boolean convertToMeetup = false;

	/*
	 * 'phoneId' of sender (REQUIRED)
	 * 
	 * 'phoneName' of sender (REQUIRED)
	 * 
	 * 'meetupName' (REQUIRED, but it doesn't have to be like that. might change
	 * later)
	 * 
	 * 'hostId' -- (required for getting information)
	 * 
	 * 'meetupId' (REQUIRED except for making new meetup)
	 * 
	 * 'isRegistering' (REQUIRED)
	 * 
	 * 'isNewMeetup' (REQUIRED)
	 * 
	 * 'isRsvp' (REQUIRED)
	 * 
	 * 'isLocCheckIn' (REQUIRED)
	 * 
	 * 'isInfoRequest' (REQUIRED)
	 * 
	 * 'peopleToInvite' (required for new meetup)
	 * 
	 * 'meetupLat' (required for new event)
	 * 
	 * 'meetupLong' (required for new event)
	 * 
	 * 'dateTime' #'2014-04-1204:34:46' formed by "%Y-%m-%d %H:%M:%S" without
	 * the space (required for new meetup and location check in)
	 */

	public ServerCommunicator(Context context) {
		mContext = context;
		//mMeetups = meetupsList;
		mNetworkHandler = newNetworkHandler();
		// mNetworkHandler.response = this;
		// mNetworkHandler = new NetworkHandler();
		mGETVarMap = new HashMap<String, Object>();

		mGETStrings = new String[] { "phoneId", "phoneName", "meetupName",
				"hostId", "meetupId", "isRegistering", "isNewMeetup", "isRsvp",
				"isLocCheckIn", "isInfoRequest", "peopleToInvite", "meetupLat",
				"meetupLong", "dateTime" };

		prefs = context
				.getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		mUserName = prefs.getString("userName", "");
		mUserNumber = prefs.getString("userNumber", "");

		System.out.println("HI");
		// --set up the hash map--
		// most of these are not used at once, just needed since the received is
		// written stupidly
		mGETVarMap.put(mGETStrings[0], mUserNumber); // phone number

		mGETVarMap.put(mGETStrings[1], mUserName); // name9
		mGETVarMap.put(mGETStrings[2], "xxx"); // meetup event name
		mGETVarMap.put(mGETStrings[3], "888"); // host phone number/id
		mGETVarMap.put(mGETStrings[4], 1); // this is the meetup event number

		// the 5 booleans
		for (int i = 5; i < 10; i++) {
			mGETVarMap.put(mGETStrings[i], false);
		}

		mGETVarMap.put(mGETStrings[10], new String[] { "8582019190" }); // list
																		// of
																		// people
																		// (for
																		// new
																		// meetup)
		mGETVarMap.put(mGETStrings[11], 0.0); // Double latitude
		mGETVarMap.put(mGETStrings[12], 0.0); // Double longitude
		mGETVarMap.put(mGETStrings[13], "2014-01-0101:00:00"); // empty datetime
																// string for
																// now

		if (!prefs.getBoolean("isRegisteredOnServer", false)) {
			registerUser();
		}

		// --end set up hash map--

		Log.i(TAG, "Finished setting up hashmap");
	}

	// really only should only be called once
	public void registerUser() {
		resetBools();
		setIsRegistering(true);
		String GETS = buildGetString();

		Glob g = new Glob(GETS, false);
		mNetworkHandler = newNetworkHandler();
		mNetworkHandler.execute(g); // just prints out response for now
		// don't need anything back from async
	}

	public void rsvp(Meetup meetup) {
		resetBools();
		setIsRsvp(true);
		String GETS = buildGetString();
		Glob g = new Glob(GETS, false);
		mNetworkHandler = newNetworkHandler();

		mNetworkHandler.execute(g);
		// don't need anything back from async
	}

	public void createMeetup(Meetup meetup) {
		/*if (!meetup.isHost()) {
			return;
		}

		resetBools();
		setIsNewMeetup(true);
		String host = meetup.getHost();
		String lat = meetup.getLat();
		String lng = meetup.getLong();

		String datetime = meetup.formattedDate();

		String[] inviteds = meetup.formattedInviteds();

		mGETVarMap.put("hostId", host);
		mGETVarMap.put("meetupLat", lat);
		mGETVarMap.put("meetupLong", lng);

		mGETVarMap.put("dateTime", datetime);
		mGETVarMap.put("peopleToInvite", inviteds);

		String GETS = buildGetString();

		Log.i(TAG, "Creating meetup with following GET commands: " + GETS);

		mNetworkHandler = newNetworkHandler();

		Glob g = new Glob(GETS, false);

		mNetworkHandler.execute(g);

		// don't need anything from async
		// hopefully the meetup object being used is already in the arraylist
		 
		 */
	}

	public void checkinAtLocation(Meetup meetup) {

		// don't need anything back from async
	}

	public void buildObjectFromPrimaryKey(int primaryKey) {
		resetBools();
		setIsInfoRequest(true);
		String GETS = buildGetString();
		Glob g = new Glob(GETS, true);
		mNetworkHandler = newNetworkHandler();

		mNetworkHandler.execute(g);

	}

	private String buildGetString() {

		String gets = "?";
		for (int i = 0; i < 10; i++) {
			gets += mGETStrings[i];
			gets += "=";
			gets += mGETVarMap.get(mGETStrings[i]);
			gets += "&";
		}

		String key = mGETStrings[10];
		String[] arr = (String[]) mGETVarMap.get(key);
		gets += key;
		gets += "=";
		for (String s : arr) {
			gets += s;
			gets += ',';
		}
		gets = gets.substring(0, gets.length() - 1);
		gets += "&";

		for (int i = 11; i < mGETStrings.length - 1; i++) {
			gets += mGETStrings[i];
			gets += "=";
			gets += mGETVarMap.get(mGETStrings[i]);
			gets += "&";
		}
		gets += mGETStrings[mGETStrings.length - 1];
		gets += "=";
		gets += mGETVarMap.get(mGETStrings[mGETStrings.length - 1]);
		return gets;

	}

	private NetworkHandler newNetworkHandler() {
		NetworkHandler n = new NetworkHandler();
		// n.response = this;
		return n;
	}

	// reset all those booleans to false to easily set one to true later (only 1
	// true at a time)
	private void resetBools() {
		for (int i = 5; i < 10; i++) {
			mGETVarMap.put(mGETStrings[i], false);
		}
	}

	private void setIsRegistering(boolean b) {
		mGETVarMap.put(mGETStrings[5], b);
	}

	private void setIsNewMeetup(boolean b) {
		mGETVarMap.put(mGETStrings[6], b);
	}

	private void setIsRsvp(boolean b) {
		mGETVarMap.put(mGETStrings[7], b);
	}

	private void setIsLocCheckIn(boolean b) {
		mGETVarMap.put(mGETStrings[8], b);
	}

	private void setIsInfoRequest(boolean b) {
		mGETVarMap.put(mGETStrings[9], b);
	}


	private class NetworkHandler extends AsyncTask<Glob, String, String> {

		public AsyncResponseInterface response = null;

		@Override
		protected String doInBackground(Glob... globs) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

			String url = globs[0].get;
			boolean convToMeetup = globs[0].toMeetup;

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

			Log.i(TAG, "RESPONSE: " + responseString);
			if (convToMeetup) {
				convertToMeetup(responseString);
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.i(TAG, result);
		}

		public void convertToMeetup(String resp) {
			Log.i(TAG, "Wanting to convert %s into object");

			// response.processReturnData(resp);
			// mMeetups.add(object)
		}
	}

	public void processReturnData(String data) {

	}

	private class Glob {
		public String get;
		public boolean toMeetup;

		public Glob(String s, boolean convertToMeetup) {
			get = s;
			toMeetup = convertToMeetup;
		}
	}
}
