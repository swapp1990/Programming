package com.ehrapp.pageviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ehrapp.EHRLables;
import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.pageviewer.sections.Sectionlog;
import com.ehrapp.dataservice.PatientService;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Page_Patient_History extends Activity {
	
	private TextView _patient_history;
	private TextView _profileName, _gender, _race, _country, _dateOfBirth;
	private TextView _medicationdTV, _allergyTV, _illnessTV, _rbsTV, _procedureHistTV, _diagFindTV, _immunizationTV, _socialhistTV, _familyHistTV, _vitalSignsTV;
	private ImageView _profilePic;
	private ImageView _medicationView, _allergyView, _illnessView, _rbsView, _procedureHistView, _diagFindView, _immunizationView, _socialhistView, _familyHistView, _vitalSignsView;
	private RelativeLayout _medicationViewR, _allergyViewR, _illnessViewR, _rbsViewR, _procedureHistViewR, _diagFindViewR, _immunizationViewR, _socialhistViewR, _familyHistViewR, _vitalSignsViewR, _profileNameViewR;
    private Bitmap bitmap;

    private ImageButton logOffBtn, exportCsvBtn, newReportBtn;
    private Button historyIllnessBtn,allergiesBtn, medicationBtn, rbsBtn, procedureHistBtn, diagFindBtn, immuneBtn, socialHistBtn, familyHistBtn, vitalSignsBtn;
    
    private Global _globals;
    private DatabaseManager db;
    
    private String S_presentIllness,S_allergies,S_medications, S_familyHistory, S_Immunization;
    private String S_rbs, S_procedureHist, S_diagFind, S_SocialHist, S_VitalSigns;
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_history_layout);
        
        _globals = ((Global)getApplicationContext());
        db = _globals.GetDatabase();
        
        InitializeLayout();
        AddButtonListeners();
        SetFonts();

        S_medications = S_allergies = S_presentIllness = S_rbs = S_procedureHist = S_diagFind = S_Immunization = S_SocialHist = S_familyHistory = S_VitalSigns = "";

        // creating all health record tables
        _globals.CreateAllOtherTables();
        //
        boolean hasInternet = Global.CheckInternetConnection(this);
        if(_globals.GetAccount().cloudID >= 0 && hasInternet)
        	SyncUserData();
        //
        SetUserProfile();
        //
        if(_globals.GetAccount().cloudID < 0 || !hasInternet)
        	SetUserData();
        
	}
	
	private void InitializeLayout()
	{
        _profileName = (TextView)findViewById(R.id.profileName);
        _gender = (TextView)findViewById(R.id.gender);
        _race = (TextView)findViewById(R.id.race);
        _country = (TextView)findViewById(R.id.country);
        _dateOfBirth = (TextView)findViewById(R.id.DOB);

        _medicationdTV = (TextView)findViewById(R.id.textViewBtn3);
        _allergyTV = (TextView)findViewById(R.id.textViewBtn2);
        _illnessTV = (TextView)findViewById(R.id.textViewBtn1);
        _rbsTV = (TextView)findViewById(R.id.TextView06);
        _procedureHistTV = (TextView)findViewById(R.id.TextView03);
        _diagFindTV = (TextView)findViewById(R.id.TextView04);
        _immunizationTV = (TextView)findViewById(R.id.TextView02);
        _socialhistTV = (TextView)findViewById(R.id.TextView07);
        _familyHistTV = (TextView)findViewById(R.id.TextView09);
        _vitalSignsTV = (TextView)findViewById(R.id.TextView05);
        
        _patient_history = (TextView)findViewById(R.id.version);
        
        _medicationViewR = (RelativeLayout) findViewById(R.id.medicationRL);
        _allergyViewR = (RelativeLayout) findViewById(R.id.allergyRL);
        _illnessViewR = (RelativeLayout) findViewById(R.id.illnessRL);
        _rbsViewR = (RelativeLayout) findViewById(R.id.rbsRL);
        _procedureHistViewR = (RelativeLayout) findViewById(R.id.procRL);
        _diagFindViewR = (RelativeLayout) findViewById(R.id.diagRL);
        _immunizationViewR = (RelativeLayout) findViewById(R.id.immunizationRL);
        _socialhistViewR = (RelativeLayout) findViewById(R.id.socialRL);
        _familyHistViewR = (RelativeLayout) findViewById(R.id.family_histRL);
        _vitalSignsViewR = (RelativeLayout) findViewById(R.id.vitalRL);
        _profileNameViewR = (RelativeLayout) findViewById(R.id.personal);
        
        newReportBtn = (ImageButton) findViewById(R.id.selectionlog_cloud_btn);
        logOffBtn = (ImageButton) findViewById(R.id.esBtn);
        exportCsvBtn = (ImageButton) findViewById(R.id.exportBtn);
        
        medicationBtn = (Button) findViewById(R.id.button3);
        allergiesBtn = (Button) findViewById(R.id.Button07);
        historyIllnessBtn = (Button) findViewById(R.id.med_create);
        rbsBtn = (Button) findViewById(R.id.Button04);
        procedureHistBtn = (Button) findViewById(R.id.Button01);
        diagFindBtn = (Button) findViewById(R.id.Button02);
        immuneBtn = (Button) findViewById(R.id.button5);
        socialHistBtn = (Button) findViewById(R.id.Button05);
        familyHistBtn = (Button) findViewById(R.id.Button08);
        vitalSignsBtn = (Button) findViewById(R.id.Button03);
        
        _profilePic = (ImageView) findViewById(R.id.logo);
        
        _medicationView = (ImageView) findViewById(R.id.ImageView09);
        _allergyView = (ImageView) findViewById(R.id.ImageView08);
        _illnessView = (ImageView) findViewById(R.id.info);
        _rbsView = (ImageView) findViewById(R.id.ImageView06);
        _procedureHistView = (ImageView) findViewById(R.id.ImageView03);
        _diagFindView = (ImageView) findViewById(R.id.ImageView04);
        _immunizationView = (ImageView) findViewById(R.id.ImageView02);
        _socialhistView = (ImageView) findViewById(R.id.ImageView07);
        _familyHistView = (ImageView) findViewById(R.id.ImageView11);
        _vitalSignsView = (ImageView) findViewById(R.id.ImageView05);
	}
	
	private void AddButtonListeners()
	{
	        newReportBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            		finish();
	            		CreateConsultationReport();
	            }
	          });
	        
	        logOffBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	       		 	Intent i = new Intent(Page_Patient_History.this, Page_Login.class);
	       		 	startActivity(i);
	            }
	          });

	        exportCsvBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	exportCsvPage();
	            }
	          });

	        
	        _medicationView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_medicationViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_Medication();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_medicationViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        medicationBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            		CreateSection_Medication();
	            }
	          });

	        _allergyView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_allergyViewR.setBackgroundDrawable(drawable);

					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_Allergies();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_allergyViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        allergiesBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_Allergies();
	            }
	          });
	        
	        _illnessView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_illnessViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_HistoryIllness();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_illnessViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        historyIllnessBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_HistoryIllness();
	            }
	          });
	        
	        _rbsView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_rbsViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_RBS();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_rbsViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        rbsBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_RBS();
	            }
	          });
	        
	        _procedureHistView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_procedureHistViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_ProcedureHist();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_procedureHistViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        procedureHistBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_ProcedureHist();
	            }
	          });
	        
	        _diagFindView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_diagFindViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_DiagnosticFind();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_diagFindViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        diagFindBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_DiagnosticFind();
	            }
	          });
	        
	        _immunizationView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_immunizationViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_Immunization();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_immunizationViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        immuneBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_Immunization();
	            }
	          });
	        
	        _socialhistView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_socialhistViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_SocialHist();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_socialhistViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        socialHistBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_SocialHist();
	            }
	          });
	        
	        _familyHistView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_familyHistViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_FamilyHist();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_familyHistViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        familyHistBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_FamilyHist();
	            }
	          });
	        
	        _vitalSignsView.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.unclick_textbox); 
						_vitalSignsViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						CreateSection_VitalSigns();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.click_textbox); 
						_vitalSignsViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

	        vitalSignsBtn.setOnClickListener(new OnClickListener() {
	        	
	            @Override
	            public void onClick(View v) {
	            	CreateSection_VitalSigns();
	            }
	          });
	}
	
	private void SetFonts()
	{
        Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");

        _patient_history.setTypeface(titleFont);
        _profileName.setTypeface(textFont);
        _gender.setTypeface(textFont);
        _race.setTypeface(textFont);
        _country.setTypeface(textFont);
        _dateOfBirth.setTypeface(textFont);
        historyIllnessBtn.setTypeface(titleFont);
        _illnessTV.setTypeface(textFont);
        medicationBtn.setTypeface(titleFont);
        _medicationdTV.setTypeface(textFont);
        allergiesBtn.setTypeface(titleFont);
        _allergyTV.setTypeface(textFont);
        socialHistBtn.setTypeface(titleFont);
        _socialhistTV.setTypeface(textFont);
        rbsBtn.setTypeface(titleFont);
        _rbsTV.setTypeface(textFont);
        vitalSignsBtn.setTypeface(titleFont);
        diagFindBtn.setTypeface(titleFont);
        _diagFindTV.setTypeface(textFont);
        procedureHistBtn.setTypeface(titleFont);
        _procedureHistTV.setTypeface(textFont);
        immuneBtn.setTypeface(titleFont);
        _immunizationTV.setTypeface(textFont);
        familyHistBtn.setTypeface(titleFont);
	}

	private void SyncUserData()
	{
		if(_globals.GetAccount().cloudID < 0) //this means the user is not signed onto the cloud
		{
			System.out.println("Not on cloud");
			return;
		}
		else
		{
			// if he is signed into the cloud, compare the latest timestamps between the local database and server database in all tables.
			//send timestamps to the server and get all the table info from the database.
			SendTimeStampsToServer();
		}
	}
	
	private void SendTimeStampsToServer()
	{
		_globals.ClearTimeStamps();
		for(int i = 0; i < EHRLables.TableNames.size(); i++)
		{
			long lastestTimeStamp = GetLatestTimeStampInLocalDatabase(EHRLables.TableNames.get(i));
			_globals.AddTimeStamp(EHRLables.TableNames.get(i), lastestTimeStamp);
		}
		new PatientService(this, EHRLables.PatientServiceAction.SendTimeStamp, _globals, null).execute();
	}
	
	public void ServerResponse_TimeStamp(JSONObject i_response)
	{
		 _globals.ClearServerTimeStamps();
		 _globals.AddServerTimeStamp(i_response);
		 CompareTimeStampAndGetLocalPatientInfo();
	}
	
	public void ServerResponse_PatientTableInfo(JSONObject i_response)
	{
		 try
		 {
			 //Get patient table info and store it into the local database.
			 String result = i_response.getString("Result");
			 //if there are any table info
			 if(result.compareTo("Success") == 0 && i_response.getString("Message").compareTo("Empty")!=0)
			 {
				JSONObject message = (JSONObject)i_response.get("Message");
				
				for(int i = 0; i < EHRLables.TableNames.size(); ++i)
				{
					if(!message.isNull(EHRLables.TableNames.get(i)))
					{
					JSONArray tableData = message.getJSONArray(EHRLables.TableNames.get(i));
					
					for(int j = 0; j <tableData.length(); ++j)
					{
						JSONObject row = tableData.getJSONObject(j);
						Map<String, Object> columnsValues = new HashMap<String, Object>();
						for(int k = 0; k < EHRLables.ColumnNames.get(i).length; ++k)
						{
							if(k != 1)
							{
								columnsValues.put(EHRLables.ColumnNames.get(i)[k], row.get(EHRLables.ColumnNames.get(i)[k]));
							}
							else
							{
								//convert Cloud ID into local ID.
								columnsValues.put(EHRLables.ColumnNames.get(i)[k], _globals.GetProfile().ProfileID);
							}
						}
						db.addRow(columnsValues, EHRLables.TableNames.get(i));
					}
					}
				}
				Toast.makeText(getApplicationContext(), "Success: Syncing", Toast.LENGTH_SHORT).show();
			 }
			 else if(result.compareTo("Fail") == 0)
			 {
				 //Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
				 JSONObject errMessage = (JSONObject)i_response.get("Message");
				 Toast.makeText(getApplicationContext(), "Fail: ErrorCode: " + (Integer)(errMessage.get("ErrorCode")) + "Content: "+ (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
			 }
			 SetUserData();
		 }
		 catch(JSONException ex)
		 {
			 System.out.println(ex.toString());
		 }
	}
	
	private void CompareTimeStampAndGetLocalPatientInfo()
	{
		try
		{
			JSONObject output = new JSONObject();
			
			for(int i = 0; i < EHRLables.TableNames.size(); ++i)
			{
				String tableName = EHRLables.TableNames.get(i);
				long ServerTimeStamp = _globals.GetServerTimeStamp().getLong(tableName);
				JSONArray tableContent = new JSONArray();
				if(_globals.GetTimeStamp().getLong(tableName) > ServerTimeStamp)
				{
					tableContent = GetNewJSONObjectFromTable(i, tableName, ServerTimeStamp);
				}
				output.put(tableName, tableContent);
			}
			new PatientService(this, EHRLables.PatientServiceAction.UpdatePatientData, _globals, output).execute();
		}
		catch(JSONException ex)
		{
			System.out.println("Compare Time: " + ex.toString());
		}
	}
	
	private JSONArray GetNewJSONObjectFromTable(int i_tableIndex, String i_tableName, long i_serverTimeStamp)
	{
		JSONArray newJArray = new JSONArray();
		
		ArrayList<Integer> columnOrder = new ArrayList<Integer>();
		
    	for( String s: EHRLables.ColumnNames.get(i_tableIndex))
    	{
    		int columnValue = db.GetColumnCount(i_tableName, s);
    		columnOrder.add(columnValue);
    	}
    		
		ArrayList<ArrayList<Object>> data = db.getAllRowsAsArrays(i_tableName);
		
		int timeStampIndex = columnOrder.get(0);
		int profileIDIndex = columnOrder.get(1);
		try
		{
	    	for (int position=0; position < data.size(); position++)
	    	{
	    		ArrayList<Object> row = data.get(position);
	    		if(Long.parseLong(row.get(timeStampIndex).toString()) > i_serverTimeStamp && Integer.parseInt((row.get(profileIDIndex).toString())) == _globals.GetProfile().ProfileID)
	    		{
	    			JSONObject newJson = new JSONObject();
	    			for(int i = 0; i < EHRLables.ColumnNames.get(i_tableIndex).length; ++i)
	    			{
	    				if(i != 1) //if it's not profileID
	    				{
	    					newJson.put(EHRLables.ColumnNames.get(i_tableIndex)[i], row.get(columnOrder.get(i)));
	    				}
	    				else
	    				{
	    					//we don't need the local profileID, we need the cloud ID.
	    					newJson.put(EHRLables.ColumnNames.get(i_tableIndex)[1], _globals.GetAccount().cloudID);
	    				}
	    			}
	    			newJArray.put(newJson);
	    		}
	    	}
		}
		catch(JSONException ex)
		{
			System.out.println("Create new json object from table: " + ex.toString());
		}
		return newJArray;
	}
	
	private long GetLatestTimeStampInLocalDatabase(String i_tableName)
	{
		long largestTimeStamp = 0;
		ArrayList<Object> dataArray = db.getTimeStampRowsFromCertainProfileAsArray(i_tableName, _globals.GetProfile().ProfileID);
		
		for (int i=0; i < dataArray.size(); ++i)
		{
			if(dataArray.get(i) != null)
			{
				long currentTimeStamp = Long.parseLong(dataArray.get(i).toString());
				if(largestTimeStamp < currentTimeStamp)
					largestTimeStamp = currentTimeStamp;
			}
		}
		
		return largestTimeStamp;
	}
	
	private void SetUserProfile()
	{
		 _profileName.setText(_globals.GetProfile().LastName + ", " + _globals.GetProfile().FirstName);
		 if(_globals.GetProfile().Gender)
			 _gender.setText("Male");
		 else
			 _gender.setText("Female");
		 
		 _race.setText(_globals.GetProfile().Race);
		 _country.setText(_globals.GetProfile().Country);

		 _dateOfBirth.setText(DateFormat.format("MM/dd/yyyy", new Date(_globals.GetProfile().DOB)).toString());

		 _profileNameViewR.setOnTouchListener(new OnTouchListener()
		    {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.personal_info_2); 
						_profileNameViewR.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.personal_box); 
						_profileNameViewR.setBackgroundDrawable(drawable);
						ShowEditProfilePage();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.personal_box); 
						_profileNameViewR.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });
		 
		 //// Display Image  ///////////////////////////////////////////////////
		 Uri currImageURI = Uri.parse(_globals.GetProfile().profilePic_path);
	        System.out.println("Uri is " + currImageURI);
	        try {
             // We need to recyle unused bitmaps
	             if (bitmap != null) {
	                 bitmap.recycle();
	             }
	             if(currImageURI != null)
	             {
		             InputStream stream = getContentResolver().openInputStream(
		             		currImageURI);
		             //optimize image
		             BitmapFactory.Options options = new BitmapFactory.Options();
		             options.inPreferredConfig = Config.RGB_565;
		             options.inSampleSize = 2;
		             //
		             bitmap = BitmapFactory.decodeStream(stream,null,options);
		             stream.close();
		             _profilePic.setImageBitmap(bitmap);
	              }
	              bitmap = null;
	        } catch (FileNotFoundException e) {
	        	e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
	     ////////////////////////////////////////////////////////////////////////
	 }
	 
	private void ShowEditProfilePage()
	 {
		 	this.finish();
	        Intent i = new Intent(Page_Patient_History.this, Page_CreateProfile.class);
	        startActivity(i);
	 }
	 
	private void SetUserData()
	 {
		 	int numberToShow = 6;
		 	////////////////////IllnessHistory //////////////////
		 	ArrayList<ArrayList<Object>> data = db.getAllRowsAsArrays("illness_history");
		 	int numberIllness = 0;
		 	if(data.size() > 0)
		 	{
		    	for (int position=data.size()-1; position > -1; position--)
		    	{
		    		ArrayList<Object> row = data.get(position);
		    		
		    		if(Integer.parseInt(row.get(2).toString()) == _globals.GetProfile().ProfileID)
		    		{
		    			numberIllness++;
		    			if(numberIllness < numberToShow)
		    			{
		    				String Symptom = row.get(5).toString();
		    				String Onset = row.get(1).toString();
		    				//String Duration = row.get(4).toString();
		    				String OTC = row.get(4).toString();
		    				String textTemp = numberIllness + ". " + Symptom + " : "
		    						+ Onset + ", " + OTC + "\n";
		    				S_presentIllness = S_presentIllness + textTemp;
		    			}
		    		}
		    	}
		 	}
    		_illnessTV.setText(S_presentIllness);

	    	 //////////////////// Allergies //////////////////
	    	ArrayList<ArrayList<Object>> dataAll = db.getAllRowsAsArrays("allergies");
		 	int numberAllergies = 0;
		 	if(dataAll.size() > 0)
		 	{
	    	for (int position=dataAll.size()-1; position > -1; position--)
	    	{
	    		ArrayList<Object> row = dataAll.get(position);
	    		
	    		if(Integer.parseInt(row.get(3).toString()) == _globals.GetProfile().ProfileID)
	    		{
	    			    numberAllergies++;
	    				if(numberAllergies < numberToShow)
	    				{
	    					String AllergyType = row.get(4).toString();
	     					String Reaction = row.get(1).toString();
	    					String Severity = row.get(2).toString();
	    					String Treatment = row.get(6).toString();
	    					String DateLast = row.get(7).toString();
	    					DateLast = Global.convertLongtoDate(DateLast);
	    					S_allergies = S_allergies + numberAllergies + ". " + AllergyType + " : "
	    							+ Reaction + ", " + Severity + ", " + Treatment + ", " + DateLast + "\n";
	    				}
	    		}
	    	}
	    	}
	    	//System.out.println("Allergies: " + S_allergies);
    		_allergyTV.setText(S_allergies);
    		_allergyTV.setTextColor(Color.RED);
	    	
	    	 ////////////////////Medications //////////////////
	    	ArrayList<ArrayList<Object>> dataMedic = db.getAllRowsAsArrays("current_medication");
		 	int numberPastMedic = 0;
		 	if(dataMedic.size() > 0)
		 	{
	    	for (int position=dataMedic.size()-1; position > -1; position--)
	    	{
	    		ArrayList<Object> row = dataMedic.get(position);
	    		if(Integer.parseInt(row.get(4).toString()) == _globals.GetProfile().ProfileID)
	    		{
	    			    numberPastMedic++;
	    			    String End_Date = row.get(db.GetColumnCount("current_medication", "stop_date")).toString();
	    			    long lEndDate = Long.parseLong(End_Date);
	    			    long lCurrentDate = Calendar.getInstance().getTimeInMillis();
	    			    if(lCurrentDate > lEndDate)
	    			    {
	    			    	//remove this one from database.
	    			    	System.out.println("deleting row: " + lEndDate + " Num: " + position);
	    			    	String S_StopDate = String.valueOf(lEndDate);
	    			    	db.deleteRowFromColumn("current_medication", "stop_date", S_StopDate);
	    			    	--numberPastMedic;
	    			    }
	    			    else
	    			    {
		    				if(numberPastMedic < numberToShow)
		    				{
		    					String Date = null;
		    					String Drug = row.get(db.GetColumnCount("current_medication", "drug")).toString();
		    					String Dosage = row.get(db.GetColumnCount("current_medication", "dosage")).toString();
		    					String Frequency = row.get(db.GetColumnCount("current_medication", "frequency")).toString();
		    					String Start_Date = row.get(db.GetColumnCount("current_medication", "start_date")).toString();
		    					Start_Date = Global.convertLongtoDate(Start_Date);
		    					
		    					End_Date = Global.convertLongtoDate(End_Date);
		    					S_medications = S_medications + numberPastMedic + ". " +  Drug + ": "
		    							+ Dosage + ", " + Frequency + ", " + Start_Date + ", " + End_Date + "\n";
		    				}
	    			    }
	    		}
	    	}
	    	}
	    	//System.out.println("Medicaiton: " + S_medications);
	    	_medicationdTV.setText(S_medications);
	    	
	    	//////////////////// RBS //////////////////
	    	String table_name = "body_systems";
	    	ArrayList<ArrayList<Object>> dataRBS = db.getAllRowsAsArrays("body_systems");
		 	int numberRBS = 0;
		 	if(dataRBS.size() > 0)
		 	{
	    	for (int position=dataRBS.size()-1; position > -1; position--)
	    	{
				ArrayList<Object> row = dataRBS.get(position);
				ArrayList<String> stringArray = new ArrayList<String>();
		
				if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
				{
					numberRBS++;
						if(numberRBS < numberToShow) 
						{
							String id = String.valueOf(numberRBS);
							stringArray.add(id);
							String skin = row.get(db.GetColumnCount(table_name, "skin")).toString();
							if(!skin.isEmpty())
								stringArray.add(skin);
							String eyes = row.get(db.GetColumnCount(table_name, "vision")).toString();
							if(!eyes.isEmpty())
								stringArray.add(eyes);
							String ears = row.get(db.GetColumnCount(table_name, "hearing")).toString();
							if(!ears.isEmpty())
								stringArray.add(ears);
							String respiratory = row.get(db.GetColumnCount(table_name, "respiratory")).toString();
							if(!respiratory.isEmpty())
								stringArray.add(respiratory);
							String cardiovascular = row.get(db.GetColumnCount(table_name, "cardiovascular")).toString();
							if(!cardiovascular.isEmpty())
								stringArray.add(cardiovascular);
							String gastrointestinal = row.get(db.GetColumnCount(table_name, "gastrointestinal")).toString();
							if(!gastrointestinal.isEmpty())
								stringArray.add(gastrointestinal);
							String gynecologic = row.get(db.GetColumnCount(table_name, "gynecologic")).toString();
							if(!gynecologic.isEmpty())
								stringArray.add(gynecologic);
							String musculoskeletal = row.get(db.GetColumnCount(table_name, "musculoskeletal")).toString();
							if(!musculoskeletal.isEmpty())
								stringArray.add(musculoskeletal);
							String peripheral_vascular = row.get(db.GetColumnCount(table_name, "vascular")).toString();
							if(!peripheral_vascular.isEmpty())
								stringArray.add(peripheral_vascular);
							String neurologic = row.get(db.GetColumnCount(table_name, "neurologic")).toString();
							if(!neurologic.isEmpty())
								stringArray.add(neurologic);
							String hematologic = row.get(db.GetColumnCount(table_name, "hematologic")).toString();
							if(!hematologic.isEmpty())
								stringArray.add(hematologic);
							String endocrine = row.get(db.GetColumnCount(table_name, "endocrine")).toString();
							if(!endocrine.isEmpty())
								stringArray.add(endocrine);
							String psychiatric = row.get(db.GetColumnCount(table_name, "psychiatric")).toString();
							if(!psychiatric.isEmpty())
								stringArray.add(psychiatric);
							String urologic = row.get(db.GetColumnCount(table_name, "urological")).toString();
							if(!urologic.isEmpty())
								stringArray.add(urologic);
							String other = row.get(db.GetColumnCount(table_name, "other")).toString();
							if(!other.isEmpty())
								stringArray.add(other);
						}
						if(stringArray.size() > 0)
						{
							S_rbs = S_rbs + stringArray.get(0).toString() + ". ";
							for(int i = 1; i < stringArray.size(); i++)
							{
								if(i < stringArray.size() - 1)
									S_rbs = S_rbs + stringArray.get(i).toString() + ", ";
								else
									S_rbs = S_rbs + stringArray.get(i).toString();
							}
							S_rbs = S_rbs + "\n";
						}
				}
	    	}
			}
			_rbsTV.setText(S_rbs);
			
			////////////////////Diag Find //////////////////
			table_name = "diagnosis_finding";
			ArrayList<ArrayList<Object>> datadiagFind = db.getAllRowsAsArrays(table_name);
		 	int numberdatadiagFind = 0;
		 	if(datadiagFind.size() > 0)
		 	{
	    	for (int position=datadiagFind.size()-1; position > -1; position--)
	    	{
				ArrayList<Object> row = datadiagFind.get(position);
				if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
				{
					numberdatadiagFind++;
						if(numberdatadiagFind < numberToShow)
						{
							String test_name = row.get(db.GetColumnCount(table_name, "test_name")).toString();
							String result_finding = row.get(db.GetColumnCount(table_name, "result_finding")).toString();
							String interpretation = row.get(db.GetColumnCount(table_name, "interpretation")).toString();
							String dateLast = row.get(db.GetColumnCount(table_name, "date")).toString();
							dateLast = Global.convertLongtoDate(dateLast);
							S_diagFind = S_diagFind + numberdatadiagFind + ". " + test_name + ", "
									+ result_finding + ", " + interpretation + ", " + dateLast + "\n";
						}
				}
	    	}
			}
			//System.out.println("Diagnostics: " + S_diagFind);
			_diagFindTV.setText(S_diagFind);
			
			////////////////////Procedure History //////////////////
			table_name = "procedure_history";	
			ArrayList<ArrayList<Object>> dataPHist = db.getAllRowsAsArrays(table_name);
			int numberPHist = 0;
			if(dataPHist.size() > 0)
		 	{
		    	for (int position=dataPHist.size()-1; position > -1; position--)
		    	{
				ArrayList<Object> row = dataPHist.get(position);
					if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
					{
							numberPHist++;
							if(numberPHist < numberToShow)
							{
								String procedure = row.get(db.GetColumnCount(table_name, "procedure_name")).toString();
								String physician_name = row.get(db.GetColumnCount(table_name, "physician_name")).toString();
								String institution_location = row.get(db.GetColumnCount(table_name, "institution_location")).toString();
									S_procedureHist = S_procedureHist + numberPHist + ". " + procedure + ","
										+ physician_name + "," + institution_location + "\n";
									//System.out.println("MedicaitonT: " + db.GetColumnCount(table_name, "procedure"));
							}
					}
		    	}
			}
			//System.out.println("Procedures: " + S_procedureHist);
			_procedureHistTV.setText(S_procedureHist);
		
			////////////////////Immunization //////////////////
			table_name = "immunization";	
			ArrayList<ArrayList<Object>> dataImmune = db.getAllRowsAsArrays(table_name);
			int numberImmune = 0;
			if(dataImmune.size() > 0)
		 	{
		    	for (int position=dataImmune.size()-1; position > -1; position--)
		    	{
				ArrayList<Object> row = dataImmune.get(position);
					if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
					{
						numberImmune++;
						if(numberImmune < numberToShow)
						{
							String Date = null;
							String name = row.get(db.GetColumnCount(table_name, "vaccine_name")).toString();
							String type = row.get(db.GetColumnCount(table_name, "vaccine_type")).toString();
							String age = row.get(db.GetColumnCount(table_name, "age")).toString();
							String dose = row.get(db.GetColumnCount(table_name, "dose")).toString();
							S_Immunization = S_Immunization + numberImmune + ". " + name + ", "
									+ type + ", " + age + ", " + dose + "\n";
								//System.out.println("MedicaitonT: " + S_medications);
						}
					}
		    	}
			}
			//System.out.println("Immuni: " + S_Immunization);
			_immunizationTV.setText(S_Immunization);
		
			////////////////////social_history //////////////////
			table_name = "social_history";	
			ArrayList<ArrayList<Object>> dataSocialHist = db.getAllRowsAsArrays(table_name);
			int numberSocial = 0;
			if(dataSocialHist.size() > 0)
		 	{
		    	for (int position=dataSocialHist.size()-1; position > -1; position--)
		    	{
				ArrayList<Object> row = dataSocialHist.get(position);
					if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
					{
						numberSocial++;
						if(numberSocial < numberToShow)
						{
							// "tobacco_use", "alcohol_use", "drug_use"
							String maritalStatus = row.get(db.GetColumnCount(table_name, "maritalStatus")).toString();
							String occupation = row.get(db.GetColumnCount(table_name, "occupation")).toString();
							String coffe_consumption = row.get(db.GetColumnCount(table_name, "coffe_consumption")).toString();
							String tobacco_use = row.get(db.GetColumnCount(table_name, "tobacco_use")).toString();
							String alcohol_use = row.get(db.GetColumnCount(table_name, "alcohol_use")).toString();
							String drug_use = row.get(db.GetColumnCount(table_name, "drug_use")).toString();
							S_SocialHist = S_SocialHist + numberSocial + ". " + 
									maritalStatus + "," + 
									occupation + "," + 
									coffe_consumption + "," +
									tobacco_use + "," +
									alcohol_use + "," +
									drug_use +
									"\n";
						}
					}
		    	}
			}
			//System.out.println("Social: " + S_SocialHist + " " + dataSocialHist.size());
			_socialhistTV.setText(S_SocialHist);
			
		////////////////////family history //////////////////
		table_name = "family_hist";	
		ArrayList<ArrayList<Object>> dataFamHist = db.getAllRowsAsArrays(table_name);
		int numberFamHist = 0;
		ArrayList<String> sysmptomList = new ArrayList<String>();
		if(dataFamHist.size() > 0)
	 	{
	    	for (int position=dataFamHist.size()-1; position > -1; position--)
	    	{
	    	
			ArrayList<Object> row = dataFamHist.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				numberFamHist++;
				if(numberFamHist < numberToShow)
				{
					String name = row.get(db.GetColumnCount(table_name, "family_name")).toString();
					String relation = row.get(db.GetColumnCount(table_name, "family_relation")).toString();
					String alcohol = row.get(db.GetColumnCount(table_name, "alcohol")).toString();
					if(alcohol.equals("1"))
						sysmptomList.add("alcohol");
					String depression = row.get(db.GetColumnCount(table_name, "depression")).toString();
					if(depression.equals("1"))
						sysmptomList.add("depression");
					String bipolar = row.get(db.GetColumnCount(table_name, "bipolar")).toString();
					if(bipolar.equals("1"))
						sysmptomList.add("bipolar");
					String anxiety = row.get(db.GetColumnCount(table_name, "anxiety")).toString();
					if(anxiety.equals("1"))
						sysmptomList.add("anxiety");
					String alzeimer = row.get(db.GetColumnCount(table_name, "alzeimer")).toString();
					if(alzeimer.equals("1"))
						sysmptomList.add("alzeimer");
					String learning = row.get(db.GetColumnCount(table_name, "learning")).toString();
					if(learning.equals("1"))
						sysmptomList.add("learning disorder");
					String adhd = row.get(db.GetColumnCount(table_name, "adhd")).toString();
					if(adhd.equals("1"))
						sysmptomList.add("adhd");
					S_familyHistory = S_familyHistory + numberFamHist + ". " + name + ", ";
					for(int i = 0; i < sysmptomList.size(); i++)
					{
						System.out.println("Symptom list: "+sysmptomList.get(i).toString());
						S_familyHistory = S_familyHistory + sysmptomList.get(i).toString() + ", ";
					}
					S_familyHistory = S_familyHistory + "\n";
					sysmptomList.clear();
				}
			}
	    	}// first for
		}// if
		//System.out.println("Family: " + S_familyHistory);
		_familyHistTV.setText(S_familyHistory);
		
		///////////////////Vital Signs //////////////////
		table_name = "vital_signs";	
		ArrayList<ArrayList<Object>> dataVSist = db.getAllRowsAsArrays(table_name);
		int numberVSist = 0;
		if(dataVSist.size() > 0)
	 	{
	    	for (int position=dataVSist.size()-1; position > -1; position--)
	    	{
			ArrayList<Object> row = dataVSist.get(position);
				if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
				{
					numberVSist++;
						if(numberVSist < numberToShow)
						{
							String pulse = row.get(db.GetColumnCount(table_name, "pulse")).toString();
							String respiratory_rate = row.get(db.GetColumnCount(table_name, "respiratory_rate")).toString();
							String systolic_blood_pressure = row.get(db.GetColumnCount(table_name, "systolic_blood_pressure")).toString();
							String diastolic_blood_pressure = row.get(db.GetColumnCount(table_name, "diastolic_blood_pressure")).toString();
							String body_temp = row.get(db.GetColumnCount(table_name, "body_temp")).toString();
							String height = row.get(db.GetColumnCount(table_name, "height")).toString();
							String weight = row.get(db.GetColumnCount(table_name, "weight")).toString();
							String BMI = row.get(db.GetColumnCount(table_name, "BMI")).toString();
								S_VitalSigns = S_VitalSigns + numberVSist + ". " 
										+ pulse + ","
									    + respiratory_rate + "," 
										+ systolic_blood_pressure + "," 
									    + diastolic_blood_pressure + ","
										+ body_temp + ","
										+ height + ","
										+ weight + ","
										+ BMI
									    + "\n";
						}
				}
			}
	 	}
		//System.out.println("Vital signs: " + S_VitalSigns);
		_vitalSignsTV.setText(S_VitalSigns);
	}

	private void CreateConsultationReport()
	 {
		 Intent i = new Intent(Page_Patient_History.this, Page_ConsultationReport.class);
	        startActivity(i);
	 }
	 
	//Show Sections for Patient History
	private void CreateSection_HistoryIllness()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 1);
	        startActivity(i);
	 }
	 
	private void CreateSection_Allergies()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 2);
	        startActivity(i);
	 }
	 
	private void CreateSection_Medication()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 3);
	        startActivity(i);
	 }
	 
	private void CreateSection_RBS()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 4);
	        startActivity(i);
	 }
	 
	private void CreateSection_ProcedureHist()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 5);
	        startActivity(i);
	 }
	 
	private void CreateSection_DiagnosticFind()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 6);
	        startActivity(i);
	 }
	 
	private void CreateSection_Immunization()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 7);
	        startActivity(i);
	 }
	 
	private void CreateSection_SocialHist()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 8);
	        startActivity(i);
	 }
	 
	private void CreateSection_FamilyHist()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Page_FamilyHistory.class);
		// i.putExtra("sectionType", 8);
	        startActivity(i);
	 }
	 
	private void CreateSection_VitalSigns()
	 {
		 this.finish();
		 Intent i = new Intent(Page_Patient_History.this, Sectionlog.class);
		 i.putExtra("sectionType", 9);
	        startActivity(i);
	 }
	 
	private void exportCsvPage()
	 {
		 String totalCsv = "";
		 totalCsv = totalCsv + "Patient Name: " +_globals.GetProfile().FirstName + " " + _globals.GetProfile().LastName + "\n\n";
		 totalCsv = totalCsv + "Illness History" + "\n";
		 String illness = db.returnAllValues("illness_history");
		 if(illness != null)
		 {
			 totalCsv = totalCsv + illness + "\n";
		 }
		 totalCsv = totalCsv + "Current Medications" + "\n";
		 String curr_medica = db.returnAllValues("current_medication");
		 if(curr_medica != null)
		 {
			 totalCsv = totalCsv + curr_medica + "\n";
		 }
		 totalCsv = totalCsv + "Allergies" + "\n";
		 String curr_allergies = db.returnAllValues("allergies");
		 if(curr_allergies != null)
		 {
			 totalCsv = totalCsv + curr_allergies + "\n";
		 }
		 totalCsv = totalCsv + "Diagnostics" + "\n";
		 String columns_diagnosis_finding = db.returnAllValues("diagnosis_finding");
		 if(columns_diagnosis_finding != null)
		 {
			 totalCsv = totalCsv + columns_diagnosis_finding + "\n";
		 }
		 totalCsv = totalCsv + "Vital Signs" + "\n";
		 String columns_vital_signs = db.returnAllValues("vital_signs");
		 if(columns_vital_signs != null)
		 {
			 totalCsv = totalCsv + columns_vital_signs + "\n";
		 }
		 totalCsv = totalCsv + "Procedure History" + "\n";
		 String columns_proc_hist = db.returnAllValues("procedure_history");
		 if(columns_proc_hist != null)
		 {
			 totalCsv = totalCsv + columns_proc_hist + "\n";
		 }
		 totalCsv = totalCsv + "Immunization" + "\n";
		 String columns_immunization = db.returnAllValues("immunization");
		 if(columns_immunization != null)
		 {
			 totalCsv = totalCsv + columns_immunization + "\n";
		 }
		 totalCsv = totalCsv + "Social History" + "\n";
		 String columns_social_history = db.returnAllValues("social_history");
		 if(columns_social_history != null)
		 {
			 totalCsv = totalCsv + columns_social_history + "\n";
		 }
		 totalCsv = totalCsv + "Family History" + "\n";
		 String columns_family_hist = db.returnAllValues("family_hist");
		 if(columns_family_hist != null)
		 {
			 totalCsv = totalCsv + columns_family_hist + "\n";
		 }
		 String to = "";
         String subject = "EHR Data";
         String message = "EHR Data Attached";
		 Intent i = new Intent(Intent.ACTION_SEND);
         i.setType("plain/text");
         File data = null;
         try {
             //String filename = dateVal.toString();
             data = File.createTempFile("Report", ".csv", this.getCacheDir());
             data.setReadable(true, false);
             FileWriter out = (FileWriter) Global.generateCsvFile(
                     data, totalCsv);
             i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(data));
             i.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
             i.putExtra(Intent.EXTRA_SUBJECT, subject);
             i.putExtra(Intent.EXTRA_TEXT, message);
             startActivity(Intent.createChooser(i, "E-mail"));
         } catch (IOException e) {
             e.printStackTrace();
         }
	 }
 
}
