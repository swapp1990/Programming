package com.ehrapp.pageviewer;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ehrapp.EHRLables;
import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.dataservice.FamilyHistoryService;

public class Page_NewFamilyHistory extends Activity  {
	
	private DatabaseManager _db;
	private Global _globals;
	private ImageButton _newFamilySaveBtn, _deleteFamilyInfoBtn, _backimageBtn;
	private TextView fam_nameTV_, fam_relationTV_, _new_family_history_header, _choose_condition;
	private CheckBox chk1, chk2, chk3, chk4, chk5, chk6, chk7, chk8;
	private boolean chk1_b, chk2_b, chk3_b, chk4_b, chk5_b, chk6_b, chk7_b, chk8_b;
	private Long _ScurrTimestamp;
	private int _SprofileId;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_family_layout);
        
        _globals = ((Global)getApplicationContext());
        _db = _globals.GetDatabase();
        
        _ScurrTimestamp = System.currentTimeMillis();
        _SprofileId = _globals.GetProfile().ProfileID;
        
        chk1_b = chk2_b = chk3_b = chk4_b = chk5_b = chk6_b = chk7_b = chk8_b = false;
        fam_nameTV_ = (TextView) findViewById(R.id.name_fam);
        fam_relationTV_ = (TextView) findViewById(R.id.relation_fam);
        
        AddListenerOnCheckboxes();
        
        _newFamilySaveBtn = (ImageButton) findViewById(R.id.saveButton);
        _newFamilySaveBtn.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View v) {
            		SaveNewFamilyinfo();
            }
          });
        
        //Visible in edit family history page
        _deleteFamilyInfoBtn= (ImageButton) findViewById(R.id.deleteBtn);
        _deleteFamilyInfoBtn.setVisibility(View.GONE);
        
        _backimageBtn = (ImageButton) findViewById(R.id.backBtn);
        _backimageBtn.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View v) {
            	finish();
 	            Intent i = new Intent(Page_NewFamilyHistory.this, Page_FamilyHistory.class);
 		        startActivity(i);
            }
          });
        
        Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
        _new_family_history_header = (TextView)findViewById(R.id.version);
        _choose_condition= (TextView)findViewById(R.id.originalVersion);

        _new_family_history_header.setTypeface(titleFont);
        fam_nameTV_.setTypeface(textFont);
        fam_relationTV_.setTypeface(textFont);
        chk1.setTypeface(textFont);
        chk2.setTypeface(textFont);
        chk3.setTypeface(textFont);
        chk4.setTypeface(textFont);
        chk5.setTypeface(textFont);
        chk6.setTypeface(textFont);
        chk7.setTypeface(textFont);
        chk8.setTypeface(textFont);
        _choose_condition.setTypeface(textFont);
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
	 
		  }
		});
		
		chk2.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk2_b = true;
				}
		 
			  }
			});
		chk3.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk3_b = true;
				}
		 
			  }
			});
		chk4.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk4_b = true;
				}
		 
			  }
			});
		chk5.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk5_b = true;
				}
		 
			  }
			});
		chk6.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk6_b = true;
				}
		 
			  }
			});
		chk7.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk7_b = true;
				}
		 
			  }
			});
		chk8.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					chk8_b = true;
				}
		 
			  }
			});
	 
	  }
	
	private void SaveNewFamilyinfo()
	{
		String Sname = fam_nameTV_.getText().toString();
		Sname = Global.titleize(Sname);
		String Srelation = fam_relationTV_.getText().toString();
		if(Srelation.equals(null))
		{
			Srelation = "Relation";
		}
		EditText b = (EditText) findViewById(R.id.editText1);
		String ScancerType = b.getText().toString();
		EditText other = (EditText) findViewById(R.id.otherText);
		String SOther = other.getText().toString();
		Map<String, Object> columnsValuesFI = new HashMap<String, Object>();
		columnsValuesFI.put("MyTimeStamp", _ScurrTimestamp);
		columnsValuesFI.put("profileID", _SprofileId);
		columnsValuesFI.put("family_name", Sname);
		columnsValuesFI.put("family_relation", Srelation);
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
		_db.addRow(columnsValuesFI, "family_hist");
		
		_globals.CacheFamilyHistory(_ScurrTimestamp, Sname, Srelation, chk1_b, chk2_b, chk3_b, chk4_b, chk5_b, chk6_b, chk7_b, chk8_b, ScancerType, SOther);
		if(_globals.GetAccount().cloudID >= 0)
		{
			new FamilyHistoryService(this, EHRLables.FamilyHistoryAction.CreateFamilyHistory, _globals).execute();
		}
		//_db.printAllValues("family_hist");
		//Back to family history page
	    this.finish();
	    Intent i = new Intent(Page_NewFamilyHistory.this, Page_FamilyHistory.class);
	    startActivity(i);
	}
	
	public void ServerResponse_CreateFamilyHistory(JSONObject i_response)
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
	     Intent i = new Intent(Page_NewFamilyHistory.this, Page_FamilyHistory.class);
		 startActivity(i);
    }
}
