package com.ehrapp.dialog;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ehrapp.R;

public class CreateImmunizationFragment extends DialogFragment implements OnDateSetListener{

    public interface CreateImmunizationListener {
        public void onImmunizationPositiveClick(String i_vaccine, String i_type, String i_dose, int i_age, Long i_date, String i_lotNumber, int i_index);
        public void onImmunizationDeleteClick(int i_index);
    }
    
    // Use this instance of the interface to deliver action events
    CreateImmunizationListener _listener;
    
    private Calendar _date = null;
	private int _year = 0;
	private int _month = 0;
	private int _day = 0;
	private int _index = 0;
	
	private EditText _vaccine_EditText = null;
	private EditText _type_EditText = null;
	private EditText _dose_EditText = null;
	private EditText _age_EditText = null;
	private EditText _date_EditText = null;
	private EditText _lotNumber_EditText = null;
	
    private ImageButton _create_Btn = null;
    private ImageButton _delete_Btn = null;
    
    private TextView _immunization_header = null;
    private TextView _vaccines_header = null;
    
    public CreateImmunizationFragment() {
    	
    }
       
    @Override
    public void onAttach(Activity activity) 
    {
        super.onAttach(activity);
        try 
        {
        	_listener = (CreateImmunizationListener) activity;
        }
        catch (ClassCastException e) 
        {
            throw new ClassCastException(activity.toString() + " must implement CreateImmunizationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
    	  super.onCreateDialog(savedInstanceState);
    	  _index = getArguments().getInt("index");
    	  final Dialog dialog = new Dialog(getActivity());  
    	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
    	  dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
    	    WindowManager.LayoutParams.FLAG_FULLSCREEN);  
    	  dialog.setContentView(R.layout.createimmunization_layout);  
    	  dialog.getWindow().setBackgroundDrawable(  
    	    new ColorDrawable(Color.TRANSPARENT));  
    	  dialog.show();
    	  _vaccine_EditText = (EditText) dialog.findViewById(R.id.iv_vaccine);  
    	  _type_EditText = (EditText) dialog.findViewById(R.id.iv_type);  
    	  _dose_EditText = (EditText) dialog.findViewById(R.id.iv_dose); 
    	  _age_EditText = (EditText) dialog.findViewById(R.id.iv_age); 
    	  _date_EditText = (EditText) dialog.findViewById(R.id.iv_date);
    	  _lotNumber_EditText = (EditText) dialog.findViewById(R.id.iv_lotnumber);  
    	  
    	  _date = Calendar.getInstance();
    	  
    	  _date_EditText.setInputType(InputType.TYPE_NULL);
    	  _date_EditText.setOnClickListener(new View.OnClickListener() {
    	      @Override
    	      public void onClick(View v) {
    	    	  showDateDialog();
    	      }
    	  });
    	  _date_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	      @Override
    	      public void onFocusChange(View v, boolean hasFocus) {
    	          if (hasFocus) {
    	        	  showDateDialog();
    	          }
    	      }
    	  });
    	  
  
    	  _create_Btn = (ImageButton) dialog.findViewById(R.id.iv_create); 
    	  _delete_Btn = (ImageButton) dialog.findViewById(R.id.iv_delete);
    	  
    	  _create_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   String age = _age_EditText.getText().toString();
		    		   String lotNumber = _lotNumber_EditText.getText().toString();
		    		   int iAge = 0;
		    		   if(!age.isEmpty())
		    		   {
		    			   iAge = Integer.parseInt(age);
		    		   }
		    		   
		    		   _listener.onImmunizationPositiveClick(
		    				   _vaccine_EditText.getText().toString(), 
		    				   _type_EditText.getText().toString(), 
		    				   _dose_EditText.getText().toString(), 
		    				   iAge,
		    				   _date.getTimeInMillis(),
		    				   lotNumber, 
		    				   _index
		    				);  
		    		   dismiss();  
		    	  }
    	   }); 
    	  
    	  _delete_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   _listener.onImmunizationDeleteClick(_index);  
		    		   dismiss();
		    	   }
    	   });   	  
    	  
    	  
    	  if(_index != -1)
    	  {
    		  String vaccine = getArguments().getString("Vaccine");
    		  String type = getArguments().getString("Type");
    		  String dose = getArguments().getString("Dose");
    		  Integer age = getArguments().getInt("Age");
    		  long date = getArguments().getLong("Date");
    		  String lotnumber = getArguments().getString("LotNumber");
        	  
        	  _vaccine_EditText.setText(vaccine);  
        	  _type_EditText.setText(type);
        	  _dose_EditText.setText(dose);
        	  if(age != null) _age_EditText.setText(age.toString());
        	  if(lotnumber != null) _lotNumber_EditText.setText(lotnumber);
        	  
        	  _date.clear();
        	  _date.setTimeInMillis(date);

        	  _date_EditText.setText((_date.get(Calendar.MONTH) + 1) + "-" +
        			  _date.get(Calendar.DATE) + "-" + 
        			  _date.get(Calendar.YEAR)); 	  
    	  }
    	  
    	  Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
          Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
          _immunization_header = (TextView)dialog.findViewById(R.id.createiv_title);
          _vaccines_header = (TextView)dialog.findViewById(R.id.version);
          _immunization_header.setTypeface(titleFont);
          _vaccines_header.setTypeface(titleFont);
          _vaccine_EditText.setTypeface(textFont);
          _type_EditText.setTypeface(textFont);
          _dose_EditText.setTypeface(textFont);
          _age_EditText.setTypeface(textFont);
          _date_EditText.setTypeface(textFont);
          _lotNumber_EditText.setTypeface(textFont);
    	  
    	  return dialog;      	
    }
    
    private void showDateDialog() 
    {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(fm, "date_picker");
    }
    
    public void SetDateData(int i_month, int i_day, int i_year)
    {
    	_date.clear();
    	_date.set(i_year, i_month, i_day, 0, 0, 0);
    	_date_EditText.setText(new StringBuilder().append(i_month + 1)
					   .append("-").append(i_day).append("-").append(i_year)
					   .append(" "));
    }
    
	// when dialog box is closed, below method will be called.
    @Override
	public void onDateSet(DatePicker view, int selectedYear,
			int selectedMonth, int selectedDay) {
		_year = selectedYear;
		_month = selectedMonth;
		_day = selectedDay;

		_date.clear();
		_date.set(_year, _month, _day, 0, 0, 0);
		_date_EditText.setText(new StringBuilder().append(_month + 1)
					   .append("-").append(_day).append("-").append(_year)
					   .append(" "));

    }
}

