package com.ehrapp.dataservice;

public class Profile {
	public String FirstName;
	public String LastName;
	public boolean Gender;
	public int ProfileID;
	public long DOB;
	public String Country;
	public String Race;
	public String profilePic_path;
	
	public Profile()
	{
		ProfileID = 1;
		FirstName = "What";
		LastName = "ever";
		DOB = 0;
		Gender = true; //male
		Country = "Silvermoon";
		Race = "Mage";
		profilePic_path = "";
	}
}
