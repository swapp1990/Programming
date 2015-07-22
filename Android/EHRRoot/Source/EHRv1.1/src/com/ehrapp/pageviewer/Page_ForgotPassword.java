package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ehrapp.EHRLables;
import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.AccountService;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.dialog.DatePickerFragment;

public class Page_ForgotPassword extends FragmentActivity implements OnDateSetListener {
		private ImageButton submit1,submit2, backButton;
		private EditText username=null;
		private EditText firstName=null;
		private EditText lastName=null;
		private EditText securityAnswerT=null;
		private TextView reminderTxt = null;
		private TextView _showDOB = null;
		
		private int profileId_;
		private boolean _reminderFound;
		
		private Global _globals;
		private DatabaseManager _db;
		
		private String securityAnswer;
		private String origUserName, origPassword;
		private TextView origUserNameT, origPasswordT = null;
		private TextView UserNamePop, PasswordPop, recovery, or;
		
		private Calendar _dob = null;
		private int _year = 0;
		private int _month = 0;
		private int _day = 0;
		private Point p;
		
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.forgot_password_recovery);
	        
	        _globals = ((Global)getApplicationContext());
	        _db = _globals.GetDatabase();
	        
	        _reminderFound = false;
	        profileId_ = 0;
	        
	        submit1 = (ImageButton) findViewById(R.id.infoBtn);
	        submit2 = (ImageButton) findViewById(R.id.submit2);
	        backButton = (ImageButton) findViewById(R.id.allergiesAdd);
	        reminderTxt = (TextView) findViewById(R.id.reminder);
	        _showDOB = (TextView) findViewById(R.id.showDOB);
	        username = (EditText)findViewById(R.id.userName);
	        firstName = (EditText)findViewById(R.id.firstName);
	        lastName = (EditText)findViewById(R.id.lastName);
	        securityAnswerT = (EditText)findViewById(R.id.securityAnswer);
	        recovery = (TextView)findViewById(R.id.CreateNewAccount);
	        or = (TextView)findViewById(R.id.origUN);
	        
	        _dob = Calendar.getInstance();
	        
	        submit1.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            		SearchDatabaseforReminder();
	            }
	          });
	        
	        submit2.setOnClickListener(new OnClickListener() {
	            
	            @Override
	            public void onClick(View v) {
	            		if(_reminderFound)
	            			SearchDatabaseforPassword();
	            }
	          });
	        
	        backButton.setOnClickListener(new OnClickListener() {
	            
	            @Override
	            public void onClick(View v) {
	       		 	Intent i = new Intent(Page_ForgotPassword.this, Page_Login.class);
	       		 	startActivity(i);
	            }
	          });
	        
	        _showDOB.setOnClickListener(new OnClickListener() {
	            
	            @Override
	            public void onClick(View v) {
	            	showDateDialog();
	            }
	          });
	        
	        Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
	        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");

	        recovery.setTypeface(titleFont);
	        username.setTypeface(textFont);
	        firstName.setTypeface(textFont);
	        lastName.setTypeface(textFont);
	        securityAnswerT.setTypeface(textFont);
	        reminderTxt.setTypeface(textFont);
	        _showDOB.setTypeface(textFont);
	        or.setTypeface(textFont);
	 }

	 // Get the x and y position after the button is draw on screen
	 // (It's important to note that we can't get the position in the onCreate(),
	 // because at that stage most probably the view isn't drawn yet, so it will return (0, 0))
	 @Override
	 public void onWindowFocusChanged(boolean hasFocus) {
		 
	    int[] location = new int[2];
	    ImageButton button = (ImageButton) findViewById(R.id.submit2);
	  
	    // Get the x, y location and store it in the location[] array
	    // location[0] = x, location[1] = y.
	    button.getLocationOnScreen(location);
	  
	    //Initialize the Point with x, and y positions
	    p = new Point();
	    p.x = location[0];
	    p.y = location[1];
	 }

	 public void SearchDatabaseforReminder()
	 {
		 String firstNameS = firstName.getText().toString();
		 ArrayList<ArrayList<Object>> data = _db.getAllRowsAsArrays("profile_table");
		 for (int position=0; position < data.size(); position++)
		 {
			 ArrayList<Object> row = data.get(position);
			 if(firstNameS.equals(row.get(_db.GetColumnCount("profile_table", "firstname")).toString()))
			 {
		    	profileId_ = Integer.parseInt(row.get(_db.GetColumnCount("profile_table", "profileID_fk")).toString());
			 }
		 }
	
		ArrayList<ArrayList<Object>> data2 = _db.getAllRowsAsArrays("user_accounts");
		for (int position=0; position < data2.size(); position++)
		{
			ArrayList<Object> row = data2.get(position);
			if(profileId_ == Integer.parseInt(row.get(0).toString()))
			{
				String reminderS = row.get(6).toString();
				securityAnswer = row.get(5).toString();
				origUserName = row.get(3).toString();
				origPassword = row.get(4).toString();
				reminderTxt.setText(reminderS);
				_reminderFound = true;
			}
		}
	
		if(!_reminderFound)
		{
			SearchServerforReminder();
		}
	 }

	 private void SearchServerforReminder()
       {
			_globals.GetAccount().username = username.getText().toString();
			String firstNameS = firstName.getText().toString();
			String lastNameS = lastName.getText().toString();
			Long timestamp = _dob.getTimeInMillis();
			new AccountService(this, EHRLables.AccountServiceAction.GetSecurityQuestion, _globals).execute(firstNameS, lastNameS, timestamp.toString());
	   }

	  public void SearchDatabaseforPassword()
		{
			if(securityAnswer.equals(securityAnswerT.getText().toString()))
			{
				if (p != null)
			       showPopup(Page_ForgotPassword.this, p);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Fail: Wrong Answer!", Toast.LENGTH_SHORT).show();
			}
		}

	   public void ServerResponse_GetReminder(JSONObject i_response)
		{
			 try
			 {
				 String result = i_response.getString("Result");

				 if(result.compareTo("Success") == 0)
				 {
					 JSONObject errMessage = (JSONObject)i_response.get("Message");
					 String reminderS = errMessage.getString("SecurityQuestion");
					 securityAnswer = errMessage.getString("SecurityAnswer");
					 origUserName = errMessage.getString("AccountName");
					 origPassword = errMessage.getString("Password");
					 reminderTxt.setText(reminderS);		
					 _reminderFound = true;
					 Toast.makeText(getApplicationContext(), "Success: Content: " + (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
				 }
				 else if(result.compareTo("Fail") == 0)
				 {
					 JSONObject errMessage = (JSONObject)i_response.get("Message");
					 Toast.makeText(getApplicationContext(), "Fail: ErrorCode: " + (Integer)(errMessage.get("ErrorCode")) + "Content: "+ (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
				 }	     
			 }
			 catch(JSONException e)
			 {
				 System.out.println(e.getMessage());
			 }
		}

	private void showDateDialog(){

	    FragmentManager fm = getSupportFragmentManager();
	    DatePickerFragment newFragment = new DatePickerFragment();
	    newFragment.show(fm, "date_picker");
	}

	// when dialog box is closed, below method will be called.
	@Override
	public void onDateSet(DatePicker view, int selectedYear,
			int selectedMonth, int selectedDay) {
		_year = selectedYear;
		_month = selectedMonth;
		_day = selectedDay;
	    _dob.clear();
		_dob.set(_year, _month, _day, 0, 0, 0);
	
		// set selected date into textview
		_showDOB.setText(new StringBuilder().append(_month + 1)
		   .append("-").append(_day).append("-").append(_year)
		   .append(" "));
	}


	//The method that displays the popup for the lost password.
	private void showPopup(final Activity context, Point p) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	 
	   // Inflate the popup_layout.xml
	   LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
	   LayoutInflater layoutInflater = (LayoutInflater) context
	     .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	   View layout = layoutInflater.inflate(R.layout.popup_forgopw_layout, viewGroup);
	   // Creating the PopupWindow
	   final PopupWindow popup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
	   popup.setFocusable(true);
	 
	   // Clear the default translucent background
	   popup.setBackgroundDrawable(new BitmapDrawable());
	 
	   // Displaying the popup at the specified location, + offsets.
	   popup.showAtLocation(layout, Gravity.CENTER,0,0);
	 
	   origUserNameT = (TextView) layout.findViewById(R.id.origUN);
	   origPasswordT = (TextView) layout.findViewById(R.id.origPW);
	   origUserNameT.setText(origUserName);
	   origPasswordT.setText(origPassword);
	   
	   UserNamePop = (TextView) layout.findViewById(R.id.FamilyHistoryTracker);
	   PasswordPop = (TextView) layout.findViewById(R.id.CreateNewAccount);
	   Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
	   origUserNameT.setTypeface(tf);
	   origPasswordT.setTypeface(tf);
	   UserNamePop.setTypeface(tf);
	   PasswordPop.setTypeface(tf);
	   // Getting a reference to Close button, and close the popup when clicked.
	   Button close = (Button) layout.findViewById(R.id.close);
	   close.setOnClickListener(new OnClickListener() {
	 
	     @Override
	     public void onClick(View v) {
	       popup.dismiss();
	     }
	   });
	}

}


