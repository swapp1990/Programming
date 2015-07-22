package com.ehrapp.dialog;

import java.util.Calendar;
import java.util.Locale;

import com.ehrapp.R;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class CreateMedicationFragment extends DialogFragment implements OnDateSetListener{

    public interface CreateMedicationListener {
        public void onDialogPositiveClick(String i_drug, String i_dosage, String i_freq, long i_startDate, long i_endDate, int i_index);
        public void onDialogDeleteClick(int i_index);
    }
    
    // Use this instance of the interface to deliver action events
    CreateMedicationListener _listener;
    
    private Calendar _startDate = null;
    private Calendar _endDate = null;
	private int _year = 0;
	private int _month = 0;
	private int _day = 0;
	private int _index = 0;
    private EditText _drug_EditText = null;
    private EditText _dosage_EditText = null;
    private EditText _freq_EditText = null;
    private EditText _startDate_EditText = null;
    private EditText _endDate_EditText = null;
    private ImageButton _create_Btn = null;
    private ImageButton _delete_Btn = null;
    private boolean  _isStartDate = false;
    private TextView _medication_header = null;
    
    public CreateMedicationFragment() {
    	
    	
    }
       
    @Override
    public void onAttach(Activity activity) 
    {
        super.onAttach(activity);
        try 
        {
        	_listener = (CreateMedicationListener) activity;
        }
        catch (ClassCastException e) 
        {
            throw new ClassCastException(activity.toString() + " must implement CreateMedicationListener");
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
    	  dialog.setContentView(R.layout.createmedication_layout);  
    	  dialog.getWindow().setBackgroundDrawable(  
    	    new ColorDrawable(Color.TRANSPARENT));  
    	  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
    	  dialog.show();
    	  _drug_EditText = (EditText) dialog.findViewById(R.id.med_drug);  
    	  _dosage_EditText = (EditText) dialog.findViewById(R.id.med_dosage);  
    	  _freq_EditText = (EditText) dialog.findViewById(R.id.med_freq);  
    	  _startDate_EditText = (EditText) dialog.findViewById(R.id.med_startdate);  
    	  _endDate_EditText = (EditText) dialog.findViewById(R.id.med_enddate);
    	  
    	  _startDate = Calendar.getInstance();
    	  _endDate = Calendar.getInstance();
    	  
    	  _startDate_EditText.setInputType(InputType.TYPE_NULL);
    	  _startDate_EditText.setOnClickListener(new View.OnClickListener() {
    	      @Override
    	      public void onClick(View v) {
    	    	  _isStartDate = true;
    	    	  showDateDialog();
    	      }
    	  });
    	  _startDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	      @Override
    	      public void onFocusChange(View v, boolean hasFocus) {
    	          if (hasFocus) {
    	        	  _isStartDate = true;
    	        	  showDateDialog();
    	          }
    	      }
    	  });
    	  
    	  _endDate_EditText.setInputType(InputType.TYPE_NULL);
    	  _endDate_EditText.setOnClickListener(new View.OnClickListener() {
    	      @Override
    	      public void onClick(View v) {
    	    	  _isStartDate = false;
    	    	  showDateDialog();
    	      }
    	  });
    	  _endDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	      @Override
    	      public void onFocusChange(View v, boolean hasFocus) {
    	          if (hasFocus) {
    	        	  _isStartDate = false;
    	        	  showDateDialog();
    	          }
    	      }
    	  });
    	  
    	  _create_Btn = (ImageButton) dialog.findViewById(R.id.med_create); 
    	  _delete_Btn = (ImageButton) dialog.findViewById(R.id.DeleteMed);
    	  _create_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   _listener.onDialogPositiveClick(
		    				   _drug_EditText.getText().toString(), _dosage_EditText.getText().toString(), _freq_EditText.getText().toString(),
		    				   _startDate.getTimeInMillis(), _endDate.getTimeInMillis(), _index
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
		    		   _listener.onDialogDeleteClick(_index);  
		    		   dismiss();
		    	   }
    	   });   	  
    	  
    	  
    	  if(_index != -1)
    	  {
    		  String drug = getArguments().getString("Drug");
    		  String dosage = getArguments().getString("Dosage");
    		  String freq = getArguments().getString("Freq");
    		  long startDate = getArguments().getLong("StartDate");
    		  long endDate = getArguments().getLong("EndDate");
        	  _drug_EditText.setText(drug);  
        	  _dosage_EditText.setText(dosage);
        	  _freq_EditText.setText(freq);
        	  _startDate.clear();
        	  _startDate.setTimeInMillis(startDate);
        	  _endDate.clear();
        	  _endDate.setTimeInMillis(endDate);
  		    
  			  _startDate_EditText.setText((_startDate.get(Calendar.MONTH) + 1) + "-" +
  					_startDate.get(Calendar.DATE) + "-" + 
  					_startDate.get(Calendar.YEAR));
  			  _endDate_EditText.setText((_endDate.get(Calendar.MONTH) + 1) + "-" +
  					_endDate.get(Calendar.DATE) + "-" + 
  					_endDate.get(Calendar.YEAR));    	  
    	  }
    	  
    	  
    	  Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
          Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
          _medication_header = (TextView)dialog.findViewById(R.id.createmedication_title);
          _medication_header.setTypeface(titleFont);
          _drug_EditText.setTypeface(textFont);
          _dosage_EditText.setTypeface(textFont);
          _freq_EditText.setTypeface(textFont);
          _startDate_EditText.setTypeface(textFont);
          _endDate_EditText.setTypeface(textFont);
    	  
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
		if(_isStartDate)
		{
			_startDate.clear();
			_startDate.set(i_year, i_month, i_day, 0, 0, 0);
			_startDate_EditText.setText(new StringBuilder().append(i_month + 1)
					   .append("-").append(i_day).append("-").append(i_year)
					   .append(" "));
		}
		else
		{
			_endDate.clear();
			_endDate.set(i_year, i_month, i_day, 0, 0, 0);			
			_endDate_EditText.setText(new StringBuilder().append(i_month + 1)
					   .append("-").append(i_day).append("-").append(i_year)
					   .append(" "));
		}    	
    }
    
	// when dialog box is closed, below method will be called.
    @Override
	public void onDateSet(DatePicker view, int selectedYear,
			int selectedMonth, int selectedDay) {
		_year = selectedYear;
		_month = selectedMonth;
		_day = selectedDay;
		
		if(_isStartDate)
		{
			_startDate.clear();
			_startDate.set(_year, _month, _day, 0, 0, 0);
			_startDate_EditText.setText(new StringBuilder().append(_month + 1)
					   .append("-").append(_day).append("-").append(_year)
					   .append(" "));
		}
		else
		{
			_endDate.clear();
			_endDate.set(_year, _month, _day, 0, 0, 0);
			_endDate_EditText.setText(new StringBuilder().append(_month + 1)
					   .append("-").append(_day).append("-").append(_year)
					   .append(" "));
		}
    }
}
