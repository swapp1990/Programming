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
import com.ehrapp.EHRLables.ProfileServiceAction;
import com.ehrapp.pageviewer.Page_Login;
import com.ehrapp.pageviewer.Page_CreateAccount;
import com.ehrapp.pageviewer.Page_CreateProfile;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ProfileService extends AsyncTask<String,Void,JSONObject> {

	private Activity _myPage = null;
	private ProfileServiceAction _action = null;
	private boolean _isPrivate = false;
	private Global _globals;
	
	public ProfileService(Activity i_activity, ProfileServiceAction i_action, boolean i_isPrivate, Global i_globals)
	{
		_globals = i_globals;
		_action = i_action;
		_myPage =  i_activity;
		_isPrivate = i_isPrivate;
	}
	
	@Override
	protected JSONObject doInBackground(String... arg0)
	{
		JSONObject jsonObj = null;
		JSONObject m1 = null;
        JSONObject json = new JSONObject();
        
        try 
        {
        	Integer cloudProfileID = _globals.GetAccount().cloudID;
	        switch(_action)
	        {
	        case DownloadProfile:
	        	json.put("Operation", "DownloadProfile");
	        	json.put("P_ID", cloudProfileID);
	        	json.put("IsPrivate", _isPrivate);
	        	break;
	        	
	        case CreateOrUpdateProfile:
	    		String firstName = _globals.GetProfile().FirstName;
	    		String lastName = _globals.GetProfile().LastName;
	    		boolean gender = _globals.GetProfile().Gender;
	    		long DOB = _globals.GetProfile().DOB;
	    		String Country = _globals.GetProfile().Country;
	    		String Race =  _globals.GetProfile().Race;
	    		
	        	json.put("Operation", "CreateProfile");
				json.put("P_ID", cloudProfileID);
				json.put("Gender", gender);
				json.put("DOB", DOB);
				json.put("Country", Country);
				json.put("Race", Race);
				json.put("IsPrivate", _isPrivate);
				if(!_isPrivate)
				{
					json.put("FirstName", firstName);
					json.put("LastName", lastName);
				}
				break;
	        }
        			
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 0);
			HttpConnectionParams.setSoTimeout(httpParams, 0);
			HttpClient client = new DefaultHttpClient(httpParams);
			
			String url =EHRConfig.ServerAddress +  EHRConfig.ProfileServerScript;
			
			HttpPost request = new HttpPost(url);
			request.setEntity(new ByteArrayEntity(json.toString().getBytes(
					"UTF8")));
			request.setHeader("json", json.toString());
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			//System.out.println("Output json: " + json.toString());

			// If the response does not enclose an entity, there is no need
			if (entity != null) {
				InputStream instream = entity.getContent();
				String jsonText = Global.GetStringFromInputStream(instream);
				//System.out.println("Input json: " + jsonText);
				jsonObj = new JSONObject(jsonText);
			}
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
				//System.out.println(t.toString());
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
        	case DownloadProfile:
        		((Page_Login)_myPage).ServerResponse_DownloadProfile(i_result);
        		break;
        	case CreateOrUpdateProfile: 		
        		((Page_CreateProfile)_myPage).ServerResponse_CreateOrUpdateProfile(i_result);
        		break;
        }
	}
}
