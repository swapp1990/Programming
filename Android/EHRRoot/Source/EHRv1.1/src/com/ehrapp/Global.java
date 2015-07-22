package com.ehrapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import com.ehrapp.dataservice.Account;
import com.ehrapp.dataservice.Consultation;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.dataservice.FamilyHistory;
import com.ehrapp.dataservice.Medication;
import com.ehrapp.dataservice.Profile;


public class Global extends Application {
	
	private Account _myAccount = new Account();
	private Profile _myProfile = new Profile();
	private Consultation _myConsultation = new Consultation();
	private FamilyHistory _myFamilyHistory = new FamilyHistory();
	private JSONObject _myTimeStamps = null;
	private JSONObject _serverTimeStamps = null;
	private JSONObject _myPatientData = null;
	private DatabaseManager _myDatabase = null;

	public void ClearGlobals()
	{
		_myAccount = new Account();
		_myProfile = new Profile();
		_myConsultation = new Consultation();
		_myTimeStamps = null;
		_serverTimeStamps = null;
		_myPatientData = null;		
	}
	
	//Cache for Family History
	public FamilyHistory GetFamilyHistory()
	{
		return _myFamilyHistory;
	}
	
	public void CacheFamilyHistory(long i_timeStamp, 
								String i_familyName, 
								String i_familyRelation, 
								boolean i_alcohol,
								boolean i_depression,
								boolean i_bipolar,
								boolean i_anxiety,
								boolean i_alzeimer,
								boolean i_learning,
								boolean i_adhd,
								boolean i_cancer,
								String i_cancerType,
								String i_other)
	{
		_myFamilyHistory.MyTimeStamp = i_timeStamp;
		_myFamilyHistory.FamilyName = i_familyName;
		_myFamilyHistory.FamilyRelation = i_familyRelation;
		_myFamilyHistory.Alcohol = i_alcohol;
		_myFamilyHistory.Depression = i_depression;
		_myFamilyHistory.Bipolar = i_bipolar;
		_myFamilyHistory.Anxiety = i_anxiety;
		_myFamilyHistory.Alzeimer = i_alzeimer;
		_myFamilyHistory.Learning = i_learning;
		_myFamilyHistory.Adhd = i_adhd;
		_myFamilyHistory.Cancer = i_cancer;
		_myFamilyHistory.CancerType = i_cancerType;
		_myFamilyHistory.Other = i_other;
	}
	
	public void CacheEditableFamilyHistory(long i_timeStamp, 
			String i_familyName, 
			String i_familyRelation, 
			String i_oldFamilyName,
			String i_oldFamilyRelation,
			boolean i_alcohol,
			boolean i_depression,
			boolean i_bipolar,
			boolean i_anxiety,
			boolean i_alzeimer,
			boolean i_learning,
			boolean i_adhd,
			boolean i_cancer,
			String i_cancerType,
			String i_other)
	{
		_myFamilyHistory.MyTimeStamp = i_timeStamp;
		_myFamilyHistory.FamilyName = i_familyName;
		_myFamilyHistory.FamilyRelation = i_familyRelation;
		_myFamilyHistory.OldFamilyName = i_oldFamilyName;
		_myFamilyHistory.OldFamilyRelation = i_oldFamilyRelation;
		_myFamilyHistory.Alcohol = i_alcohol;
		_myFamilyHistory.Depression = i_depression;
		_myFamilyHistory.Bipolar = i_bipolar;
		_myFamilyHistory.Anxiety = i_anxiety;
		_myFamilyHistory.Alzeimer = i_alzeimer;
		_myFamilyHistory.Learning = i_learning;
		_myFamilyHistory.Adhd = i_adhd;
		_myFamilyHistory.Cancer = i_cancer;
		_myFamilyHistory.CancerType = i_cancerType;
		_myFamilyHistory.Other = i_other;
	}
	
	public void ClearFamilyHistory()
	{
		_myFamilyHistory = new FamilyHistory();
	}
	
	public DatabaseManager GetDatabase()
	{
		if(_myDatabase == null)
			_myDatabase = new DatabaseManager(getApplicationContext());
		return _myDatabase;
	}
	
	//Server TimeStamp Functions
	public void AddServerTimeStamp(String i_tableName, long i_timeStamp)
	{
		try
		{
			_serverTimeStamps.put(i_tableName, i_timeStamp);
		}
		catch(JSONException e)
		{
			System.out.println("Error puting stuff in the time stamp: " + e.toString());
		}
	}
	
	public void AddServerTimeStamp(JSONObject i_obj)
	{
		_serverTimeStamps = i_obj;
	}
	
