package com.ehrapp.dataservice;

import java.util.ArrayList;
import java.util.List;

import com.ehrapp.EHRLables.FragmentType;

//Consultation Data
public class Consultation {
	private long _activeSections = 0;
	
	private int _consultationID =0;
	private String _assessment = null;
	private SocialHistory _sh = null;
	private PhysicianInfo _pi = null;
	private BodySystemReview _rbs = null;
	private VitalSigns _vs = null;
	private List<Medication> _meds = null;
	private List<Allergy>    _algs = null;
	private List<ChiefComplaint> _ccs = null;
	private List<ProcedureHistory> _phs = null;
	private List<DiagnosticFinding> _dfs = null;
	private List<Immunization> _ivs = null;
	
	public void ClearThemAll()
	{
		_activeSections = 0;
		_consultationID =0;
		_assessment = null;
		_sh = null;
		_pi = null;
		_rbs = null;
		_vs = null;
		_meds = null;
		_algs = null;
		_ccs = null;
		_phs = null;
		_dfs = null;
		_ivs = null;		
	}
	
	public long GetActiveSection()
	{
		return _activeSections;
	}
	
	public void RemoveActiveSection(FragmentType i_type)
	{
		if((_activeSections & i_type.getStatusFlagValue()) == 1)
			_activeSections -= i_type.getStatusFlagValue();
	}
	
	//Assessment & notes
	public void SetAssessment(String i_assessment)
	{
		_assessment = i_assessment;
		_activeSections |= FragmentType.CreateAssessment.getStatusFlagValue();
	}
	
	public void ClearAssessment()
	{
		_assessment = null;
		if((_activeSections & FragmentType.CreateAssessment.getStatusFlagValue()) == 1)
			_activeSections -= FragmentType.CreateAssessment.getStatusFlagValue();
	}
	
	public String GetAssessment()
	{
		return _assessment;
	}
	
	//Physician info
	public PhysicianInfo AddPhysicianInfo(
			 String i_name,
			 String i_organization
			)
	{
		_pi = new PhysicianInfo();

		_pi.PhysicianName = i_name;
		_pi.PhysicianOrganization = i_organization;
		
		_activeSections |= FragmentType.CreatePhysicianInfo.getStatusFlagValue();
		return _pi;
	}
	
	public void SetPhysicianInfo(
			 String i_name,
			 String i_organization
			)
	{
		_pi.PhysicianName = i_name;
		_pi.PhysicianOrganization = i_organization;
	}
	
	public PhysicianInfo GetPI()
	{
		return _pi;
	}
	
	public void ClearPI()
	{
		if((_activeSections & FragmentType.CreatePhysicianInfo.getStatusFlagValue()) == 1)
			_activeSections -= FragmentType.CreatePhysicianInfo.getStatusFlagValue();
		_pi = null;
	}	

	//Vital Info
	public VitalSigns AddVitalSigns(
			int i_pulse,
			int i_respirateRate,
			float i_systolicBloodPressure,
			float i_disstolicBloodPressure,
			String i_bodyTemp,
			String i_height,
			String i_weight,
			float i_BMI
			)
	{
		_vs = new VitalSigns();
		_vs.Pulse = i_pulse;
		_vs.RespirateRate = i_respirateRate;
		_vs.SystolicBloodPressure = i_systolicBloodPressure;
		_vs.DisstolicBloodPressure = i_disstolicBloodPressure;
		_vs.BodyTemp = i_bodyTemp;
		_vs.Height = i_height;
		_vs.Weight = i_weight;
		_vs.BMI = i_BMI;
		
		_activeSections |= FragmentType.CreateVitalSigns.getStatusFlagValue();
		return _vs;
	}
	
	public void SetVitalSigns(
			int i_pulse,
			int i_respirateRate,
			float i_systolicBloodPressure,
			float i_disstolicBloodPressure,
			String i_bodyTemp,
			String i_height,
			String i_weight,
			float i_BMI
			)
	{
		_vs.Pulse = i_pulse;
		_vs.RespirateRate = i_respirateRate;
		_vs.SystolicBloodPressure = i_systolicBloodPressure;
		_vs.DisstolicBloodPressure = i_disstolicBloodPressure;
		_vs.BodyTemp = i_bodyTemp;
		_vs.Height = i_height;
		_vs.Weight = i_weight;
		_vs.BMI = i_BMI;
	}
	
