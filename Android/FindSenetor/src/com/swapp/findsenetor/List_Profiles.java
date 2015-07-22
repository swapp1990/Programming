package com.swapp.findsenetor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class List_Profiles extends Activity {
	private static String url = "http://whoismyrepresentative.com/getall_mems.php?zip=";
	JSONArray user = null;
	
	TextView noDataText;
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.list_profiles);

	        noDataText = (TextView) findViewById(R.id.NoData);
	        
	        Intent intent = getIntent();
	        String message = intent.getStringExtra("message");
	        String isSenete = intent.getStringExtra("isSenate");
	        //System.out.println("Senate : " + isSenete);
	        String searchTerm = intent.getStringExtra("searchTerm");
	        if(searchTerm.equals("Zip"))
	        {
	        	url = "http://whoismyrepresentative.com/getall_mems.php?zip=";
	        	url = url + message + "&output=json";
	        }
	        else if(searchTerm.equals("Name"))
	        {
	        	if(isSenete.equals("Yes"))
	        	{
	        		url = "http://whoismyrepresentative.com/getall_sens_byname.php?name=";
	        	}
	        	else
	        	{
	        		url = "http://whoismyrepresentative.com/getall_reps_byname.php?name=";
	        	}
	        	url = url + message + "&output=json";
	        }
	        else if(searchTerm.equals("City"))
	        {
	        	if(isSenete.equals("Yes"))
	        	{
	        		url = "http://whoismyrepresentative.com/getall_sens_bystate.php?state=";
	        	}
	        	else
	        	{
	        		url = "http://whoismyrepresentative.com/getall_reps_bystate.php?state=";
	        	}
	        	url = url + message + "&output=json";
	        }
	        
	        if(isNetworkAvailable())
	        	new JSONParse().execute();
	        else
	        	 noDataText.setText("Network Issues");
	 }
	 
	 //Back Pressed
	 public void onBackPressed()
	 {
		 user = null;
		 super.onBackPressed(); 
	 }
	 
	 //Check if network is enabled or not
	 private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
	 
	 private class JSONParse extends AsyncTask<String, String, JSONObject> {
		 	//Progess Window
	    	private ProgressDialog pDialog;
	    	@Override
	    	protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(List_Profiles.this);
	            pDialog.setMessage("Getting Data ...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	    	}
	    	 
	    	 @Override
	         protected JSONObject doInBackground(String... args) {
	    		 JSONParser jParser = new JSONParser();
	    		 JSONObject json = jParser.getJSONFromUrl(url);
	    		 System.out.println(url);
	    		 return json;
	    	 }
	    	 
	    	 @Override
	    	 protected void onPostExecute(JSONObject json) {
	    		 pDialog.dismiss();
	    		 try {
	    			 if(json != null)
	    			 {
	    			 user = json.getJSONArray("results");
	    			 //ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);
	    			 LinearLayout layout =  (LinearLayout) findViewById(R.id.linear1);
	    			 //setContentView(layout);
	    			 layout.setOrientation(LinearLayout.VERTICAL);  
	    			 //Retrieve all of the info
	    			 for(int i = 0; i < user.length(); i++)
	    			 {
		    			 JSONObject c = user.getJSONObject(i);
		    			 final String id = c.getString("name");
		    			 final String telephone = c.getString("phone");
		    			 final String address = c.getString("office");
		    			 final String party = c.getString("party");
		    			 final String weblink = c.getString("link");
		    			 Button btv=new Button(getApplicationContext());
		    			 btv.setText(id);
		    			 layout.addView(btv);
		    			 
		    			 btv.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getApplicationContext(), Profile_Page.class);
								intent.putExtra("name", id);
								intent.putExtra("telephone", telephone);
								intent.putExtra("address", address);
								intent.putExtra("party", party);
								intent.putExtra("weblink", weblink);
								startActivity(intent);
							}
						});
	    			 }
	    			 //scrollView.addView(layout);
	    			 }
	    			 else
	    			 {
	    				 noDataText.setText("No Data Found");
	    			 }
	    		 }
	    		 catch (JSONException e) {
	                 e.printStackTrace();
	             }
	    	 }
	    }

}


