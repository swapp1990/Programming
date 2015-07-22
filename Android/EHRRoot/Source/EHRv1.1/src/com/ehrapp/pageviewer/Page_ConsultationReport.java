package com.ehrapp.pageviewer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.ehrapp.EHRLables.FragmentType;
import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.Allergy;
import com.ehrapp.dataservice.BodySystemReview;
import com.ehrapp.dataservice.ChiefComplaint;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.dataservice.DiagnosticFinding;
import com.ehrapp.dataservice.Immunization;
import com.ehrapp.dataservice.Medication;
import com.ehrapp.dataservice.PhysicianInfo;
import com.ehrapp.dataservice.ProcedureHistory;
import com.ehrapp.dataservice.SocialHistory;
import com.ehrapp.dataservice.VitalSigns;
import com.ehrapp.dialog.CreateAllergyFragment;
import com.ehrapp.dialog.CreateAllergyFragment.CreateAllergyListener;
import com.ehrapp.dialog.CreateBodySystemReviewFragment;
import com.ehrapp.dialog.CreateBodySystemReviewFragment.CreateBodySystemReviewListener;
import com.ehrapp.dialog.CreateChiefComplaintFragment;
import com.ehrapp.dialog.CreateChiefComplaintFragment.CreateChiefComplaintListener;
import com.ehrapp.dialog.CreateDiagnosticFindingFragment;
import com.ehrapp.dialog.CreateDiagnosticFindingFragment.CreateDiagnosticFindingListener;
import com.ehrapp.dialog.CreateImmunizationFragment;
import com.ehrapp.dialog.CreateImmunizationFragment.CreateImmunizationListener;
import com.ehrapp.dialog.CreateMedicationFragment;
import com.ehrapp.dialog.CreateMedicationFragment.CreateMedicationListener;
import com.ehrapp.dialog.CreateProcedureHistoryFragment;
import com.ehrapp.dialog.CreateProcedureHistoryFragment.CreateProcedureHistoryListener;
import com.ehrapp.dialog.DatePickerFragment;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Page_ConsultationReport extends FragmentActivity implements
		CreateImmunizationListener, CreateDiagnosticFindingListener,
		CreateProcedureHistoryListener, CreateBodySystemReviewListener,
		CreateChiefComplaintListener, CreateAllergyListener,
		CreateMedicationListener, OnDateSetListener {

	View _medicationListLinearLayout = null;
	View _allergyListLinearLayout = null;
	View _chiefcomplaintListLinearLayout = null;
	View _procedureHistoryListLinearLayout = null;
	View _diagnosticFindingListLinearLayout = null;
	View _immunizationListLinearLayout = null;
	View _vitalSignsLinearLayout = null;

	DialogFragment _currentFragment = null;
	FragmentType _currentFragmentType = null;

	private ImageButton saveButton_;
	private ImageButton _backButton = null;
	DatabaseManager _db;

	ImageButton _addMedicationButton = null;
	ImageButton _addAllergyButton = null;
	ImageButton _addChiefComplaintButton = null;
	ImageButton _addProcedureHistoryButton = null;
	ImageButton _addDiagnosticFindingButton = null;
	ImageButton _addImmunizationButton = null;

	// none fragments fields
	private EditText _physicianName_EditText = null;
	private EditText _physicianOrganization_EditText = null;
	private EditText _sh_MaritalStatus_EditText = null;
	private EditText _sh_Occupation_EditText = null;
	private EditText _sh_CoffeeConsumption_EditText = null;
	private EditText _sh_TobaccoUse_EditText = null;
	private EditText _sh_AlcoholUse_EditText = null;
	private EditText _sh_DruglUse_EditText = null;
	private EditText _ap_Note_EditText = null;

	private EditText _vs_Pulse_EditText = null;
	private EditText _vs_RespirateRate_EditText = null;
	private EditText _vs_SystolicBloodPressure_EditText = null;
	private EditText _vs_DisstolicBloodPressure_EditText = null;
	private EditText _vs_BodyTemp_EditText = null;
	private EditText _vs_Height_EditText = null;
	private EditText _vs_Weight_EditText = null;
	private EditText _vs_BMI_EditText = null;
	
	

	
	private EditText _assesmentPlans_EditText = null;

	private TextView _consultation_report, _physician_info_header,
			_chief_complain_header, allergy_header, medication_header,
			_social_history_header;
	private TextView _review_of_body_system_header, _procedure_history_header,
			_diagnostic_findings_header, _assessment_and_plan_header,
			_immunization_header, _vitalsigns_header;

	EditText _reviewBodySystem_EditText = null;

	private Global _globals;

	Long ScurrTimestamp;
	int SprofileId_;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cosultationreport_layout);

		_globals = ((Global) getApplicationContext());
		_db = _globals.GetDatabase();

		ScurrTimestamp = System.currentTimeMillis();
		SprofileId_ = _globals.GetProfile().ProfileID;

		InitLayouts();
		InitFonts();
		AddListeners();
		
		showCurrentMedications();
		showCurrentAllergies();

	}
	
	void InitLayouts()
	{
		_physicianName_EditText = (EditText) findViewById(R.id.physicianname);
		_physicianOrganization_EditText = (EditText) findViewById(R.id.physicianorganization);
		_sh_MaritalStatus_EditText = (EditText) findViewById(R.id.maritalstatus_sh);
		_sh_Occupation_EditText = (EditText) findViewById(R.id.occupation_sh);
		_sh_CoffeeConsumption_EditText = (EditText) findViewById(R.id.coffeeconsumption_sh);
		_sh_TobaccoUse_EditText = (EditText) findViewById(R.id.tobaccouse_sh);
		_sh_AlcoholUse_EditText = (EditText) findViewById(R.id.alcoholuse_sh);
		_sh_DruglUse_EditText = (EditText) findViewById(R.id.druguse_sh);
		_ap_Note_EditText = (EditText) findViewById(R.id.textbox_plan);

		_vs_Pulse_EditText = (EditText) findViewById(R.id.pulse_vs);
		_vs_RespirateRate_EditText = (EditText) findViewById(R.id.respiratoryrate_vs);
		_vs_SystolicBloodPressure_EditText = (EditText) findViewById(R.id.systolicbp_vs);
		_vs_DisstolicBloodPressure_EditText = (EditText) findViewById(R.id.diastolicbp_vs);
		_vs_BodyTemp_EditText = (EditText) findViewById(R.id.bodytemp_vs);
		_vs_Height_EditText = (EditText) findViewById(R.id.height_vs);
		_vs_Weight_EditText = (EditText) findViewById(R.id.weight_vs);
		_vs_BMI_EditText = (EditText) findViewById(R.id.bmi_vs);
		
		_assesmentPlans_EditText = (EditText) findViewById(R.id.textbox_plan);

		_medicationListLinearLayout = findViewById(R.id.medication_list_layout);
		_allergyListLinearLayout = findViewById(R.id.allergy_list_layout);
		_chiefcomplaintListLinearLayout = findViewById(R.id.chiefcomplaint_list_layout);
		_procedureHistoryListLinearLayout = findViewById(R.id.procedure_list_layout);
		_diagnosticFindingListLinearLayout = findViewById(R.id.diagnosticfindings_list_layout);
		_immunizationListLinearLayout = findViewById(R.id.immunization_list_layout);

		saveButton_ = (ImageButton) findViewById(R.id.saveButton);
		_backButton = (ImageButton) findViewById(R.id.selectionlog_cloud_btn);
		
		
		_addMedicationButton = (ImageButton) findViewById(R.id.AddMedicationButton);
		_addAllergyButton = (ImageButton) findViewById(R.id.AddAllergyButton);
		_addChiefComplaintButton = (ImageButton) findViewById(R.id.AddChiefComplaintButton);
		_addProcedureHistoryButton = (ImageButton) findViewById(R.id.AddProcedureButton);
		_addDiagnosticFindingButton = (ImageButton) findViewById(R.id.AddDiagnosticFindingsButton);
		_addImmunizationButton = (ImageButton) findViewById(R.id.AddImmunizationButton);

		_reviewBodySystem_EditText = (EditText) findViewById(R.id.textbox_rbs);
		
		_consultation_report = (TextView) findViewById(R.id.header);
		_physician_info_header = (TextView) findViewById(R.id.physicianinfo);
		_chief_complain_header = (TextView) findViewById(R.id.header_chiefcomplaint);
		allergy_header = (TextView) findViewById(R.id.header_allergy);
		medication_header = (TextView) findViewById(R.id.header_medication);
		_social_history_header = (TextView) findViewById(R.id.header_socialhistory);
		_review_of_body_system_header = (TextView) findViewById(R.id.header_rbs);
		_procedure_history_header = (TextView) findViewById(R.id.header_procedure);
		_diagnostic_findings_header = (TextView) findViewById(R.id.header_diagnosticfindings);
		_assessment_and_plan_header = (TextView) findViewById(R.id.header_plan);
		_immunization_header = (TextView) findViewById(R.id.header_immunization);
		_vitalsigns_header = (TextView) findViewById(R.id.header_vitalsigns);
	}
	
	void InitFonts()
	{
		Typeface textFont = Typeface.createFromAsset(getAssets(),
				"fonts/MYRIADPRO-REGULAR.OTF");
		Typeface titleFont = Typeface.createFromAsset(getAssets(),
				"fonts/MYRIADPRO-BOLD.OTF");
		
		_consultation_report.setTypeface(titleFont);
		_physician_info_header.setTypeface(titleFont);
		_physicianName_EditText.setTypeface(textFont);
		_physicianOrganization_EditText.setTypeface(textFont);
		_chief_complain_header.setTypeface(titleFont);
		allergy_header.setTypeface(titleFont);
		medication_header.setTypeface(titleFont);
		_social_history_header.setTypeface(titleFont);
		_sh_MaritalStatus_EditText.setTypeface(textFont);
		_sh_Occupation_EditText.setTypeface(textFont);
		_sh_CoffeeConsumption_EditText.setTypeface(textFont);
		_sh_TobaccoUse_EditText.setTypeface(textFont);
		_sh_AlcoholUse_EditText.setTypeface(textFont);
		_sh_DruglUse_EditText.setTypeface(textFont);
		_review_of_body_system_header.setTypeface(titleFont);
		_procedure_history_header.setTypeface(titleFont);
		_diagnostic_findings_header.setTypeface(titleFont);
		_assessment_and_plan_header.setTypeface(titleFont);
		_immunization_header.setTypeface(titleFont);
		_vitalsigns_header.setTypeface(titleFont);
	}
	
	void AddListeners()
	{
		_vs_Height_EditText.addTextChangedListener(new TextWatcher() {          
			 public void afterTextChanged(Editable s) {
				 System.out.println("has not focus 1");
				 CalculateBMI();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		_vs_Weight_EditText.addTextChangedListener(new TextWatcher() {          
			 public void afterTextChanged(Editable s) {
				 System.out.println("has not focus");
				 CalculateBMI();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		

		saveButton_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveInfo();
			}
		});
		
		
		_backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backToPatientHistory();
			}
		});

		_reviewBodySystem_EditText.setInputType(InputType.TYPE_NULL);
		_reviewBodySystem_EditText
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!_reviewBodySystem_EditText.getText().toString()
								.isEmpty()) {
							OnEditReviewBodySystemClicked(v);
						} else {
							OnAddReviewBodySystemClicked(v);
						}
					}
				});
		
		_reviewBodySystem_EditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							if (!_reviewBodySystem_EditText.getText()
									.toString().isEmpty()) {
								OnEditReviewBodySystemClicked(v);
							} else {
								OnAddReviewBodySystemClicked(v);
							}
						}
					}
				});

		_addMedicationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OnAddMedicationClicked(v);
			}
		});

		_addAllergyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OnAddAllergyClicked(v);
			}
		});

		_addChiefComplaintButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OnAddChiefComplaintClicked(v);
			}
		});

		_addProcedureHistoryButton
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						OnAddProcedureHistoryClicked(v);
					}
				});

		_addDiagnosticFindingButton
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						OnAddDiagnosticFindingClicked(v);
					}
				});

		_addImmunizationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OnAddImmunizationClicked(v);
			}
		});
	}
	
	void CalculateBMI()
	{
		if(!_vs_Height_EditText.getText().toString().equals("") && !_vs_Weight_EditText.getText().toString().equals(""))
		{
			float height = Float.parseFloat(_vs_Height_EditText.getText().toString());
			float weight = Float.parseFloat(_vs_Weight_EditText.getText().toString());
			float bmi = 0;
			if(height != 0 || weight != 0)
			{
				NumberFormat formatter = NumberFormat.getNumberInstance();
				formatter.setMaximumFractionDigits(1);
				float height_m = height/100;
				bmi = weight/(height_m * height_m);
				String bmi_string = formatter.format(bmi);
				_vs_BMI_EditText.setText(bmi_string);
			}
			
		}
		else
		{
			_vs_BMI_EditText.setText("");
		}
	}
	
	/* [START] ------- Add Button Section --------- [START] */

	public void OnAddMedicationClicked(View view) {
		showCreateMedicationDialog(null);
	}

	public void OnAddAllergyClicked(View view) {
		showCreateAllergyDialog(null);

	}

	public void OnAddChiefComplaintClicked(View view) {
		showCreateChiefComplaintDialog(null);

	}

	public void OnAddReviewBodySystemClicked(View view) {
		showCreateReviewBodySystemDialog(null);
	}

	public void OnAddProcedureHistoryClicked(View view) {
		showCreateProcedureHistoryDialog(null);
	}

	public void OnAddDiagnosticFindingClicked(View view) {
		showCreateDiagnosticFindingDialog(null);
	}

	public void OnAddImmunizationClicked(View view) {
		showCreateImmunizationDialog(null);
	}

	public void OnEditReviewBodySystemClicked(View view) {
		showCreateReviewBodySystemDialog(_globals.GetConsultation().GetRBS());
	}
	
	private void backToPatientHistory()
	{
	    Intent i = new Intent(Page_ConsultationReport.this, Page_Patient_History.class);
	    startActivity(i);
	}
	
	//when you click the save button, save all the consultation data you input into the local database
	private void saveInfo() {

		// make sure the data in here is stored in the consultation class.
		VerifyPhysicianInfo();
		VerifySocialHistory();
		VerifyAssessmentAndPlan();
		VerifyVitalSigns();

		// ////////////////Physician Info ////////////////////////////
		PhysicianInfo pI = _globals.GetConsultation().GetPI();
		if (pI != null) 
		{
			String Sname = pI.PhysicianName;
			String Sorg = pI.PhysicianOrganization;
			Map<String, Object> columnsValuesPI = new HashMap<String, Object>();
			columnsValuesPI.put("MyTimeStamp", ScurrTimestamp);
			columnsValuesPI.put("profileID", SprofileId_);
			columnsValuesPI.put("Name", Sname);
			columnsValuesPI.put("Organization", Sorg);

			_db.addRow(columnsValuesPI, "physician_info");
			_db.printAllValues("physician_info");
		}
		
		// ////////////////Assesmnt & Plans ////////////////////////////
		String sAssesmentPlans = " ";
		sAssesmentPlans = _assesmentPlans_EditText.getText().toString();
		
		Map<String, Object> columnsValuesAP = new HashMap<String, Object>();
		columnsValuesAP.put("MyTimeStamp", ScurrTimestamp);
		columnsValuesAP.put("profileID", SprofileId_);
		columnsValuesAP.put("Note", sAssesmentPlans);

		_db.addRow(columnsValuesAP, "assesment_note");
		_db.printAllValues("assesment_note");
			
		// ////////////////IllnessHistory ////////////////////////////
		List<ChiefComplaint> listHistory = _globals.GetConsultation().GetCCs();
		if (listHistory != null)
		{
			for (int i = 0; i < listHistory.size(); i++) 
			{
				ChiefComplaint hist1 = listHistory.get(i);
				// Long ScurrTimestamp = System.currentTimeMillis()/1000;
				// int profileId_ = _globals.GetProfile().ProfileID;
				String Ssymptom = hist1.Symptom;
				String Sonset = hist1.OnsetOfSymptom;
				String Lduration = hist1.duration;
				String SOTC = hist1.OTCTreatment;
				// String LendDate = hist1.Note;
				// System.out.println("dosage " + med1.StartDate);
				Map<String, Object> columnsValues2 = new HashMap<String, Object>();
				columnsValues2.put("MyTimeStamp", ScurrTimestamp);
				columnsValues2.put("profileID", SprofileId_);
				columnsValues2.put("Symptom", Ssymptom);
				columnsValues2.put("Onset", Sonset);
				columnsValues2.put("Duration", Lduration);
				columnsValues2.put("OTC", SOTC);

				_db.addRow(columnsValues2, "illness_history");
				_db.printAllValues("illness_history");
			}
		}
		// //////////////////////////////////////////////////////////

		// //////////////// Medication ////////////////////////////
		List<Medication> listMeds = _globals.GetConsultation().GetMeds();
		if (listMeds != null) {
			for (int i = 0; i < listMeds.size(); i++) {
				Medication med1 = listMeds.get(i);

				String Sdosage = med1.Dosage;
				String Sdrug = med1.Drug;
				String Sfreq = med1.Freq;
				Long LstartDate = med1.StartDate;
				Long LendDate = med1.EndDate;
				// System.out.println("dosage " + med1.StartDate);
				Map<String, Object> columnsValues2 = new HashMap<String, Object>();
				columnsValues2.put("MyTimeStamp", ScurrTimestamp);
				columnsValues2.put("profileID", SprofileId_);
				columnsValues2.put("drug", Sdrug);
				columnsValues2.put("dosage", Sdosage);
				columnsValues2.put("frequency", Sfreq);
				columnsValues2.put("start_date", LstartDate);
				columnsValues2.put("stop_date", LendDate);

				_db.addRow(columnsValues2, "current_medication");
				_db.printAllValues("current_medication");
			}
		}
		// //////////////////////////////////////////////////////////

		// //////////////// Allergies ////////////////////////////

		List<Allergy> listAllergies = _globals.GetConsultation().GetAlgs();
		if (listAllergies != null) {
			for (int i = 0; i < listAllergies.size(); i++) {
				Allergy all1 = listAllergies.get(i);

				String Stype = all1.Type;
				String Sreaction = all1.Reaction;
				String Sseverity = all1.Severity;
				Long LlastDate = all1.LastDate;
				String Streatment = all1.Treatment;
				// System.out.println("dosage " + med1.StartDate);
				Map<String, Object> columnsValues2 = new HashMap<String, Object>();
				columnsValues2.put("MyTimeStamp", ScurrTimestamp);
				columnsValues2.put("profileID", SprofileId_);
				columnsValues2.put("allergyType", Stype);
				columnsValues2.put("reaction", Sreaction);
				columnsValues2.put("severity", Sseverity);
				columnsValues2.put("date_last_occurred", LlastDate);
				columnsValues2.put("treatment", Streatment);

				_db.addRow(columnsValues2, "allergies");
				_db.printAllValues("allergies");
			}
		}
		// //////////////////////////////////////////////////////////

		// //////////////// Body Systems ////////////////////////////
		BodySystemReview listRBS = _globals.GetConsultation().GetRBS();
		if (listRBS != null) {
			//System.out.println("RBS " + listRBS);
			BodySystemReview rbs1 = listRBS;
			
			String SSkin = rbs1.Skin;
			System.out.println("RBS " + SSkin);
			String SVision = rbs1.Vision;
			String SHearing = rbs1.Hearing;
			String SRespiratory = rbs1.Respiratory;
			String SCardiovascular = rbs1.Cardiovascular;
			String SGastrointestinal = rbs1.Gastrointestinal;
			String SGynecologic = rbs1.Gynecologic;
			String SMusculoskeletal = rbs1.Musculoskeletal;
			String SVascular = rbs1.Vascular;
			String SNeurologic = rbs1.Neurologic;
			String SHematologic = rbs1.Hematologic;
			String SEndocrine = rbs1.Endocrine;
			String SPsychiatric = rbs1.Psychiatric;
			String SUrological = rbs1.Urological;
			System.out.println("S_Uro: " + rbs1.Urological);
			String SOther = rbs1.Other;

			// System.out.println("dosage " + med1.StartDate);
			Map<String, Object> columnsValues2 = new HashMap<String, Object>();
			columnsValues2.put("MyTimeStamp", ScurrTimestamp);
			columnsValues2.put("profileID", SprofileId_);
			columnsValues2.put("skin", SSkin);
			columnsValues2.put("vision", SVision);
			columnsValues2.put("hearing", SHearing);
			columnsValues2.put("respiratory", SRespiratory);
			columnsValues2.put("cardiovascular", SCardiovascular);
			columnsValues2.put("gastrointestinal", SGastrointestinal);
			columnsValues2.put("gynecologic", SGynecologic);
			columnsValues2.put("musculoskeletal", SMusculoskeletal);
			columnsValues2.put("vascular", SVascular);
			columnsValues2.put("neurologic", SNeurologic);
			columnsValues2.put("hematologic", SHematologic);
			columnsValues2.put("endocrine", SEndocrine);
			columnsValues2.put("psychiatric", SPsychiatric);
			columnsValues2.put("urological", SUrological);
			columnsValues2.put("other", SOther);
			
			_db.addRow(columnsValues2, "body_systems");
			_db.printAllValues("body_systems");

		}
		// //////////////////////////////////////////////////////////

		// //////////////// Procedure History ////////////////////////////

		List<ProcedureHistory> listProcedures = _globals.GetConsultation()
				.GetPHs();
		if (listProcedures != null) {
			for (int i = 0; i < listProcedures.size(); i++) {
				ProcedureHistory ph1 = listProcedures.get(i);

				String SProcedure = ph1.Procedure;
				String SPhysician = ph1.Physician;
				String SLocation = ph1.Location;
				Long LDate = ph1.Date;
				String SResult = ph1.Result;
				// System.out.println("dosage " + med1.StartDate);
				Map<String, Object> columnsValues2 = new HashMap<String, Object>();
				columnsValues2.put("MyTimeStamp", ScurrTimestamp);
				columnsValues2.put("profileID", SprofileId_);
				columnsValues2.put("procedure_name", SProcedure);
				columnsValues2.put("physician_name", SPhysician);
				columnsValues2.put("institution_location", SLocation);
				columnsValues2.put("date", LDate);
				columnsValues2.put("result", SResult);
				_db.addRow(columnsValues2, "procedure_history");
				_db.printAllValues("procedure_history");
			}
		}
		// //////////////////////////////////////////////////////////

		// ////////////////Diagnostic Findings ////////////////////////////

		List<DiagnosticFinding> listDiagnostics = _globals.GetConsultation()
				.GetDFs();
		if (listDiagnostics != null) {
			for (int i = 0; i < listDiagnostics.size(); i++) {
				DiagnosticFinding df1 = listDiagnostics.get(i);

				String STest = df1.Test;
				String SResult = df1.Result;
				String SInterpretation = df1.Interpretation;
				Long LDate = df1.Date;

				// System.out.println("dosage " + med1.StartDate);
				Map<String, Object> columnsValues2 = new HashMap<String, Object>();
				columnsValues2.put("MyTimeStamp", ScurrTimestamp);
				columnsValues2.put("profileID", SprofileId_);
				columnsValues2.put("test_name", STest);
				columnsValues2.put("result_finding", SResult);
				columnsValues2.put("interpretation", SInterpretation);
				columnsValues2.put("date", LDate);

				_db.addRow(columnsValues2, "diagnosis_finding");
				_db.printAllValues("diagnosis_finding");
			}
		}
		// //////////////////////////////////////////////////////////

		// ////////////////Immunization ////////////////////////////
		List<Immunization> listImmune = _globals.GetConsultation().GetIVs();
		if (listImmune != null) {
			for (int i = 0; i < listImmune.size(); i++) {
				Immunization if1 = listImmune.get(i);

				String SName = if1.Vaccine;
				String SType = if1.Type;
				String SDose = if1.Dose;
				Long LDate = if1.Date;
				int IAge = if1.Age;
				String SLot = if1.LotNumber;

				// System.out.println("dosage " + med1.StartDate);
				Map<String, Object> columnsValues2 = new HashMap<String, Object>();
				columnsValues2.put("MyTimeStamp", ScurrTimestamp);
				columnsValues2.put("profileID", SprofileId_);
				columnsValues2.put("vaccine_name", SName);
				columnsValues2.put("vaccine_type", SType);
				columnsValues2.put("dose", SDose);
				columnsValues2.put("age", IAge);
				columnsValues2.put("date_administered", LDate);
				columnsValues2.put("lot_number", SLot);

				_db.addRow(columnsValues2, "immunization");
				_db.printAllValues("immunization");
			}
		}
		// //////////////////////////////////////////////////////////

		// ////////////////Social History ////////////////////////////
		SocialHistory listSocialhist = _globals.GetConsultation().GetSH();
		if (listSocialhist != null) {
			String SmaritalStatus = listSocialhist.MaritalStatus;
			String Soccupation = listSocialhist.Occupation;
			String Scoffe_consumption = listSocialhist.CoffeeConsumption;
			String Stobacco_use = listSocialhist.TobaccoUse;
			String Salcohol_Use = listSocialhist.AlcoholUse;
			String Sdrug_use = listSocialhist.DrugUse;

			// System.out.println("dosage " + med1.StartDate);
			Map<String, Object> columnsValuessoc = new HashMap<String, Object>();
			columnsValuessoc.put("MyTimeStamp", ScurrTimestamp);
			columnsValuessoc.put("profileID", SprofileId_);
			columnsValuessoc.put("maritalStatus", SmaritalStatus);
			columnsValuessoc.put("occupation", Soccupation);
			columnsValuessoc.put("coffe_consumption", Scoffe_consumption);
			columnsValuessoc.put("tobacco_use", Stobacco_use);
			columnsValuessoc.put("alcohol_use", Salcohol_Use);
			columnsValuessoc.put("drug_use", Sdrug_use);

			_db.addRow(columnsValuessoc, "social_history");
			_db.printAllValues("social_history");
		}
		// //////////////////////////////////////////////////////////
		
		// ////////////////Vital Signs ////////////////////////////
		VitalSigns listVS = _globals.GetConsultation().GetVS();
		if (listVS != null) {
					 
			// System.out.println("dosage " + med1.StartDate);
			Map<String, Object> columnsValuessoc = new HashMap<String, Object>();
			columnsValuessoc.put("MyTimeStamp", ScurrTimestamp);
			columnsValuessoc.put("profileID", SprofileId_);
			columnsValuessoc.put("pulse", listVS.Pulse);
			columnsValuessoc.put("respiratory_rate", listVS.RespirateRate);
			columnsValuessoc.put("systolic_blood_pressure", listVS.SystolicBloodPressure);
			columnsValuessoc.put("diastolic_blood_pressure", listVS.DisstolicBloodPressure);
			columnsValuessoc.put("body_temp", listVS.BodyTemp);
			columnsValuessoc.put("height", listVS.Height);
			columnsValuessoc.put("weight", listVS.Weight);
			columnsValuessoc.put("BMI", listVS.BMI);
			System.out.println("Systolic " + listVS.SystolicBloodPressure + " " + listVS.DisstolicBloodPressure + " " +  listVS.BodyTemp);
			_db.addRow(columnsValuessoc, "vital_signs");
			_db.printAllValues("vital_signs");
		}
		// //////////////////////////////////////////////////////////
		_globals.GetConsultation().ClearThemAll();
		Intent i = new Intent(Page_ConsultationReport.this,
				Page_Patient_History.class);
		startActivity(i);
	}

	private void showCurrentMedications()
	{
		String table_name = "current_medication";
		ArrayList<ArrayList<Object>> dataMedic = _db.getAllRowsAsArrays(table_name);
		int numberPastMedic = 0;
		for (int position=0; position < dataMedic.size(); position++)
		{
			ArrayList<Object> row = dataMedic.get(position);
			if(Integer.parseInt(row.get(_db.GetColumnCount(table_name, "profileID")).toString()) == _globals.GetProfile().ProfileID)
			{
				String Date = null;
				String Drug = row.get(_db.GetColumnCount("current_medication", "drug")).toString();
				String Dosage = row.get(_db.GetColumnCount("current_medication", "dosage")).toString();
				String Frequency = row.get(_db.GetColumnCount("current_medication", "frequency")).toString();
				long lStartdate = Long.parseLong(row.get(_db.GetColumnCount(table_name, "start_date")).toString());
				long lenddate = Long.parseLong(row.get(_db.GetColumnCount(table_name, "stop_date")).toString());
				TextView valueTV = new TextView(this);
				
				Calendar startDate = Calendar.getInstance();
				Calendar endDate = Calendar.getInstance();

				startDate.clear();
				endDate.clear();

				startDate.setTimeInMillis(lStartdate);
				endDate.setTimeInMillis(lenddate);

				valueTV.setText(Drug
						+ ", "
						+ Dosage
						+ ", "
						+ Frequency
						+ ", "
						+ startDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
								Locale.getDefault())
						+ "-"
						+ startDate.get(Calendar.DATE)
						+ "-"
						+ startDate.get(Calendar.YEAR)
						+ ", "
						+ endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
								Locale.getDefault()) + "-"
						+ endDate.get(Calendar.DATE) + "-"
						+ endDate.get(Calendar.YEAR));
				// valueTV.setId(5);
				valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				((LinearLayout) _medicationListLinearLayout).addView(valueTV);
				
			}
		}
	}
	
	private void showCurrentAllergies()
	{
    	ArrayList<ArrayList<Object>> dataAll = _db.getAllRowsAsArrays("allergies");
	 	int numberAllergies = 0;
    	for (int position=0; position < dataAll.size(); position++)
    	{
    		ArrayList<Object> row = dataAll.get(position);
    		
    		if(Integer.parseInt(row.get(3).toString()) == _globals.GetProfile().ProfileID)
    		{
    			String AllergyType = row.get(4).toString();
     			String Reaction = row.get(1).toString();
    			String Severity = row.get(2).toString();
    			String Treatment = row.get(6).toString();
    			String DateLast = row.get(7).toString();
    			long lDateLast = Long.parseLong(DateLast);
    			
    			TextView valueTV = new TextView(this);
    			
    			Calendar lastDate = Calendar.getInstance();
    			lastDate.clear();
    			lastDate.setTimeInMillis(lDateLast);

    			valueTV.setText(AllergyType
    					+ ", "
    					+ Reaction
    					+ ", "
    					+ Severity
    					+ ", "
    					+ lastDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
    							Locale.getDefault()) + "-"
    					+ lastDate.get(Calendar.DATE) + "-"
    					+ lastDate.get(Calendar.YEAR) + ", " + Treatment);
    			valueTV.setTextColor(Color.RED);
    			valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
    					LayoutParams.WRAP_CONTENT));
    			((LinearLayout) _allergyListLinearLayout).addView(valueTV);
    		}
    	}
	}
	/* [END] ------- Add Button Section --------- [END] */

	/* [START] ----- Non-Fragment Stuff Check ---- [START] */
	public void VerifyPhysicianInfo() {
		String physicianName = _physicianName_EditText.getText().toString();
		String physicianOrganization = _physicianOrganization_EditText
				.getText().toString();

		if (physicianName.isEmpty() && physicianOrganization.isEmpty()) {
			_globals.GetConsultation().ClearPI();
		} else {
			_globals.GetConsultation().AddPhysicianInfo(physicianName,
					physicianOrganization);
		}
	}

	public void VerifySocialHistory() {

		String matitalStatus = _sh_MaritalStatus_EditText.getText().toString();
		String occupation = _sh_Occupation_EditText.getText().toString();
		String coffeeConsumption = _sh_CoffeeConsumption_EditText.getText()
				.toString();
		String tobaccoUse = _sh_TobaccoUse_EditText.getText().toString();
		String alcoholUse = _sh_AlcoholUse_EditText.getText().toString();
		String druglUse = _sh_DruglUse_EditText.getText().toString();

		if (matitalStatus.isEmpty() && occupation.isEmpty()
				&& coffeeConsumption.isEmpty() && alcoholUse.isEmpty()
				&& druglUse.isEmpty()) {
			_globals.GetConsultation().ClearSH();
		} else {
			_globals.GetConsultation().AddSocialHistory(matitalStatus,
					occupation, coffeeConsumption, tobaccoUse, alcoholUse,
					druglUse);
		}

	}

	public void VerifyVitalSigns() {
		String sPulse = _vs_Pulse_EditText.getText().toString();
		int Pulse = 0;
		if (!sPulse.isEmpty()) {
			Pulse = Integer.parseInt(sPulse);
		}

		int RespirateRate = 0;
		String sRespirateRate = _vs_RespirateRate_EditText.getText().toString();
		if (!sRespirateRate.isEmpty()) {
			RespirateRate = Integer.parseInt(sRespirateRate);
		}

		float SystolicBloodPressure = 0.0f;
		String sSBP = _vs_SystolicBloodPressure_EditText.getText().toString();
		if (!sSBP.isEmpty()) {
			SystolicBloodPressure = Float.parseFloat(sSBP);
		}

		float DisstolicBloodPressure = 0.0f;
		String sDBP = _vs_DisstolicBloodPressure_EditText.getText().toString();
		if (!sDBP.isEmpty()) {
			DisstolicBloodPressure = Float.parseFloat(sDBP);
		}
		String BodyTemp = _vs_BodyTemp_EditText.getText().toString();
		String Height = _vs_Height_EditText.getText().toString();
		String Weight = _vs_Weight_EditText.getText().toString();

		float BMI = 0.0f;
		String sBMI = _vs_BMI_EditText.getText().toString();
		sBMI = sBMI.replace(",","");
		if(!sBMI.isEmpty())
		{
			BMI = Float.parseFloat(sBMI);
		}

		if (Pulse == 0 && RespirateRate == 0 && DisstolicBloodPressure == 0.0f
				&& SystolicBloodPressure == 0.0f && BodyTemp.isEmpty()
				&& Height.isEmpty() && Weight.isEmpty()) {
			_globals.GetConsultation().ClearVS();
		} else {
			_globals.GetConsultation().AddVitalSigns(Pulse, RespirateRate,
					SystolicBloodPressure, DisstolicBloodPressure, BodyTemp,
					Height, Weight, BMI);
		}
	}

	public void VerifyAssessmentAndPlan() {
		_ap_Note_EditText = (EditText) findViewById(R.id.textbox_plan);
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/MYRIADPRO-REGULAR.OTF");
		_ap_Note_EditText.setTypeface(tf);

		String plan = _ap_Note_EditText.getText().toString();
		if (plan.isEmpty()) {
			_globals.GetConsultation().ClearAssessment();
		} else {
			_globals.GetConsultation().SetAssessment(plan);
		}
	}

	/* [END] ----- Non-Fragment Stuff Check ----- [END] */

	/* [START] ------- Display Fragment Section --------- [START] */
	private void showCreateAllergyDialog(Allergy i_alg) {
		FragmentManager fm = getSupportFragmentManager();
		_currentFragment = new CreateAllergyFragment();
		int index = -1;
		if (i_alg != null) {
			index = _globals.GetConsultation().GetAlgs().indexOf(i_alg);
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		if (index != -1) {
			args.putString("Type", i_alg.Type);
			args.putString("Reaction", i_alg.Reaction);
			args.putString("Severity", i_alg.Severity);
			args.putLong("LastDate", i_alg.LastDate);
			args.putString("Treatment", i_alg.Treatment);
		}
		_currentFragment.setArguments(args);
		_currentFragmentType = FragmentType.CreateAllergy;
		_currentFragment.show(fm, "Allergy");
	}

	private void showCreateDiagnosticFindingDialog(DiagnosticFinding i_df) {
		FragmentManager fm = getSupportFragmentManager();
		_currentFragment = new CreateDiagnosticFindingFragment();
		int index = -1;
		if (i_df != null) {
			index = _globals.GetConsultation().GetDFs().indexOf(i_df);
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		if (index != -1) {
			args.putString("Test", i_df.Test);
			args.putString("Result", i_df.Result);
			args.putLong("Date", i_df.Date);
			args.putString("Interpretation", i_df.Interpretation);
		}
		_currentFragment.setArguments(args);
		_currentFragmentType = FragmentType.CreateDiagnosticFinding;
		_currentFragment.show(fm, "DiagnosticFinding");
	}

	private void showCreateImmunizationDialog(Immunization i_iv) {
		FragmentManager fm = getSupportFragmentManager();
		_currentFragment = new CreateImmunizationFragment();
		int index = -1;
		if (i_iv != null) {
			index = _globals.GetConsultation().GetIVs().indexOf(i_iv);
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		if (index != -1) {
			args.putString("Vaccine", i_iv.Vaccine);
			args.putString("Type", i_iv.Type);
			args.putString("Dose", i_iv.Dose);
			args.putInt("Age", i_iv.Age);
			args.putLong("Date", i_iv.Date);
			args.putString("LotNumber", i_iv.LotNumber);
		}
		_currentFragment.setArguments(args);
		_currentFragmentType = FragmentType.CreateImmunization;
		_currentFragment.show(fm, "Immunization");
	}

	private void showCreateProcedureHistoryDialog(ProcedureHistory i_ph) {
		FragmentManager fm = getSupportFragmentManager();
		_currentFragment = new CreateProcedureHistoryFragment();
		int index = -1;
		if (i_ph != null) {
			index = _globals.GetConsultation().GetPHs().indexOf(i_ph);
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		if (index != -1) {
			args.putString("Procedure", i_ph.Procedure);
			args.putLong("Date", i_ph.Date);
			args.putString("Physician", i_ph.Physician);
			args.putString("Location", i_ph.Location);
			args.putString("Result", i_ph.Result);
		}
		_currentFragment.setArguments(args);
		_currentFragmentType = FragmentType.CreateProcedureHistory;
		_currentFragment.show(fm, "ProcedureHistory");
	}

	private void showCreateChiefComplaintDialog(ChiefComplaint i_cc) {
		FragmentManager fm = getSupportFragmentManager();
		_currentFragment = new CreateChiefComplaintFragment();
		int index = -1;
		if (i_cc != null) {
			index = _globals.GetConsultation().GetCCs().indexOf(i_cc);
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		if (index != -1) {
			args.putString("Sympton", i_cc.Symptom);
			args.putString("OnsetOfSympton", i_cc.OnsetOfSymptom);
			args.putString("Duration", i_cc.duration);
			args.putString("OtcTreatment", i_cc.OTCTreatment);

		}
		_currentFragment.setArguments(args);
		_currentFragmentType = FragmentType.CreateChiefComplaint;
		_currentFragment.show(fm, "ChiefComplaint");
	}

	private void showCreateMedicationDialog(Medication i_med) {
		FragmentManager fm = getSupportFragmentManager();
		_currentFragment = new CreateMedicationFragment();
		int index = -1;
		if (i_med != null) {
			index = _globals.GetConsultation().GetMeds().indexOf(i_med);
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		if (index != -1) {
			args.putString("Drug", i_med.Drug);
			args.putString("Dosage", i_med.Dosage);
			args.putString("Freq", i_med.Freq);
			args.putLong("StartDate", i_med.StartDate);
			args.putLong("EndDate", i_med.EndDate);
		}
		_currentFragment.setArguments(args);
		_currentFragmentType = FragmentType.CreateMedication;
		_currentFragment.show(fm, "Medication");
	}

	private void showCreateReviewBodySystemDialog(BodySystemReview i_rbs) {
		FragmentManager fm = getSupportFragmentManager();
		_currentFragment = new CreateBodySystemReviewFragment();
		int index = -1;
		if (i_rbs != null) {
			index = 1;
		}
		Bundle args = new Bundle();
		args.putInt("index", index);
		if (index != -1) {
			args.putString("skin", i_rbs.Skin);
			args.putString("vision", i_rbs.Vision);
			args.putString("hearing", i_rbs.Hearing);
			args.putString("respiratory", i_rbs.Respiratory);
			args.putString("cardiovascular", i_rbs.Cardiovascular);
			args.putString("gastrointestinal", i_rbs.Gastrointestinal);
			args.putString("gynecologic", i_rbs.Gynecologic);
			args.putString("musculoskeletal", i_rbs.Musculoskeletal);
			args.putString("vascular", i_rbs.Vascular);
			args.putString("neurologic", i_rbs.Neurologic);
			args.putString("hematologic", i_rbs.Hematologic);
			args.putString("endocrine", i_rbs.Endocrine);
			args.putString("psychiatric", i_rbs.Psychiatric);
			args.putString("urological", i_rbs.Urological);
			args.putString("other", i_rbs.Other);
		}
		_currentFragment.setArguments(args);
		_currentFragmentType = FragmentType.CreateBodySystemReview;
		_currentFragment.show(fm, "BodySystemReview");
	}

	/* [END] ------- Display Fragment Section --------- [END] */

	/* [START] ------- Fragment Listener Section --------- [START] */

	/* --- Immunization stuff --- */

	public void onImmunizationPositiveClick(String i_vaccine, String i_type,
			String i_dose, int i_age, Long i_date, String i_lotNumber, int i_index) {
		// if index is -1, which means the user opens the fragments by using the "+" button, so we add a new textview to the consultation layout and add it to the global.
		if (i_index == -1) {
			TextView valueTV = new TextView(this);
			Calendar date = Calendar.getInstance();

			date.clear();
			date.setTimeInMillis(i_date);

			valueTV.setText(i_vaccine
					+ ", "
					+ i_type
					+ ", "
					+ i_dose
					+ ", "
					+ i_age
					+ ", "
					+ date.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ date.get(Calendar.DATE) + "-" + date.get(Calendar.YEAR)
					+ ", " + i_lotNumber);

			final Immunization iv = _globals.GetConsultation().AddIV(i_vaccine,
					i_type, i_dose, i_age, i_date, i_lotNumber);
			
			valueTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showCreateImmunizationDialog(iv);
				}
			});

			valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			
			((LinearLayout) _immunizationListLinearLayout).addView(valueTV);
			
		}
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we edit the textview and save it to global
		else {
			TextView valueTV = (TextView) ((LinearLayout) _immunizationListLinearLayout)
					.getChildAt(i_index);
			Calendar date = Calendar.getInstance();

			date.clear();

			date.setTimeInMillis(i_date);

			valueTV.setText(i_vaccine
					+ ", "
					+ i_type
					+ ", "
					+ i_dose
					+ ", "
					+ i_age
					+ ", "
					+ date.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ date.get(Calendar.DATE) + "-" + date.get(Calendar.YEAR)
					+ ", " + i_lotNumber);

			int errCode = _globals.GetConsultation().SetIV(i_vaccine, i_type,
					i_dose, i_age, i_date, i_lotNumber, i_index);
			
			if (errCode == -1) 
			{
				System.out.println("Immunization does not exists!");
			}
		}
	}

	public void onImmunizationDeleteClick(int i_index) {
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we delete the textview and remove it from global
		if(i_index != -1)
		{
			((LinearLayout) _immunizationListLinearLayout).removeViewAt(i_index);
			_globals.GetConsultation().GetIVs().remove(i_index);
		}
	}

	/* --- Diagnostic Finding stuff --- */
	public void onDiagnosticFindingPositiveClick(String i_test,
			String i_result, long i_date, String i_interpretation, int i_index) {
		// if index is -1, which means the user opens the fragments by using the "+" button, so we add a new textview to the consultation layout and add it to the global.
		if (i_index == -1) {
			TextView valueTV = new TextView(this);
			Calendar date = Calendar.getInstance();

			date.clear();

			date.setTimeInMillis(i_date);

			valueTV.setText(i_test
					+ ", "
					+ i_result
					+ ", "
					+ date.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ date.get(Calendar.DATE) + "-" + date.get(Calendar.YEAR)
					+ ", " + i_interpretation);

			final DiagnosticFinding df = _globals.GetConsultation().AddDF(
					i_test, i_result, i_date, i_interpretation);
			
			valueTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showCreateDiagnosticFindingDialog(df);
				}
			});

			valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			((LinearLayout) _diagnosticFindingListLinearLayout)
					.addView(valueTV);
		} 
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we edit the textview and save it to global
		else {
			TextView valueTV = (TextView) ((LinearLayout) _diagnosticFindingListLinearLayout)
					.getChildAt(i_index);
			Calendar date = Calendar.getInstance();

			date.clear();

			date.setTimeInMillis(i_date);

			valueTV.setText(i_test
					+ ", "
					+ i_result
					+ ", "
					+ date.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ date.get(Calendar.DATE) + "-" + date.get(Calendar.YEAR)
					+ ", " + i_interpretation);

			int errCode = _globals.GetConsultation().SetDF(i_test, i_result,
					i_date, i_interpretation, i_index);
			
			if (errCode == -1) {
				System.out.println("df does not exists!");
			}
		}
	}

	public void onDiagnosticFindingDeleteClick(int i_index) {
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we delete the textview and remove it from global
		if(i_index != -1)
		{
			((LinearLayout) _diagnosticFindingListLinearLayout)
				.removeViewAt(i_index);
			_globals.GetConsultation().GetDFs().remove(i_index);
		}
	}

	/* --- Procedure History stuff --- */
	public void onProcedureHistoryPositiveClick(String i_procedure,
			long i_date, String i_physician, String i_location,
			String i_result, int i_index) {
		// if index is -1, which means the user opens the fragments by using the "+" button, so we add a new textview to the consultation layout and add it to the global.
		if (i_index == -1) {
			TextView valueTV = new TextView(this);
			Calendar date = Calendar.getInstance();

			date.clear();

			date.setTimeInMillis(i_date);

			valueTV.setText(i_procedure
					+ ", "
					+ date.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ date.get(Calendar.DATE) + "-" + date.get(Calendar.YEAR)
					+ ", " + i_physician + ", " + i_location + ", " + i_result);

			final ProcedureHistory ph = _globals.GetConsultation().AddPH(
					i_procedure, i_date, i_physician, i_location, i_result);
			
			valueTV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showCreateProcedureHistoryDialog(ph);
				}
			});
			
			valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			((LinearLayout) _procedureHistoryListLinearLayout).addView(valueTV);
		}
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we edit the textview and save it to global
		else {
			TextView valueTV = (TextView) ((LinearLayout) _procedureHistoryListLinearLayout)
					.getChildAt(i_index);
			Calendar date = Calendar.getInstance();

			date.clear();

			date.setTimeInMillis(i_date);

			valueTV.setText(i_procedure
					+ ", "
					+ date.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ date.get(Calendar.DATE) + "-" + date.get(Calendar.YEAR)
					+ ", " + i_physician + ", " + i_location + ", " + i_result);

			int errCode = _globals.GetConsultation().SetPH(i_procedure, i_date,
					i_physician, i_location, i_result, i_index);
			
			if (errCode == -1) {
				System.out.println("ph does not exists!");
			}
		}
	}

	public void onProcedureHistoryDeleteClick(int i_index) {
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we delete the textview and remove it from global
		if(i_index != -1)
		{
			((LinearLayout) _procedureHistoryListLinearLayout)
				.removeViewAt(i_index);
			_globals.GetConsultation().GetPHs().remove(i_index);
		}
	}

	/* --- Allergy stuff --- */
	@Override
	public void onAllergyPositiveClick(String i_type, String i_reaction,
			String i_severity, long i_lastDate, String i_treatment, int i_index) {
		// if index is -1, which means the user opens the fragments by using the "+" button, so we add a new textview to the consultation layout and add it to the global.
		if (i_index == -1) {
			TextView valueTV = new TextView(this);
			Calendar lastDate = Calendar.getInstance();

			lastDate.clear();

			lastDate.setTimeInMillis(i_lastDate);

			valueTV.setText(i_type
					+ ", "
					+ i_reaction
					+ ", "
					+ i_severity
					+ ", "
					+ lastDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ lastDate.get(Calendar.DATE) + "-"
					+ lastDate.get(Calendar.YEAR) + ", " + i_treatment);
			valueTV.setTextColor(Color.RED);

			final Allergy alg = _globals.GetConsultation().AddAlg(i_type,
					i_reaction, i_severity, i_lastDate, i_treatment);
			valueTV.setTag(alg);
			valueTV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showCreateAllergyDialog(alg);
				}
			});

			valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			((LinearLayout) _allergyListLinearLayout).addView(valueTV);
		} 
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we edit the textview and save it to global
		else {
			TextView valueTV = (TextView) ((LinearLayout) _allergyListLinearLayout)
					.getChildAt(i_index);
			Calendar lastDate = Calendar.getInstance();

			lastDate.clear();

			lastDate.setTimeInMillis(i_lastDate);

			valueTV.setText(i_type
					+ ", "
					+ i_reaction
					+ ", "
					+ i_severity
					+ ", "
					+ lastDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ lastDate.get(Calendar.DATE) + "-"
					+ lastDate.get(Calendar.YEAR) + ", " + i_treatment);

			int errCode = _globals.GetConsultation().SetAlg(i_type, i_reaction,
					i_severity, i_lastDate, i_treatment, i_index);
			if (errCode == -1) {
				System.out.println("Alg does not exists!");
			}
		}
	}

	@Override
	public void onAllergyDeleteClick(int i_index) {
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we delete the textview and remove it from global
		if(i_index != -1)
		{
			((LinearLayout) _allergyListLinearLayout).removeViewAt(i_index);
			_globals.GetConsultation().GetAlgs().remove(i_index);
		}
	}

	/* --- Medication stuff --- */
	@Override
	public void onDialogPositiveClick(String i_drug, String i_dosage,
			String i_freq, long i_startDate, long i_endDate, int i_index) {
		// if index is -1, which means the user opens the fragments by using the "+" button, so we add a new textview to the consultation layout and add it to the global.
		if (i_index == -1) {
			TextView valueTV = new TextView(this);
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();

			startDate.clear();
			endDate.clear();

			startDate.setTimeInMillis(i_startDate);
			endDate.setTimeInMillis(i_endDate);

			valueTV.setText(i_drug
					+ ", "
					+ i_dosage
					+ ", "
					+ i_freq
					+ ", "
					+ startDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault())
					+ "-"
					+ startDate.get(Calendar.DATE)
					+ "-"
					+ startDate.get(Calendar.YEAR)
					+ ", "
					+ endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ endDate.get(Calendar.DATE) + "-"
					+ endDate.get(Calendar.YEAR));
			
			final Medication med = _globals.AddMedication(i_drug, i_dosage,
					i_freq, i_startDate, i_endDate);
			
			valueTV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showCreateMedicationDialog(med);
				}
			});
			valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			((LinearLayout) _medicationListLinearLayout).addView(valueTV);
		}
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we edit the textview and save it to global
		else {
			TextView valueTV = (TextView) ((LinearLayout) _medicationListLinearLayout)
					.getChildAt(i_index);
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();

			startDate.clear();
			endDate.clear();

			startDate.setTimeInMillis(i_startDate);
			endDate.setTimeInMillis(i_endDate);

			valueTV.setText(i_drug
					+ ", "
					+ i_dosage
					+ ", "
					+ i_freq
					+ ", "
					+ startDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault())
					+ "-"
					+ startDate.get(Calendar.DATE)
					+ "-"
					+ startDate.get(Calendar.YEAR)
					+ ", "
					+ endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + "-"
					+ endDate.get(Calendar.DATE) + "-"
					+ endDate.get(Calendar.YEAR));

			int errCode = _globals.GetConsultation().SetMed(i_drug, i_dosage,
					i_freq, i_startDate, i_endDate, i_index);
			if (errCode == -1) {
				System.out.println("Med does not exists!");
			}
		}

	}

	@Override
	public void onDialogDeleteClick(int i_index) {
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we delete the textview and remove it from global
		if(i_index != -1)
		{
			((LinearLayout) _medicationListLinearLayout).removeViewAt(i_index);
			_globals.GetConsultation().GetMeds().remove(i_index);
		}
	}

	/* --- ChiefComplaint stuff --- */
	@Override
	public void onChiefComplaintPositiveClick(String i_sympton,
			String i_onsetOfSympton, String i_duration, String i_OTCTreatment,
			int i_index) {
		// if index is -1, which means the user opens the fragments by using the "+" button, so we add a new textview to the consultation layout and add it to the global.
		if (i_index == -1) {
			TextView valueTV = new TextView(this);

			valueTV.setText(i_sympton + ", " + i_duration + ", "
					+ i_onsetOfSympton + ", " + i_OTCTreatment);

			final ChiefComplaint cc = _globals.GetConsultation().AddCC(
					i_sympton, i_onsetOfSympton, i_duration, i_OTCTreatment);

			valueTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showCreateChiefComplaintDialog(cc);
				}
			});
			
			valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			valueTV.setMaxLines(7);
			((LinearLayout) _chiefcomplaintListLinearLayout).addView(valueTV);
		} 
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we edit the textview and save it to global
		else {
			TextView valueTV = new TextView(this);

			valueTV.setText(i_sympton + ", " + i_duration + ", "
					+ i_onsetOfSympton + ", " + i_OTCTreatment);

			int errCode = _globals.GetConsultation().SetCC(i_sympton,
					i_onsetOfSympton, i_duration, i_OTCTreatment, i_index);
			if (errCode == -1) {
				System.out.println("Alg does not exists!");
			}
		}
	}

	@Override
	public void onChiefComplaintDeleteClick(int i_index) {
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we delete the textview and remove it from global
		if(i_index != -1)
		{
			((LinearLayout) _chiefcomplaintListLinearLayout).removeViewAt(i_index);
			_globals.GetConsultation().GetCCs().remove(i_index);
		}
	}

	/* --- BodySystemReview stuff --- */
	public void onBodySystemReviewPositiveClick(
			String i_skin,
			String i_vision,
			String i_hearing,
			String i_respiratory,
			String i_cardiovascular,
			String i_gastrointestinal,
			String i_gynecologic,
			String i_musculoskeletal,
			String i_vascular,
			String i_neurologic,
			String i_hematologic,
			String i_endocrine,
			String i_psychiatric,
			String i_urological,
			String i_other,
			int i_index
			)
	{
		// if index is -1, which means the user opens the fragments by using the "+" button, so we add a new textview to the consultation layout and add it to the global.
		if (i_index == -1) {
			String endString = "";
			if(!i_skin.isEmpty())
			{
				endString += "Skin: ";
				endString += i_skin;
				endString += ", ";
			}
			if(!i_vision.isEmpty())
			{
				endString += "Visioin: ";
				endString += i_vision;
				endString += ", ";
			}
			if(!i_hearing.isEmpty())
			{
				endString += "Hearing: ";
				endString += i_hearing;
				endString += ", ";
			}
			if(!i_respiratory.isEmpty())
			{
				endString += "Respiratory: ";
				endString += i_respiratory;
				endString += ", ";
			}
			if(!i_cardiovascular.isEmpty())
			{
				endString += "Cardiovascular: ";
				endString += i_cardiovascular;
				endString += ", ";
			}
			if(!i_gastrointestinal.isEmpty())
			{
				endString += "Gastrointestinal: ";
				endString += i_gastrointestinal;
				endString += ", ";
			}
			if(!i_gynecologic.isEmpty())
			{
				endString += "Gynecologic: ";
				endString += i_gynecologic;
				endString += ", ";
			}
			if(!i_musculoskeletal.isEmpty())
			{
				endString += "Musculoskeletal: ";
				endString += i_musculoskeletal;
				endString += ", ";
			}
			if(!i_vascular.isEmpty())
			{
				endString += "Vascular: ";
				endString += i_vascular;
				endString += ", ";
			}
			if(!i_neurologic.isEmpty())
			{
				endString += "Neurologic: ";
				endString += i_neurologic;
				endString += ", ";
			}
			if(!i_hematologic.isEmpty())
			{
				endString += "Hematologic: ";
				endString += i_hematologic;
				endString += ", ";
			}
			if(!i_endocrine.isEmpty())
			{
				endString += "Endocrine: ";
				endString += i_endocrine;
				endString += ", ";
			}
			if(!i_psychiatric.isEmpty())
			{
				endString += "Psychiatric: ";
				endString += i_psychiatric;
				endString += ", ";
			}
			if(!i_urological.isEmpty())
			{
				endString += "Urological: ";
				endString += i_urological;
				endString += ", ";
			}
			if(!i_other.isEmpty())
			{
				endString += "Other: ";
				endString += i_other;
				endString += ", ";
			}			
			if(endString.endsWith(", "))
			{
				System.out.println("true");
				endString = endString.substring(0, endString.length() - 2);
			}
			
			_reviewBodySystem_EditText
					.setText(endString);

			_globals.GetConsultation().AddRBS(i_skin, i_vision, i_hearing, 
					i_respiratory, i_cardiovascular,
					i_gastrointestinal, i_gynecologic, i_musculoskeletal,
					i_vascular, i_neurologic, i_hematologic,
					i_endocrine, i_psychiatric, i_urological, i_other);
		} 
		// if index is not -1, which means the user opens the fragments by clicking on the textview, so we edit the textview and save it to global
		else {
			String endString = "";
			if(!i_skin.isEmpty())
			{
				endString += "Skin: ";
				endString += i_skin;
				endString += ", ";
			}
			if(!i_vision.isEmpty())
			{
				endString += "Visioin: ";
				endString += i_vision;
				endString += ", ";
			}
			if(!i_hearing.isEmpty())
			{
				endString += "Hearing: ";
				endString += i_hearing;
				endString += ", ";
			}
			if(!i_respiratory.isEmpty())
			{
				endString += "Respiratory: ";
				endString += i_respiratory;
				endString += ", ";
			}
			if(!i_cardiovascular.isEmpty())
			{
				endString += "Cardiovascular: ";
				endString += i_cardiovascular;
				endString += ", ";
			}
			if(!i_gastrointestinal.isEmpty())
			{
				endString += "Gastrointestinal: ";
				endString += i_gastrointestinal;
				endString += ", ";
			}
			if(!i_gynecologic.isEmpty())
			{
				endString += "Gynecologic: ";
				endString += i_gynecologic;
				endString += ", ";
			}
			if(!i_musculoskeletal.isEmpty())
			{
				endString += "Musculoskeletal: ";
				endString += i_musculoskeletal;
				endString += ", ";
			}
			if(!i_vascular.isEmpty())
			{
				endString += "Vascular: ";
				endString += i_vascular;
				endString += ", ";
			}
			if(!i_neurologic.isEmpty())
			{
				endString += "Neurologic: ";
				endString += i_neurologic;
				endString += ", ";
			}
			if(!i_hematologic.isEmpty())
			{
				endString += "Hematologic: ";
				endString += i_hematologic;
				endString += ", ";
			}
			if(!i_endocrine.isEmpty())
			{
				endString += "Endocrine: ";
				endString += i_endocrine;
				endString += ", ";
			}
			if(!i_psychiatric.isEmpty())
			{
				endString += "Psychiatric: ";
				endString += i_psychiatric;
				endString += ", ";
			}
			if(!i_urological.isEmpty())
			{
				endString += "Urological: ";
				endString += i_urological;
				endString += ", ";
			}
			if(!i_other.isEmpty())
			{
				endString += "Other: ";
				endString += i_other;
				endString += ", ";
			}			
			if(endString.endsWith(", "))
			{
				System.out.println("true");
				endString = endString.substring(0, endString.length() - 2);
			}
			
			_reviewBodySystem_EditText
					.setText(endString);

			int errCode = _globals.GetConsultation().SetRBS(i_skin, i_vision, i_hearing, 
					i_respiratory, i_cardiovascular,
					i_gastrointestinal, i_gynecologic, i_musculoskeletal,
					i_vascular, i_neurologic, i_hematologic,
					i_endocrine, i_psychiatric, i_urological, i_other);
			if (errCode == -1) {
				System.out.println("Rbs does not exists!");
			}
		}
	}

	public void onBodySystemReviewDeleteClick() {
		_globals.GetConsultation().ClearRBS();
		_reviewBodySystem_EditText.setText(null);
	}

	// when dialog box is closed, below method will be called.
	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
			int selectedDay) {

		switch (_currentFragmentType) {
		case CreateMedication:
			((CreateMedicationFragment) _currentFragment).SetDateData(
					selectedMonth, selectedDay, selectedYear);
			break;
		case CreateAllergy:
			((CreateAllergyFragment) _currentFragment).SetDateData(
					selectedMonth, selectedDay, selectedYear);
			break;
		case CreateProcedureHistory:
			((CreateProcedureHistoryFragment) _currentFragment).SetDateData(
					selectedMonth, selectedDay, selectedYear);
			break;
		case CreateDiagnosticFinding:
			((CreateDiagnosticFindingFragment) _currentFragment).SetDateData(
					selectedMonth, selectedDay, selectedYear);
			break;
		case CreateImmunization:
			((CreateImmunizationFragment) _currentFragment).SetDateData(
					selectedMonth, selectedDay, selectedYear);
			break;
		}
	}
	
	@Override
    public void onBackPressed() {
            super.onBackPressed();
            this.finish();
            Intent i = new Intent(Page_ConsultationReport.this, Page_Patient_History.class);
	        startActivity(i);
    }

	/* [END] ------- Fragment Listener Section --------- [END] */

}
