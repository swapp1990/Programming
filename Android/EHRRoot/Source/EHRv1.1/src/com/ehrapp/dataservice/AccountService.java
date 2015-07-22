package com.ehrapp.dataservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.ehrapp.EHRConfig;
import com.ehrapp.EHRLables;
import com.ehrapp.Global;
import com.ehrapp.EHRLables.AccountServiceAction;
import com.ehrapp.pageviewer.Page_Login;
import com.ehrapp.pageviewer.Page_CreateAccount;
import com.ehrapp.pageviewer.Page_ForgotPassword;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AccountService extends AsyncTask<String,Void,JSONObject> {

	private Activity _myPage = null;
	private AccountServiceAction _action = null;
	private Global _globals;
	
	
	//Activity is the current page that's instantiating this class;
	//Globals is the global data.
	public AccountService(Activity i_activity, AccountServiceAction i_action, Global i_globals)
	{
		_action = i_action;
		_myPage =  i_activity;
		_globals = i_globals;
	}
	
	@Override
	protected JSONObject doInBackground(String... arg0) 
	{
		JSONObject jsonObj = null;
		JSONObject m1 = null;


        JSONObject json = new JSONObject();
        
        String username = null;
        String password = null;
        String firstName = null;
        String lastName = null;
        Long DOB = null;

        
        try 
        {
	        switch(_action)
	        {
	        case Authenticate:
	            username = _globals.GetAccount().username;
	            password = _globals.GetAccount().password;
	        	json.put("Operation", "Authentication");
				json.put("Username", username);
				json.put("Password", password);
	        	break;
	        case CreateAccount:
	            username = _globals.GetAccount().username;
	            password = _globals.GetAccount().password;
	            String securityQ = _globals.GetAccount().securityQ;
	            String securityA = _globals.GetAccount().securityA;
	        	json.put("Operation", "CreateAccount");
				json.put("Username", username);
				json.put("Password", password);	        	
				json.put("SecurityQ", securityQ);
				json.put("SecurityA", securityA);
				break;
	        case GetSecurityQuestion:
	            firstName = arg0[0];
	            lastName = arg0[1];
	            DOB = Long.parseLong(arg0[2]);
	            username = _globals.GetAccount().username;
	            json.put("Operation", "GetSecurityQuestion");
	            if(username.isEmpty())
	            {
	            	json.put("UseUserName", false);
	            	json.put("Username", "Fake");
	            	json.put("FirstName", firstName);
	            	json.put("LastName", lastName);
	            	json.put("DOB", DOB);
	            }
	            else
	            {
	            	json.put("UseUserName", true);
	            	json.put("Username", username);
	            	json.put("FirstName", "Fake");
	            	json.put("LastName", "Fake");
	            	json.put("DOB", 00);
	            }
	        	break;
	        case Recover:
	            firstName = arg0[0];
	            lastName = arg0[1];
	            DOB = Long.parseLong(arg0[2]);
	            username = _globals.GetAccount().username;
	        	securityA = _globals.GetAccount().securityA;
	        	json.put("Operation", "Recover");
	            if(username == null)
	            {
	            	json.put("UseUserName", false);
	            	json.put("FirstName", firstName);
	            	json.put("LastName", lastName);
	            	json.put("DOB", DOB);
	            }
	            else
	            {
	            	json.put("UseUserName", true);
	            	json.put("Username", username);
	            }
	            json.put("SecurityA", securityA);
	            
	        	break;
	        }
        	
			//System.out.println("Output json: " + json.toString());
			
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 0);
			HttpConnectionParams.setSoTimeout(httpParams, 0);
			HttpClient client = new DefaultHttpClient(httpParams);
			
			String url = EHRConfig.ServerAddress + EHRConfig.AccountServerScript;
						
			HttpPost request = new HttpPost(url);
			request.setEntity(new ByteArrayEntity(json.toString().getBytes(
					"UTF8")));
			request.setHeader("json", json.toString());
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				String jsonText = Global.GetStringFromInputStream(instream);
				//System.out.println("Input json: " + jsonText);
				jsonObj = new JSONObject(jsonText);
			}
			// If the entity is null, you are not getting the message from the server!
			else
			{
				jsonObj = new JSONObject();
				jsonObj.put("Result", "Fail");
				m1 = new JSONObject();
				m1.put("ErrorCode", -1);
				m1.put("Content", "Entity is empty");
				jsonObj.put("Message", m1);
			}
		} 
		catch (Throwable t) 
		{
			try 
			{
				System.out.println(t.toString());
				jsonObj = new JSONObject();
				jsonObj.put("Result", "Fail");
				m1 = new JSONObject();
				m1.put("ErrorCode", -1);
				m1.put("Content", t.toString());
				jsonObj.put("Message", m1);
			} 
			catch (JSONException ex) 
			{
				// do nothing
			}
		}
        
		return jsonObj;		
	}
	
	@Override
	protected void onPostExecute(JSONObject i_result)
	{
        switch(_action)
        {
        	case Authenticate:
        		((Page_Login)_myPage).ServerResponse_LoginCheck(i_result);
        		break;
        	case CreateAccount: 		
        		((Page_CreateAccount)_myPage).ServerResponse_CreateAccount(i_result);
        		break;
        	case GetSecurityQuestion:
        		((Page_ForgotPassword)_myPage).ServerResponse_GetReminder(i_result);
        		break;
        }
	}
}