	public VitalSigns GetVS()
	{
		return _vs;
	}
	
	public void ClearVS()
	{
		if((_activeSections & FragmentType.CreateVitalSigns.getStatusFlagValue()) == 1)
			_activeSections -= FragmentType.CreateVitalSigns.getStatusFlagValue();
		_vs = null;
	}
	
	//Social History
	public SocialHistory AddSocialHistory(
			 String i_maritalStatus,
			 String i_occupation,
			 String i_coffeeConsumption,
			 String i_tobaccoUse,
			 String i_alcoholUse,
			 String i_drugUse
			)
	{
		_sh = new SocialHistory();
		_sh.MaritalStatus = i_maritalStatus;
		_sh.Occupation = i_occupation;
		_sh.CoffeeConsumption = i_coffeeConsumption;
		_sh.TobaccoUse = i_tobaccoUse;
		_sh.AlcoholUse = i_alcoholUse;
		_sh.DrugUse = i_drugUse;
		
		_activeSections |= FragmentType.CreateSocialHistory.getStatusFlagValue();
		return _sh;
	}
	
	public void SetSocialHistory(
			 String i_maritalStatus,
			 String i_occupation,
			 String i_coffeeConsumption,
			 String i_tobaccoUse,
			 String i_alcoholUse,
			 String i_drugUse
			)
	{
		_sh.MaritalStatus = i_maritalStatus;
		_sh.Occupation = i_occupation;
		_sh.CoffeeConsumption = i_coffeeConsumption;
		_sh.TobaccoUse = i_tobaccoUse;
		_sh.AlcoholUse = i_alcoholUse;
		_sh.DrugUse = i_drugUse;
	}
	
	public SocialHistory GetSH()
	{
		return _sh;
	}
	
	public void ClearSH()
	{
		if((_activeSections & FragmentType.CreateSocialHistory.getStatusFlagValue()) == 1)
			_activeSections -= FragmentType.CreateSocialHistory.getStatusFlagValue();
		_sh = null;
	}
	
	//Immunization
	public Immunization AddIV(String i_vaccine, String i_type, String i_dose, int i_age, Long i_date, String i_lotNumber)
	{
		Immunization newIV = new Immunization();
		newIV.Vaccine = i_vaccine;
		newIV.Type = i_type;
		newIV.Dose = i_dose;
		newIV.Age = i_age;
		newIV.Date = i_date;
		newIV.LotNumber = i_lotNumber;

		if(_ivs != null)
		{
			_ivs.add(newIV);
			_activeSections |= FragmentType.CreateImmunization.getStatusFlagValue();
		}
		else
		{
			_ivs = new ArrayList<Immunization>();
			_ivs.add(newIV);
			_activeSections |= FragmentType.CreateImmunization.getStatusFlagValue();
		}
		
		return newIV;
	}
	
	public int SetIV(String i_vaccine, String i_type, String i_dose, int i_age, Long i_date, String i_lotNumber, int i_index)
	{
		if(i_index < _ivs.size())
		{
			Immunization newIV = _ivs.get(i_index);
			newIV.Vaccine = i_vaccine;
			newIV.Type = i_type;
			newIV.Dose = i_dose;
			newIV.Age = i_age;
			newIV.Date = i_date;
			newIV.LotNumber = i_lotNumber;
			return i_index;
		}

		return -1;
	}
	
	public List<Immunization> GetIVs()
	{
		return _ivs;
	}	
	
	//Diagnostic Finding
	public DiagnosticFinding AddDF(String i_test, String i_result, Long i_date, String i_interpretation)
	{
		DiagnosticFinding newDF = new DiagnosticFinding();
		newDF.Test = i_test;
		newDF.Result = i_result;
		newDF.Date = i_date;
		newDF.Interpretation = i_interpretation;
		
		if(_dfs != null)
		{
			_dfs.add(newDF);
			_activeSections |= FragmentType.CreateDiagnosticFinding.getStatusFlagValue();
		}
		else
		{
			_dfs = new ArrayList<DiagnosticFinding>();
			_dfs.add(newDF);
			_activeSections |= FragmentType.CreateDiagnosticFinding.getStatusFlagValue();
		}
		
		return newDF;
	}
	
