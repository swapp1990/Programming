package com.ehrapp.pageviewer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.CheckBox;

import com.ehrapp.Global;
import com.ehrapp.R;
import com.ehrapp.dataservice.DatabaseManager;
import com.ehrapp.dataservice.ProfileService;
import com.ehrapp.dialog.DatePickerFragment;
import android.app.DatePickerDialog.OnDateSetListener;
import com.ehrapp.EHRLables;

public class Page_CreateProfile extends FragmentActivity implements OnDateSetListener{
		  
	 private EditText _firstNameField=null;
	 private EditText _lastNameField=null;
	 private TextView _DOBView=null;
	 private RadioButton _buttonMale=null;
	 private RadioButton _buttonFemale=null;

	 private EditText _countryField=null;
	 private CheckBox _cloudCheckBox=null;
	 private Spinner _mySpinner = null;
	 private ArrayAdapter<CharSequence> _adapter = null;
	 private Calendar _dob = null;
	 
	 static final int DATE_DIALOG_ID = 999;
	 private int _year = 0;
	 private int _month = 0;
	 private int _day = 0;
	 private String _race = null;
	 private boolean _isCloud = false;
	 
	 ///// Image Profile
	 private ImageButton _profilePic;
	 private static final int REQUEST_CODE = 1;
	 private Bitmap bitmap;
	 public Uri currImageURI;
	 
	 private Global _globals;
	 private DatabaseManager _db;
	 
