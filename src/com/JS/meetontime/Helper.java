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
	
	public static int getLocalCounter(Context context) {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		int tempId = prefs.getInt("localTempCounter", -1);
		incrementLocalCounter(context);
		return tempId;
	}
	
	private static void incrementLocalCounter(Context context) {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.JS.app", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("localTemptCounter", getLocalCounter(context)+1);
		editor.commit();
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
				writer.newLine(); //probably just write "\n"
			}
			
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
	
	public static ArrayList<Integer> parseToIntegerList(String str, String splitChar) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		
		String[] split = str.split(splitChar);
		for (String s : split) {
			l.add(Integer.parseInt(s));
		}
		
		return l;
	}
	
}
