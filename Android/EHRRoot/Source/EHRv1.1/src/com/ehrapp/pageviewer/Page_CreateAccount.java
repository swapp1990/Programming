package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehrapp.EHRLables;
import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.AccountService;
import com.ehrapp.dataservice.DatabaseManager;

public class Page_CreateAccount extends Activity{
	
	 private EditText _usernameField=null;
	 private EditText _passwordField=null;
	 private EditText _confirmpasswordField=null;
	 private EditText _securityQ=null;
	 private EditText _securityA=null;
	 private CheckBox _cloudCheckBox = null;
	 private TextView _createNewAccount = null;
	 
	 private Global _globals;
	 private DatabaseManager _db;
	 
	 private int _cloudId;
	 private boolean _isCloud;
	 
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.createaccount_layout);
        
	        _usernameField 			= (EditText)findViewById(R.id.Username);
	        _passwordField 			= (EditText)findViewById(R.id.Password);
	        _confirmpasswordField 	= (EditText)findViewById(R.id.confirm);
	        _securityQ              = (EditText)findViewById(R.id.securityQ);
	        _securityA              = (EditText)findViewById(R.id.securityA);
	        _cloudCheckBox 			= (CheckBox)findViewById(R.id.cbx_Cloud);
	        _createNewAccount 		= (TextView)findViewById(R.id.CreateNewAccount);
	        
	        //Comment this if u want to enable cloud functionality
	        _cloudCheckBox.setEnabled(false);
	        _cloudCheckBox.setVisibility(View.GONE);
	        
	        _globals = ((Global)getApplicationContext());
	        _cloudId = -1;
	        _isCloud = false;
	        
	        Map<String, String> columns = new HashMap<String,String>();
	        columns.put("username", "text");
	        columns.put("password", "text");
	        columns.put("securityQ", "text");
	        columns.put("securityA", "text");
	        columns.put("cloud_id", "integer");
	        columns.put("is_cloud", "boolean");
	        _db = _globals.GetDatabase();
	        if(!_db.isTableExists("user_accounts"))
			{
				System.out.println("Table does not exists!");
				_db.createTable("user_accounts",columns);
			}
	        
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // for scrolling when soft keyboard is up
	        
	        Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
	        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
	        _createNewAccount.setTypeface(titleFont);
	        _usernameField.setTypeface(textFont);
	        _passwordField.setTypeface(textFont);
	        _confirmpasswordField.setTypeface(textFont);
	        _securityQ.setTypeface(textFont);
	        _securityA.setTypeface(textFont);
	 }
	 
	 public void onCreateClicked(View view)
	 {
		 String username = _usernameField.getText().toString();
		 String password = _passwordField.getText().toString();
		 String confirmpassword = _confirmpasswordField.getText().toString();
		 String securityQ = _securityQ.getText().toString();
		 String securityA = _securityA.getText().toString();
		 
		 if(username == null || password == null|| confirmpassword == null || securityQ == null || securityA == null)
		 {
			 Toast.makeText(getApplicationContext(), "You can't leave any field empty", Toast.LENGTH_SHORT).show();
			 return;			 
		 }
		 
		 if(CheckIfUsernameExistsInDatabase(username))
		 {
			 Toast.makeText(getApplicationContext(), "Username Already Exists", Toast.LENGTH_SHORT).show();
			 return;
		 }
		 
		 if(!password.equals(confirmpassword))
		 {
			 Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
			 return;			 
		 }
		 	 
		 //cache the account since we probably need the username to find the row to set the profileID
		 CacheUserAccount();
		 _globals.ClearProfile();

		 if(_cloudCheckBox.isChecked())
		 {
			 if(!Global.CheckInternetConnection(this))
			 {
				 Toast.makeText(getApplicationContext(), "You can't create a cloud user when there is no internet.", Toast.LENGTH_SHORT).show();
				 return;						 
			 }
			 Toast.makeText(getApplicationContext(), "Saving Account...", Toast.LENGTH_SHORT).show();
			 new AccountService(this, EHRLables.AccountServiceAction.CreateAccount, _globals).execute();
			 //JSONObject response = AccountService.StoreAccountOntoTheCloud(username, password); 		 
		 }
		 else
		 {
			 StoreUsernameAndPasswordIntoLocalDataabase();
			 	 
			 //redirect the user to the create profile page
			 Toast.makeText(getApplicationContext(), "Saving Account...", Toast.LENGTH_SHORT).show();
			 
			 this.finish();
			 
		     Intent i = new Intent(Page_CreateAccount.this, Page_CreateProfile.class);
		     startActivity(i);
		 }
	 }
    
	 public void ServerResponse_CreateAccount(JSONObject i_response)
	 {
		 try
		 {
			 Integer UserId = -1;
			 //System.out.println("Received Json: " + (CharSequence)i_response.getString("Message"));
			 String result = i_response.getString("Result");
			 if(result.compareTo("Success") == 0)
			 {
				 JSONObject errMessage = (JSONObject)i_response.get("Message");
				 UserId = Integer.valueOf((String)errMessage.get("UserID"));
				 _cloudId = UserId;
				 //cache cloud id so I could use it in profile settings.
				 _globals.GetAccount().cloudID = UserId;
				 //store this userid into the local account table;
				 //System.out.println("Success Account");
				 Toast.makeText(getApplicationContext(), "Success: UserID: " + UserId + "Content: " + (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
				 StoreUsernameAndPasswordIntoLocalDataabase();
			     Intent i = new Intent(Page_CreateAccount.this, Page_CreateProfile.class);
			     startActivity(i);
			 }
			 else if(result.compareTo("Fail") == 0)
			 {
				 JSONObject errMessage = (JSONObject)i_response.get("Message");
				 Toast.makeText(getApplicationContext(), "Fail: ErrorCode: " + (Integer)(errMessage.get("ErrorCode")) + "Content: "+ (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
			     //Intent i = new Intent(Page_CreateAccount.this, Page_Login.class);
			     //System.out.println("Failed Account");
			     //startActivity(i);
			 }
		 }
		 catch(JSONException e)
		 {
			 
		 }
		 
	 }
	 
	 //table - users
	 //field1 - username
	 //field2 - password
	 //field3 - profileID - we don't set it here, we set it in the create profile page when the profile is created
	 private void StoreUsernameAndPasswordIntoLocalDataabase()
	 {
		 String Susr = _usernameField.getText().toString();
		 String Spwd = _passwordField.getText().toString();
		 String SsecurityQ = _securityQ.getText().toString();
		 String SsecurityA = _securityA.getText().toString();
		
		 Map<String, Object> columnsValues = new HashMap<String,Object>();
		 columnsValues.put("username", Susr);
		 columnsValues.put("password", Spwd);
		 columnsValues.put("securityQ", SsecurityQ);
		 columnsValues.put("securityA", SsecurityA);
		 columnsValues.put("cloud_id", _cloudId);
		 columnsValues.put("is_cloud", _isCloud);
		 
		 _db.addRow(columnsValues,"user_accounts");
		 _db.printAllValues("user_accounts");
		 _globals.GetProfile().ProfileID = _db.getLastInsertedRow("user_accounts");
	 }
	 
	 private boolean CheckIfUsernameExistsInDatabase(String i_username)
	 {
		 ArrayList<Object> data = _db.getRowsAsArray("user_accounts","userName");
		 for (int position=0; position < data.size(); position++)
	     {
	    	Object row = data.get(position);
	    	String rowValue = row.toString();
	    	if(rowValue.equals(i_username))
	    	{
	    		return true;
	    	}
	     }
		 return false;
	 }
	 	 
	 public void onCancelClicked(View view)
	 {
		 this.finish();
		 Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
	     Intent i = new Intent(Page_CreateAccount.this, Page_Login.class);
	     startActivity(i);
	 }
	 
	 private void CacheUserAccount()
	 {
		 _globals.GetAccount().username = _usernameField.getText().toString();
		 _globals.GetAccount().password = _passwordField.getText().toString();
		 _globals.GetAccount().securityQ = _securityQ.getText().toString();
		 _globals.GetAccount().securityA = _securityA.getText().toString();
	 }
	 
	 @Override
	 public void onBackPressed() {
	     super.onBackPressed();
	     this.finish();
	 }
}