	 private TextView _profile_info, _DoB, _gender, race, _identified;
	 
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.createprofile_layout);

	        _globals = ((Global)getApplicationContext());
	        _db = _globals.GetDatabase();
	        
	        InitLayouts();
	        InitFonts();
	        SetupListener();
	        
	        CreateProfileTable();
	        //Creating all the health record tables
	        _globals.CreateAllOtherTables();
	        
	        LoadCachedProfile();
	 }
	 
	 private void InitLayouts()
	 {            
	        _profilePic			= (ImageButton)findViewById(R.id.profilePic);
	        _firstNameField 	= (EditText)findViewById(R.id.FirstName);
	        _lastNameField 		= (EditText)findViewById(R.id.LastName);
	        _DOBView 			= (TextView)findViewById(R.id.showDOB);
	        _countryField 		= (EditText)findViewById(R.id.editCountry);
	        _buttonMale			= (RadioButton)findViewById(R.id.radioButton1);
	        _buttonFemale		= (RadioButton)findViewById(R.id.radioButton2);
	        _cloudCheckBox		= (CheckBox)findViewById(R.id.cbx_Cloud);
	        _mySpinner 			= (Spinner)findViewById(R.id.spinner1);
	        _dob 				= Calendar.getInstance();
	        _adapter 			= ArrayAdapter.createFromResource(this, R.array.races_array, android.R.layout.simple_spinner_item);
	        
	        _mySpinner.setAdapter(_adapter);
	        _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 }
	 
	 private void SetupListener()
	 {
	        _profilePic.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            		OpenGallery();
	            }
	          });
	        
	        _cloudCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() 
	        {
	        	 @Override
	        	   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
	        		 if (isChecked){
	                     // perform logic
	        			 System.out.println("IsChecked");
	        			 _isCloud = true;
	                 }
	        	   }
	        });
	        
	        _DOBView.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	showDateDialog();
	            }
	          });
	        
		 _mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			    	_race = (String)parentView.getSelectedItem();
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			        // your code here
			    }

			});
	 }
	 
	 private void InitFonts()
	 {
	        Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
	        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
	        _profile_info = (TextView)findViewById(R.id.futurePossibleFeature);
	        _DoB = (TextView)findViewById(R.id.originalVersion);
	        _gender = (TextView)findViewById(R.id.gender);
	        race = (TextView)findViewById(R.id.profileName);
	        _identified = (TextView)findViewById(R.id.version);
	        _profile_info.setTypeface(titleFont);
	        _firstNameField.setTypeface(textFont);
	        _lastNameField.setTypeface(textFont);
	        _DOBView.setTypeface(textFont);
	        _DoB.setTypeface(textFont);
	        _gender.setTypeface(textFont);
	        race.setTypeface(textFont);
	        _countryField.setTypeface(textFont);
	        _identified.setTypeface(textFont);
	 }
	 
	 private void CreateProfileTable()
	 {
	        Map<String, String> columns = new HashMap<String,String>();
	        columns.put("firstname", "text");
	        columns.put("lastname", "text");
	        columns.put("date_of_birth", "long");
	        columns.put("country", "text");
	        columns.put("race", "text");
	        columns.put("gender", "int");
	        columns.put("profilePic_path", "text");
	        columns.put("is_cloud", "integer");	        
	        if(!_db.isTableExists("profile_table"))
			{
				System.out.println("Table does not exists!");
				_db.createTablewithForeignKey("profile_table",columns,"user_accounts");
			}
	 }
	 
	 private void LoadCachedProfile()
	 {
		 if(_globals.GetProfile().FirstName.compareTo("What") != 0)
		 {
			 _firstNameField.setText(_globals.GetProfile().FirstName);
		 }
		 
		 if(_globals.GetProfile().LastName.compareTo("ever") != 0)
		 {
			 _lastNameField.setText(_globals.GetProfile().LastName);
		 }
		 
		 if(_globals.GetProfile().DOB != 0)
		 {
			 _dob.clear();
			_dob.setTimeInMillis(_globals.GetProfile().DOB);
			_year = _dob.get(Calendar.YEAR);
			_month = _dob.get(Calendar.MONTH);
			_day = _dob.get(Calendar.DATE);
			
			_DOBView.setText(new StringBuilder().append(_month + 1)
						   .append("-").append(_day).append("-").append(_year)
						   .append(" "));
		 }
		 
		 if( _globals.GetProfile().Gender)
		 {
			 _buttonMale.setChecked(true);
			 _buttonFemale.setChecked(false);
		 }
		 else
		 {
			 _buttonMale.setChecked(false);
			 _buttonFemale.setChecked(true);
		 }
		 
		 if(_globals.GetProfile().Country.compareTo("Silvermoon") != 0)
		 {
			 _countryField.setText(_globals.GetProfile().Country);
		 }
		 
		 if(_globals.GetProfile().Race.compareTo("Mage") != 0)
		 {
			 if(_globals.GetProfile().Race.compareTo("African American") == 0)
			 {
				 _mySpinner.setSelection(1);
			 }
			 else if(_globals.GetProfile().Race.compareTo("Asian") == 0)
			 {
				 _mySpinner.setSelection(2);
			 }			 
			 else if(_globals.GetProfile().Race.compareTo("Caucasian") == 0)
			 {
				 _mySpinner.setSelection(3);
			 }			 
			 else if(_globals.GetProfile().Race.compareTo("Latin or Hispanic") == 0)
			 {
				 _mySpinner.setSelection(4);
			 }			 
			 else if(_globals.GetProfile().Race.compareTo("Native American") == 0)
			 {
				 _mySpinner.setSelection(5);
			 }			 
			 else if(_globals.GetProfile().Race.compareTo("Middle Eastern") == 0)
			 {
				 _mySpinner.setSelection(6);
			 }			 
			 _race = _globals.GetProfile().Race;
		 }
		 
		 if(_globals.GetAccount().isPrivate)
		 {
			 _cloudCheckBox.setChecked(true);
		 }
		 
		 
		 try{
			 if(_globals.GetProfile().profilePic_path!=null && !_globals.GetProfile().profilePic_path.isEmpty())
			 {
				 Uri what = Uri.parse(_globals.GetProfile().profilePic_path);
		         InputStream stream = getContentResolver().openInputStream(
		        		 what);
		         bitmap = BitmapFactory.decodeStream(stream);
		         stream.close();
		         int width = bitmap.getWidth();
		         int height = bitmap.getHeight();
		         float xScale = ((float) 250) / width;
		         float yScale = ((float) 250) / height;
		         
		         float scale = (xScale <= yScale) ? xScale : yScale;
		         // Create a matrix for the scaling and add the scaling data
		         Matrix matrix = new Matrix();
		         matrix.postScale(scale, scale);
		         // Create a new bitmap and convert it to a format understood by the ImageView
		         Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		         BitmapDrawable result = new BitmapDrawable(scaledBitmap);
		         width = scaledBitmap.getWidth();
		         height = scaledBitmap.getHeight();

		         _profilePic.setImageDrawable(result);
		          scaledBitmap = null;
		         result = null;
		         bitmap = null;
			 }
		 }
         catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
		 
	 }

	 public void onCreateClicked(View view)
	 {
		 //if they clicked create, cache the profile and store it in the database,
		 //FOR the profile ID, do the increment by yourself.
		 CacheProfile();
		 
		 //if the private is checked, upload it to the cloud, get the profileID, and override all the profileIDs in all tables for that user.
		 if(_globals.GetAccount().cloudID >= 0) //this means the user is signed onto the cloud
		 {
			 StoreProfile_Server(_cloudCheckBox.isChecked());
		 }
		 else
		 {
			 //store it in the local database 
			 StoreProfile_Local();
		 }

		 Toast.makeText(getApplicationContext(), "Saving Profile...", Toast.LENGTH_SHORT).show();
		 this.finish();
	     Intent i = new Intent(Page_CreateProfile.this, Page_Patient_History.class);
	     startActivity(i);
	 }
	  
	 private void StoreProfile_Server(boolean i_isPrivate) 
	 {
		 _globals.GetAccount().isPrivate = i_isPrivate;
		 new ProfileService(this, EHRLables.ProfileServiceAction.CreateOrUpdateProfile,
				 i_isPrivate, _globals).execute();
	 }
				
	 public void ServerResponse_CreateOrUpdateProfile(JSONObject i_response)
	 {
		  try
		  {
			  //System.out.println("Recevice Json: " + (CharSequence)i_response.getString("Message"));
			  String result = i_response.getString("Result");
			  if(result.compareTo("Success") == 0)
			  {
				 JSONObject errMessage = (JSONObject)i_response.get("Message");
				 //store this userid into the local account table;
				 Toast.makeText(getApplicationContext(), "Success: Content: " + (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
					 
				 StoreProfile_Local();
						
				 this.finish();
				 Intent i = new Intent(Page_CreateProfile.this, Page_Patient_History.class);
				 startActivity(i);
						
			  }
			  else if(result.compareTo("Fail") == 0)
			  {
				  JSONObject errMessage = (JSONObject)i_response.get("Message");
				  Toast.makeText(getApplicationContext(), "Fail: ErrorCode: " + (Integer)(errMessage.get("ErrorCode")) + "Content: "+ (CharSequence)(errMessage.get("Content")), Toast.LENGTH_SHORT).show();
			  }
		   }
		   catch(JSONException e)
		   {
				 
		   }
	  }
	 
	 private void StoreProfile_Local()
	 {
		 StoreProfile_Local(-1);
	 }
	 
	 private void StoreProfile_Local(int i_profileID)
	 {		 
		 //now insert the whole profile into the profiles table into database
		 String Sfirstname = _firstNameField.getText().toString();
		 String Slastname = _lastNameField.getText().toString();
		 long Sage = _dob.getTimeInMillis();
		 String Scountry = _countryField.getText().toString();
		 int Igender = _globals.GetProfile().Gender == true? 1: 0;
		 String Srace = _race;
		 String Sprofile_id = String.valueOf(_globals.GetProfile().ProfileID);
		 int BisCloud = (_isCloud)? 1 : 0;
	 
		 _globals.GetProfile().Race	= _race;
		 //System.out.println("ProfileID i'M STORing: "+_globals.GetProfile().ProfileID);
		 Map<String, Object> columnsValues = new HashMap<String,Object>();
		 columnsValues.put("firstname", Sfirstname);
		 columnsValues.put("lastname", Slastname);
		 columnsValues.put("date_of_birth", Sage);
		 columnsValues.put("country", Scountry);
		 columnsValues.put("gender", Igender);
		 columnsValues.put("race", Srace);
		 columnsValues.put("profileID_fk",Sprofile_id);
		 columnsValues.put("is_cloud",BisCloud);
		 if(currImageURI != null)
		 {
			 columnsValues.put("profilePic_path",currImageURI.toString());
		 }
		 _db.addorUpdateRow(columnsValues,"profile_table","profileID_fk");
		 //_db.printAllValues("profile_table");
	 }
		 
	 private void CacheProfile()
	 {
		 _globals.GetProfile().FirstName 	= _firstNameField.getText().toString();
		 _globals.GetProfile().LastName 	= _lastNameField.getText().toString();
		 _globals.GetProfile().DOB 		=  _dob.getTimeInMillis();
		 _globals.GetProfile().Country 	= _countryField.getText().toString();
		 _globals.GetProfile().Gender 	= _buttonMale.isChecked(); //true-male, false- female
		 _globals.GetProfile().Race		= _race;
	 }
	 
	 private void showDateDialog(){
		 FragmentManager fm = getSupportFragmentManager();
		 DatePickerFragment newFragment = new DatePickerFragment();
		 newFragment.show(fm, "date_picker");
	 }
	    
	// when dialog box is closed, below method will be called.
	 @Override
	 public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
		_year = selectedYear;
			
		_month = selectedMonth;
		_day = selectedDay;
		_dob.clear();
		_dob.set(_year, _month, _day, 0, 0, 0);

		// set selected date into textview
		_DOBView.setText(new StringBuilder().append(_month + 1)
				.append("-").append(_day).append("-").append(_year)
				.append(" "));
	 }	    
	    
	 public void OpenGallery()
	 {
		 Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		 startActivityForResult(intent, REQUEST_CODE);
	 }
	    
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
	            try {
	                // We need to recyle unused bitmaps
	            	currImageURI = data.getData();
	            	_globals.GetProfile().profilePic_path = currImageURI.toString();
	                if (bitmap != null) {
	                    bitmap.recycle();
	                }
	                InputStream stream = getContentResolver().openInputStream(
	                		currImageURI);
	                //optimizr image
	                BitmapFactory.Options options = new BitmapFactory.Options();
	                options.inPreferredConfig = Config.RGB_565;
	                options.inSampleSize = 2;
	                //
	                bitmap = BitmapFactory.decodeStream(stream,null, options);
	                stream.close();
	                int width = bitmap.getWidth();
	                int height = bitmap.getHeight();
	                float xScale = ((float) 250) / width;
	                float yScale = ((float) 250) / height;
	                float scale = (xScale <= yScale) ? xScale : yScale;
	                // Create a matrix for the scaling and add the scaling data
	                Matrix matrix = new Matrix();
	                matrix.postScale(scale, scale);
	                // Create a new bitmap and convert it to a format understood by the ImageView
	                Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	                BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	                width = scaledBitmap.getWidth();
	                height = scaledBitmap.getHeight();

	                _profilePic.setImageDrawable(result);
	                bitmap = null;
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	     }
		 super.onActivityResult(requestCode, resultCode, data);
	 }
	    
	 @Override
	 public void onBackPressed() {
		 super.onBackPressed();
		 this.finish();
	 }

}
