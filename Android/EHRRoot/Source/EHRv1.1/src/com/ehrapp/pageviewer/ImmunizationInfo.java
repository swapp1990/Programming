package com.ehrapp.pageviewer;

import com.ehrapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ImmunizationInfo extends Fragment {
	
	
	public static final ImmunizationInfo newInstance(String message)
	{
		ImmunizationInfo f = new ImmunizationInfo();
		Bundle bdl = new Bundle(1);
	    //bdl.putString(EXTRA_MESSAGE, message);
	    f.setArguments(bdl);
	    return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		//String message = getArguments().getString(EXTRA_MESSAGE);
		View v = inflater.inflate(R.layout.immunization_form, container, false);
		//TextView messageTextView = (TextView)v.findViewById(R.id.textView);
		//messageTextView.setText(message);
      
        
       /* Button buttonNext = (Button) v.findViewById(R.id.btnClick2);
		  
		  buttonNext.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	Intent i = new Intent(getActivity(), SlidePages.class);
	                startActivity(i);
	        }
	        });*/
		  
		  return v;
    }

}