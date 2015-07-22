package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ehrapp.EHRLables;
import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.AccountService;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.dataservice.ProfileService;

public class Page_Login extends Activity  {

	   private EditText  username=null;
	   private EditText  password=null;
	   private TextView Login = null;
	   private ImageButton _forgotPassword, _infoButton, _englishButton, _frenshButton, _spanishButton;
	   
	   private DatabaseManager _db;
	   private Global _globals;
  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);
        
        username = (EditText)findViewById(R.id.Username);
        password = (EditText)findViewById(R.id.Password);
        Login = (TextView)findViewById(R.id.CreateNewAccount);
        
        _forgotPassword = (ImageButton)findViewById(R.id.ForgotPassword);
        _infoButton = (ImageButton) findViewById(R.id.infoBtn);
        _englishButton = (ImageButton)findViewById(R.id.engBtn);
        _frenshButton = (ImageButton)findViewById(R.id.frBtn);
        _spanishButton = (ImageButton)findViewById(R.id.esBtn);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        _globals = ((Global)getApplicationContext());
        _globals.ClearGlobals();
        _db = _globals.GetDatabase();

        AddListenerOnButton();
        
        //Set Fonts
        Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
        Login.setTypeface(titleFont);
        username.setTypeface(textFont);
        password.setTypeface(textFont);
    }
    
    private void AddListenerOnButton() {
    	
    	_forgotPassword.setOnClickListener(new OnClickListener() {
    		 
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Page_Login.this, Page_ForgotPassword.class);
			    startActivity(i);
			}
		});
    	
    	 _infoButton.setOnClickListener(new OnClickListener() {
    		 
             @Override
             public void onClick(View v) {
             		openinfoPage();
             }
           });
         
         _englishButton.setOnClickListener(new OnClickListener() {
         	
             @Override
             public void onClick(View v) {
             	setLocale("en");
             }
           });
         _frenshButton.setOnClickListener(new OnClickListener() {
         	
             @Override
             public void onClick(View v) {
             	setLocale("fr");
             }
           });
         _spanishButton.setOnClickListener(new OnClickListener() {
         	
             @Override
             public void onClick(View v) {
             	setLocale("es");
             }
           });
    }
    
    //For language change, page refresh with new language
    private void setLocale(String lang) {
    	 
    	Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, Page_Login.class);
        startActivity(refresh);
    }
    
 	private void openinfoPage()
 	{
 		Intent i = new Intent(Page_Login.this, Page_Info.class);
	    startActivity(i);
  	}

    //login with username and password
 	public void login(View view)
    {  
        String user= username.getText().toString();
        String pwd = password.getText().toString();
        
		 _globals.GetAccount().username = user;
		 _globals.GetAccount().password = pwd;
		 
        //check the username and password list in the database and then fetch the profile based on the profileID
        ArrayList<String> columns = new ArrayList<String>();
		columns.add("userName");
		columns.add("password");
		//_db.printAllValues("user_accounts");
		ArrayList<ArrayList<Object>> data = _db.getAllRowsAsArrays("user_accounts");
        boolean loginCheck = false;
        
		for (int position=0; position < data.size(); position++)
    	{
    		ArrayList<Object> row = data.get(position);
    		for(int i = 0; i < row.size(); i++)
    		{
    			if(user.equals(row.get(3).toString()) && pwd.equals(row.get(4).toString()))
    			{
    				_globals.GetProfile().ProfileID = Integer.parseInt(row.get(0).toString());
    				_globals.GetAccount().cloudID = Integer.parseInt(row.get(1).toString());
    				loginCheck = true;
    				break;
    			}
    		}
    	}
		
		//if the user passes local check, we directly fetch the profile from the local table and bypass the cloud check.
        if(loginCheck)
        {
        	 SetProfileValues();
        	 Toast.makeText(getApplicationContext(), "Redirecting...", 
	                    Toast.LENGTH_SHORT).show();
	         Intent i = new Intent(Page_Login.this, Page_Patient_History.class);
	         startActivity(i);
	    }
	    else
	    {
	    	if(Global.CheckInternetConnection(this))
	    	{
	    		//start cloud login check.
	    		new AccountService(this, EHRLables.AccountServiceAction.Authenticate, _globals).execute();
	    	}
	    	else
	    	{
		        Toast.makeText(getApplicationContext(), "No internet... Wrong Credentials",
		    	        Toast.LENGTH_SHORT).show();
		    	        password.setText("");
	    	}
	     }
    }
    
    public void ServerResponse_DownloadProfile(JSONObject i_response)
    {
		 try
		 {
			 String result = i_response.getString("Result");
			 if(result.compareTo("Success") == 0)
			 {
				JSONObject errMessage = (JSONObject)i_response.get("Message");
				 
				Server_SetProfileValues(errMessage);

			    Map<String, String> columns = new HashMap<String,String>();
			    columns.put("firstname", "text");
			    columns.put("lastname", "text");
			    columns.put("date_of_birth", "long");
			    columns.put("country", "text");
			    columns.put("race", "text");
			    columns.put("gender", "int");
			    columns.put("profilePic_path", "text");
			    columns.put("is_cloud", "integer");

			    if(!_db.isTableExists("profile_table"))
			    {
					//System.out.println("Table does not exists!");
					_db.createTablewithForeignKey("profile_table",columns,"user_accounts");
				}
			        
				 Map<String, Object> columnsValues = new HashMap<String,Object>();
				 columnsValues.put("firstname", _globals.GetProfile().FirstName);
				 columnsValues.put("lastname", _globals.GetProfile().LastName);
				 columnsValues.put("date_of_birth", _globals.GetProfile().DOB);
				 columnsValues.put("country", _globals.GetProfile().Country);
				 columnsValues.put("gender", _globals.GetProfile().Gender);
				 columnsValues.put("race", _globals.GetProfile().Race);
				 columnsValues.put("profileID_fk",String.valueOf(_globals.GetProfile().ProfileID));
				 columnsValues.put("is_cloud",1);

				 _db.addRow(columnsValues,"profile_table");
				 //_db.printAllValues("profile_table");
				 
				 Toast.makeText(getApplicationContext(), "Success: Content: " + (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
				
				 //Display the patient history
		         Intent i = new Intent(Page_Login.this, Page_Patient_History.class);
		         startActivity(i);
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
    
    public void ServerResponse_LoginCheck(JSONObject i_response)
    {
		 try
		 {
			 Integer UserId = -1;
			 Boolean isPrivate = false;
			 Boolean hasProfile = false;
			 String result = i_response.getString("Result");
			
			 if(result.compareTo("Success") == 0)
			 {
				 JSONObject errMessage = (JSONObject)i_response.get("Message");
				 UserId = Integer.valueOf((String)errMessage.get("UserID"));
				 isPrivate = Integer.valueOf((String)errMessage.get("IsPrivate"))==1? true: false;
				 hasProfile = Integer.valueOf((String)errMessage.get("HasProfile"))==1? true: false;
				 _globals.GetAccount().securityQ = errMessage.getString("SecurityQuestion");
				 _globals.GetAccount().securityA = errMessage.getString("SecurityAnswer");
				 _globals.GetAccount().cloudID = UserId;
				 _globals.GetAccount().isPrivate = isPrivate;
				 _globals.GetAccount().hasProfile = hasProfile;
				 
				 Toast.makeText(getApplicationContext(), "Success: UserID: " + UserId + "Content: " + (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
				 
			      Map<String, String> columns = new HashMap<String,String>();
			      columns.put("username", "text");
			      columns.put("password", "text");
			      columns.put("securityQ", "text");
			      columns.put("securityA", "text");
			      columns.put("cloud_id", "integer");
			      columns.put("is_cloud", "boolean");
			      _db = new DatabaseManager(this);  
				 if(!_db.isTableExists("user_accounts"))
				 {
					//System.out.println("Table does not exists!");
					_db.createTable("user_accounts",columns);
				 }
				 Map<String, Object> columnsValues = new HashMap<String,Object>();
				 columnsValues.put("username", _globals.GetAccount().username);
				 columnsValues.put("password", _globals.GetAccount().password);
				 columnsValues.put("securityQ", _globals.GetAccount().securityQ);
				 columnsValues.put("securityA", _globals.GetAccount().securityA);
				 columnsValues.put("cloud_id", _globals.GetAccount().cloudID);
				 columnsValues.put("is_cloud", true);
				 
				 _db.addRow(columnsValues,"user_accounts");
				 //_db.printAllValues("user_accounts");
				 _globals.GetProfile().ProfileID = _db.getLastInsertedRow("user_accounts");
				 //if the guy doesn't have a profile, goto create profile page, otherwise get the profile from the cloud and do stuff.
				 if(hasProfile == true)
				 {
					 //now we should get the profile and EHR data from the cloud.
					 new ProfileService(this, EHRLables.ProfileServiceAction.DownloadProfile, isPrivate, _globals).execute();
				 }
				 else
				 {
			         Intent i = new Intent(Page_Login.this, Page_CreateProfile.class);
			         startActivity(i);					 
				 }
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
    
    //go to create account page
    public void CreateAccount(View view)
    {
        Toast.makeText(getApplicationContext(), "Redirecting...", 
                    Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Page_Login.this, Page_CreateAccount.class);
        startActivity(i);
    }
    
    public void Server_SetProfileValues(JSONObject i_object)
    {
    	try
    	{
    		if(_globals.GetAccount().isPrivate == false)
    		{
    			_globals.GetProfile().FirstName = (String)(i_object.get("FirstName"));
    			_globals.GetProfile().LastName = (String)(i_object.get("LastName"));
    		}
    		_globals.GetProfile().DOB = Long.parseLong((String)(i_object.get("DateOfBirth")));
    		_globals.GetProfile().Race = (String)(i_object.get("Race"));
    		_globals.GetProfile().Country = (String)(i_object.get("Country"));
    		_globals.GetProfile().Gender = (Boolean)(i_object.get("Gender"));
    	}
		 catch(JSONException e)
		 {
			 // do nothing
		 }
    }
    
    //Once logged in, set the global profile values
    private void SetProfileValues()
	{
    	ArrayList<ArrayList<Object>> data = _db.getAllRowsAsArrays("profile_table");
    	//_db.printAllValues("profile_table");
	    for (int position=0; position < data.size(); position++)
	    {
	    	ArrayList<Object> row = data.get(position);
	    	for(int i = 0; i < row.size(); i++)
	    	{	
	    		 //System.out.println("Local profile ID: " + Integer.parseInt(row.get(_db.GetColumnCount("profile_table", "profileID_fk")).toString()));
	    		 //System.out.println("Global profile ID: " +_globals.GetProfile().ProfileID);
	    		if(Integer.parseInt(row.get(_db.GetColumnCount("profile_table", "profileID_fk")).toString()) == _globals.GetProfile().ProfileID)
	    		{
	    			_globals.GetProfile().FirstName = row.get(_db.GetColumnCount("profile_table", "firstname")).toString();	    			
	    			_globals.GetProfile().LastName = row.get(_db.GetColumnCount("profile_table", "lastname")).toString();
	    			_globals.GetProfile().DOB = Long.parseLong(row.get(_db.GetColumnCount("profile_table", "date_of_birth")).toString());
	    			_globals.GetProfile().Race = row.get(_db.GetColumnCount("profile_table", "race")).toString();
	    			_globals.GetProfile().Country = row.get(_db.GetColumnCount("profile_table", "country")).toString();
	    			int getGenderValue = Integer.parseInt((String) row.get(_db.GetColumnCount("profile_table", "gender")));
	    			
	    			if(getGenderValue == 1)
	    				_globals.GetProfile().Gender = true;
	    			else
	    				_globals.GetProfile().Gender = false;
		    		if(row.get(_db.GetColumnCount("profile_table", "profilePic_path")) != null)
		    		{
		    			_globals.GetProfile().profilePic_path = row.get(_db.GetColumnCount("profile_table", "profilePic_path")).toString();
		    		}
	    		}
	    	}
	    }
	 }
}
