package com.JS.emittanceapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class CreateMeetingActivity extends Activity implements OnItemClickListener{
	protected static final String TAG = "CreateMeeting";
	List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    private EditText meetingName;
    private EditText meetingTime;
    MyAdapter ma ;
    Button select;
	
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
             
             meetingTime = (EditText) findViewById(R.id.meetingTimeValue);
             String meetingTimeString = meetingTime.getText().toString();
             Log.d(TAG, meetingNameString + " " + meetingTimeString);

             Intent intent = new Intent(CreateMeetingActivity.this, NewMeetingLocationActivity.class);
             intent.putExtra("checkedContacts", checkedcontacts);
             intent.putExtra("meetingName", meetingNameString);
             intent.putExtra("meetingTime", meetingTimeString);
             startActivity(intent);
                //Toast.makeText(CreateMeetingActivity.this, checkedcontacts,1000).show();
            }
        });
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
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
		// TODO Auto-generated method stub
		ma.toggle(arg2);
		
	}
	
    public  void getAllContacts(ContentResolver cr) {

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

	class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
    {  private SparseBooleanArray mCheckStates;
       LayoutInflater mInflater;
        TextView tv1,tv;
        CheckBox cb;
        MyAdapter()
        {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater) CreateMeetingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name1.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi=convertView;
            if(convertView==null)
             vi = mInflater.inflate(R.layout.row, null); 
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
            // TODO Auto-generated method stub

             mCheckStates.put((Integer) buttonView.getTag(), isChecked);         
        }   
    }   
}
