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

import com.ehrapp.EHRConfig;
import com.ehrapp.EHRLables.FamilyHistoryAction;
import com.ehrapp.Global;
import com.ehrapp.EHRLables.ProfileServiceAction;
import com.ehrapp.pageviewer.Page_CreateProfile;
import com.ehrapp.pageviewer.Page_EditFamilyHistory;
import com.ehrapp.pageviewer.Page_Login;
import com.ehrapp.pageviewer.Page_NewFamilyHistory;

import android.app.Activity;
import android.os.AsyncTask;

public class FamilyHistoryService extends AsyncTask<String,Void,JSONObject> {

	private Activity _myPage = null;
	private FamilyHistoryAction _action = null;
	private Global _globals;
	
	public FamilyHistoryService(Activity i_activity, FamilyHistoryAction i_action, Global i_globals)
	{
		_globals = i_globals;
		_action = i_action;
		_myPage =  i_activity;
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
	        case CreateFamilyHistory:
	        	json.put("Operation", "CreateFamilyHistory");
	        	json.put("MyTimeStamp", _globals.GetFamilyHistory().MyTimeStamp);
	        	json.put("profileID", cloudProfileID);
	        	json.put("family_name", _globals.GetFamilyHistory().FamilyName);
	        	json.put("family_realtion", _globals.GetFamilyHistory().FamilyRelation);
	        	json.put("alcohol", _globals.GetFamilyHistory().Alcohol);
	        	json.put("depression", _globals.GetFamilyHistory().Depression);
	        	json.put("bipolar", _globals.GetFamilyHistory().Bipolar);
	        	json.put("anxiety", _globals.GetFamilyHistory().Anxiety);
	        	json.put("alzeimer", _globals.GetFamilyHistory().Alzeimer);
	        	json.put("learning", _globals.GetFamilyHistory().Learning);
	        	json.put("adhd", _globals.GetFamilyHistory().Adhd);
	        	json.put("cancer", _globals.GetFamilyHistory().Cancer);
	        	json.put("cancerType", _globals.GetFamilyHistory().CancerType);
	        	json.put("other", _globals.GetFamilyHistory().Other);
	        	break;
	        case EditFamilyHistory:
	        	json.put("Operation", "EditFamilyHistory");
	        	json.put("MyTimeStamp", _globals.GetFamilyHistory().MyTimeStamp);
	        	json.put("profileID", cloudProfileID);
	        	json.put("family_name", _globals.GetFamilyHistory().FamilyName);
	        	json.put("family_realtion", _globals.GetFamilyHistory().FamilyRelation);
	        	json.put("old_family_name", _globals.GetFamilyHistory().OldFamilyName);
	        	json.put("old_family_realtion", _globals.GetFamilyHistory().OldFamilyRelation);
	        	json.put("alcohol", _globals.GetFamilyHistory().Alcohol);
	        	json.put("depression", _globals.GetFamilyHistory().Depression);
	        	json.put("bipolar", _globals.GetFamilyHistory().Bipolar);
	        	json.put("anxiety", _globals.GetFamilyHistory().Anxiety);
	        	json.put("alzeimer", _globals.GetFamilyHistory().Alzeimer);
	        	json.put("learning", _globals.GetFamilyHistory().Learning);
	        	json.put("adhd", _globals.GetFamilyHistory().Adhd);
	        	json.put("cancer", _globals.GetFamilyHistory().Cancer);
	        	json.put("cancerType", _globals.GetFamilyHistory().CancerType);
	        	json.put("other", _globals.GetFamilyHistory().Other);
				break;
	        case DeleteFamilyHistory:
	        	json.put("Operation", "DeleteFamilyHistory");
	        	json.put("profileID", cloudProfileID);
	        	json.put("family_name", _globals.GetFamilyHistory().FamilyName);
	        	json.put("family_realtion", _globals.GetFamilyHistory().FamilyRelation);
	        	break;
	        }		
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 0);
			HttpConnectionParams.setSoTimeout(httpParams, 0);
			HttpClient client = new DefaultHttpClient(httpParams);
			
			String url = EHRConfig.ServerAddress + EHRConfig.FamilyHistoryServerScript;
						
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
        	case CreateFamilyHistory:
        		((Page_NewFamilyHistory)_myPage).ServerResponse_CreateFamilyHistory(i_result);
        		break;
        	case EditFamilyHistory:
        		((Page_EditFamilyHistory)_myPage).ServerResponse_EditFamilyHistory(i_result);
        		break;
        	case DeleteFamilyHistory:
        		((Page_EditFamilyHistory)_myPage).ServerResponse_DeleteFamilyHistory(i_result);
        		break;
        }
	}
}
