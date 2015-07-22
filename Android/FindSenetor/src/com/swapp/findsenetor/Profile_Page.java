package com.swapp.findsenetor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Profile_Page extends Activity {
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.profile_page_l);
	        
	        Intent intent = getIntent();
	        String message = intent.getStringExtra("name");
	        
	        TextView txtV = (TextView) findViewById(R.id.NoData);
	        txtV.setText(message);
	        
	        String party = intent.getStringExtra("party");
	        TextView txtP = (TextView) findViewById(R.id.party1);
	        if(party.equals("R"))
	        	txtP.setText("Republican");
	        else
	        	txtP.setText("Democrat");
	        
	        String telephone = intent.getStringExtra("telephone");
	        TextView txtT = (TextView) findViewById(R.id.telephone);
	        txtT.setText(telephone);
	        
	        String address = intent.getStringExtra("address");
	        TextView txtA = (TextView) findViewById(R.id.address);
	        txtA.setText(address);
	        
	        String weblink = intent.getStringExtra("weblink");
	        String toLink = "<a href='" + weblink + "'>" + weblink + "</a>";
	        TextView txtW = (TextView) findViewById(R.id.web_link);
	        txtW.setClickable(true);
	        txtW.setMovementMethod(LinkMovementMethod.getInstance());
	        txtW.setText(Html.fromHtml(toLink));
	 }
}
