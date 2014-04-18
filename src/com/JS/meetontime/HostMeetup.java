package com.JS.meetontime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class HostMeetup implements AsyncResponseInterface {

	public static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-ddHH:mm:ss");
	private static final String TAG = "HostMeetup";

	public static final String baseUrl = "http://172.4.32.172:8001/";

	public static String[] getStrings = { "phoneId", "phoneName", "meetupName",
			"hostId", "meetupId", "isRegistering", "isNewMeetup", "isRsvp",
			"isLocCheckIn", "isInfoRequest", "peopleToInvite", "meetupLat",
			"meetupLong", "dateTime" };

	private String mLat;
	private String mLong;
	private String mName;
	private String[] mInviteds;
	private Date mDate;

	private Context mContext;

	private NetworkHandler netHandler;

	public HostMeetup(String lat, String lng, String name, String[] inviteds,
			Date date, Context context) {
		mLat = lat;
		mLong = lng;
		mName = name;
		mInviteds = inviteds;
		mDate = date;

		netHandler = newNetworkHandler();

		mContext = context;

		prepForAsync();

	}

	public void prepForAsync() {
		SharedPreferences prefs = mContext.getSharedPreferences("com.JS.app",
				Context.MODE_PRIVATE);
		String phoneName = prefs.getString("userName", "");
		String phoneId = prefs.getString("userNumber", "");

		String peopleToInvite = "";
		for (String s : mInviteds) {
			peopleToInvite += s;
			peopleToInvite += ",";
		}

		peopleToInvite = peopleToInvite.substring(0,
				peopleToInvite.length() - 1); // chop off last comma

		String formattedDate = dateFormat.format(mDate);

		String get = "?";
		get = get + getStrings[0] + "=" + phoneId + "&";
		get = get + getStrings[1] + "=" + phoneName + "&";
		get = get + getStrings[2] + "=" + mName + "&";
		get = get + getStrings[3] + "=" + phoneId + "&";
		get = get + getStrings[4] + "=" + "-1&";
		get = get + getStrings[5] + "=" + "false&";
		get = get + getStrings[6] + "=" + "true&";
		get = get + getStrings[7] + "=" + "false&";
		get = get + getStrings[8] + "=" + "false&";
		get = get + getStrings[9] + "=" + "false&";
		get = get + getStrings[10] + "=" + peopleToInvite + "&";
		get = get + getStrings[11] + "=" + mLat + "&";
		get = get + getStrings[12] + "=" + mLong + "&";
		get = get + getStrings[13] + "=" + formattedDate;

		Log.i(TAG, "GET IS: " + get);

		netHandler = newNetworkHandler();
		Log.i(TAG, "HELLO");
		netHandler.execute(get);

	}

	private NetworkHandler newNetworkHandler() {
		NetworkHandler nethandler = new NetworkHandler();
		nethandler.response = this;
		return nethandler;
	}

	private class NetworkHandler extends AsyncTask<String, String, String> {

		public AsyncResponseInterface response = null;

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
			response.addNewMeetupCallback(result, "");
			Log.i(TAG, result);
		}
	}

	public void addNewMeetupCallback(String res, String hostId) {
		//ignore hostId in this usage
		Log.i(TAG, "Processing result " + res);
		SharedPreferences prefs = mContext.getSharedPreferences("com.JS.app",
				Context.MODE_PRIVATE);
		String meetups = prefs.getString("meetups", "");
		
		String primaryKey = getTrailingInteger(res);
		if (primaryKey.length() == 0) {
			return;
		}
		Log.i(TAG, "primary key: " + primaryKey);
		if (meetups.length() == 0) {
			meetups = primaryKey;
		}
		else {
			meetups = meetups + "," + primaryKey;
		}

		SharedPreferences.Editor editor = prefs.edit();

		editor.putString("meetups", meetups);
		editor.commit();
	}

	String getTrailingInteger(String str) {
		int positionOfLastDigit = getPositionOfLastDigit(str);
		if (positionOfLastDigit == str.length()) {
			// string does not end in digits
			return "";
		}
		return str.substring(positionOfLastDigit);
	}

	int getPositionOfLastDigit(String str) {
		int pos;
		for (pos = str.length() - 1; pos >= 0; --pos) {
			char c = str.charAt(pos);
			if (!Character.isDigit(c))
				break;
		}
		return pos + 1;
	}

}

interface AsyncResponseInterface {
	void addNewMeetupCallback(String res, String hostId);
}