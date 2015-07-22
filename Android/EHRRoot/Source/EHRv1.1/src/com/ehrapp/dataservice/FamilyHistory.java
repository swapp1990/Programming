package com.ehrapp.dataservice;

public class FamilyHistory {
	public long MyTimeStamp;
	public String FamilyName;
	public String FamilyRelation;
	public String OldFamilyName;
	public String OldFamilyRelation;
	public boolean Alcohol;
	public boolean Depression;
	public boolean Bipolar;
	public boolean Anxiety;
	public boolean Alzeimer;
	public boolean Learning;
	public boolean Adhd;
	public boolean Cancer;
	public String CancerType;
	public String Other;
	
	public FamilyHistory()
	{
		MyTimeStamp = 0;
		FamilyName = "";
		FamilyRelation = "";
		OldFamilyName = "";
		OldFamilyRelation = "";
		Alcohol = false;
		Depression = false;
		Bipolar = false;
		Anxiety = false;
		Alzeimer = false;
		Learning = false;
		Adhd = false;
		Cancer = false;
		CancerType = "";
		Other = "";
	}
}
