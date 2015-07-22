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


public class CreateChiefComplaintFragment extends DialogFragment{
	
    public interface CreateChiefComplaintListener {
        public void onChiefComplaintPositiveClick(String i_sympton, String i_onsetOfSympton, String i_duration, String i_OTCTreatment, int i_index);
        public void onChiefComplaintDeleteClick(int i_index);
    }
    
    // Use this instance of the interface to deliver action events
    CreateChiefComplaintListener _listener;
    
	private int _index = 0;
    private EditText _sympton_EditText = null;
    private EditText _onsetOfSympton_EditText = null;
    private EditText _duration_EditText = null;
    private EditText _otctreatment_EditText = null;
    private ImageButton _create_Btn = null;
    private ImageButton _delete_Btn = null;
    private TextView _chief_complaint_header = null;
    
    
    
    public CreateChiefComplaintFragment() {
    	
    	
    }
       
    @Override
    public void onAttach(Activity activity) 
    {
        super.onAttach(activity);
        try 
        {
        	_listener = (CreateChiefComplaintListener) activity;
        }
        catch (ClassCastException e) 
        {
            throw new ClassCastException(activity.toString() + " must implement CreateChiefComplaintListener");
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
    	  dialog.setContentView(R.layout.createchiefcomplaint_layout);  
    	  dialog.getWindow().setBackgroundDrawable(  
    	    new ColorDrawable(Color.TRANSPARENT));  
    	  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
    	  dialog.show();
    	  _sympton_EditText = (EditText) dialog.findViewById(R.id.cc_sympton);  
    	  _onsetOfSympton_EditText = (EditText) dialog.findViewById(R.id.cc_onset);  
    	  _duration_EditText = (EditText) dialog.findViewById(R.id.cc_duration);  
    	  _otctreatment_EditText = (EditText) dialog.findViewById(R.id.cc_otc);

    	  _create_Btn = (ImageButton) dialog.findViewById(R.id.cc_create); 
    	  _delete_Btn = (ImageButton) dialog.findViewById(R.id.cc_delete);
          
    	  _create_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   _listener.onChiefComplaintPositiveClick(
		    				   _sympton_EditText.getText().toString(), _onsetOfSympton_EditText.getText().toString(),
		    				   _duration_EditText.getText().toString(), _otctreatment_EditText.getText().toString(), _index
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
		    		   _listener.onChiefComplaintDeleteClick(_index);
		    		   dismiss();
		    	   }
    	   });   	  
    	  
    	  
    	  if(_index != -1)
    	  {
    		  String sympton = getArguments().getString("Sympton");
    		  String onsetOfSympton = getArguments().getString("OnsetOfSympton");
    		  String duration = getArguments().getString("Duration");
    		  String otcTreatment = getArguments().getString("OtcTreatment");
    		     	  
        	  _sympton_EditText.setText(sympton);  
        	  _onsetOfSympton_EditText.setText(onsetOfSympton);
        	  _duration_EditText.setText(duration);
        	  _otctreatment_EditText.setText(otcTreatment);
    	  }
    	  
    	  Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
          Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
          _chief_complaint_header = (TextView)dialog.findViewById(R.id.createcc_title);
          _chief_complaint_header.setTypeface(titleFont);
          _sympton_EditText.setTypeface(textFont);
          _onsetOfSympton_EditText.setTypeface(textFont);
          _duration_EditText.setTypeface(textFont);
          _otctreatment_EditText.setTypeface(textFont);
              	  
    	  return dialog;      	
    }   
}
