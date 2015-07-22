package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.List;

import com.ehrapp.R;



import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;


public class SlidePages extends FragmentActivity implements ActionBar.TabListener  {
	SecondPageAdapter pageAdapter;
	 ViewPager pager;
	 public void onCreate(Bundle icicle)
	   {
		  super.onCreate(icicle);
		  setContentView(R.layout.screen2);
		  
		  final ActionBar actionBar = getActionBar();
		  actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		  actionBar.setHomeButtonEnabled(true);
		  
		  ArrayList<String> test = getIntent().getStringArrayListExtra("checkBoxesList");
		  List<Fragment> fragments = getFragments(test);
		  
		  pageAdapter = new SecondPageAdapter(getSupportFragmentManager(), fragments);
		  
		    pager = (ViewPager)findViewById(R.id.viewpager2);
	        pager.setAdapter(pageAdapter);
	        
	        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	                // When swiping between different app sections, select the corresponding tab.
	                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
	                // Tab.
	                actionBar.setSelectedNavigationItem(position);
	            }
	        });
	        
	        for (int i = 0; i < test.size(); i++) {
	            // Create a tab with text corresponding to the page title defined by the adapter.
	            // Also specify this Activity object, which implements the TabListener interface, as the
	            // listener for when this tab is selected.
	            actionBar.addTab(
	                    actionBar.newTab()
	                            .setText(test.get(i))
	                            .setTabListener(this));
	        }
	        
	        
	     //  final ActionBar actionBar = getActionBar();
	   }
	 
	 @Override
	    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	    }
	 
	 @Override
	    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	    }
	 
	 @Override
	    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	        // When the given tab is selected, switch to the corresponding page in the ViewPager.
		 pager.setCurrentItem(tab.getPosition());
	    }
	 
/*	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     // Inflate the menu items for use in the action bar
	     MenuInflater inflater = getMenuInflater();
	    // inflater.inflate(R.menu.main_activity_actions, menu);
	     return super.onCreateOptionsMenu(menu);
	 }
	*/ 
	 
	 private List<Fragment> getFragments(ArrayList<String> test){
	    	List<Fragment> fList = new ArrayList<Fragment>();
	    	
	    	for(int i = 0; i < test.size(); i++)
	    	{
	    		System.out.println("CheckBoxes List Size: " + test.get(i));
	    	if(test.get(i).equals("Immunization"))
	    		fList.add(ImmunizationInfo.newInstance("Fragment 1"));
	    	else if(test.get(i).equals("Disease"))
	    		fList.add(DiseaseList.newInstance("Fragment 2"));
	    	else if(test.get(i).equals("Medical"))
	    		fList.add(MedicalHistory.newInstance("Fragment 3"));
	    	else if(test.get(i).equals("Other"))
	    		fList.add(OtherInfo.newInstance("Fragment 4"));
	    	}
	    	return fList;
	    }
	 
	 private class SecondPageAdapter extends FragmentPagerAdapter {
	    	private List<Fragment> fragments;

	        public SecondPageAdapter(FragmentManager fm, List<Fragment> fragments) {
	            super(fm);
	            this.fragments = fragments;
	        }
	        @Override
	        public Fragment getItem(int position) {
	            return this.fragments.get(position);
	        }
	     
	        @Override
	        public int getCount() {
	            return this.fragments.size();
	        }
	    }
}