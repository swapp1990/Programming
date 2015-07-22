package com.swapp.findsenetor;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Profile_Slides extends Activity {
	private static String url = "http://whoismyrepresentative.com/getall_mems.php?zip=";
	JSONArray user = null;
	
	GPSTracker gps;
	Geocoder geocoder;
	
	int profile_counter = 0;
	String phone_number = null;
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.profile_slides);
	        
	        if(isNetworkAvailable())
	        {
	        gps = new GPSTracker(Profile_Slides.this);
	        // check if GPS enabled     
	        if(gps.canGetLocation()){
	        	
		        double latitude = gps.getLatitude();
		        double longitude = gps.getLongitude();
		                
		        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
		        List<Address> addresses;
		        geocoder = new Geocoder(this, Locale.getDefault());
		        
		        try 
		        {
		        	addresses = geocoder.getFromLocation(latitude, longitude, 1);
		   			String postalCode = addresses.get(0).getPostalCode();
		   			System.out.println("In Here 2");
		   			//TextView txtV = (TextView) findViewById(R.id.btn_view);
		   		    //txtV.setText(postalCode);	
		   			String cityName = addresses.get(0).getLocality();
		   		    TextView txtC = (TextView) findViewById(R.id.city_text);
		   		    txtC.setText(cityName);
		   			//System.out.println(postalCode);
		   			url = url + postalCode + "&output=json";
		   			new JSONParse().execute();
			    	//make textview web a hyperlink
		   			TextView txtW = (TextView) findViewById(R.id.web_link);
		   			txtW.setClickable(true);
		   			txtW.setMovementMethod(LinkMovementMethod.getInstance());

		   			
		   		} 
		        catch (IOException e) 
		        {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
					System.out.println("No Network");
		   		    TextView txtNI = (TextView) findViewById(R.id.NoInternet);
		   		    txtNI.setText("No Internet");
		   		    RelativeLayout profileLay = (RelativeLayout) findViewById(R.id.profile_lay);
		   		 	profileLay.setVisibility(View.GONE);
		   		    gps.showSettingsAlert();
		   		}
		    }
	        else{
	            // can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings
	            gps.showSettingsAlert();
	        }
	        }
	        else
	        {
	        	TextView txtNI = (TextView) findViewById(R.id.NoInternet);
	   		    txtNI.setText("No Internet");
	   		    RelativeLayout profileLay = (RelativeLayout) findViewById(R.id.profile_lay);
	   		    profileLay.setVisibility(View.GONE);
	        }

}
	 
	 public void NextProfile(View view)
	 {
		 //System.out.println("Length ----" + user.length());
		 profile_counter++;
		 if(profile_counter >= user.length())
		 {
			 profile_counter = 0;
		 }
		 SetProfileInfo(profile_counter);
	 }
	 
	 public void PreviousProfile(View view)
	 {
		// System.out.println("Length ----" + user.length());
		 profile_counter--;
		 if(profile_counter <= 0)
		 {
			 profile_counter = user.length() - 1;
		 }
		 SetProfileInfo(profile_counter);
	 }
	 
	 public void CallPhone(View view)
	 {
		 Intent callIntent = new Intent(Intent.ACTION_DIAL);
		 callIntent.setData(Uri.parse("tel:" + phone_number));
		 startActivity(callIntent);
	 }
	 
	 private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
	 
	 public void SetProfileInfo(int counter)
	 {
		TextView txtN = (TextView) findViewById(R.id.NoData);
 		TextView txtP = (TextView) findViewById(R.id.party1);
	    TextView txtT = (TextView) findViewById(R.id.telephone);
	    TextView txtA = (TextView) findViewById(R.id.address);
	    TextView txtW = (TextView) findViewById(R.id.web_link);
	    
		try {
		JSONObject c = user.getJSONObject(counter);
 		final String name = c.getString("name");
 		final String telephone = c.getString("phone");
 		final String address = c.getString("office");
 		final String party = c.getString("party");
 		final String weblink = c.getString("link");

 		txtN.setText(name);

	        if(party.equals("R"))
	        	txtP.setText("Republican");
	        else
	        	txtP.setText("Democrat");
	        
	    phone_number = telephone;
	    txtT.setText(telephone);

	    txtA.setText(address);

	    String toLink = "<a href='" + weblink + "'>" + weblink + "</a>";
	    txtW.setText(Html.fromHtml(toLink));
		}
		catch (JSONException e) {
            e.printStackTrace();
            
        }
	 }
	 
	 private class JSONParse extends AsyncTask<String, String, JSONObject> {
	    	private ProgressDialog pDialog;
	    	@Override
	    	protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(Profile_Slides.this);
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
	    			user = json.getJSONArray("results");
	    			 //Populate 1st senetor
	    			SetProfileInfo(0);
	    			
	    		 }
	    		 catch (JSONException e) {
	                 e.printStackTrace();
	             }
	    	 }
	    }

}
