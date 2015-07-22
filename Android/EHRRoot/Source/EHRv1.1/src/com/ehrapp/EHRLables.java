package com.ehrapp;

import java.util.Arrays;
import java.util.List;

public class EHRLables {
	public static final List<String> TableNames = Arrays.asList("physician_info", "assesment_note", "illness_history", "current_medication",
			"allergies", "social_history", "body_systems", "vital_signs", "diagnosis_finding", "procedure_history", "immunization", "family_hist");

	//Column Names for all tables
	public static final String[] physician_info = {"MyTimeStamp", "profileID", "Name", "Organization"};
	public static final String[] assesment_note = {"MyTimeStamp", "profileID", "Note"};	
	public static final String[] illness_history = {"MyTimeStamp", "profileID", "Symptom", "Onset", "Duration", "OTC"};
	public static final String[] current_medication = {"MyTimeStamp", "profileID", "drug", "dosage", "frequency", "start_date", "stop_date"};	
	public static final String[] allergies = {"MyTimeStamp", "profileID", "allergyType", "reaction", "severity", "date_last_occurred", "treatment"};
	public static final String[] social_history = {"MyTimeStamp", "profileID", "maritalStatus", "occupation", "coffe_consumption", "tobacco_use", "alcohol_use", "drug_use"};
	public static final String[] body_systems = {"MyTimeStamp", "profileID", 
		 "skin",
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
	
	public static final String[] vital_signs = {
			 "MyTimeStamp", 
			 "profileID", 
			 "pulse", 
			 "respiratory_rate", 
			 "systolic_blood_pressure",
			 "diastolic_blood_pressure", 
			 "body_temp", 
			 "height", 
			 "weight", 
			 "BMI"
	};
	
	public static final String[] diagnosis_finding = 
	{
		 "MyTimeStamp", 
		 "profileID", 
		 "test_name", 
		 "result_finding",
		 "date", 
		 "interpretation"
	};
	
	public static final String[] procedure_history = 
	{
		 "MyTimeStamp", 
		 "profileID", 
		 "procedure_name", 
		 "date",
		 "physician_name",
		 "institution_location",
		 "result"
	};
	
	public static final String[] immunization = 
	{
		 "MyTimeStamp", 
		 "profileID", 
		 "vaccine_name", 
		 "vaccine_type", 
		 "dose",
		 "age", 
		 "date_administered", 
		 "lot_number"
	};
	
	public static final String[] family_hist = 
	{
		 "MyTimeStamp", 
		 "profileID", 
		 "family_name", 
		 "family_relation", 
		 "alcohol",
		 "depression", 
		 "bipolar", 
		 "anxiety",
		 "alzeimer",
		 "learning",
		 "adhd",
		 "cancer",
		 "cancerType",
		 "other"
	};

	public static final List<String[]> ColumnNames = Arrays.asList(physician_info, assesment_note, illness_history, current_medication, allergies, social_history, 
			body_systems, vital_signs, diagnosis_finding, procedure_history, immunization, family_hist);
	
	//Actions to send to the server
	public enum AccountServiceAction
	{
		CreateAccount,
		Authenticate,
		GetSecurityQuestion,
		Recover,
	}
	
	public enum ProfileServiceAction
	{
		CreateOrUpdateProfile,
		DownloadProfile,
	}
	
	public enum PatientServiceAction
	{
		SendTimeStamp,
		GetTimeStamp,
		UpdatePatientData,
	}
	
	public enum FamilyHistoryAction
	{
		CreateFamilyHistory,
		EditFamilyHistory,
		DeleteFamilyHistory,
	}
	
	//Fragment Types for Consultation Report
	public enum FragmentType
	{
		CreateMedication (1<<0),
		CreateAllergy (1<<1),
		CreateChiefComplaint (1<<2),
		CreateBodySystemReview (1<<3),
		CreateProcedureHistory (1<<4),
		CreateDiagnosticFinding (1<<5),
		CreateImmunization (1<<6),
		CreateSocialHistory (1<<7),
		CreatePhysicianInfo (1<<8),
		CreateAssessment (1<<9),
		CreateVitalSigns (1<<10);
		
		private final long _fragmentType;
		
		FragmentType(long i_fragmentType)
		{
			_fragmentType = i_fragmentType;
		}
		
        public long getStatusFlagValue(){
            return _fragmentType;
        } 
	}
}
