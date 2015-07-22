package com.ehrapp.pageviewer.sections;

import java.util.ArrayList;

import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.pageviewer.Page_ConsultationReport;
import com.ehrapp.pageviewer.Page_Patient_History;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class FullConsultationReport extends Activity{
	
	private Global _globals;
	private DatabaseManager db;
	
	private TextView header, _physician_info_header, _chief_complain_header, allergy_header, medication_header, _social_history_header, _vital_signs_header;
	private TextView _review_of_body_system_header, _procedure_history_header, _diagnostic_findings_header, _assessment_and_plan_header, _immunization_header;
	
    private TextView nameTitle;
    private TextView _complaintTV, _allergyTV, _medicationTV, _procedureTV, _diagnosticTV, _immunizationTV;
    private TextView _rbsTV, vitalSignsTV_, _physicianNameTV, _physicianOrgTV;
    private String S_presentIllness,S_allergies,S_medications, S_Immunization;
    private String S_rbs, S_procedureHist, S_diagFind, S_VitalSigns;
    
	private TextView _sh_MaritalStatus_EditText = null;
	private TextView _sh_Occupation_EditText = null;
	private TextView _sh_CoffeeConsumption_EditText = null;
	private TextView _sh_TobaccoUse_EditText = null;
	private TextView _sh_AlcoholUse_EditText = null;
	private TextView _sh_DruglUse_EditText = null;
	
	private TextView _assesmentPlans_TV = null;
	private ImageButton _backButton = null;

	private Long myTimeStamp;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullconsultationreport);
        Intent intent = getIntent();
        
        _globals = ((Global)getApplicationContext());
        db = _globals.GetDatabase();
        
        S_medications = S_allergies = S_presentIllness = S_rbs = S_procedureHist = S_diagFind = S_Immunization = S_VitalSigns = "";
        
        String name = intent.getStringExtra("name");
        nameTitle = (TextView) findViewById(R.id.header);
        nameTitle.setText(name);
        
        myTimeStamp = intent.getLongExtra("timeStamp", 0);
        //System.out.println("myTimeStamp: " + myTimeStamp);
        InitializeLayout();
        SetFonts();
        ShowInfo();

        _backButton = (ImageButton)findViewById(R.id.saveButton);
        _backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	 finish();
            }
          });
	}
    
    private void InitializeLayout()
    {
        _complaintTV = (TextView) findViewById(R.id.chiefcomplaint_list_layout);
        _allergyTV = (TextView) findViewById(R.id.textViewAll);
        _medicationTV = (TextView) findViewById(R.id.medication_list_layout);
        _procedureTV = (TextView) findViewById(R.id.procedure_list_layout);
        _diagnosticTV = (TextView) findViewById(R.id.diagnosticfindings_list_layout);
        _immunizationTV = (TextView) findViewById(R.id.immunization_list_layout);
		_sh_MaritalStatus_EditText = (TextView) findViewById(R.id.maritalstatus_sh);
		_sh_Occupation_EditText = (TextView) findViewById(R.id.occupation_sh);
		_sh_CoffeeConsumption_EditText = (TextView) findViewById(R.id.coffeeconsumption_sh);
		_sh_TobaccoUse_EditText = (TextView) findViewById(R.id.tobaccouse_sh);
		_sh_AlcoholUse_EditText = (TextView) findViewById(R.id.alcoholuse_sh);
		_sh_DruglUse_EditText = (TextView) findViewById(R.id.druguse_sh);
        _rbsTV = (TextView) findViewById(R.id.textbox_rbs);
        _physicianNameTV = (TextView) findViewById(R.id.physicianname);
        _physicianOrgTV = (TextView) findViewById(R.id.physicianorganization);
        vitalSignsTV_ = (TextView)findViewById(R.id.TextView05);
        _assesmentPlans_TV = (TextView)findViewById(R.id.textbox_plan);
        _physician_info_header = (TextView)findViewById(R.id.physicianinfo);
        _chief_complain_header = (TextView)findViewById(R.id.header_chiefcomplaint);
        allergy_header = (TextView)findViewById(R.id.header_allergy);
        medication_header = (TextView)findViewById(R.id.header_medication);
        _social_history_header = (TextView)findViewById(R.id.header_socialhistory);
    	_review_of_body_system_header = (TextView)findViewById(R.id.header_rbs);
    	_procedure_history_header = (TextView)findViewById(R.id.header_procedure);
    	_diagnostic_findings_header = (TextView)findViewById(R.id.header_diagnosticfindings);
    	_assessment_and_plan_header = (TextView)findViewById(R.id.header_plan);
    	_immunization_header = (TextView)findViewById(R.id.header_immunization);
    	_vital_signs_header = (TextView)findViewById(R.id.Button03);
    }						
	
    private void SetFonts()
    {
        Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
        
        _complaintTV.setTypeface(textFont);
        _allergyTV.setTypeface(textFont);
        _medicationTV.setTypeface(textFont);
        _procedureTV.setTypeface(textFont);
        _diagnosticTV.setTypeface(textFont);
        _immunizationTV.setTypeface(textFont);
        _sh_MaritalStatus_EditText.setTypeface(textFont);
        _sh_Occupation_EditText.setTypeface(textFont);
        _sh_CoffeeConsumption_EditText.setTypeface(textFont);
        _sh_TobaccoUse_EditText.setTypeface(textFont);
        _sh_AlcoholUse_EditText.setTypeface(textFont);
        _sh_DruglUse_EditText.setTypeface(textFont);
        vitalSignsTV_.setTypeface(textFont);
        //header.setTypeface(titleFont);
        _physician_info_header.setTypeface(titleFont);
        _chief_complain_header.setTypeface(titleFont);
        allergy_header.setTypeface(titleFont);
        medication_header.setTypeface(titleFont);
        _social_history_header.setTypeface(titleFont);
        _review_of_body_system_header.setTypeface(titleFont);
        _procedure_history_header.setTypeface(titleFont);
        _diagnostic_findings_header.setTypeface(titleFont);
        _assessment_and_plan_header.setTypeface(titleFont);
        _immunization_header.setTypeface(titleFont);
        _vital_signs_header.setTypeface(titleFont);
    }
    
	private void ShowInfo()
	{
		////////////////////Physician Info //////////////////
		ArrayList<ArrayList<Object>> dataPhy = db.getAllRowsAsArrays("physician_info");
		for (int position=0; position < dataPhy.size(); position++)
		{
			ArrayList<Object> row = dataPhy.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount("physician_info", "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				if(Long.parseLong(row.get(db.GetColumnCount("physician_info", "MyTimeStamp")).toString()) == myTimeStamp)
				{
						String PhysicianName = row.get(db.GetColumnCount("physician_info", "Name")).toString();
						String Organization = row.get(db.GetColumnCount("physician_info", "Organization")).toString();
						_physicianNameTV.setText(PhysicianName);
						_physicianOrgTV.setText(Organization);
						_physicianNameTV.setTextColor(Color.BLACK);
						_physicianOrgTV.setTextColor(Color.BLACK);
				}
			}
			
		}
		
		////////////////////Assesement & Plans //////////////////
		ArrayList<ArrayList<Object>> dataAss = db.getAllRowsAsArrays("assesment_note");
		String AssesmentPlans = "";
		for (int position=0; position < dataAss.size(); position++)
		{
		ArrayList<Object> row = dataAss.get(position);
		if(Integer.parseInt(row.get(db.GetColumnCount("assesment_note", "profileID")).toString()) == _globals.GetProfile().ProfileID)
		{
			if(Long.parseLong(row.get(db.GetColumnCount("assesment_note", "MyTimeStamp")).toString()) == myTimeStamp)
			{
				AssesmentPlans = row.get(3).toString();
			}
		}
		//System.out.println("S_AssesmentPlans: " + AssesmentPlans);
		_assesmentPlans_TV.setText(AssesmentPlans);
		_assesmentPlans_TV.setTextColor(Color.BLACK);
		}

		
		////////////////////IllnessHistory //////////////////
		ArrayList<ArrayList<Object>> data = db.getAllRowsAsArrays("illness_history");
		int numberIllness = 0;
		for (int position=0; position < data.size(); position++)
		{
			ArrayList<Object> row = data.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount("illness_history", "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				numberIllness++;
				if(Long.parseLong(row.get(db.GetColumnCount("illness_history", "MyTimeStamp")).toString()) == myTimeStamp)
				{
						String Symptom = row.get(db.GetColumnCount("illness_history", "Symptom")).toString();
						String Onset = row.get(db.GetColumnCount("illness_history", "Onset")).toString();
						String OTC = row.get(db.GetColumnCount("illness_history", "OTC")).toString();
						S_presentIllness = S_presentIllness + numberIllness + ". " + Symptom + " : "
								+ Onset + ", " + OTC + "\n";
				}
						
			}
		}
		//System.out.println("S_presentIllness: " + S_presentIllness);
		_complaintTV.setText(S_presentIllness);
		_complaintTV.setTextColor(Color.BLACK);
		
		 //////////////////// Allergies //////////////////
    	ArrayList<ArrayList<Object>> dataAll = db.getAllRowsAsArrays("allergies");
	 	int numberAllergies = 0;
    	for (int position=0; position < dataAll.size(); position++)
    	{
    		ArrayList<Object> row = dataAll.get(position);
    		if(Integer.parseInt(row.get(db.GetColumnCount("allergies", "profileID")).toString()) == _globals.GetProfile().ProfileID)
    		{
    			    numberAllergies++;
    			    if(Long.parseLong(row.get(db.GetColumnCount("allergies", "MyTimeStamp")).toString()) == myTimeStamp)
    				{
    					String AllergyType = row.get(db.GetColumnCount("allergies", "allergyType")).toString();
     					String Reaction = row.get(db.GetColumnCount("allergies", "reaction")).toString();
    					String Severity = row.get(db.GetColumnCount("allergies", "severity")).toString();
    					String Treatment = row.get(db.GetColumnCount("allergies", "treatment")).toString();
    					String DateLast = row.get(db.GetColumnCount("allergies", "date_last_occurred")).toString();
    					DateLast = Global.convertLongtoDate(DateLast);
    					S_allergies = S_allergies + numberAllergies + ". " + AllergyType + " : "
    							+ Reaction + ", " + Severity + ", " + Treatment + ", " + DateLast + "\n";
    				}
    		}
    	}
    	//System.out.println("Allergies: " + S_allergies);
		_allergyTV.setText(S_allergies);
		_allergyTV.setTextColor(Color.RED);
	
		////////////////////Medication //////////////////
		String table_name = "current_medication";
		ArrayList<ArrayList<Object>> dataMedic = db.getAllRowsAsArrays(table_name);
		int numberPastMedic = 0;
		for (int position=0; position < dataMedic.size(); position++)
		{
		ArrayList<Object> row = dataMedic.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				numberPastMedic++;
				if(Long.parseLong(row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString()) == myTimeStamp)
				{
						String Drug = row.get(db.GetColumnCount("current_medication", "drug")).toString();
						String Dosage = row.get(db.GetColumnCount("current_medication", "dosage")).toString();
						String Frequency = row.get(db.GetColumnCount("current_medication", "frequency")).toString();
							S_medications = S_medications + numberPastMedic + ". " +  Drug + ": "
								+ Dosage + "," + Frequency + "\n";
				}
			}
		}
		//System.out.println("S_medications: " + S_medications);
		_medicationTV.setText(S_medications);
		_medicationTV.setTextColor(Color.BLACK);
		
		////////////////////Diag Find //////////////////
		table_name = "diagnosis_finding";
		ArrayList<ArrayList<Object>> datadiagFind = db.getAllRowsAsArrays(table_name);
		int numberdatadiagFind = 0;
		for (int position=0; position < datadiagFind.size(); position++)
		{
		ArrayList<Object> row = datadiagFind.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				numberdatadiagFind++;
				if(Long.parseLong(row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString()) == myTimeStamp)
				{
						String test_name = row.get(db.GetColumnCount(table_name, "test_name")).toString();
						String result_finding = row.get(db.GetColumnCount(table_name, "result_finding")).toString();
						String interpretation = row.get(db.GetColumnCount(table_name, "interpretation")).toString();
						String DateLast = row.get(db.GetColumnCount(table_name, "date")).toString();
						DateLast = Global.convertLongtoDate(DateLast);
						S_diagFind = S_diagFind + numberdatadiagFind + ". " + test_name + ": "
								+ DateLast + "," + result_finding + "," + interpretation + "\n";
				}
			}
		}
		System.out.println("Diagnostics: " + S_diagFind);
		_diagnosticTV.setText(S_diagFind);
		_diagnosticTV.setTextColor(Color.BLACK);
		
		////////////////////Procedure Hist //////////////////
		table_name = "procedure_history";
		ArrayList<ArrayList<Object>> dataPHist = db.getAllRowsAsArrays(table_name);
		int numberPHist = 0;
		for (int position=0; position < dataPHist.size(); position++)
		{
		ArrayList<Object> row = dataPHist.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				numberPHist++;
				if(Long.parseLong(row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString()) == myTimeStamp)
				{
						String procedure = row.get(db.GetColumnCount(table_name, "procedure_name")).toString();
						String physician_name = row.get(db.GetColumnCount(table_name, "physician_name")).toString();
						String institution_location = row.get(db.GetColumnCount(table_name, "institution_location")).toString();
						String result = row.get(db.GetColumnCount(table_name, "result")).toString();
							S_procedureHist = S_procedureHist + numberPHist + ". " + procedure + ","
								+ physician_name + "," + institution_location + "," + result + "\n";
				}
			}
		}
		//System.out.println("Procedures: " + S_procedureHist);
		_procedureTV.setText(S_procedureHist);
		_procedureTV.setTextColor(Color.BLACK);
		
		////////////////////Immunization //////////////////
		table_name = "immunization";	
		ArrayList<ArrayList<Object>> dataImmune = db.getAllRowsAsArrays(table_name);
		int numberImmune = 0;
		for (int position=0; position < dataImmune.size(); position++)
		{
		ArrayList<Object> row = dataImmune.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
			numberImmune++;
				if(Long.parseLong(row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString()) == myTimeStamp)
				{
				String name = row.get(db.GetColumnCount(table_name, "vaccine_name")).toString();
				String type = row.get(db.GetColumnCount(table_name, "vaccine_type")).toString();
				String age = row.get(db.GetColumnCount(table_name, "age")).toString();
				S_Immunization = S_Immunization + numberImmune + ". " + name + ","
						+ type + "," + age + "\n";
				}
			}
		}
		//System.out.println("Immuni: " + S_Immunization);
		_immunizationTV.setText(S_Immunization);
		_immunizationTV.setTextColor(Color.BLACK);
		
		////////////////////social_history //////////////////
		table_name = "social_history";	
		ArrayList<ArrayList<Object>> dataSocialHist = db.getAllRowsAsArrays(table_name);
		int numberSocial = 0;
		for (int position=0; position < dataSocialHist.size(); position++)
		{
		ArrayList<Object> row = dataSocialHist.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
			numberSocial++;
				if(Long.parseLong(row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString()) == myTimeStamp)
				{
				String maritalStatus 	 = "Marital Status      : " + row.get(db.GetColumnCount(table_name, "maritalStatus")).toString();
				String occupation 	 	 = "Occupation          : " + row.get(db.GetColumnCount(table_name, "occupation")).toString();
				String coffe_consumption = "Coffee Consumption  : " + row.get(db.GetColumnCount(table_name, "coffe_consumption")).toString();
				String tobacco_use 		 = "Tobacco Use         : " + row.get(db.GetColumnCount(table_name, "tobacco_use")).toString();
				String alcohol_use 		 = "Alcohol Use         : " + row.get(db.GetColumnCount(table_name, "alcohol_use")).toString();
				String drug_use 		 = "Drug Use            : " + row.get(db.GetColumnCount(table_name, "drug_use")).toString();
				
				_sh_MaritalStatus_EditText.setText(maritalStatus);
				_sh_Occupation_EditText.setText(occupation);
				_sh_CoffeeConsumption_EditText.setText(coffe_consumption);
				_sh_TobaccoUse_EditText.setText(tobacco_use);
				_sh_AlcoholUse_EditText.setText(alcohol_use);
				_sh_DruglUse_EditText.setText(drug_use);
				
				_sh_MaritalStatus_EditText.setTextColor(Color.BLACK);
				_sh_Occupation_EditText.setTextColor(Color.BLACK);
				_sh_TobaccoUse_EditText.setTextColor(Color.BLACK);
				_sh_CoffeeConsumption_EditText.setTextColor(Color.BLACK);
				_sh_AlcoholUse_EditText.setTextColor(Color.BLACK);
				_sh_DruglUse_EditText.setTextColor(Color.BLACK);
				}
			}
		}
		//System.out.println("Social: " + S_SocialHist);

		////////////////////RBS //////////////////
		table_name = "body_systems";
		ArrayList<ArrayList<Object>> dataRBS = db.getAllRowsAsArrays("body_systems");
		int numberRBS = 0;
		for (int position=0; position < dataRBS.size(); position++)
		{
		ArrayList<Object> row = dataRBS.get(position);
		ArrayList<String> stringArray = new ArrayList<String>();
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				numberRBS++;
				if(Long.parseLong(row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString()) == myTimeStamp)
				{
						//String Date = row.get(db.GetColumnCount(table_name, "profileID")).toString();
					String skin = "Skin: " + row.get(db.GetColumnCount(table_name, "skin")).toString();
					if(!skin.isEmpty())
						stringArray.add(skin);
					String eyes = "Vision: " + row.get(db.GetColumnCount(table_name, "vision")).toString();
					if(!eyes.isEmpty())
						stringArray.add(eyes);
					String ears = "Hearing: " + row.get(db.GetColumnCount(table_name, "hearing")).toString();
					if(!ears.isEmpty())
						stringArray.add(ears);
					String respiratory = "Respiratory: " + row.get(db.GetColumnCount(table_name, "respiratory")).toString();
					if(!respiratory.isEmpty())
						stringArray.add(respiratory);
					String cardiovascular = "Cardiovascular: " + row.get(db.GetColumnCount(table_name, "cardiovascular")).toString();
					if(!cardiovascular.isEmpty())
						stringArray.add(cardiovascular);
					String gastrointestinal = "Gastrointestinal: " + row.get(db.GetColumnCount(table_name, "gastrointestinal")).toString();
					if(!gastrointestinal.isEmpty())
						stringArray.add(gastrointestinal);
					String gynecologic = "Gynecologic: " + row.get(db.GetColumnCount(table_name, "gynecologic")).toString();
					if(!gynecologic.isEmpty())
						stringArray.add(gynecologic);
					String musculoskeletal = "Musculoskeletal: " + row.get(db.GetColumnCount(table_name, "musculoskeletal")).toString();
					if(!musculoskeletal.isEmpty())
						stringArray.add(musculoskeletal);
					String peripheral_vascular = "Vascular: " + row.get(db.GetColumnCount(table_name, "vascular")).toString();
					if(!peripheral_vascular.isEmpty())
						stringArray.add(peripheral_vascular);
					String neurologic = "Neurologic: " +  row.get(db.GetColumnCount(table_name, "neurologic")).toString();
					if(!neurologic.isEmpty())
						stringArray.add(neurologic);
					String hematologic = "Hematologic: " + row.get(db.GetColumnCount(table_name, "hematologic")).toString();
					if(!hematologic.isEmpty())
						stringArray.add(hematologic);
					String endocrine = "Endocrine: " + row.get(db.GetColumnCount(table_name, "endocrine")).toString();
					if(!endocrine.isEmpty())
						stringArray.add(endocrine);
					String psychiatric =  "Psychiatric: " + row.get(db.GetColumnCount(table_name, "psychiatric")).toString();
					if(!psychiatric.isEmpty())
						stringArray.add(psychiatric);
					String urologic = "Urologic: " + row.get(db.GetColumnCount(table_name, "urologic")).toString();
					if(!urologic.isEmpty())
						stringArray.add(urologic);
					String other = "Other: " + row.get(db.GetColumnCount(table_name, "other")).toString();
					if(!other.isEmpty())
						stringArray.add(other);
						for(int i = 0; i < stringArray.size(); i++)
						{
							S_rbs = S_rbs + stringArray.get(i).toString() + ", ";
						}
					}
			}
		}
		System.out.println("RBS: " + S_rbs);
		_rbsTV.setText(S_rbs);
		_rbsTV.setTextColor(Color.BLACK);
	
		///////////////////Vital Signs //////////////////
		table_name = "vital_signs";	
		ArrayList<ArrayList<Object>> dataVSist = db.getAllRowsAsArrays(table_name);
		int numberVSist = 0;
		for (int position=0; position < dataVSist.size(); position++)
		{
		ArrayList<Object> row = dataVSist.get(position);
			if(Integer.parseInt(row.get(db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
			numberVSist++;
				if(Long.parseLong(row.get(db.GetColumnCount(table_name, "MyTimeStamp")).toString()) == myTimeStamp)
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
		//System.out.println("Vitl signs: " + S_VitalSigns);
		vitalSignsTV_.setText(S_VitalSigns);
		vitalSignsTV_.setTextColor(Color.BLACK);
	}//End EditInfo();
	
	@Override
    public void onBackPressed() {
            super.onBackPressed();
            this.finish();
    }
}
