package com.airbrush.airbrushrecorder;

import java.util.HashMap;

public class DataStorage
{
	//private static String TAG = "DATA_STORAGE";
	private static DataStorage _instance = null;
	
	private HashMap<String, Boolean> _boolValues = new HashMap<String, Boolean>();
	private HashMap<String, String> _stringValues = new HashMap<String, String>();
	
	private DataStorage()
	{
		
	}
	
	public static DataStorage getInstance()
	{
		if(_instance == null)
		{
			_instance = new DataStorage();
		}
		
		return _instance;
	}
	
	public void setBoolean(String key, Boolean value)
	{
		_boolValues.put(key, value);
	}
	
	public void setString(String key, String value)
	{
		_stringValues.put(key, value);
	}
	
	public Boolean getBoolean(String key)
	{
		return _boolValues.get(key);
	}
	
	public String getString(String key)
	{
		return _stringValues.get(key);
	}
}
