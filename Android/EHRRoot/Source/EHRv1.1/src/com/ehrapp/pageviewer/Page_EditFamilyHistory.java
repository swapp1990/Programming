package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ehrapp.EHRLables;
import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.dataservice.FamilyHistoryService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Page_EditFamilyHistory extends Activity {
	
	private DatabaseManager _db;
	private Global _globals;
	private ImageButton _saveFamilyInfoBtn, _deleteFamilyInfoBtn, _backImageBtn;
	private Long _ScurrTimestamp;
	private int _SprofileId_;
	private int _rowId;
	private int _familyId;
	private String _familyName;
	private String _familyRelation;
	private TextView fam_nameTV_, fam_relationTV_, cancerTypeTV_, otherTypeTV_;
	private CheckBox chk1, chk2, chk3, chk4, chk5, chk6, chk7, chk8;
	private boolean chk1_b, chk2_b, chk3_b, chk4_b, chk5_b, chk6_b, chk7_b, chk8_b;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_family_layout);
        
        _globals = ((Global)getApplicationContext());
        _db = _globals.GetDatabase();
        
        Intent intent = getIntent();
        _SprofileId_ = _globals.GetProfile().ProfileID;
        _ScurrTimestamp = System.currentTimeMillis();
        _familyId = intent.getIntExtra("familyID", 0);

        fam_nameTV_ = (TextView) findViewById(R.id.name_fam);
        fam_relationTV_ = (TextView) findViewById(R.id.relation_fam);
        cancerTypeTV_ = (TextView) findViewById(R.id.editText1);
        otherTypeTV_ = (TextView) findViewById(R.id.otherText);
        
        AddListenerOnCheckboxes();
        InitiateCheckBoxes();
        
        _saveFamilyInfoBtn = (ImageButton) findViewById(R.id.saveButton);
        _saveFamilyInfoBtn.setOnClickListener(new OnClickListener() {
        	
        @Override
        	public void onClick(View v) {
            	SaveEditFamilyinfo();
            	}
          	});
        
        //Visible in new family history page
        _backImageBtn =  (ImageButton) findViewById(R.id.backBtn);
        _backImageBtn.setVisibility(View.GONE);
        
        _deleteFamilyInfoBtn= (ImageButton) findViewById(R.id.deleteBtn);
        _deleteFamilyInfoBtn.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View v) {
            		DeleteFamilyinfo();
            }
          });
	}
	
	private void AddListenerOnCheckboxes() {
		 
		chk1 = (CheckBox) findViewById(R.id.checkBox1);
		chk2 = (CheckBox) findViewById(R.id.checkBox2);
		chk3 = (CheckBox) findViewById(R.id.checkBox3);
		chk4 = (CheckBox) findViewById(R.id.checkBox4);
		chk5 = (CheckBox) findViewById(R.id.checkBox5);
		chk6 = (CheckBox) findViewById(R.id.checkBox6);
		chk7 = (CheckBox) findViewById(R.id.checkBox7);
		chk8 = (CheckBox) findViewById(R.id.checkBox8);
	 
		chk1.setOnClickListener(new OnClickListener() {
	 
		  @Override
		  public void onClick(View v) {
			if (((CheckBox) v).isChecked()) {
					System.out.println("Cheknbox Clicked");
					chk1_b = true;
			}
			else
			{
				chk1_b = false;
			}
		  }
		});
		
		chk2.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk2_b = true;
				}
				else
				{
					chk2_b = false;
				}
			  }
			});
		chk3.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk3_b = true;
				}
				else
				{
					chk3_b = false;
				}
			  }
			});
		chk4.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk4_b = true;
				}
				else
				{
					chk4_b = false;
				}
			  }
			});
		chk5.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk5_b = true;
				}
				else
				{
					chk5_b = false;
				}
			  }
			});
		chk6.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk6_b = true;
				}
				else
				{
					chk6_b = false;
				}
			  }
			});
		chk7.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk7_b = true;
				}
				else
				{
					chk7_b = false;

				}
			  }
			});
		chk8.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk8_b = true;
				}
				else
				{
					chk8_b = false;
					cancerTypeTV_.setText("");
				}
			  }
			});
	 
	  }
	
	private void InitiateCheckBoxes()
	{
		String table_name = "family_hist";	
        ArrayList<ArrayList<Object>> dataFamHist = _db.getAllRowsAsArrays(table_name);
        for (int position=0; position < dataFamHist.size(); position++)
		{
        	ArrayList<Object> row = dataFamHist.get(position);
        	if(Integer.parseInt(row.get(_db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
    		{
        		
        		String currId = row.get(_db.GetColumnCount(table_name,"id")).toString();
        		_rowId = Integer.parseInt(currId);
        		if(_rowId == _familyId)
        		{
        			_familyName = row.get(_db.GetColumnCount(table_name, "family_name")).toString();
             		fam_nameTV_.setText(_familyName);
             		_familyRelation = row.get(_db.GetColumnCount(table_name, "family_relation")).toString();
        			fam_relationTV_.setText(_familyRelation);
        			String otherType = row.get(_db.GetColumnCount(table_name, "other")).toString();
        			otherTypeTV_.setText(otherType);
        			String cancerType = row.get(_db.GetColumnCount(table_name, "cancerType")).toString();
        			cancerTypeTV_.setText(cancerType);
        			String chk1B = row.get(_db.GetColumnCount(table_name, "alcohol")).toString();
        			if(chk1B.equals("1"))
        			{
        				chk1.setChecked(true);
        				chk1_b = true;
        			}
        			String chk2B = row.get(_db.GetColumnCount(table_name, "depression")).toString();
        			if(chk2B.equals("1"))
        			{
        				chk2.setChecked(true);
        				chk2_b = true;
        			}
        			String chk3B = row.get(_db.GetColumnCount(table_name, "bipolar")).toString();
        			if(chk3B.equals("1"))
        			{
        				chk3.setChecked(true);
        				chk3_b = true;
        			}
        			String chk4B = row.get(_db.GetColumnCount(table_name, "anxiety")).toString();
        			if(chk4B.equals("1"))
        			{
        				chk4.setChecked(true);
        				chk4_b = true;
        			}
        			String chk5B = row.get(_db.GetColumnCount(table_name, "alzeimer")).toString();
        			if(chk5B.equals("1"))
        			{
        				chk5.setChecked(true);
        				chk5_b = true;
        			}
        			String chk6B = row.get(_db.GetColumnCount(table_name, "learning")).toString();
        			if(chk6B.equals("1"))
        			{
        				chk6.setChecked(true);
        				chk6_b = true;
        			}
        			String chk7B = row.get(_db.GetColumnCount(table_name, "adhd")).toString();
        			if(chk7B.equals("1"))
        			{
        				chk7.setChecked(true);
        				chk7_b = true;
        			}
        			String chk8B = row.get(_db.GetColumnCount(table_name, "cancer")).toString();
        			if(chk8B.equals("1"))
        			{
          				chk8.setChecked(true);
        				chk8_b = true;
        			}
        			else
        			{
        				cancerTypeTV_.setText("");
        			}

        		}
    		}
		}
	}
	
	private void SaveEditFamilyinfo()
	{
		String SfamilyName = fam_nameTV_.getText().toString();
		String SfamilyRelation = fam_relationTV_.getText().toString();
		EditText b = (EditText) findViewById(R.id.editText1);
		String ScancerType = b.getText().toString();
		EditText other = (EditText) findViewById(R.id.otherText);
		String SOther = other.getText().toString();
		
		Map<String, Object> columnsValuesFI = new HashMap<String, Object>();
		columnsValuesFI.put("MyTimeStamp", _ScurrTimestamp);
		columnsValuesFI.put("profileID", _SprofileId_);
		columnsValuesFI.put("family_name", SfamilyName);
		columnsValuesFI.put("family_relation", SfamilyRelation);
		columnsValuesFI.put("alcohol", chk1_b);
		columnsValuesFI.put("depression", chk2_b);
		columnsValuesFI.put("bipolar", chk3_b);
		columnsValuesFI.put("anxiety", chk4_b);
		columnsValuesFI.put("alzeimer", chk5_b);
		columnsValuesFI.put("learning", chk6_b);
		columnsValuesFI.put("adhd", chk7_b);
		columnsValuesFI.put("cancer", chk8_b);
		columnsValuesFI.put("other", SOther);
		columnsValuesFI.put("cancerType", ScancerType);
		_db.deleteRow(_familyId, "family_hist");
		_db.addRow(columnsValuesFI, "family_hist");
		
		_globals.CacheEditableFamilyHistory(_ScurrTimestamp, SfamilyName, SfamilyRelation, _familyName, _familyRelation, chk1_b, chk2_b, chk3_b, chk4_b, chk5_b, chk6_b, chk7_b, chk8_b, ScancerType, SOther);
		if(_globals.GetAccount().cloudID >= 0)
		{
			new FamilyHistoryService(this, EHRLables.FamilyHistoryAction.EditFamilyHistory, _globals).execute();
		}
		//_db.printAllValues("family_hist");
		//Back to family history page
		this.finish();
		Intent i = new Intent(Page_EditFamilyHistory.this, Page_FamilyHistory.class);
	    startActivity(i);
	}
	
	private void DeleteFamilyinfo()
	{
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            	_db.deleteRow(_familyId, "family_hist");
            	_globals.CacheFamilyHistory(_ScurrTimestamp, _familyName, _familyRelation, false, false, false, false, false, false, false, false, "", "");
        		if(_globals.GetAccount().cloudID >= 0)
        		{
        			new FamilyHistoryService(Page_EditFamilyHistory.this, EHRLables.FamilyHistoryAction.DeleteFamilyHistory, _globals).execute();
        		}
       		 	finish();
       		 	Intent i = new Intent(Page_EditFamilyHistory.this, Page_FamilyHistory.class);
       	        startActivity(i);
            }
        });
        
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				dialog.cancel();
        			}
        		});

        AlertDialog alert11 = builder1.create();
        alert11.show();
	}
	
	public void ServerResponse_EditFamilyHistory(JSONObject i_response)
	{
		 try
		 {
			 String result = i_response.getString("Result");
			 if(result.compareTo("Success") == 0)
			 {
				JSONObject errMessage = (JSONObject)i_response.get("Message");
				//store this userid into the local account table;
				Toast.makeText(getApplicationContext(), "Success: Content: " + (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();			
			 }
			 else if(result.compareTo("Fail") == 0)
			 {
				 JSONObject errMessage = (JSONObject)i_response.get("Message");
				 Toast.makeText(getApplicationContext(), "Fail: ErrorCode: " + (Integer)(errMessage.get("ErrorCode")) + "Content: "+ (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
			 }
		 }
		 catch(JSONException e)
		 {
			 
		 }
	}
	
	public void ServerResponse_DeleteFamilyHistory(JSONObject i_response)
	{
		 try
		 {
			 String result = i_response.getString("Result");
			 if(result.compareTo("Success") == 0)
			 {
				JSONObject errMessage = (JSONObject)i_response.get("Message");
				//store this userid into the local account table;
				Toast.makeText(getApplicationContext(), "Success: Content: " + (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();			
			 }
			 else if(result.compareTo("Fail") == 0)
			 {
				 JSONObject errMessage = (JSONObject)i_response.get("Message");
				 Toast.makeText(getApplicationContext(), "Fail: ErrorCode: " + (Integer)(errMessage.get("ErrorCode")) + "Content: "+ (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
			 }
		 }
		 catch(JSONException e)
		 {
			 
		 }
	}
	
	@Override
	public void onBackPressed() {
	     super.onBackPressed();
	     this.finish();
	     Intent i = new Intent(Page_EditFamilyHistory.this, Page_FamilyHistory.class);
		 startActivity(i);
	}
}
