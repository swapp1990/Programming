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

public class CreateBodySystemReviewFragment extends DialogFragment{
	
    public interface CreateBodySystemReviewListener {
        public void onBodySystemReviewPositiveClick(
    			String i_skin,
    			String i_vision,
    			String i_hearing,
    			String i_respiratory,
    			String i_cardiovascular,
    			String i_gastrointestinal,
    			String i_gynecologic,
    			String i_musculoskeletal,
    			String i_vascular,
    			String i_neurologic,
    			String i_hematologic,
    			String i_endocrine,
    			String i_psychiatric,
    			String i_urological,
    			String i_other,
    			int i_index
    			);
        public void onBodySystemReviewDeleteClick();
    }
    
    // Use this instance of the interface to deliver action events
    CreateBodySystemReviewListener _listener;
    
    private int _index = -1;
    private ImageButton _create_Btn = null;
    private ImageButton _delete_Btn = null;
    
    private EditText _skin_EditText = null;
	private EditText _vision_EditText = null;
	private EditText _hearing_EditText = null;
	private EditText _respiratory_EditText = null;
	private EditText _cardiovascular_EditText = null;
	private EditText _gastrointestinal_EditText = null;
	private EditText _gynecologic_EditText = null;
	private EditText _musculoskeletal_EditText = null;
	private EditText _vascular_EditText = null;
	private EditText _neurologic_EditText = null;
	private EditText _hematologic_EditText = null;
	private EditText _endocrine_EditText = null;
	private EditText _psychiatric_EditText = null;
	private EditText _urological_EditText = null;
	private EditText _other_EditText = null;
	private TextView _create_body_review_header = null;
    
    public CreateBodySystemReviewFragment() {
    	
    	
    }
       