	public int SetDF(String i_test, String i_result, Long i_date, String i_interpretation, int i_index)
	{
		if(i_index < _dfs.size())
		{
			DiagnosticFinding newDF = _dfs.get(i_index);
			newDF.Test = i_test;
			newDF.Result = i_result;
			newDF.Date = i_date;
			newDF.Interpretation = i_interpretation;
			return i_index;
		}

		return -1;
	}
	
	public List<DiagnosticFinding> GetDFs()
	{
		return _dfs;
	}	
	
	//Procedure History
	public ProcedureHistory AddPH(String i_procedure, Long i_date, String i_physician, String i_location, String i_result)
	{
		ProcedureHistory newPH = new ProcedureHistory();
		newPH.Procedure = i_procedure;
		newPH.Date = i_date;
		newPH.Physician = i_physician;
		newPH.Location = i_location;
		newPH.Result = i_result;
		
		if(_phs != null)
		{
			_phs.add(newPH);
			_activeSections |= FragmentType.CreateProcedureHistory.getStatusFlagValue();
		}
		else
		{
			_phs = new ArrayList<ProcedureHistory>();
			_phs.add(newPH);
			_activeSections |= FragmentType.CreateProcedureHistory.getStatusFlagValue();
		}
		
		return newPH;
	}
	
	public int SetPH(String i_procedure, Long i_date, String i_physician, String i_location, String i_result, int i_index)
	{
		if(i_index < _phs.size())
		{
			ProcedureHistory newPH = _phs.get(i_index);
			newPH.Procedure = i_procedure;
			newPH.Date = i_date;
			newPH.Physician = i_physician;
			newPH.Location = i_location;
			newPH.Result = i_result;
			return i_index;
		}

		return -1;
	}
	
	public List<ProcedureHistory> GetPHs()
	{
		return _phs;
	}
	
	//Body System Review
	public BodySystemReview AddRBS(
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
			String i_other
			)
	{
		_rbs = new BodySystemReview();

		_rbs.Skin = i_skin;
		_rbs.Vision = i_vision;
		_rbs.Hearing = i_hearing;
		_rbs.Respiratory = i_respiratory;
		_rbs.Cardiovascular = i_cardiovascular;
		_rbs.Gastrointestinal = i_gastrointestinal;
		_rbs.Gynecologic = i_gynecologic;
		_rbs.Musculoskeletal = i_musculoskeletal;
		_rbs.Vascular = i_vascular;
		_rbs.Neurologic = i_neurologic;
		_rbs.Hematologic = i_hematologic;
		_rbs.Endocrine = i_endocrine;
		_rbs.Psychiatric = i_psychiatric;
		_rbs.Urological = i_urological;
		_rbs.Other = i_other;

		_activeSections |= FragmentType.CreateBodySystemReview.getStatusFlagValue();

		return _rbs;
	}
	
	public int SetRBS(
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
			String i_other
			)
	{
		_rbs.Skin = i_skin;
		_rbs.Vision = i_vision;
		_rbs.Hearing = i_hearing;
		_rbs.Respiratory = i_respiratory;
		_rbs.Cardiovascular = i_cardiovascular;
		_rbs.Gastrointestinal = i_gastrointestinal;
		_rbs.Gynecologic = i_gynecologic;
		_rbs.Musculoskeletal = i_musculoskeletal;
		_rbs.Vascular = i_vascular;
		_rbs.Neurologic = i_neurologic;
		_rbs.Hematologic = i_hematologic;
		_rbs.Endocrine = i_endocrine;
		_rbs.Psychiatric = i_psychiatric;
		_rbs.Urological = i_urological;
		_rbs.Other = i_other;
		
		return 1;
	}
	
	public BodySystemReview GetRBS()
	{
		return _rbs;
	}
	
	public void ClearRBS()
	{
		if((_activeSections & FragmentType.CreateBodySystemReview.getStatusFlagValue()) == 1)
			_activeSections -= FragmentType.CreateBodySystemReview.getStatusFlagValue();
		_rbs = null;
	}	
	
