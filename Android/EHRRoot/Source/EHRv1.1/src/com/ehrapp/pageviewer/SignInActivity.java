package com.ehrapp.pageviewer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import com.ehrapp.EHRLables;

public class SignInActivity extends AsyncTask<String,Void,String>{
	
		private int _createOrView = 0; 
		private Context _loginViewActivity = null;
		
		public SignInActivity(Context i_loginViewActivity, int i_flag) {
			
			_createOrView = i_flag;
			_loginViewActivity = i_loginViewActivity;
		}
		
		protected void onPreExecute(){
	
		}
	
		@Override
		protected String doInBackground(String... arg0) {
		      if(_createOrView == 0)
		      { //means by Create Account
		    	  try{
				            String username = (String)arg0[0];
				            String password = (String)arg0[1];
				            String op = "Insert";
				            String link="http://gapp.eaemgs.utah.edu/connectionScript.php";
				            String data  = URLEncoder.encode("op", "UTF-8") 
				            + "=" + URLEncoder.encode(op, "UTF-8");
				            data += "&" + URLEncoder.encode("username", "UTF-8") 
				    	    + "=" + URLEncoder.encode(username, "UTF-8");
				            data += "&" + URLEncoder.encode("password", "UTF-8") 
				            + "=" + URLEncoder.encode(password, "UTF-8");
				            URL url = new URL(link);
				            URLConnection conn = url.openConnection(); 
				            conn.setDoOutput(true); 
				            OutputStreamWriter wr = new OutputStreamWriter
				            (conn.getOutputStream()); 
				            wr.write( data ); 
				            wr.flush(); 
				            BufferedReader reader = new BufferedReader
				            (new InputStreamReader(conn.getInputStream()));
				            StringBuilder sb = new StringBuilder();
				            String line = null;
				            // Read Server Response
				            while((line = reader.readLine()) != null)
				            {
				               sb.append(line);
				               break;
				            }
				            return sb.toString();
				         }catch(Exception e){
				            return new String("Exception: " + e.getMessage());
				         }
		      }
		      else
		      {
		         try{
		            String username = (String)arg0[0];
		            String password = (String)arg0[1];
		            String op = "Select";
		            String link="http://gapp.eaemgs.utah.edu/connectionScript.php";
		            String data  = URLEncoder.encode("op", "UTF-8") 
		            + "=" + URLEncoder.encode(op, "UTF-8");
		            data += "&" + URLEncoder.encode("username", "UTF-8") 
		    	    + "=" + URLEncoder.encode(username, "UTF-8");
		            data += "&" + URLEncoder.encode("password", "UTF-8") 
		            + "=" + URLEncoder.encode(password, "UTF-8");
		            URL url = new URL(link);
		            URLConnection conn = url.openConnection(); 
		            conn.setDoOutput(true); 
		            OutputStreamWriter wr = new OutputStreamWriter
		            (conn.getOutputStream()); 
		            wr.write( data ); 
		            wr.flush(); 
		            
		            BufferedReader reader = new BufferedReader
		            (new InputStreamReader(conn.getInputStream()));
		            StringBuilder sb = new StringBuilder();
		            String line = null;
		            // Read Server Response
		            while((line = reader.readLine()) != null)
		            {
		               sb.append(line);
		               break;
		            }
		            return sb.toString();
		         }catch(Exception e){
		            return new String("Exception: " + e.getMessage());
		         }
		      }
		}
		@Override
		protected void onPostExecute(String result){
			
			if(result != null)
			{
	            Toast.makeText(_loginViewActivity, "Redirecting...", 
	                    Toast.LENGTH_SHORT).show();
	            Intent i = new Intent(_loginViewActivity, PickPages.class);
	            JSONObject jsonObj;
	            try
	            {
	            	jsonObj = new JSONObject(result);
	            	String tUserName = jsonObj.getString(EHRLables.NAME);
	            	String tRole = jsonObj.getString(EHRLables.ROLE);
	            	i.putExtra("Name", tUserName);
	            	i.putExtra("Role", tRole);
	            	_loginViewActivity.startActivity(i);
	            }
	            catch(JSONException e)
	            {
	            	Log.e("JSON Parser", "Error parsing data " + e.toString());
	            }
	            
	            
	            
	            
			}
			
		   //this.statusField.setText("Login Successful");
		   //this.roleField.setText(result);
		}
}