    @Override
    public void onAttach(Activity activity) 
    {
        super.onAttach(activity);
        try 
        {
        	_listener = (CreateBodySystemReviewListener) activity;
        }
        catch (ClassCastException e) 
        {
            throw new ClassCastException(activity.toString() + " must implement CreateBodySystemReviewListener");
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
    	  dialog.setContentView(R.layout.createrbs_layout);  
    	  dialog.getWindow().setBackgroundDrawable(  
    	    new ColorDrawable(Color.TRANSPARENT));  
    	  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
    	  dialog.show();
    	  
    	  _skin_EditText 				= (EditText) dialog.findViewById(R.id.rbs_skin);
    	  _vision_EditText 				= (EditText) dialog.findViewById(R.id.rbs_vision);
    	  _hearing_EditText 			= (EditText) dialog.findViewById(R.id.rbs_hearing);
    	  _respiratory_EditText 		= (EditText) dialog.findViewById(R.id.rbs_respiratory);
    	  _cardiovascular_EditText 		= (EditText) dialog.findViewById(R.id.rbs_cardiovascular);
    	  _gastrointestinal_EditText 	= (EditText) dialog.findViewById(R.id.rbs_gastrointestinal);
    	  _gynecologic_EditText		 	= (EditText) dialog.findViewById(R.id.rbs_gynecologic);
    	  _musculoskeletal_EditText 	= (EditText) dialog.findViewById(R.id.rbs_musculoskeletal);
    	  _vascular_EditText 			= (EditText) dialog.findViewById(R.id.rbs_vascular);
    	  _neurologic_EditText 			= (EditText) dialog.findViewById(R.id.rbs_neurologic);
    	  _hematologic_EditText			= (EditText) dialog.findViewById(R.id.rbs_hematologic);
    	  _endocrine_EditText 			= (EditText) dialog.findViewById(R.id.rbs_endocrine);
    	  _psychiatric_EditText 		= (EditText) dialog.findViewById(R.id.rbs_psychiatric);
    	  _urological_EditText 			= (EditText) dialog.findViewById(R.id.rbs_urological);
    	  _other_EditText				= (EditText) dialog.findViewById(R.id.rbs_other);
    		
    	  _create_Btn = (ImageButton) dialog.findViewById(R.id.rbs_create); 
    	  _delete_Btn = (ImageButton) dialog.findViewById(R.id.rbs_delete);
    	  _create_Btn.setOnClickListener
    	  (
    			  new OnClickListener() 
	    	      {  
		    	   @Override  
		    	   public void onClick(View v) 
		    	   {  
		    		   _listener.onBodySystemReviewPositiveClick(
		    				    _skin_EditText.getText().toString(),
		    					_vision_EditText.getText().toString(),
		    					_hearing_EditText.getText().toString(),
		    					_respiratory_EditText.getText().toString(),
		    					_cardiovascular_EditText.getText().toString(),
		    					_gastrointestinal_EditText.getText().toString(),
		    					_gynecologic_EditText.getText().toString(),
		    					_musculoskeletal_EditText.getText().toString(),
		    					_vascular_EditText.getText().toString(),
		    					_neurologic_EditText.getText().toString(),
		    					_hematologic_EditText.getText().toString(),
		    					_endocrine_EditText.getText().toString(),
		    					_psychiatric_EditText.getText().toString(),
		    					_urological_EditText.getText().toString(),
		    					_other_EditText.getText().toString(),
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
		    		   _listener.onBodySystemReviewDeleteClick();
		    		   dismiss();
		    	   }
    	   });
    	  

    	  if(_index != -1)
    	  {
    		  String skin 			= getArguments().getString("skin");
    		  String vision 		= getArguments().getString("vision");
    		  String hearing 		= getArguments().getString("hearing");
    		  String respiratory 	= getArguments().getString("respiratory");
    		  String cardiovascular = getArguments().getString("cardiovascular");
    		  String gastrointestinal = getArguments().getString("gastrointestinal");
    		  String gynecologic 	= getArguments().getString("gynecologic");;
    		  String musculoskeletal = getArguments().getString("musculoskeletal");
    		  String vascular 		= getArguments().getString("vascular");
    		  String neurologic 	= getArguments().getString("neurologic");;
    		  String hematologic 	= getArguments().getString("hematologic");
    		  String endocrine		= getArguments().getString("endocrine");
    		  String psychiatric 	= getArguments().getString("psychiatric");
        	  String urological 	= getArguments().getString("urological"); 
        	  String other 			= getArguments().getString("other");
        	  
			  _skin_EditText.setText(skin);
			  _vision_EditText.setText(vision);
			  _hearing_EditText.setText(hearing);
			  _respiratory_EditText.setText(respiratory);
			  _cardiovascular_EditText.setText(cardiovascular);
			  _gastrointestinal_EditText.setText(gastrointestinal);
			  _gynecologic_EditText.setText(gynecologic);
			  _musculoskeletal_EditText.setText(musculoskeletal);
			  _vascular_EditText.setText(vascular);
			  _neurologic_EditText.setText(neurologic);
			  _hematologic_EditText.setText(hematologic);
			  _endocrine_EditText.setText(endocrine);
			  _psychiatric_EditText.setText(psychiatric);
			  _urological_EditText.setText(urological);
			  _other_EditText.setText(other);
    	  }
    	  
    	  
    	  Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-REGULAR.OTF");
          Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
          _create_body_review_header = (TextView)dialog.findViewById(R.id.createrbs_title);
          _create_body_review_header.setTypeface(titleFont);
          _skin_EditText.setTypeface(textFont);
          _vision_EditText.setTypeface(textFont);
          _hearing_EditText.setTypeface(textFont);
          _respiratory_EditText.setTypeface(textFont);
          _cardiovascular_EditText.setTypeface(textFont);
          _gastrointestinal_EditText.setTypeface(textFont);
          _gynecologic_EditText.setTypeface(textFont);
          _musculoskeletal_EditText.setTypeface(textFont);
          _vascular_EditText.setTypeface(textFont);
          _neurologic_EditText.setTypeface(textFont);
          _hematologic_EditText.setTypeface(textFont);
          _endocrine_EditText.setTypeface(textFont);
          _psychiatric_EditText.setTypeface(textFont);
		  _urological_EditText.setTypeface(textFont);
		  _other_EditText.setTypeface(textFont);       
          
    	  return dialog;
    }   
}

