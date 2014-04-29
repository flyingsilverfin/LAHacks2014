package com.JS.meetontime;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
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
	private final static String baseUrl = "http://someurl.com";

	// public NetworkHandler communicator; //dont even need this since each
	// function creates its own

	private Context mContext;

	public Networking(Context context) {
		mContext = context;
		// don't need to instantiate communicator here
		// instantiated new each time a function requiring the async is called
	}

	/*
	 * Check for network connection
	 */
	public boolean isOnline() {
		return ContinuousNetworkChecker.getInstance(mContext).isOnline();
	}

	/*
	 * Register name/phone IF THIS FAILS because there's no network or something
	 * Then that is a failure of the program
	 */
	public void registerUser(AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String userName = Helper.getUserName(mContext);
		String userNumber = Helper.getUserNumber(mContext);

		String GET = "/register/?number=" + userNumber + "&name=" + userName;
		com.execute(GET);
	}

	/*
	 * Update name/phone IF THIS FAILs because there's no network Then the
	 * update never gets registered... Easiest solution: don't even save update
	 * to shared prefs if there's no network, and never call this
	 */
	public void updateUserRegistration(String oldNumber,
			AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String userName = Helper.getUserName(mContext);
		String userNumber = Helper.getUserNumber(mContext);

		String GET = "/registerUpdate/?oldNumber=" + oldNumber + "&newNumber="
				+ userNumber + "&newName=" + userName;

		com.execute(GET);
	}

	/*
	 * Make new meetup
	 */

	public void newMeetup(Meetup meetup, AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String GET = "/newMeetup/?";
		GET += "hostId=" + meetup.getHostId();
		GET += "&eventName=" + meetup.getEventName();
		GET += "&lat=" + meetup.getLat();
		GET += "&long=" + meetup.getLong();
		GET += "&datetime=" + meetup.getFormattedDate();
		GET += "&inviteds=" + meetup.getInvitedsString();
		GET += "&invitedsNames=" + meetup.getInvitedsNamesString();

		com.execute(GET);
	}

	public void joinMeetup(int eventId, AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String GET = "/joinMeetup/?";
		GET += "eventid=" + eventId;

		com.execute(GET);
	}

	public void rsvpMeetup(Meetup meetup, AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String GET = "/rsvpMeetup/?";
		GET += "eventId=" + meetup.getEventId();

		com.execute(GET);
	}

	public void comingToMeeting(Meetup meetup,
			AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String GET = "/comingToMeetup/?";
		GET += "eventId=" + meetup.getEventId();

		com.execute(GET);
	}

	public void checkForUpdate(Meetup meetup, AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String GET = "/checkForUpdate/?";
		GET += "eventId=" + meetup.getEventId();

		com.execute(GET);
	}

	public void pullMeetup(Meetup meetup, AsyncResponseInterface callbackObj) {
		NetworkHandler com = new NetworkHandler();
		com.asyncCallback = callbackObj;

		String GET = "/pullMeetup/?";
		GET += "eventId" + meetup.getEventId();

		com.execute(GET);
	}

	private class NetworkHandler extends AsyncTask<String, String, String> {

		public AsyncResponseInterface asyncCallback = null;

		@Override
		protected String doInBackground(String... urls) {
			
			//wait max of 10 sec for connection
			for (int i = 0; i < 10; i++) {
				if (!isOnline()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.d(TAG, "sleep inside asyncTask got interruped");
						e.printStackTrace();
					}
				}
				else {
					break;
				}
			}
			//if still not online...
			if (!isOnline()) {
				Log.e(TAG, "Aborting, no internet!");
				cancel(true);
			}
		
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

			String url = urls[0];

			Log.i(TAG, "url: " + baseUrl + url);
			url = (baseUrl + url).replace(" ", "");

			HttpGet httpGet = new HttpGet(url);
			// String responseString = "HELLO, YOU ENTERED: " + url;

			// testing for new meetup:
			String responseString = "eventId=0&eventname=HELLO&hostId=ME&hostName=JoshuaSend&lat=301.01928394&long=118.01840582&datetime=2014-04-2020:20:20&inviteds=8584365309&invitedsNames=NilminiSilvasend&invitedsStatuses=false&invitedsRatings=4.02";

			//String responseString = url;
			Log.i(TAG, responseString);
/*
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
					Log.e("Getter", "Failed to connect");
				//	cancel(true);
				}
			
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/

			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			asyncCallback.asyncCallback(result);
		}
	}

}
