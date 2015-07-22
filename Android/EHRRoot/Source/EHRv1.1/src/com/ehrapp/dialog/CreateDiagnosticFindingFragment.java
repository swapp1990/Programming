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

public class CreateDiagnosticFindingFragment extends DialogFragment implements OnDateSetListener{

    public interface CreateDiagnosticFindingListener {
        public void onDiagnosticFindingPositiveClick(String i_test, String i_result, long i_date, String i_interpretation, int i_index);
        public void onDiagnosticFindingDeleteClick(int i_index);
    }
    
    // Use this instance of the interface to deliver action events
    CreateDiagnosticFindingListener _listener;
    
    private Calendar _date = null;
	private int _year = 0;
	private int _month = 0;
	private int _day = 0;
	private int _index = 0;
	
	private EditText _test_EditText = null;
	private EditText _date_EditText = null;
	private EditText _result_EditText = null;
	private EditText _interpretation_EditText = null;
	
    private ImageButton _create_Btn = null;
    private ImageButton _delete_Btn = null;
    private TextView _diagnostic_findings_header = null;
    
    public CreateDiagnosticFindingFragment() {
    	
    }
       
    @Override
    public void onAttach(Activity activity) 
    {
        super.onAttach(activity);
        try 
        {
        	_listener = (CreateDiagnosticFindingListener) activity;
        }
        catch (ClassCastException e) 
        {
            throw new ClassCastException(activity.toString() + " must implement CreateDiagnosticFindingListener");
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
    	  dialog.setContentView(R.layout.creatediagnosticfinding_layout);  
    	  dialog.getWindow().setBackgroundDrawable(  
    	    new ColorDrawable(Color.TRANSPARENT));  
    	  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
    	  dialog.show();
    	  _test_EditText = (EditText) dialog.findViewById(R.id.df_test);  
    	  _result_EditText = (EditText) dialog.findViewById(R.id.df_result);  
    	  _date_EditText = (EditText) dialog.findViewById(R.id.df_date);  
    	  _interpretation_EditText = (EditText) dialog.findViewById(R.id.df_interpretation);  
    	  
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
    	  
  
    	  _create_Btn = (ImageButton) dialog.findViewById(R.id.df_create); 
    	  _delete_Btn = (ImageButton) dialog.findViewById(R.id.df_delete);
    	  
    	  _create_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   _listener.onDiagnosticFindingPositiveClick(
		    				   _test_EditText.getText().toString(), 
		    				   _result_EditText.getText().toString(), 
		    				   _date.getTimeInMillis(),
		    				   _interpretation_EditText.getText().toString(), 
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
		    		   _listener.onDiagnosticFindingDeleteClick(_index);  
		    		   dismiss();
		    	   }
    	   });   	  
    	  
    	  
    	  if(_index != -1)
    	  {
    		  String test = getArguments().getString("Test");
    		  String result = getArguments().getString("Result");
    		  long date = getArguments().getLong("Date");
    		  String interpretation = getArguments().getString("Interpretation");
        	  
        	  _test_EditText.setText(test);  
        	  _result_EditText.setText(result);
        	  _interpretation_EditText.setText(interpretation);
        	  
        	  _date.clear();
        	  _date.setTimeInMillis(date);

        	  _date_EditText.setText((_date.get(Calendar.MONTH) + 1) + "-" +
        			  _date.get(Calendar.DATE) + "-" + 
        			  _date.get(Calendar.YEAR)); 	  
    	  }
    	  
    	  Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
          Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
          _diagnostic_findings_header = (TextView)dialog.findViewById(R.id.createdf_title);
          _diagnostic_findings_header.setTypeface(titleFont);
          _test_EditText.setTypeface(textFont);
          _result_EditText.setTypeface(textFont);
          _date_EditText.setTypeface(textFont);
          _interpretation_EditText.setTypeface(textFont);
    	  
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
