package com.swapp.findsenetor;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //Scene for GPS Location Profiles
    public void GoProfileSlides(View view) 
    {
    	Intent intent = new Intent(this, Profile_Slides.class);
    	startActivity(intent);
    }
    
    //Scene for Search Feature
    public void GoSearchMenu(View view) 
    {
    	Intent intent = new Intent(this, SearchPage.class);
    	startActivity(intent);
    }
  
}
