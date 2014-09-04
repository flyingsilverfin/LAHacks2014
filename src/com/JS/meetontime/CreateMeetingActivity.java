package com.JS.meetontime;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateMeetingActivity extends Activity implements OnItemClickListener{
	protected static final String TAG = "CreateMeeting";
	List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    private EditText meetingName;
    private Button meetingTime;
    private Button meetDatePicker;
    private CheckBox pm;
    private String date = "";
    private String time = "";
    MyAdapter ma ;
    Button select;
    
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_meeting);
		
		getAllContacts(this.getContentResolver());
        ListView lv= (ListView) findViewById(R.id.lv);
        ma = new MyAdapter();
        lv.setAdapter(ma);
        lv.setOnItemClickListener(this); 
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);
        // adding
        select = (Button) findViewById(R.id.button1);
        select.setOnClickListener(new OnClickListener()
        {

            @SuppressLint("ShowToast")
			@Override
            public void onClick(View v) {
                ArrayList<String[]> checkedcontacts = new ArrayList<String[]>();
                int checkedContactsSize = 0;
                for(int i = 0; i < name1.size(); i++)

                    {
                    if(ma.mCheckStates.get(i)==true)
                    {
                         checkedcontacts.add(new String[]{name1.get(i).toString(), phno1.get(i).toString()});
                         Log.d(TAG, "Added " + checkedcontacts.get(checkedContactsSize)[0] + " with number " + checkedcontacts.get(checkedContactsSize)[1]);
                         checkedContactsSize++;
                    }
                }
	             meetingName = (EditText) findViewById(R.id.meetingName);
	             String meetingNameString = meetingName.getText().toString();
	             if (!ContinuousNetworkChecker.getInstance(getApplicationContext()).isOnline()) {
	            	 Toast.makeText(CreateMeetingActivity.this, "Must be online to create event", Toast.LENGTH_LONG).show();
	            	 return;
	             }
	             if (meetingNameString.length() == 0) {
	            	 Toast.makeText(CreateMeetingActivity.this, "Enter a name", Toast.LENGTH_SHORT).show();
	            	 return;
	             }
	             if (date.length() == 0 || time.length() == 0) {
	            	 Toast.makeText(CreateMeetingActivity.this, "Must set a time and date", Toast.LENGTH_SHORT).show();
	            	 return;
	             }
	             if (checkedcontacts.size() == 0) {
	            	 Toast.makeText(CreateMeetingActivity.this, "You haven't invited anyone!", Toast.LENGTH_SHORT).show();
	            	 return;
	             }

	             Intent intent = new Intent(CreateMeetingActivity.this, NewMeetingLocationActivity.class);
	             intent.putExtra("checkedContacts", checkedcontacts);
	             intent.putExtra("meetingName", meetingNameString);
	             intent.putExtra("meetingDateTime", date + time);
	             startActivity(intent);
	             //Toast.makeText(CreateMeetingActivity.this, checkedcontacts,1000).show();
            }
        });
        
        meetDatePicker = (Button) findViewById(R.id.meetDatePicker);
        meetDatePicker.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Log.d(TAG, "DatePicker clicked");
				selectDate();
			}});
        
        meetingTime = (Button) findViewById(R.id.meetingTimeValue);
        meetingTime.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Log.d(TAG, "TimePicker clicked");
				selectTime();
			}});
		
        
      
        
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
	}
	
	
	protected void onResume() {
		super.onResume();
		//update the checker as to where we are
        ContinuousNetworkChecker.getInstance(this).setStatusView(findViewById(R.id.networkStatusBar));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_meeting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_create_meeting,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ma.toggle(arg2);
	}
	
    public void getAllContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext())
        {
        	int phoneType = phones.getInt(phones.getColumnIndex(Phone.TYPE));
        	if(phoneType == Phone.TYPE_MOBILE){
        		String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        		String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        		if(name.charAt(0) != '#'){
        			name1.add(name);
        			String fixedPhone = "";
        			
        			if(phoneNumber.charAt(0) == '+'){			//Check for international numbers
        				phoneNumber = phoneNumber.substring(2);	//Remove +1 on numbers
        			} else if (phoneNumber.charAt(0) == '1') {
        				phoneNumber = phoneNumber.substring(1);
        			}
        			
        			for (int phoneLength = 0; phoneLength < phoneNumber.length(); phoneLength++){
        				if(Character.isDigit(phoneNumber.charAt(phoneLength))){
        						fixedPhone = fixedPhone + phoneNumber.charAt(phoneLength);
        				}
        			}
        			
        			phno1.add(fixedPhone);
        			
        		}
        	}
        }
        phones.close();
     }

	class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
		private SparseBooleanArray mCheckStates;
		private LayoutInflater mInflater;
        private TextView tv1,tv;
        private CheckBox cb;
        
        public MyAdapter() {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater) CreateMeetingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        @Override
        public int getCount() {
            return name1.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi=convertView;
            if(convertView==null){
	            vi = mInflater.inflate(R.layout.row, null); 
            }
            tv= (TextView) vi.findViewById(R.id.textView1);
            //tv1= (TextView) vi.findViewById(R.id.textView2); Removed phone numbers from list
            cb = (CheckBox) vi.findViewById(R.id.checkBox1);
            tv.setText(name1.get(position));
            //tv1.setText(phno1.get(position));
            cb.setTag(position);
            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);

            return vi;
    	}
        public boolean isChecked(int position) {
        	return mCheckStates.get(position, false);
        }

		public void setChecked(int position, boolean isChecked) {
			mCheckStates.put(position, isChecked);
		}

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
        	mCheckStates.put((Integer) buttonView.getTag(), isChecked);         
        }   
    }
	
	public void selectDate(){
		DialogFragment dateFragment = new SelectDateFragment();
		dateFragment.show(getFragmentManager(), "DatePicker");
	}
	
	public void selectTime(){
		DialogFragment timeFragment = new SelectTimeFragment();
		timeFragment.show(getFragmentManager(), "DatePicker");
	}
	
	public void populateSetDate(int year, int month, int day) {
		
		Calendar c = Calendar.getInstance();
		c.set(year, month-1,  day);
		Date d = c.getTime();
		String dateString = dateFormatter.format(d);
		
		meetDatePicker.setText(dateString);
		String tMonth = "" + month;
		String tDay = "" + day;
		if(month < 10)
			tMonth = "0" + month;
		if(day < 10)
			tDay = "0" + day;
		
		date = year + "-" + tMonth + "-" + tDay;
		
	}
	
	public void populateSetTime(int hour, int minute){
		String tHour = "" + hour;
		String tMinute = "" + minute;
		if(hour < 10)
			tHour = "0" + hour;
		if(minute < 10)
			tMinute = "0" + minute;
		meetingTime.setText(hour + ":" + tMinute);
		time = tHour + ":" + tMinute + ":00";
	}
	
	@SuppressLint("ValidFragment")
	public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}
		
		public void onDateSet(DatePicker view, int yy, int mm,
				int dd) {
			populateSetDate(yy, mm+1, dd);
		}
		
		
	}
	
	@SuppressLint("ValidFragment")
	public class SelectTimeFragment extends DialogFragment implements OnTimeSetListener{

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int hh = calendar.get(Calendar.HOUR);
			int mm = calendar.get(Calendar.MINUTE);
			boolean timeFormat = DateFormat.is24HourFormat(getActivity());
			return new CustomTimePickerDialog(getActivity(), this, hh, mm, timeFormat, 5);
		}

		public void onTimeSet(TimePicker view, int hour, int minute) {
			populateSetTime(hour, minute);
		}		
	}
	
	
	private class CustomTimePickerDialog extends TimePickerDialog {
		final OnTimeSetListener mCallback;
		TimePicker mTimePicker;
		final int increment;
	
		public CustomTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView, int increment) {
	        super(context, callBack, hourOfDay, minute/increment, is24HourView);
	        this.mCallback = callBack;
	        this.increment = increment;
	    }
	
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
            if (mCallback != null && mTimePicker!=null) {
                mTimePicker.clearFocus();
                mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                        mTimePicker.getCurrentMinute()*increment);
            }
	    }
	
	    @Override
	    protected void onStop() {
	        // override and do nothing
	    }
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        try {
	        	//copied this off stack overflow, no idea how it works
	        	//"reflection" or something
	            Class<?> rClass = Class.forName("com.android.internal.R$id");
	            Field timePicker = rClass.getField("timePicker");
	            this.mTimePicker = (TimePicker)findViewById(timePicker.getInt(null));
	            Field m = rClass.getField("minute");
	
	            NumberPicker mMinuteSpinner = (NumberPicker)mTimePicker.findViewById(m.getInt(null));
	            mMinuteSpinner.setMinValue(0);
	            mMinuteSpinner.setMaxValue((60/increment)-1);
	            List<String> displayedValues = new ArrayList<String>();
	            for(int i=0;i<60;i+=increment) {
	                displayedValues.add(String.format("%02d", i));
	            }
	            mMinuteSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}