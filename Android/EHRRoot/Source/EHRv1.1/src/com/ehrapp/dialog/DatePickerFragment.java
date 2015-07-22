package com.ehrapp.dialog;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
//import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class DatePickerFragment extends DialogFragment {
	
    private OnDateSetListener _listener;
    
    public DatePickerFragment() {
    	
    }
       
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try 
        {
        	_listener = (OnDateSetListener) activity;
        } 
        catch (ClassCastException e) 
        {
           throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
       
        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), _listener, year,month,day);
    }
    
    
}
