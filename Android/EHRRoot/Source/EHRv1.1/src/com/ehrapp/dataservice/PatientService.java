package com.ehrapp.dataservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.ehrapp.EHRConfig;
import com.ehrapp.EHRLables.PatientServiceAction;
import com.ehrapp.Global;
import com.ehrapp.EHRLables.ProfileServiceAction;
import com.ehrapp.pageviewer.Page_Login;
import com.ehrapp.pageviewer.Page_CreateProfile;
import com.ehrapp.pageviewer.Page_Patient_History;

public class PatientService extends AsyncTask<String,Void,JSONObject> {

	private Activity _myPage = null;
	private PatientServiceAction _action = null;
	private JSONObject _myJSON = null;
	private Global _globals;
	
	public PatientService(Activity i_activity, PatientServiceAction i_action, Global i_globals, JSONObject i_jsonFile)
	{
		_globals = i_globals;
		_action = i_action;
		_myPage =  i_activity;
		_myJSON = i_jsonFile;
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
	        case SendTimeStamp:
	        	json.put("Operation", "SendTimeStamp");
	        	json.put("P_ID", cloudProfileID);
	        	m1 = _globals.GetTimeStamp();        	
	        	json.put("TimeStamps", m1);
	        	break;
	        case UpdatePatientData:
	        	json.put("Operation", "UpdatePatientData");
	        	json.put("P_ID", cloudProfileID);
	        	json.put("TableInfos", _myJSON);
	        	break;
	        }
        		
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 0);
			HttpConnectionParams.setSoTimeout(httpParams, 0);
			HttpClient client = new DefaultHttpClient(httpParams);
			
			String url = EHRConfig.ServerAddress + EHRConfig.PatientServerScript;
						
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
        	case SendTimeStamp:
        		try
        		{
        			JSONObject timestamps= i_result.getJSONObject("TimeStamps");
        			((Page_Patient_History)_myPage).ServerResponse_TimeStamp(timestamps);
        			((Page_Patient_History)_myPage).ServerResponse_PatientTableInfo(i_result);
        		}
        		catch (JSONException ex)
        		{
        			
        		}
        		break;
        	case UpdatePatientData:
        		
        		break;
        }
	}
}

