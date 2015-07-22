package com.ehrapp.pageviewer.sections;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import com.ehrapp.pageviewer.Page_Patient_History;

public class Sectionlog extends Activity {
	
	private DatabaseManager db;
	private FragmentTransaction t;
	private LinearLayout layout;
	private String _timeStamp;
	private String table_name = null;
	private ArrayList<String> sectionFragmentTexts;
	private ImageButton _backButton = null;
	
	private Global _globals;
	private ArrayList<Integer> columnOrder;
	private TextView _sectionlog;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sectionlog_layout);
        
        db = new DatabaseManager(this);
        _globals = ((Global)getApplicationContext());
        
        columnOrder = new ArrayList<Integer>();
        sectionFragmentTexts = new ArrayList<String>();
        int value = getIntent().getExtras().getInt("sectionType");

        //Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
        _sectionlog = (TextView)findViewById(R.id.version);
        _sectionlog.setTypeface(tf2);
        
        SetUserData(value);
        
        _backButton = (ImageButton)findViewById(R.id.btn_back);
        _backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
       		 	Intent i = new Intent(Sectionlog.this, Page_Patient_History.class);
       		 	startActivity(i);
            }
          });
	}
	
	public void SetUserData(int value)
	{
		ScrollView lm = (ScrollView) findViewById(R.id.scrollView1);
		if(value == 1)
        {
			_sectionlog.setText("Chief Complaint");
        	table_name = "illness_history";
        	String[] string_needed = {"Symptom", "Onset", "OTC", "Duration"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
        else if(value == 2)
        {
        	_sectionlog.setText("Allergies");
        	table_name = "allergies";
        	String[] string_needed = {"allergyType", "reaction", "severity", "date_last_occurred","treatment"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        	
        }
        else if(value == 3)
        {
        	_sectionlog.setText("Medication");
        	table_name = "current_medication";
        	String[] string_needed = {"drug", "dosage", "frequency", "start_date","stop_date"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
        else if(value == 4)
        {
        	_sectionlog.setText("Body Systems");
        	table_name = "body_systems";
        	String[] string_needed = {"id","skin",
        			 "vision", 
        			 "hearing", 
        			 "respiratory", 
        			 "cardiovascular", 
        			 "gastrointestinal", 
        			 "gynecologic", 
        			 "musculoskeletal", 
        			 "vascular", 
        			 "neurologic", 
        			 "hematologic", 
        			 "endocrine", 
        			 "psychiatric",
        			 "urological",
        			 "other"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
        else if(value == 5)
        {
        	_sectionlog.setText("Procedure History");
        	table_name = "procedure_history";
        	String[] string_needed = {"procedure_name", "date", "physician_name", "institution_location"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
        else if(value == 6)
        {
        	_sectionlog.setText("Diagnostic Findings");
        	table_name = "diagnosis_finding";
        	String[] string_needed = {"test_name", "result_finding", "date", "interpretation"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
        else if(value == 7)
        {
        	_sectionlog.setText("Immunizations");
        	table_name = "immunization";
        	String[] string_needed = {"vaccine_name", "vaccine_type", "dose", "age","date_administered","lot_number"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
        else if(value == 8)
        {
        	_sectionlog.setText("Social History");
        	table_name = "social_history";
        	String[] string_needed = {"maritalStatus", "occupation", "coffe_consumption", "tobacco_use","alcohol_use","drug_use"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
        else if(value == 9)
        {
        	_sectionlog.setText("Vital Signs");
        	table_name = "vital_signs";
        	String[] string_needed = {"id","pulse", "respiratory_rate", "systolic_blood_pressure", "diastolic_blood_pressure","body_temp","height","weight","BMI"};
        	for( String s: string_needed)
        	{
        		int columnValue = db.GetColumnCount(table_name, s);
        		columnOrder.add(columnValue);
        	}
        }
		
		//Create a frame layout, so we can add fragments to it.
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setId(0x1234);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(lp);
 
		//Create and add two fragments to linear layout created above.
		t = getFragmentManager().beginTransaction();
		
		lm.addView(layout);
		if(db == null)
		{
			System.out.println("Database Error!!!");
		}
		
		ArrayList<ArrayList<Object>> data = db.getAllRowsAsArrays(table_name);
    	for (int position=data.size()-1; position > -1 ; position--)
    	{
	    		ArrayList<Object> row = data.get(position);
	    		String printData = "Values: ";
	    		for(int i = 0; i < row.size(); i++)
	    		{
	    			if(row.get(i) != null)
	    			{
	    				if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
	    	    		{
		    			    if(i == 1)
		    				{
		    			    	for(int j = 0; j < columnOrder.size(); j++)
		    			    	{
		    			    		String textTemp = " ";
		    			    		if(row.get(columnOrder.get(j)) != null)
		    	    				{
		    			    		textTemp = row.get(columnOrder.get(j)).toString();
		    	    				}
		    			    		if(Global.isLong(textTemp))
		    			    		{
		    			    			textTemp = Global.convertLongtoDate(textTemp);
		    			    		}
		    			    		sectionFragmentTexts.add(textTemp);
		    			    	}
		    					_timeStamp = row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString();
		    					AddaFragment();
		    					sectionFragmentTexts.clear();
		    				}
	    			    }
	    				printData = printData + row.get(i).toString() + " ";
	    			}
	    		}
    	}
    	t.commit();
	}
	
	public void AddaFragment()
	{
		//Add first fragment
		String date = Global.convertLongtoDate(_timeStamp);
		Fragment myFragment = new SectionFragment();
		Bundle bundle2 = new Bundle();
		
		ArrayList<String> colNames = db.getAllColsAsArray(table_name);
		LinkedHashMap<String, String> columnName = new LinkedHashMap<String,String>();
		int idTemp = 0;
		for( Integer i: columnOrder)
    	{
    		String colName = colNames.get(i);
    		if(idTemp > 0)
    		{
    			columnName.put(colName, sectionFragmentTexts.get(idTemp));
    		}
    		idTemp++;
    	}
		bundle2.putSerializable("HashMap",columnName);
		bundle2.putString("name", "Consultation Report");
		bundle2.putString("dateValue", date);
		bundle2.putString("version", sectionFragmentTexts.get(0));
		bundle2.putString("timeStamp", _timeStamp);
		myFragment.setArguments(bundle2);
		t.add(layout.getId(), myFragment, "Consultation Report");
	}
	
	@Override
    public void onBackPressed() {
            super.onBackPressed();
            this.finish();
            Intent i = new Intent(Sectionlog.this, Page_Patient_History.class);
	        startActivity(i);
    }
}
