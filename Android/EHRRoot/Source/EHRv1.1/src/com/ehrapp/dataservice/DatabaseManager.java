package com.ehrapp.dataservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ehrapp.EHRLables;
import com.ehrapp.Global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;
 
public class DatabaseManager
{
	// the Activity or Application that is creating an object from this class.
	Context context;
 
	// a reference to the database used by this application/object
	private SQLiteDatabase _db;
	private Global _globals;
	
	// These constants are specific to the database.  They should be 
	// changed to suit your needs.
	private final String DB_NAME = "ehr_database";
	private final int DB_VERSION = 1;
 
	// These constants are specific to the database table.  They should be
	// changed to suit your needs.
	private final String TABLE_ROW_ID = "id";
 
	public DatabaseManager(Context context)
	{
		this.context = context;
		_globals = ((Global)context.getApplicationContext());
		// create or open the database
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this._db = helper.getWritableDatabase();
	}	

	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper
	{
		public CustomSQLiteOpenHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
		}
 
		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			//
		}
 
		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion)
		{
			//
		}
	}

	public void dropTable(String table_name)
	{
		_db.execSQL("DROP TABLE IF EXISTS " + table_name);
	}
 
	public void createTable(String table_name, Map<String, String> map)
	{
		String newTableQueryString = "create table " +
				table_name +
				" (" +
				TABLE_ROW_ID + " integer primary key autoincrement not null,";
		Set keys = map.keySet();
		int position = 0;
		for (Iterator i = keys.iterator(); i.hasNext();) 
        {
			position++;
            String column_name = (String) i.next();
            newTableQueryString = newTableQueryString + column_name;
            String column_type = (String) map.get(column_name);
            newTableQueryString = newTableQueryString + " ";
            newTableQueryString = newTableQueryString + column_type;
            if(position < keys.size())
			{
            	newTableQueryString = newTableQueryString + ",";
			}
        }
		newTableQueryString = newTableQueryString + ");";
		// execute the query string to the database.
		_db.execSQL(newTableQueryString);
	}
	
	public void createTablewithForeignKey(String table_name, Map<String, String> map, String parent_table)
	{
		Set keys = map.keySet();
      
		String newTableQueryString = "create table " +
				table_name +
				" (" +
				TABLE_ROW_ID + " integer primary key autoincrement not null,";

	    for (Iterator i = keys.iterator(); i.hasNext();) 
	        {
	            String column_name = (String) i.next();
	            newTableQueryString = newTableQueryString + column_name;
	            String column_type = (String) map.get(column_name);
	            newTableQueryString = newTableQueryString + " ";
	            newTableQueryString = newTableQueryString + column_type;
	            newTableQueryString = newTableQueryString + ",";
	        }

		newTableQueryString = newTableQueryString + "profileID_fk" 
    	+ " integer references " +parent_table+"(id))";

		// execute the query string to the database.
		_db.execSQL(newTableQueryString);
	}
	
	public void createTablewithForeignKey(String table_name, Map<String, String> map, String parent_table, String foreign_key)
	{
		Set keys = map.keySet();
	       
		String newTableQueryString = "create table " +
				table_name +
				" (" +
				TABLE_ROW_ID + " integer primary key autoincrement not null,";
	    for (Iterator i = keys.iterator(); i.hasNext();) 
	        {
	            String column_name = (String) i.next();
	            //System.out.println("key "+key);
	            newTableQueryString = newTableQueryString + column_name;
	            String column_type = (String) map.get(column_name);
	            //System.out.println("value "+value);
	            newTableQueryString = newTableQueryString + " ";
	            newTableQueryString = newTableQueryString + column_type;
	            newTableQueryString = newTableQueryString + ",";
	        }

		newTableQueryString = newTableQueryString + foreign_key 
    	+ " integer references " +parent_table+"(id))";
		// execute the query string to the database.
		_db.execSQL(newTableQueryString);
	}

	//If you want add some other type of object, add it to these function
	public void addRow(Map<String, Object> map, String table_name)
	{
		ContentValues values = new ContentValues();
		Set keys = map.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) 
        {
            String t_column_name = (String) i.next();
            Object t_column_value = (Object) map.get(t_column_name);
           if(t_column_value == null)
            {
            	int column_value = 0;
            	values.put(t_column_name, column_value);
            }
           else
           {
           if(t_column_value.getClass().equals(Integer.class))
            {
            	int column_value = (Integer) t_column_value;
            	values.put(t_column_name, column_value);
            }
           else if(t_column_value.getClass().equals(Float.class))
           {
        	Float column_value = (Float) t_column_value;
           	values.put(t_column_name, column_value);
           }
            else if(t_column_value.getClass().equals(Long.class))
            {
            	long column_value = (Long) t_column_value;
            	values.put(t_column_name, column_value);
            }
            else if(t_column_value.getClass().equals(String.class))
            {
            	String column_value = (String) t_column_value;
            	values.put(t_column_name, column_value);
            }
            else if(t_column_value.getClass().equals(Boolean.class))
            {
            	Boolean column_value = (Boolean) t_column_value;
            	values.put(t_column_name, column_value);
            }
           }
     	}
		try{_db.insert(table_name, null, values);}
		catch(Exception e)
		{
			Log.e("_db ERROR", e.toString());
			e.printStackTrace();
		}
	}
	
	//This updates the row if already present with new values, or adds a new row to the table_name
	public void addorUpdateRow(Map<String, Object> map, String table_name, String fk_column_name)
	{
		String profileValue = "-1";
		
		ContentValues values = new ContentValues();
		Set keys = map.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) 
        {
            String t_column_name = (String) i.next();
            Object t_column_value = (Object) map.get(t_column_name);
            if(t_column_name.compareTo(fk_column_name) == 0)
            {
            	profileValue = (String)t_column_value;
            }
           if(t_column_value == null)
            {
            	int column_value = 0;
            	values.put(t_column_name, column_value);
            }
           else
           {
           if(t_column_value.getClass().equals(Integer.class))
            {
            	int column_value = (Integer) t_column_value;
            	values.put(t_column_name, column_value);
            }
            else if(t_column_value.getClass().equals(Long.class))
            {
            	long column_value = (Long) t_column_value;
            	values.put(t_column_name, column_value);
            }
            else if(t_column_value.getClass().equals(String.class))
            {
            	String column_value = (String) t_column_value;
            	values.put(t_column_name, column_value);
            }
            else if(t_column_value.getClass().equals(Boolean.class))
            {
            	Boolean column_value = (Boolean) t_column_value;
            	values.put(t_column_name, column_value);
            }
           }
     	}
		try{
			//if update failed, insert
			if(_db.update(table_name, values, fk_column_name + "=" + profileValue, null) == 0)
				_db.insert(table_name, null, values);
			}
		catch(Exception e)
		{
			Log.e("_db ERROR", e.toString());
			e.printStackTrace();
		}
	
	}
	
	public int getLastInsertedRow(String MYTABLE)
	{
		int lastId = 0;
		String query = "SELECT " +TABLE_ROW_ID+ " from " +MYTABLE+ " order by " +TABLE_ROW_ID+ " DESC limit 1";
		Cursor c = _db.rawQuery(query,null);
		if (c != null && c.moveToFirst())
		{
		    lastId = (int) c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
		}
		c.close();
		return lastId;
	}

	//deletes the row using column name
	public void deleteRowFromColumn(String table_name, String column_name, String column_value)
	{
		// ask the database manager to delete the row of given id
		try {_db.delete(table_name, column_name + "=" + column_value, null);}
		catch (Exception e)
		{
			Log.e("_db ERROR", e.toString());
			e.printStackTrace();
		}
	}
	
	//deletes the row using id
	public void deleteRow(int id, String table_name)
	{
		// ask the database manager to delete the row of given id
		try {_db.delete(table_name, "id" + "=" + id, null);}
		catch (Exception e)
		{
			Log.e("_db ERROR", e.toString());
			e.printStackTrace();
		}
	}
 
	//gets the column number for a particuar column
	public int GetColumnCount(String table_name, String col_name)
	{
		int column_count = 0;
		
		ArrayList<String> colNames = getAllColsAsArray(table_name);
		for (int position=0; position < colNames.size(); position++)
    	{
    		String colName = colNames.get(position);
    		if(colName.equals(col_name))
    		{
    			column_count = position;
      			break;
    		}
    	}
		return column_count;
	}
	
	public void addColumn(String table_name, String columName)
	{
		String newTableQueryString = "ALTER TABLE " +
				table_name +
				" ADD COLUMN " +
				columName + " text";
		_db.execSQL(newTableQueryString);
	}
 
	//Gets all columns for a table as arrayist.
	public ArrayList<String> getAllColsAsArray(String table_name)
	{
		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<String> colArray = new ArrayList<String>();
		// this is a database call that creates a "cursor" object.
		// the cursor object store the information collected from the
		// database and is used to iterate through the data.
		Cursor cursor = null;
		
		try
		{
			// ask the database object to create the cursor.
			cursor = _db.query(
					table_name,
					null,  //return all columns
					null, null, null, null, null
			);
 
			// move the cursor's pointer to position zero.
			cursor.moveToFirst();
			// if there is data after the current cursor position, add it
			// to the ArrayList.
			for(int i = 0; i <cursor.getColumnCount(); i++)
			{
				String colname = cursor.getColumnName(i);
				colArray.add(colname);
 			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}
		// return the ArrayList that holds the data collected from
		// the database.
		return colArray;
	}
	
	//Gets all columns for a table as a single string.
	public String getAllColsAsString(String table_name)
	{
		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<String> colArray = new ArrayList<String>();
		
		// this is a database call that creates a "cursor" object.
		// the cursor object store the information collected from the
		// database and is used to iterate through the data.
		Cursor cursor = null;
		String colsString = "";
		try
		{
			// ask the database object to create the cursor.
			cursor = _db.query(
					table_name,
					null,  //return all columns
					null, null, null, null, null
			);
 
			// move the cursor's pointer to position zero.
			cursor.moveToFirst();
			for(int i = 0; i <getColumnNumberinOrder(table_name).length; i++)
			{
				if(getColumnNumberinOrder(table_name)[i] != -1)
				{
					 String colsStringName = cursor.getColumnName(getColumnNumberinOrder(table_name)[i]);
					 colsString = colsString + colsStringName + ",";
				}
 			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}
		return colsString;
	}
	
	public String getAllColsAsStringInOrder(String table_name, int[] colNumber)
	{
		ArrayList<String> colArray = new ArrayList<String>();
		Cursor cursor = null;
		String colsString = "";
		try
		{
			cursor = _db.query(
					table_name,
					null,  //return all columns
					null, null, null, null, null
			);
 
			cursor.moveToFirst();
			for(int i = 0; i <colNumber.length; i++)
			{
				if(colNumber[i] != -1)
				{
					 String colsStringName = cursor.getColumnName(colNumber[i]);
					 colsString = colsString + colsStringName + ",";
				}
 			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}
		return colsString;
	}
	
	//Gets all rows for a table as arraylist.
	public ArrayList<ArrayList<Object>> getAllRowsAsArrays(String table_name)
	{
		ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();

		Cursor cursor = null;
		
		try
		{
			cursor = _db.query(
					table_name,
					null,  //return all columns
					null, null, null, null, null
			);
 
			// move the cursor's pointer to position zero.
			cursor.moveToFirst();
			if (!cursor.isAfterLast())
			{
				do
				{
					ArrayList<Object> dataList = new ArrayList<Object>();
					
					for(int i = 0; i <cursor.getColumnCount(); i++)
					{
						dataList.add(cursor.getString(i));
 					}
					dataArrays.add(dataList);
				}
				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}
		return dataArrays;
	}
	
	//Gets all rows for a table as arraylist ordered by columnname.
	public ArrayList<ArrayList<Object>> getAllRowsAsArraysOrderBy(String table_name, String col_name)
	{
		ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
		Cursor cursor = null;
		
		try
		{
			cursor = _db.query(
					table_name,
					null,  //return all columns
					null, null, null, null, col_name
			);
 
			cursor.moveToFirst();

			if (!cursor.isAfterLast())
			{
				do
				{
					ArrayList<Object> dataList = new ArrayList<Object>();
					
					for(int i = 0; i <cursor.getColumnCount(); i++)
					{
					 dataList.add(cursor.getString(i));
 					}
					dataArrays.add(dataList);
				}
				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}
		return dataArrays;
	}
	
	public ArrayList<ArrayList<Object>> getAllRowsAsArrays(String table_name, ArrayList<String> column_name)
	{
		ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();

		Cursor cursor = null;
 
		try
		{
			String[] mStringArray = new String[column_name.size()];
			mStringArray = column_name.toArray(mStringArray);
			cursor = _db.query(
					table_name,
					mStringArray,
					null, null, null, null, null
			);

			cursor.moveToFirst();

			if (!cursor.isAfterLast())
			{
				do
				{
					ArrayList<Object> dataList = new ArrayList<Object>();
					for(int i = 0; i < column_name.size(); i++)
					{
						dataList.add(cursor.getString(i));
					}
					dataArrays.add(dataList);
				}
				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}

		return dataArrays;
	}
	
	public ArrayList<Object> getTimeStampRowsFromCertainProfileAsArray(String table_name, int profileID)
	{
		ArrayList<Object> dataArray = new ArrayList<Object>();
		Cursor cursor = null;
		try
		{
			// ask the database object to create the cursor.
			String[] mStringArray = new String[2];
			mStringArray[0] = "MyTimeStamp";
			mStringArray[1] = "profileID";
			cursor = _db.query(
					table_name,
					mStringArray,
					null, null, null, null, null
			);
			cursor.moveToFirst();

			if (!cursor.isAfterLast())
			{
				do
				{
					int id = cursor.getInt(1);
					if(id == profileID)
					{
						dataArray.add(cursor.getString(0));
					}
				}
				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}
		return dataArray;		
	}
	
	//Get all values for a single column in a table.
	public ArrayList<Object> getRowsAsArray(String table_name, String column_name)
	{
		ArrayList<Object> dataArray = new ArrayList<Object>();

		Cursor cursor = null;
 
		try
		{
			String[] mStringArray = new String[1];
			mStringArray[0] = column_name;
			cursor = _db.query(
					table_name,
					mStringArray,
					null, null, null, null, null
			);
 
			cursor.moveToFirst();
			if (!cursor.isAfterLast())
			{
				do
				{
						dataArray.add(cursor.getString(0));
				}
				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("_db Error", e.toString());
			e.printStackTrace();
		}
		finally 
		{
			if(cursor!= null)
				cursor.close();
		}
		return dataArray;
	}
	
	//Prints all values of a table
	public void printAllValues(String table_name)
	{
		ArrayList<ArrayList<Object>> data = getAllRowsAsArrays(table_name);
		System.out.println("No of rows: " + data.size());
    	for (int position=0; position < data.size(); position++)
    	{
    		ArrayList<Object> row = data.get(position);
    		String printData = "Values: ";
    		for(int i = 0; i < row.size(); i++)
    		{
    			if(row.get(i) != null)
    			{
    			printData = printData + row.get(i).toString() + " ";
    			}
    		}
    		printData = printData + "\n";
    		System.out.println(printData);
    	}
	}
	
	//Return all values of a table as a single String
	public String returnAllValues(String table_name)
	{
		ArrayList<ArrayList<Object>> data = getAllRowsAsArrays(table_name);
		int[] col_numbers = { -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

		col_numbers = getColumnNumberinOrder(table_name);
		String printData = "";
			for (int position=0; position < data.size(); position++)
	    	{
	    		ArrayList<Object> row = data.get(position);
	    		if(Integer.parseInt((row.get(GetColumnCount(table_name, "profileID")).toString())) == _globals.GetProfile().ProfileID)
    			{
	    		for(int i = 0; i < col_numbers.length; i++)
	    		{
	    			if(col_numbers[i] != -1)
	    			{
	    				String tempValue = row.get(col_numbers[i]).toString();
	    				if(Global.isLong(tempValue))
	    				{
	    					tempValue = Global.convertLongtoDate(tempValue);
	    				}
	    				if(tempValue.contains(","))
	    				{
	    					tempValue = tempValue.replace(",", "|");
	    				}
	    				printData = printData + tempValue + ",";
	    			}
	    		}
	    		printData = printData + "\n";
    			}
	    	}
		String finalString = "";
    	if(printData != null || !printData.isEmpty())
    	{
    		String stringCol = getAllColsAsStringInOrder(table_name,col_numbers);
    		finalString = finalString + stringCol + "\n";
    		finalString = finalString + printData;
    	}
    	return finalString;
	}
	
	public int[] getColumnNumberinOrder(String table_name)
	{
		int[] col_numbers = { -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		col_numbers[0] = GetColumnCount(table_name,"id");

		if(table_name.equals("illness_history"))
		{
			for(int i = 0; i < EHRLables.illness_history.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.illness_history[i]);
			}
		}
		else if(table_name.equals("current_medication"))
		{
			for(int i = 0; i < EHRLables.current_medication.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.current_medication[i]);
			}
		}
		else if(table_name.equals("allergies"))
		{
			for(int i = 0; i < EHRLables.allergies.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.allergies[i]);
			}
		}
		else if(table_name.equals("social_history"))
		{
			for(int i = 0; i < EHRLables.social_history.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.social_history[i]);
			}
		}
		else if(table_name.equals("body_systems"))
		{
			for(int i = 0; i < EHRLables.body_systems.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.body_systems[i]);
			}
		}
		else if(table_name.equals("vital_signs"))
		{
			for(int i = 0; i < EHRLables.vital_signs.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.vital_signs[i]);
			}
		}
		else if(table_name.equals("diagnosis_finding"))
		{
			for(int i = 0; i < EHRLables.diagnosis_finding.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.diagnosis_finding[i]);
			}
		}
		else if(table_name.equals("procedure_history"))
		{
			for(int i = 0; i < EHRLables.procedure_history.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.procedure_history[i]);
			}
		}
		else if(table_name.equals("immunization"))
		{
			for(int i = 0; i < EHRLables.immunization.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.immunization[i]);
			}
		}
		else if(table_name.equals("family_hist"))
		{
			for(int i = 0; i < EHRLables.family_hist.length; i++)
			{
				col_numbers[i+1] = GetColumnCount(table_name, EHRLables.family_hist[i]);
			}
		}
		
		return col_numbers;
	}
 
	public boolean isTableExists(String tableName) {
	    Cursor cursor = _db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
	    if(cursor!=null) {
	        if(cursor.getCount()>0) {
	                            cursor.close();
	            return true;
	        }
	                    cursor.close();
	    }
	    return false;
	}
	
}