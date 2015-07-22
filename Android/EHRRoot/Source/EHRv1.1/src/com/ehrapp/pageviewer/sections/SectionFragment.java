package com.ehrapp.pageviewer.sections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ehrapp.R;
import com.ehrapp.pageviewer.Page_EditFamilyHistory;

public class SectionFragment extends Fragment{

	public TextView dateView, textView1;
	private String nameFragment;
	private long myTimeStamp_;
	LinearLayout layout;
	boolean isFamilyHist_;
	int familyId;
	
	public View onCreateView(LayoutInflater inflater,
	       ViewGroup container, Bundle savedInstanceState) 
		{
		   View V = inflater.inflate(R.layout.sectionlog_fragment, container, false);
		   layout = (LinearLayout) V.findViewById(R.id.sectionFragment);
		   V.setOnTouchListener(new OnTouchListener()
		    {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if (arg1.getAction() == MotionEvent.ACTION_DOWN){
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.box_click); 
						layout.setBackgroundDrawable(drawable);
					}
					else if (arg1.getAction() == MotionEvent.ACTION_UP) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.box_unclick); 
						layout.setBackgroundDrawable(drawable);
						CallFullConsultationReport();
					}
					else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
						Resources res = getResources(); //resource handle
						Drawable drawable = res.getDrawable(R.drawable.box_unclick); 
						layout.setBackgroundDrawable(drawable);
					}
					return true;
				}
		    });

		   myTimeStamp_ = 0;
		   isFamilyHist_ = false;
	       //Inflate the layout for this fragment
		   Bundle bundle = this.getArguments();
		   String dateValue = bundle.getString("dateValue", "This");
		   dateView = (TextView) V.findViewById(R.id.dateValue);
		   dateView.setText(dateValue);
		   
		   String versionText = bundle.getString("version", "1");
		   textView1 = (TextView) V.findViewById(R.id.version);
		   textView1.setText(versionText);
		   
		   myTimeStamp_ = Long.parseLong(bundle.getString("timeStamp", "0"));
		   
		   ArrayList<String> stringList = new ArrayList<String>();
		   String text = null;
		   LinkedHashMap Hash_Map= (LinkedHashMap) bundle.getSerializable("HashMap");
		   Set keys = Hash_Map.keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) 
	        {
	            String t_column_name = (String) i.next();
	            String t_column_value = (String) Hash_Map.get(t_column_name);
	            text = t_column_name + ": " + t_column_value;
	            stringList.add(text);
	           
	        }
		   //For family history ..
		   String isFamilyHistS = bundle.getString("isFamily", "false");
		   String familyIDS = bundle.getString("FamilyID", "0");
		   System.out.println("Family " + familyIDS);
		   familyId = Integer.parseInt(familyIDS);
		   if(isFamilyHistS.equals("true"))
		   {
			   isFamilyHist_ = true;
		   }
		   WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		   Display display = wm.getDefaultDisplay();
		   Point size = new Point();
		   display.getSize(size);
		   int height = size.y;
		   int height_layout = 110;
		   if(height == 1920)
		   {
			   height_layout = 240;
		   }
		   for(int i = 0; i < stringList.size(); i++)
		   {
			   TextView tv_ = new TextView(V.getContext());
			   tv_.setText(stringList.get(i));
			   tv_.setTextColor(Color.parseColor("black"));
			   tv_.setTextSize(16);
			   tv_.setPadding(30,0,30,0);
			   if(height == 1920)
			   {
				  height_layout += 75;
			   }
			   else
			   {
				  height_layout += 40;
			   }
			   layout.addView(tv_);
		   }
		   layout.getLayoutParams().height = height_layout;
		   nameFragment = bundle.getString("name", "This");
		   return V;
	   }
	   
	   public void CallFullConsultationReport()
	   {
		   if(!isFamilyHist_)
		   {
			   Intent i = new Intent(getActivity(), FullConsultationReport.class);
			   i.putExtra("name", nameFragment);
			   i.putExtra("timeStamp", myTimeStamp_);
			   getActivity().startActivity(i);   
		   }
		   else
		   {
			   Intent i = new Intent(getActivity(), Page_EditFamilyHistory.class);
			   System.out.println("Family when called " + familyId);
			   i.putExtra("name", nameFragment);
			   i.putExtra("timeStamp", myTimeStamp_);
			   i.putExtra("familyID", familyId);
			   getActivity().startActivity(i);   
		   }
	   }
}
