package com.ehrapp.dialog;

import java.util.Calendar;

import com.ehrapp.R;
import com.ehrapp.dialog.CreateMedicationFragment.CreateMedicationListener;

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
import android.widget.Toast;

public class CreateAllergyFragment extends DialogFragment implements OnDateSetListener{
	
    public interface CreateAllergyListener {
        public void onAllergyPositiveClick(String i_type, String i_reaction, String i_severity, long i_lastDate, String i_treatment, int i_index);
        public void onAllergyDeleteClick(int i_index);
    }
    
    // Use this instance of the interface to deliver action events
    CreateAllergyListener _listener;
    
    private Calendar _lastDate = null;
	private int _year = 0;
	private int _month = 0;
	private int _day = 0;
	private int _index = 0;
    private EditText _type_EditText = null;
    private EditText _reaction_EditText = null;
    private EditText _severity_EditText = null;
    private EditText _lastDate_EditText = null;
    private EditText _treatment_EditText = null;
    private ImageButton _create_Btn = null;
    private ImageButton _delete_Btn = null;
    private TextView _create_allergy_header = null;

    public CreateAllergyFragment() {
    	
    	
    }
       
    @Override
    public void onAttach(Activity activity) 
    {
        super.onAttach(activity);
        try 
        {
        	_listener = (CreateAllergyListener) activity;
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
    	  dialog.setContentView(R.layout.createallergy_layout);  
    	  dialog.getWindow().setBackgroundDrawable(  
    	    new ColorDrawable(Color.TRANSPARENT));  
    	  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // for scrolling when soft keyboard is up
    	  dialog.show();
    	  _type_EditText = (EditText) dialog.findViewById(R.id.alg_type);  
    	  _reaction_EditText = (EditText) dialog.findViewById(R.id.alg_reaction);  
    	  _severity_EditText = (EditText) dialog.findViewById(R.id.alg_severity);  
    	  _lastDate_EditText = (EditText) dialog.findViewById(R.id.alg_lastoccurred);  
    	  _treatment_EditText = (EditText) dialog.findViewById(R.id.alg_treatment);
    	  
    	  _lastDate = Calendar.getInstance();
    	  
    	  _lastDate_EditText.setInputType(InputType.TYPE_NULL);
    	  _lastDate_EditText.setOnClickListener(new View.OnClickListener() {
    	      @Override
    	      public void onClick(View v) {
    	    	  showDateDialog();
    	      }
    	  });
    	  _lastDate_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	      @Override
    	      public void onFocusChange(View v, boolean hasFocus) {
    	          if (hasFocus) {
    	        	  showDateDialog();
    	          }
    	      }
    	  });
    	  
 	  
    	  _create_Btn = (ImageButton) dialog.findViewById(R.id.alg_create); 
    	  _delete_Btn = (ImageButton) dialog.findViewById(R.id.alg_delete);
    	  _create_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   if(!_type_EditText.getText().toString().isEmpty())
		    		   {
			    		   _listener.onAllergyPositiveClick(
			    				   _type_EditText.getText().toString(), _reaction_EditText.getText().toString(), _severity_EditText.getText().toString(),
			    				   _lastDate.getTimeInMillis(), _treatment_EditText.getText().toString(), _index
			    				);  
			    		   dismiss();  
		    		   }
		    		   else
		    		   {
		    			   Toast.makeText(getActivity(), "Allergy Type Cannot Be Empty " , Toast.LENGTH_SHORT).show();
		    		   }
		    	  }
    	   }); 
    	  
    	  _delete_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   _listener.onAllergyDeleteClick(_index);
		    		   dismiss();
		    	   }
    	   });   	  
    	  
    	  
    	  if(_index != -1)
    	  {
    		  String type = getArguments().getString("Type");
    		  String reaction = getArguments().getString("Reaction");
    		  String severity = getArguments().getString("Severity");
    		  long lastDate = getArguments().getLong("LastDate");
    		  String treatment = getArguments().getString("Treatment");
    		  
        	  _type_EditText.setText(type);  
        	  _reaction_EditText.setText(reaction);
        	  _severity_EditText.setText(severity);
        	  _lastDate.clear();
        	  _lastDate.setTimeInMillis(lastDate);

  		    
        	  _lastDate_EditText.setText((_lastDate.get(Calendar.MONTH) + 1) + "-" +
        			  _lastDate.get(Calendar.DATE) + "-" + 
        			  _lastDate.get(Calendar.YEAR));
        	  _treatment_EditText.setText(treatment);
    	  }
    	  
    	  Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
          Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
          _create_allergy_header = (TextView)dialog.findViewById(R.id.createalg_title);
          _create_allergy_header.setTypeface(titleFont);
          _type_EditText.setTypeface(textFont);
          _reaction_EditText.setTypeface(textFont);
          _severity_EditText.setTypeface(textFont);
          _lastDate_EditText.setTypeface(textFont);
          _treatment_EditText.setTypeface(textFont);
          
    	  
    	  
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
    	_lastDate.clear();
    	_lastDate.set(i_year, i_month, i_day, 0, 0, 0);
		_lastDate_EditText.setText(new StringBuilder().append(i_month + 1)
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
		
    	_lastDate.clear();
    	_lastDate.set(_year, _month, _day, 0, 0, 0);
		_lastDate_EditText.setText(new StringBuilder().append(_month + 1)
					   .append("-").append(_day).append("-").append(_year)
					   .append(" "));
    }
}