	public void ClearServerTimeStamps()
	{
		_serverTimeStamps = new JSONObject();
	}
	
	public JSONObject GetServerTimeStamp()
	{
		return _serverTimeStamps;
	}
	
	//Local TimeStamp Functions
	public void AddTimeStamp(String i_tableName, long i_timeStamp)
	{
		try
		{
			_myTimeStamps.put(i_tableName, i_timeStamp);
		}
		catch(JSONException e)
		{
			System.out.println("Error puting stuff in the time stamp: " + e.toString());
		}
	}
	
	public JSONObject GetTimeStamp()
	{
		return _myTimeStamps;
	}

	public void ClearTimeStamps()
	{
		_myTimeStamps = new JSONObject();
	}	
	
	public void ClearPatientData()
	{
		_myPatientData = new JSONObject();
	}

	//Patient Data Cache
	public void AddPatientData(String i_tableName, JSONObject i_data)
	{
		try
		{
			_myPatientData.put(i_tableName, i_data);
		}
		catch(JSONException e)
		{
			System.out.println("Error puting stuff in the time stamp: " + e.toString());
		}
	}
	
	public void SetProfile(Profile i_profile)
	{
		_myProfile = i_profile;
	}
	
	public void SetAccount(Account i_account)
	{
		_myAccount = i_account;
	}
	
	public Account GetAccount()
	{
		return _myAccount;
	}
	
	public Profile GetProfile()
	{
		return _myProfile;
	}
	
	public void ClearProfile()
	{
		_myProfile = new Profile();
	}
	
	public Medication AddMedication(String i_drug, String i_dosage, String i_freq, Long i_startDate, Long i_endDate)
	{
		return _myConsultation.AddMed(i_drug, i_dosage, i_freq, i_startDate, i_endDate);
	}
	
	public Consultation GetConsultation()
	{
		return _myConsultation;
	}
	
	/* Utility Functions */
	
