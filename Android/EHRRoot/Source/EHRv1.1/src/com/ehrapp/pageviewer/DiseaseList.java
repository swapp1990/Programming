package com.ehrapp.pageviewer;

import com.ehrapp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiseaseList extends Fragment {
	
	public static final DiseaseList newInstance(String message)
	{
		DiseaseList f = new DiseaseList();
		Bundle bdl = new Bundle(1);
	    //bdl.putString(EXTRA_MESSAGE, message);
	    f.setArguments(bdl);
	    return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.disease_layout, container, false);
    	  return v;
    }
	 

}
