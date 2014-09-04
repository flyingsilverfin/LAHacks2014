package com.JS.meetontime;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.app.Activity;
import android.app.Service;


public class LocatorClass extends Service implements LocationListener {
	
	private static final String TAG = "LocatorClass";

	Context mContext;
	Boolean isGpsEnabled;
	LocationManager locationManager;
	Location location;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private static final long MIN_DISTANCE_CHANGE = 10; //100 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute in milliseconds
	
	public LocatorClass(Context mContext){
		this.mContext = mContext;
	}
		
	
	public double[] getLocation(){
		try{
			locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(isGpsEnabled){
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
														MIN_TIME_BW_UPDATES, 
														MIN_DISTANCE_CHANGE, 
														this);
				Log.d(TAG, "GPS Enabled");
				if(locationManager != null) {
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if(location != null){
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					} else {
						Log.d(TAG, "Location is null");
					}
				} else {
					Log.d(TAG, "locationManager is null");
				}
			} else {
				Log.d(TAG, "GPS not enabled");
			}
			double[] latlng = {latitude, longitude};
			return latlng;
		} catch (Exception e) {
			e.printStackTrace();
			e.toString();
			return null;
		}
	}


	@Override
	public void onLocationChanged(Location arg0) {
		Log.i(TAG, "Location changed: lat: " + arg0.getLatitude() + ", long: " + arg0.getLongitude());
		arg0.
	}


	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.i(TAG, "Status changed: " + arg0 + " " + arg1);		
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
