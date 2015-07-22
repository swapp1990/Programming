package com.ehrapp.dataservice;

public class Account {
	
	public String username;
	public String password;
	public String securityQ;
	public String securityA;
	public Integer cloudID;
	public Boolean isPrivate;
	public Boolean hasProfile;
	
	public Account()
	{
		username = "admin";
		password = "admin";
		securityQ = "Hi";
		securityA = "Hi";
		cloudID = -1;
		isPrivate = false;
		hasProfile = false;
	}
}
