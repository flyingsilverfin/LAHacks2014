package com.JS.meetontime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

public class ContinuousNetworkChecker implements Runnable {

	private static final String TAG = "ContinuousNetworkChecker";

	private static ContinuousNetworkChecker mInstance;
	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mEditor;
	private boolean isStopped = false;
	private boolean isOnline = false;
	private View mStatusBar;
	private Context mContext;
	private Thread mRunningThread;

	private ContinuousNetworkChecker(Context context) {
		mPrefs = context.getSharedPreferences("com.JS.app",
				Context.MODE_PRIVATE);
		mEditor = mPrefs.edit();
		mContext = context;
	}

	public static synchronized ContinuousNetworkChecker getInstance(
			Context context) {
		if (mInstance == null) {
			mInstance = new ContinuousNetworkChecker(context);
		}
		return mInstance;
	}

	@Override
	public void run() {
		while (!isStopped) {
			Log.i(TAG, "checking network");
			try {
				/*
				 * This doesn't work since we get redirected to the Tmobile site
				 * telling me to get data which gives a connection OK with
				 * status 200, therefore this fails
				 * 
				 * HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.bbc.co.uk").openConnection());
				 * urlc.setRequestProperty("User-Agent", "Test");
				 * urlc.setRequestProperty("Connection", "close");
				 * urlc.setConnectTimeout(1500);
				 * 
				 * if (!isOnline) { // checking to see if there is a network or
				 * // not... orangeStatusBar(); } urlc.connect(); Log.i(TAG,""+
				 * urlc.getResponseCode() + " " + isNetworkAvailable());
				 * Log.i(TAG, urlc.getResponseMessage()); Log.i(TAG,
				 * urlc.getContent().);
				 */
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://google.com");
				HttpResponse response = client.execute(request);

				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String line = null;
				boolean foundGoogle = false;
				while ((line = reader.readLine()) != null && (!foundGoogle)) {
					// this phrase is at the top of google's HEAD section
					if (line.contains("Search the world's")) {
						foundGoogle = true;
					}
				}

				// if online and weren't before
				if (foundGoogle) {
					if (!isOnline) {
						mEditor.putBoolean("isOnline", true);
						isOnline = true;
						greenStatusBar();
						mEditor.commit();
					}
				} else {
					Log.i(TAG, "non");
					if (isOnline) {
						mEditor.putBoolean("isOnline", false);
						isOnline = false;
						redStatusBar();
						mEditor.commit();
					}
				}
			} catch (IOException e) {
				Log.e(TAG, "Error checking internet connection", e);
				mEditor.putBoolean("isOnline", false);
				isOnline = false;
				redStatusBar();
				mEditor.commit();
			}

			try {
				if (!isOnline) {
					Thread.sleep(30000); //check every 30 sec if no connection
				}
				else {
					Thread.sleep(120000); // check every 2 minutes if online
				}
			} catch (InterruptedException e) {
				Log.i(TAG, "Interrupted sleep!");
			}

		}
	}

	public void pause() {
		isStopped = true;
	}

	public void begin() {
		Log.i(TAG, "beginning");
		// if first time using this class
		if (mRunningThread == null) {
			mRunningThread = new Thread(mInstance);
			mRunningThread.start();
			return;
		}
		// if not first time, but thread is dead
		if (!mRunningThread.isAlive()) {
			isStopped = false;
			mRunningThread = new Thread(mInstance);
			mRunningThread.start();
		}
		// this means it's already running
		else {
			Log.i(TAG,
					"Thread should already be running, not making new thread");
		}
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setStatusView(View v) {
		mStatusBar = v;
		if (isOnline) {
			greenStatusBar();
		}
		else {
			redStatusBar();
		}
		isOnline = false;
		if (mRunningThread != null) {
			mRunningThread.interrupt();
		}
	}
	
	public void interrupt() {
		if (mRunningThread != null) {
			mRunningThread.interrupt();
		}
	}

	private void redStatusBar() {
		((Activity) mContext).runOnUiThread(new Runnable() {
			public void run() {
				mStatusBar.setBackgroundColor(Color
						.argb(0x66, 0xFF, 0x00, 0x00));
			}
		});
	}

	private void orangeStatusBar() {
		((Activity) mContext).runOnUiThread(new Runnable() {
			public void run() {
				mStatusBar.setBackgroundColor(Color
						.argb(0x66, 0xFF, 0x99, 0x00));
			}
		});
	}

	private void greenStatusBar() {
		((Activity) mContext).runOnUiThread(new Runnable() {
			public void run() {
				mStatusBar.setBackgroundColor(Color
						.argb(0x66, 0x00, 0xFF, 0x00));
			}
		});
	}

}
