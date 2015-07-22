package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.List;

import com.ehrapp.R;
import com.ehrapp.dataservice.DatabaseManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LoginView extends Activity {

	   private EditText  username=null;
	   private EditText  password=null;
	   //private TextView attempts;
	   //private Button login;
	   //int counter = 3;
	   DatabaseManager db;
  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);
        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        //login = (Button)findViewById(R.id.button1);
        db = new DatabaseManager(this,"login_table");
        //db.add
        
    }
    
    //login with username and password
    public void login(View view)
    {  
        String user= username.getText().toString();
        String pwd = password.getText().toString();
        
        //TODO: check the username and password list in the database and then fetch the profile based on the profileID
        ArrayList<String> columns = new ArrayList<String>();
		columns.add("userName");
		columns.add("password");
        ArrayList<ArrayList<Object>> data = db.getAllRowsAsArrays("user_accounts",columns);
        boolean loginCheck = false;
		for (int position=0; position < data.size(); position++)
    	{
    		ArrayList<Object> row = data.get(position);
    		if(user.equals(row.get(0).toString()) && pwd.equals(row.get(1).toString()))
    		{
    			loginCheck = true;
    			break;
    		}
    	}
		
        if(loginCheck)
        {
        	 Toast.makeText(getApplicationContext(), "Redirecting...", 
	                    Toast.LENGTH_SHORT).show();
	         Intent i = new Intent(LoginView.this, PickPages.class);
	            startActivity(i);
	    }	
	    else{
	        Toast.makeText(getApplicationContext(), "Wrong Credentials",
	        Toast.LENGTH_SHORT).show();
	        password.setText("");
	        //attempts.setBackgroundColor(Color.RED);	
	        //counter--;
	        //attempts.setText(Integer.toString(counter));
	        //if(counter==0){
	        //   login.setEnabled(false);
	        //}
	     }
    }
    
    
    //go to create account page
    public void CreateAccount(View view)
    {
        Toast.makeText(getApplicationContext(), "Redirecting...", 
                    Toast.LENGTH_SHORT).show();
        Intent i = new Intent(LoginView.this, Page_CreateAccount.class);
        startActivity(i);
    }
}
