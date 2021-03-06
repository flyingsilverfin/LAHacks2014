package com.JS.meetontime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Helper {

	public static String meetupFile = "meetupFile";
	
	
	/*
	 * Shared preferences
	 */
	public static String getUserName(Context context) {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		String userName = prefs.getString("userName", "");
		return userName;
	}
	
	public static String getUserNumber(Context context) {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		String userName = prefs.getString("userNumber", "");
		return userName;	
	}
	
	public static String getUserRating(Context context) {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		String userRating = prefs.getString("userRating", "0");
		return userRating;
	}
	
	public static void setUserRating(Context context, String rating) {
		SharedPreferences.Editor editor = context.getSharedPreferences("com.JS.app", Context.MODE_PRIVATE).edit();
		editor.putString("userRating", rating);
		editor.commit();
	}
	
	public static void setTimeDiff(Context context, long diffInMilis) {
		SharedPreferences.Editor editor = context.getSharedPreferences("com.JS.app", Context.MODE_PRIVATE).edit();
		editor.putLong("timediff", diffInMilis);
		editor.commit();
	}
	
	//public static Date getServerTime(Context context, Date localTime) {
		
	//}
	
	public static int getLocalCounter(Context context) {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		int tempId = prefs.getInt("localTempCounter", -1);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("localTempCounter", tempId+1);
		editor.commit();
		return tempId;
	}
	


	
	/*
	 * Private file edits
	 */
	public synchronized static void writeFile(Context context, String file, ArrayList<String> contents) {
		try{
			FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (String s : contents) {
				writer.write(s);
				writer.newLine(); //probably just writes "\n"
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();		
		} //could be writing error or file opening error
					
	}
	
	public synchronized static ArrayList<String> readFile(Context context, String file) {
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			FileInputStream fis = context.openFileInput(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public synchronized static void appendToFile(Context context, String file, ArrayList<String> contents) {
		try{
			FileOutputStream fos = context.openFileOutput(file, Context.MODE_APPEND);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (String s : contents) {
				writer.write(s);
				writer.newLine(); //probably just write "\n"
			}
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();		
		} //could be writing error or file opening error
	}
	
	public synchronized static void appendToFile(Context context, String file, String data) {
		try{
			FileOutputStream fos = context.openFileOutput(file, Context.MODE_APPEND);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
			writer.write(data);
			writer.newLine(); //probably just write "\n"
			
		} catch (Exception e) {
			e.printStackTrace();		
		} //could be writing error or file opening error
	}
	
	
	
	/*
	 * Maps tools
	 */
	public static double getMetersBetween(LatLng p1, LatLng p2) {
		float[] result = new float[1];
		Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, new float[1]);
		return result[0];
	}

	
	 /*
	 * Misc tools
	 */
	
	public static int findNextIndexOf(String s, int startingIndex, String strToFind) {
		String cut = s.substring(startingIndex);
		return cut.indexOf(strToFind);
	}
	
	public static ArrayList<String> parseToStringList(String str, String splitChar) {
		ArrayList<String> l = new ArrayList<String>();
		
		String[] split = str.split(splitChar);
		for (String s : split) {
			l.add(s);
		}
		
		return l;
	}

	
	public static ArrayList<Float> parseToFloatList(String str, String splitChar) {
		ArrayList<Float> l = new ArrayList<Float>();
		
		String[] split = str.split(splitChar);
		for (String s : split) {
			l.add(Float.parseFloat(s));
		}
		
		return l;
	}
	
	
	public static String capsToSpaces(String camelCaseString) {
		StringBuilder str = new StringBuilder();
		int lastSpace = 0;
		for (int i = 0; i < camelCaseString.length(); i++) {
			if (isUpperCase(camelCaseString.charAt(i))) {
				str.append(camelCaseString.substring(lastSpace, i));
				str.append(" ");
				lastSpace = i;
			}
		}
		
		str.append(camelCaseString.substring(lastSpace));
		
		return str.toString();
	}
	
	
	public static void printArrayList(ArrayList<String> list) {
		for (String s : list) {
			System.out.println(s);
		}
	}
	
	
	public static String removeNonDigits(String str) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (isDigit(str.charAt(i))) {
				s.append(str.charAt(i));
			}
		}
		return s.toString();
	}
	
	public static boolean isUpperCase(char ch) {
	    return ch >= 'A' && ch <= 'Z';
	}
	
	public static boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}
	
	
	public static String formatPhoneNumber(String num) {
		StringBuilder str = new StringBuilder();
		if (num.length() == 10) {
			str.append("(");
			str.append(num.substring(0,3));
			str.append(") ");
			str.append(num.substring(3, 6));
			str.append("-");
			str.append(num.substring(6, num.length()));
			return str.toString();
		}
		else {
			return num;
		}
	}
	
	
}
