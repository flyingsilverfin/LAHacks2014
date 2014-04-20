package com.JS.meetontime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;



/*
 * DO NOT NEED TO MAKE MULTIPLE INSTANCES OF THIS
 * each time you call a method in here
 * it creates its own new async so networking is always parallel
 * and will not cause problems
 * synchronization is fixed with synchronized keyword in databasebuilder
 */
public class Networking {

	private final static String TAG = "Networking";
	private final static String baseUrl = "http://someurl.com/";

	public NetworkHandler communicator;

	private Context mContext;

	public Networking(Context context) {
		mContext = context;
		communicator = new NetworkHandler();
	}

	public NetworkHandler newNetworkHandler() {
		//copy out the callback and put it in the new one
		NetworkHandler n = new NetworkHandler();
		AsyncResponseInterface resp = communicator.asyncCallback;
		n.asyncCallback = resp;
		return n;
	}
	

	/*
	 * Check for network connection
	 */
	public boolean isOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;

	}
	
	
	
	/*
	 * Register name/phone
	 * IF THIS FAILS because there's no network or something
	 * Then that is a failure of the program
	 */
	public void registerUser() {
		communicator = newNetworkHandler();
		
		String userName = Helper.getUserName(mContext);
		String userNumber = Helper.getUserNumber(mContext);
		
		String GET = "/register/?number=" + userNumber + "&name=" + userName;
		communicator.execute(GET);
	}
	
	
	/*
	 * Update name/phone
	 * IF THIS FAILs because there's no network
	 * Then the update never gets registered...
	 * Easiest solution: don't even save update to shared prefs if there's no network, and never call this
	 */
	public void updateUserRegistration(String oldNumber) {
		communicator = newNetworkHandler();
		
		String userName = Helper.getUserName(mContext);
		String userNumber = Helper.getUserNumber(mContext);
		
		String GET = "/registerUpdate/?oldNumber=" + oldNumber + 
				"&newNumber=" + userNumber + 
				"&newName=" + userName;
					
		communicator.execute(GET);
	}
	

	
	/*
	 * Make new meetup
	 */
	
	public void newMeetup(Meetup meetup) {
		///newMeetup/?hostId=...&eventName=...&lat=&long=&datetime=&inviteds=
		/*
		 * TODO
		 */
		
		communicator = newNetworkHandler();
		
		String GET = "/newMeetup/?";
		GET += "hostId=" + meetup.getHostId();
		GET += "&eventName=" + meetup.getEventName();
		GET += "&lat=" + meetup.getLat();
		GET += "&long=" + meetup.getLong();
		GET += "&datetime=" + meetup.getFormattedDate();
		GET += "&inviteds=" + meetup.getInvitedsString();
		
		communicator.execute(GET);	
	}

	
	public void joinMeetup(int eventId) {
		/*
		 * TODO
		 */
	}
	
	public void rsvpMeetup(Meetup meetup) {
		/*
		 * TODO
		 */
	}
	
	public void updateMeetups() {
		/*
		 * TODO
		 * this one is most possible right now
		 */
	}
	
	
	
	
	private class NetworkHandler extends AsyncTask<String, String, String> {

		public AsyncResponseInterface asyncCallback = null;

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
			asyncCallback.asyncCallback(result);
		}
	}

}