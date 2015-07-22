package com.ehrapp.pageviewer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.TextView;
import com.ehrapp.*;

public class PickPages extends Activity {
	 private Button choose;
	 private CheckBox checkBoxObj1, checkBoxObj2, checkBoxObj3, checkBoxObj4, checkBoxObj5;
	 private List<CheckBox> checkBoxes;
	 private int numberCheckboxes;
	 private List<String> checkBoxesList;
	 
	 private TextView _profileName, _gender, _age, _country;
	 private Global _globals;
	 
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.pickpages_layout);
	        numberCheckboxes = 5;
	        addListenerOnChkWindows();
	        
	        for(int i = 0; i < 5; i++)
	        {
	        	CheckBox checkBoxObj = new CheckBox(this);
	        	//checkBoxes.add(checkBoxObj);
	        }
	        
	        checkBoxesList = new ArrayList<String>();
	        
	        choose = (Button)findViewById(R.id.btnClick2);
	        
	        _profileName = (TextView)findViewById(R.id.textView4);
	        _gender = (TextView)findViewById(R.id.textView5);
	        _age = (TextView)findViewById(R.id.textView7);
	        _country = (TextView)findViewById(R.id.textView8);
	        
	        _globals = ((Global)getApplicationContext());
	        SetUserProfile();
	 }
	 
	 private void SetUserProfile()
	 {
		 _profileName.setText(_globals.GetProfile().Name);
		 if(_globals.GetProfile().Gender)
			 _gender.setText("Male");
		 else
			 _gender.setText("Female");
		 
		 _age.setText(Long.toString(_globals.GetProfile().DOB));
		 _country.setText(_globals.GetProfile().Country);
	 }
	 
	 public void addListenerOnChkWindows() {
		 	
		 for(int i = 0; i < numberCheckboxes; i++)
		 {
			// CheckBox checkBoxObj;
			 if(i == 0)
			 {
				 checkBoxObj1 = (CheckBox) findViewById(R.id.checkBox1);
			 }
			 else if(i == 1)
			 {
				 checkBoxObj2 = (CheckBox) findViewById(R.id.checkBox2);
			 }
			 else if(i == 2)
			 {
				 checkBoxObj3 = (CheckBox) findViewById(R.id.checkBox3);
			 }
			 else if(i == 3)
			 {
				 checkBoxObj4 = (CheckBox) findViewById(R.id.checkBox4);
			 }
		 }
			 checkBoxObj1.setOnClickListener(new View.OnClickListener() {
				 
				  public void onClick(View v) {

					if (((CheckBox) v).isChecked()) {
						//Toast.makeText(PickPages.this,"Bro, try Linux :)", Toast.LENGTH_LONG).show();
						checkBoxesList.add("Immunization");
						//System.out.println("CheckBoxes List Count: " + checkBoxesList.size());
					}//

				  }
				});
			 
			checkBoxObj2.setOnClickListener(new View.OnClickListener() {
				 
				  public void onClick(View v) {

					if (((CheckBox) v).isChecked()) {
						//Toast.makeText(PickPages.this,"Bro, try Linux :)", Toast.LENGTH_LONG).show();
						checkBoxesList.add("Disease");
						//System.out.println("CheckBoxes List Count: " + checkBoxesList.size());
					}//

				  }
				});
			 
			 checkBoxObj3.setOnClickListener(new View.OnClickListener() {
				 
				  public void onClick(View v) {

					if (((CheckBox) v).isChecked()) {
						//Toast.makeText(PickPages.this,"Bro, try Linux :)", Toast.LENGTH_LONG).show();
						checkBoxesList.add("Medical");
						//System.out.println("CheckBoxes List Count: " + checkBoxesList.size());
					}//

				  }
				});
			 
			 checkBoxObj4.setOnClickListener(new View.OnClickListener() {
				 
				  public void onClick(View v) {

					if (((CheckBox) v).isChecked()) {
						//Toast.makeText(PickPages.this,"Bro, try Linux :)", Toast.LENGTH_LONG).show();
						checkBoxesList.add("Other");
						//System.out.println("CheckBoxes List Count: " + checkBoxesList.size());
					}//

				  }
				});
			 

			//windows = (CheckBox) findViewById(R.id.checkBox1);

			

		  }
	 
	 public void choose(View view){
		 System.out.println("CheckBoxes List Count: " + checkBoxesList.size());
		 Intent i = new Intent(PickPages.this, SlidePages.class);
		 i.putStringArrayListExtra("checkBoxesList", (ArrayList<String>) checkBoxesList);
	        startActivity(i);
	  }
}
