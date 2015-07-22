package com.ehrapp.pageviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.ehrapp.R;

public class Page_Info extends Activity{

	private ImageButton _imageView;
	
	public Uri currImageURI;
	
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.info_page);
	        
	       _imageView = (ImageButton) findViewById(R.id.titlebar_full);
	       _imageView.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	onCloseClick();
	            }
	          });
	 }
	 
	 private void onCloseClick()
	 {
	 		Intent i = new Intent(Page_Info.this, Page_Login.class);
		    startActivity(i);
	 }
	 
	 
}



