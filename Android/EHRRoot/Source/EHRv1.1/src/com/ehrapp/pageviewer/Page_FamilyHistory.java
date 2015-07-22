package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.pageviewer.sections.SectionFragment;

public class Page_FamilyHistory extends Activity {
	
	private DatabaseManager _db;
	private FragmentTransaction t;
	private Global _globals;

	private ArrayList<Integer> columnOrder;
	private ArrayList<String> colNames, sectionFragmentTexts;
	private String fam_id, cancerType;
	
	private TextView _family_history_header = null;
	private ImageButton newFamilyInfoBtn, backbtn;
	private LinearLayout layout;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family_hist_layout);
        
        _globals = ((Global)getApplicationContext());
        _db = _globals.GetDatabase();
        
        //Init
        columnOrder = new ArrayList<Integer>();
        sectionFragmentTexts = new ArrayList<String>();
        colNames = new ArrayList<String>();
        
        SetUserData();

        newFamilyInfoBtn = (ImageButton) findViewById(R.id.selectionlog_cloud_btn);
        newFamilyInfoBtn.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View v) {
            		CreateNewFamilyinfo();
            }
          });
        
        backbtn = (ImageButton) findViewById(R.id.allergiesAdd);
        backbtn.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View v) {
            	finish();
                Intent i = new Intent(Page_FamilyHistory.this, Page_Patient_History.class);
    	        startActivity(i);
            }
          });
        
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
        _family_history_header = (TextView)findViewById(R.id.version);
        _family_history_header.setTypeface(titleFont);
	}
	
	private void SetUserData()
	{
		String[] string_needed = {"family_name", "family_relation", "alcohol", "depression","bipolar","anxiety","alzeimer","learning","adhd","cancer","other"};
    	for( String s: string_needed)
    	{
    		int columnValue = _db.GetColumnCount("family_hist", s);
    		columnOrder.add(columnValue);
    		colNames.add(s);
    	}

    	cancerType = null;
    	
    	//add a new scrollview to the linear layout
		ScrollView lm = (ScrollView) findViewById(R.id.scrollView1);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setId(0x1235);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(lp);
		
		t = getFragmentManager().beginTransaction();
		lm.addView(layout);
		
		//Add new fragment boxes for each family history row in the table for the profile id
		ArrayList<ArrayList<Object>> data = _db.getAllRowsAsArraysOrderBy("family_hist", "family_name");
		int profileFamilyHistoryCount = 0;
    	for (int position=0; position < data.size(); position++)
    	{
    		ArrayList<Object> row = data.get(position);
    		for(int i = 0; i < row.size(); i++)
    		{
    			if(row.get(i) != null)
    			{
    				if(Integer.parseInt(row.get(_db.GetColumnCount("family_hist", "profileID")).toString()) == _globals.GetProfile().ProfileID)
    	    		{
    					profileFamilyHistoryCount++;
    					fam_id = row.get(_db.GetColumnCount("family_hist","id")).toString();
    					if(i == 1)
	    				{
    						cancerType = row.get(_db.GetColumnCount("family_hist", "cancerType")).toString();
	    			    	for(int j = 0; j < columnOrder.size(); j++)
	    			    	{
	    			    		String textTemp = row.get(columnOrder.get(j)).toString();
	    			    		sectionFragmentTexts.add(textTemp);
	    			    	}
	    				}
    	    		}
    			}
    		}
    		if(sectionFragmentTexts.size() > 0)
    		{
    			AddaFragment();
    		}
    		sectionFragmentTexts.clear();
    	}
    	
    	//If there are no family history rows for the profile id.
    	if(profileFamilyHistoryCount == 0)
    	{
			TextView tvNoadded = (TextView) new TextView(this);
			tvNoadded.setText("No Family Added.  Use The Plus Button to Add a Family Member.");
			tvNoadded.setTextColor(Color.parseColor("#90000000"));
			tvNoadded.setTextSize(18);
			tvNoadded.setPadding(30,30,0,0);
			layout.addView(tvNoadded);
    	}
    	t.commit();
	}
	
	private void AddaFragment()
	{
		Fragment myFragment = new SectionFragment();
		Bundle bundle2 = new Bundle();
		bundle2.putString("version", sectionFragmentTexts.get(0));
		bundle2.putString("dateValue", sectionFragmentTexts.get(1));
		bundle2.putString("isFamily", "true");
		bundle2.putString("FamilyID", fam_id);
		
		LinkedHashMap<String, String> columnNameMap = new LinkedHashMap<String,String>();
		int idTemp = 0;
		//i = 0 is FamilyName, i = 1 is FamilyRelation
		for(int i = 2; i < sectionFragmentTexts.size(); i++)
		{
			String SysmptomId = "Symptom";
			if(sectionFragmentTexts.get(i).equals("1"))
			{
				idTemp++;
				SysmptomId = SysmptomId + " " +idTemp;
				String colName = colNames.get(i);
				if(colNames.get(i).equals("cancer"))
				{
					colName = colName + " (" + cancerType + ")";
				}
				columnNameMap.put(SysmptomId, colName);
			}
			else if(!sectionFragmentTexts.get(i).isEmpty() && !sectionFragmentTexts.get(i).equals("0"))
			{
				idTemp++;
				SysmptomId = SysmptomId + " " +idTemp;
				columnNameMap.put(SysmptomId, sectionFragmentTexts.get(i));
			}
		}
		bundle2.putSerializable("HashMap",columnNameMap);
		myFragment.setArguments(bundle2);
		t.add(layout.getId(), myFragment, "Family History");
	}
	
	public void CreateNewFamilyinfo()
	{
		 this.finish();
		 Intent i = new Intent(Page_FamilyHistory.this, Page_NewFamilyHistory.class);
	     startActivity(i);
	}
	
	@Override
    public void onBackPressed() {
         super.onBackPressed();
         this.finish();
         Intent i = new Intent(Page_FamilyHistory.this, Page_Patient_History.class);
	     startActivity(i);
    }
}