	//Chief Complaint
	public ChiefComplaint AddCC(String i_sympton, String i_onsetOfSympton, String i_duration, String i_OTCTreatment)
	{
		ChiefComplaint newCC = new ChiefComplaint();
		newCC.Symptom = i_sympton;
		newCC.OnsetOfSymptom = i_onsetOfSympton;
		newCC.duration = i_duration;
		newCC.OTCTreatment = i_OTCTreatment;
		
		if(_ccs != null)
		{
			_ccs.add(newCC);
			_activeSections |= FragmentType.CreateChiefComplaint.getStatusFlagValue();
		}
		else
		{
			_ccs = new ArrayList<ChiefComplaint>();
			_ccs.add(newCC);
			_activeSections |= FragmentType.CreateChiefComplaint.getStatusFlagValue();
		}
		
		return newCC;
	}
	
	public int SetCC(String i_sympton, String i_onsetOfSympton, String i_duration, String i_OTCTreatment, int i_index)
	{
		if(i_index < _ccs.size())
		{
			ChiefComplaint newCC = _ccs.get(i_index);
			newCC.Symptom = i_sympton;
			newCC.OnsetOfSymptom = i_onsetOfSympton;
			newCC.duration = i_duration;
			newCC.OTCTreatment = i_OTCTreatment;
			return i_index;
		}

		return -1;
	}
	
	public List<ChiefComplaint> GetCCs()
	{
		return _ccs;
	}
	
	//Allergy
	public Allergy AddAlg(String i_type, String i_reaction, String i_severity, Long i_lastDate, String i_treatment)
	{
		Allergy newAlg = new Allergy();
		newAlg.Type = i_type;
		newAlg.Reaction = i_reaction;
		newAlg.Severity = i_severity;
		newAlg.LastDate = i_lastDate;
		newAlg.Treatment = i_treatment;
		if(_algs != null)
		{
			_algs.add(newAlg);
			_activeSections |= FragmentType.CreateAllergy.getStatusFlagValue();
		}
		else
		{
			_algs = new ArrayList<Allergy>();
			_algs.add(newAlg);
			_activeSections |= FragmentType.CreateAllergy.getStatusFlagValue();
		}
		
		return newAlg;
	}
		
	public int SetAlg(String i_type, String i_reaction, String i_severity, Long i_lastDate, String i_treatment, int i_index)
	{
		if(i_index < _algs.size())
		{
			Allergy newAlg = _algs.get(i_index);
			newAlg.Type = i_type;
			newAlg.Reaction = i_reaction;
			newAlg.Severity = i_severity;
			newAlg.LastDate = i_lastDate;
			newAlg.Treatment = i_treatment;
			return i_index;
		}

		return -1;
	}
	
	public List<Allergy> GetAlgs()
	{
		return _algs;
	}
	
	//Medication
	public Medication AddMed(String i_drug, String i_dosage, String i_freq, Long i_startDate, Long i_endDate)
	{
		Medication newMed = new Medication();
		newMed.Dosage = i_dosage;
		newMed.Drug = i_drug;
		newMed.Freq = i_freq;
		newMed.StartDate = i_startDate;
		newMed.EndDate = i_endDate;
		if(_meds != null)
		{
			_meds.add(newMed);
			_activeSections |= FragmentType.CreateMedication.getStatusFlagValue();
		}
		else
		{
			_meds = new ArrayList<Medication>();
			_meds.add(newMed);
			_activeSections |= FragmentType.CreateMedication.getStatusFlagValue();
		}
		
		return newMed;
	}
	
	public int SetMed(String i_drug, String i_dosage, String i_freq, Long i_startDate, Long i_endDate, int i_index)
	{
		if(i_index < _meds.size())
		{
			Medication med = _meds.get(i_index);
			med.Drug = i_drug;
			med.Dosage = i_dosage;
			med.Freq = i_freq;
			med.StartDate = i_startDate;
			med.EndDate = i_endDate;
			return i_index;
		}

		return -1;
	}
	
	public List<Medication> GetMeds()
	{
		return _meds;
	}
}