	public static String GetStringFromInputStream(InputStream is) 
	{
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try
		{
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) 
			{
				sb.append(line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (br != null) 
			{
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
	
	public static String convertLongtoDate(String data)
	{
		String _dateValue;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
		long millisecond = Long.parseLong(data);

		_dateValue = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
		return _dateValue;
		
	}
	
	public static boolean isLong( String input )  
	{  
	   try  
	   {  
		  long value = Long.parseLong( input );  
		  if(value > 10000000000L)
			  return true;  
		  else
			  return false;
	   }  
	   catch( Exception e)  
	   {  
	      return false;  
	   }  
	}  
	
	public static String titleize(String source){
        boolean cap = true;
        char[]  out = source.toCharArray();
        int i, len = source.length();
        for(i=0; i<len; i++){
            if(Character.isWhitespace(out[i])){
                cap = true;
                continue;
            }
            if(cap){
                out[i] = Character.toUpperCase(out[i]);
                cap = false;
            }
        }
        return new String(out);
    }
	
	public static FileWriter generateCsvFile(File sFileName,String fileContent) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(sFileName);
            writer.append(fileContent);
                         writer.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally
        {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return writer;
    }

	 public static boolean CheckInternetConnection(Activity i_activity)
	 {
		    ConnectivityManager cm = (ConnectivityManager) i_activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo[] ni = cm.getAllNetworkInfo();
		    if (ni == null) 
		    {
		    	// There are no active networks.
		    	return false;
		    }
		    else
		    {
		    	for (int i = 0; i < ni.length; i++)
		        {
		    		if (ni[i].getState() == NetworkInfo.State.CONNECTED)                        
		    		{
		    			return true;
		    		}
		        }
		        return false;
		    }
	 }
	
	//Create Tables for Health Records
	public void CreateAllOtherTables()
	 {
		 ////////////////////Physician Info //////////////////
		Map<String, String> columns_Physician = new HashMap<String,String>();
		columns_Physician.put("MyTimeStamp", "long");
		columns_Physician.put("profileID", "integer");
		columns_Physician.put("Name", "text");
		columns_Physician.put("Organization", "text");
		if(!_myDatabase.isTableExists("physician_info"))
		{
		// System.out.println("Table does not exists!");
		 _myDatabase.createTablewithForeignKey("physician_info",columns_Physician,"user_accounts","physician_info_fk_uid");
		}	
		
		 ////////////////////Assesment & Plans//////////////////
		Map<String, String> columns_assesment = new HashMap<String,String>();
		columns_assesment.put("MyTimeStamp", "long");
		columns_assesment.put("profileID", "integer");
		columns_assesment.put("Note", "text");
		if(!_myDatabase.isTableExists("assesment_note"))
		{
		// System.out.println("Assesmtn Table does not exists!");
		 _myDatabase.createTablewithForeignKey("assesment_note",columns_assesment,"user_accounts","assesment_note_fk_uid");
		}	

		 //////////////////// IllnessHistory //////////////////
		 Map<String, String> columns_Illness = new HashMap<String,String>();
		 columns_Illness.put("MyTimeStamp", "long");
		 columns_Illness.put("profileID", "integer");
		 columns_Illness.put("Symptom", "text");
		 columns_Illness.put("Onset", "text");
		 columns_Illness.put("Duration", "long");
		 columns_Illness.put("OTC", "text");
		 if(!_myDatabase.isTableExists("illness_history"))
		 {
			// System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("illness_history",columns_Illness,"user_accounts","past_medical_fk_uid");
		 }
		 
		 //////////////////// PastMedical //////////////////
		 Map<String, String> columns_pastMedical = new HashMap<String,String>();
		 columns_pastMedical.put("MyTimeStamp", "long");
		 columns_pastMedical.put("profileID", "integer");
		 columns_pastMedical.put("conditionType", "text");
		 columns_pastMedical.put("onset", "long");
		 columns_pastMedical.put("treatment", "text");
		 columns_pastMedical.put("conditionStatus", "text");
		 if(!_myDatabase.isTableExists("past_medical"))
		 {
			// System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("past_medical",columns_pastMedical,"user_accounts","illness_fk_uid");
		 }
		   
		 //////////////////// Current Medications //////////////////
		 Map<String, String> columns_currentMedication = new HashMap<String,String>();
		 columns_currentMedication.put("MyTimeStamp", "long");
		 columns_currentMedication.put("profileID", "integer");
		 columns_currentMedication.put("drug", "text");
		 columns_currentMedication.put("dosage", "text");
		 columns_currentMedication.put("frequency", "text");
		 columns_currentMedication.put("start_date", "long");
		 columns_currentMedication.put("stop_date", "long");
		 if(!_myDatabase.isTableExists("current_medication"))
		 {
			// System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("current_medication",columns_currentMedication,"user_accounts","current_medication_fk_uid");
		 }
		 
		 //////////////////// Allergies //////////////////
		 Map<String, String> columns_allergies = new HashMap<String,String>();
		 columns_allergies.put("MyTimeStamp", "long");
		 columns_allergies.put("profileID", "integer");
		 columns_allergies.put("allergyType", "text");
		 columns_allergies.put("reaction", "text");
		 columns_allergies.put("severity", "text");
		 columns_allergies.put("date_last_occurred, ", "long");
		 columns_allergies.put("treatment", "text");
		 if(!_myDatabase.isTableExists("allergies"))
		 {
			// System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("allergies",columns_allergies,"user_accounts","allergies_fk_uid");
		 }
		 
		 //////////////////// Social History //////////////////
		 Map<String, String> columns_social_history = new HashMap<String,String>();
		 columns_social_history.put("MyTimeStamp", "long");
		 columns_social_history.put("profileID", "integer");
		 columns_social_history.put("maritalStatus", "text");
		 columns_social_history.put("occupation", "text");
		 columns_social_history.put("coffe_consumption", "text");
		 columns_social_history.put("tobacco_use", "text");
		 columns_social_history.put("alcohol_use", "text");
		 columns_social_history.put("drug_use", "text");
		 if(!_myDatabase.isTableExists("social_history"))
		 {
			 //System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("social_history",columns_social_history,"user_accounts","social_history_fk_uid");
		 }
		 
		 //////////////////// Body Systems //////////////////
		 Map<String, String> columns_body_systems = new HashMap<String,String>();
		 columns_body_systems.put("MyTimeStamp", "long");
		 columns_body_systems.put("profileID", "integer");
		 columns_body_systems.put("skin", "text");
		 columns_body_systems.put("vision", "text");
		 columns_body_systems.put("hearing", "text");
		 columns_body_systems.put("respiratory", "text");
		 columns_body_systems.put("cardiovascular", "text");
		 columns_body_systems.put("gastrointestinal", "text");
		 columns_body_systems.put("gynecologic", "text");
		 columns_body_systems.put("musculoskeletal", "text");
		 columns_body_systems.put("vascular", "text");
		 columns_body_systems.put("neurologic", "text");
		 columns_body_systems.put("hematologic", "text");
		 columns_body_systems.put("endocrine", "text");
		 columns_body_systems.put("psychiatric", "text");
		 columns_body_systems.put("urological", "text");
		 columns_body_systems.put("other", "text");
		 if(!_myDatabase.isTableExists("body_systems"))
		 {
			// System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("body_systems",columns_body_systems,"user_accounts","body_systems_fk_uid");
		 }
		 
		 //////////////////// Vital Signs //////////////////
		 Map<String, String> columns_vital_signs = new HashMap<String,String>();
		 columns_vital_signs.put("MyTimeStamp", "long");
		 columns_vital_signs.put("profileID", "integer");
		 columns_vital_signs.put("pulse", "int");
		 columns_vital_signs.put("respiratory_rate", "float");
		 columns_vital_signs.put("systolic_blood_pressure", "float");
		 columns_vital_signs.put("diastolic_blood_pressure", "float");
		 columns_vital_signs.put("body_temp", "text");
		 columns_vital_signs.put("height", "text");
		 columns_vital_signs.put("weight", "text");
		 columns_vital_signs.put("BMI", "text");
		 if(!_myDatabase.isTableExists("vital_signs"))
		 {
			// System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("vital_signs",columns_vital_signs,"user_accounts","vital_signs_fk_uid");
		 }
		 
		 //////////////////// Diagnostic Findings //////////////////
		 Map<String, String> columns_diagnosis_finding = new HashMap<String,String>();
		 columns_diagnosis_finding.put("MyTimeStamp", "long");
		 columns_diagnosis_finding.put("profileID", "integer");
		 columns_diagnosis_finding.put("test_name", "text");
		 columns_diagnosis_finding.put("result_finding", "text");
		 columns_diagnosis_finding.put("date", "long");
		 columns_diagnosis_finding.put("interpretation", "text");
		 if(!_myDatabase.isTableExists("diagnosis_finding"))
		 {
			 //System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("diagnosis_finding",columns_diagnosis_finding,"user_accounts","diagnosis_finding_fk_uid");
		 }
		 
		 //////////////////// Procedure History //////////////////
		 Map<String, String> columns_procedure_history = new HashMap<String,String>();
		 columns_procedure_history.put("MyTimeStamp", "long");
		 columns_procedure_history.put("profileID", "integer");
		 columns_procedure_history.put("procedure_name", "text");
		 columns_procedure_history.put("date", "long");
		 columns_procedure_history.put("physician_name", "text");
		 columns_procedure_history.put("institution_location", "text");
		 columns_procedure_history.put("result", "text");
		 //columns_vital_signs.put("result", "text");
		 if(!_myDatabase.isTableExists("procedure_history"))
		 {
			 //System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("procedure_history",columns_procedure_history,"user_accounts","procedure_history_fk_uid");
		 }
		 
		 //////////////////// Immunication/Vaccine //////////////////
		 Map<String, String> columns_immunization = new HashMap<String,String>();
		 columns_immunization.put("MyTimeStamp", "long");
		 columns_immunization.put("profileID", "integer");
		 columns_immunization.put("vaccine_name", "text");
		 columns_immunization.put("vaccine_type", "text");
		 columns_immunization.put("dose", "text");
		 columns_immunization.put("age", "integer");
		 columns_immunization.put("date_administered", "long");
		 columns_immunization.put("lot_number", "text");
		 if(!_myDatabase.isTableExists("immunization"))
		 {
			// System.out.println("Table does not exists!");
			 _myDatabase.createTablewithForeignKey("immunization",columns_immunization,"user_accounts","immunization_fk_uid");
		 }
		 
		////////////////////Family History //////////////////
		Map<String, String> columns_family_hist = new HashMap<String,String>();
		columns_family_hist.put("MyTimeStamp", "long");
		columns_family_hist.put("profileID", "integer");
		columns_family_hist.put("family_name", "text");
		columns_family_hist.put("family_relation", "text");
		columns_family_hist.put("alcohol", "boolean");
		columns_family_hist.put("depression", "boolean");
		columns_family_hist.put("bipolar", "boolean");
		columns_family_hist.put("anxiety", "boolean");
		columns_family_hist.put("alzeimer", "boolean");
		columns_family_hist.put("learning", "boolean");
		columns_family_hist.put("adhd", "boolean");
		columns_family_hist.put("cancer", "boolean");
		columns_family_hist.put("cancerType", "text");
		columns_family_hist.put("other", "text");
		if(!_myDatabase.isTableExists("family_hist"))
		{
		  // System.out.println("Table does not exists!");
		  _myDatabase.createTablewithForeignKey("family_hist",columns_family_hist,"user_accounts","family_hist_fk_uid");
		}
	 }
}
