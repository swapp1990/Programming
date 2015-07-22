package com.swapp.findsenetor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchPage extends Activity {
	 private RadioGroup radioGroup_1, radioGroup_2;
	 private EditText searchText;
	 private int radioSearchTerm = 0;
	 private boolean isSenate;
	 private Spinner spinner1;
	 public String CityText;
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.search_page_l);
	     
	        
	     spinner1 = (Spinner) findViewById(R.id.spinner1);
	     addListenerOnSpinnerItemSelection();
	     spinner1.setVisibility(View.GONE);
	        
	     searchText = (EditText) findViewById(R.id.searchText);
	     searchText.setInputType(InputType.TYPE_CLASS_NUMBER);
	     isSenate = true;
	     radioGroup_1 = (RadioGroup) findViewById(R.id.radioGroup1);
	     radioGroup_1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    	 public void onCheckedChanged(RadioGroup group, int checkedId) {
	    		 if(checkedId == R.id.radio0) {
	    			 isSenate = true;
	    		 }
	    		 else {
	    			 isSenate = false;
	    		 }
	    	 }
	     });
	     
	     radioGroup_2 = (RadioGroup) findViewById(R.id.radioGroup2);
	     radioGroup_2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    	 public void onCheckedChanged(RadioGroup group, int checkedId) {
	    		 if(checkedId == R.id.radioZip) {
	    			 searchText.setInputType(InputType.TYPE_CLASS_NUMBER);
	    			 radioSearchTerm = 0;
	    			 spinner1.setVisibility(View.GONE);
	    			 searchText.setVisibility(View.VISIBLE);
	    		 }
	    		 else if(checkedId == R.id.radioName) {
	    			 searchText.setInputType(InputType.TYPE_CLASS_TEXT);
	    			 radioSearchTerm = 1;
	    			 spinner1.setVisibility(View.GONE);
	    			 searchText.setVisibility(View.VISIBLE);
	    		 }
	    		 else
	    		 {
	    			 spinner1.setVisibility(View.VISIBLE);
	    			 searchText.setVisibility(View.GONE);
	    			 //searchText.setInputType(InputType.TYPE_CLASS_TEXT);
	    			 radioSearchTerm = 2;
	    		 }
	    	 }
	     });

	 }
	 
	 public void addListenerOnSpinnerItemSelection() {
			spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		  }
	 
	 public void DisplayList(View view) {
		 System.out.println(radioSearchTerm);
		 if(!searchText.getText().toString().trim().equals("") || radioSearchTerm == 2)
		 {
			Intent intent = new Intent(this, List_Profiles.class);
			if(isSenate)
				intent.putExtra("isSenate", "Yes");
			else
				intent.putExtra("isSenate", "No");
			if(radioSearchTerm == 0)
			{
				 if(searchText.getText().toString().trim().length() < 5)
				 {
					 Toast.makeText(getBaseContext(), "Zip Code should be 5 digits", 1).show();
					 return;
				 }
				 intent.putExtra("searchTerm", "Zip");
			}
			else if(radioSearchTerm == 1)
			{
				intent.putExtra("searchTerm", "Name");
			}
			else
			{
				intent.putExtra("searchTerm", "City");
			}

	    	String message = searchText.getText().toString();
	    	if(radioSearchTerm == 2)
	    	{
	    		message = CityText;
	    	}
	        intent.putExtra("message", message);
	        //System.out.println("Zip -------- " + message);
	    	startActivity(intent);
		 }
		 else
		 {
			Toast.makeText(getBaseContext(), "Field is blank", 1).show();
		 }
		 /*EditText txtV = (EditText) findViewById(R.id.editText1);
		 EditText txtN = (EditText) findViewById(R.id.name_edit);
		 if(!txtV.getText().toString().trim().equals(""))
		 {
			Intent intent = new Intent(this, List_Profiles.class);
	    	String message = txtV.getText().toString();
	        intent.putExtra("zipcode", message);
	        System.out.println("Zip -------- " + message);
	    	startActivity(intent);
		 }
		 else if(!txtN.getText().toString().trim().equals(""))
		 {
			Intent intent = new Intent(this, List_Profiles.class);
	    	String message = txtN.getText().toString();
	    	System.out.println("Name -------- " + message);
	        intent.putExtra("name", message);
	    	startActivity(intent);
		 }
		 else
		 {
			// Toast.makeText(getBaseContext(), "Please enter a value!", 1).show();
		 }*/

	 }
	 
	 public class CustomOnItemSelectedListener implements OnItemSelectedListener {
		 
		  public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			/*Toast.makeText(parent.getContext(), 
				"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
				Toast.LENGTH_SHORT).show();*/
			  CityText = parent.getItemAtPosition(pos).toString();
			  ((TextView) parent.getChildAt(0)).setTextSize(25);
			  ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
		  }
		 
		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		  }
		 
		}
	 
}
