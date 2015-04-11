package com.airbrush.airbrushrecorder;

import java.util.Calendar;

public class DebugLog
{
	private static DebugLog _instance = null;
	private LogWriter _logWriter = null;
	
	public static DebugLog getInstance()
	{
		if(_instance == null)
		{
			_instance = new DebugLog();
		}
		
		return _instance;
	}
	
	public static void DEBUG(String message)
	{
		getInstance().write("DEBUG", message);
	}
	
	private DebugLog()
	{
		initializeLogFile();
	}
	
	private void initializeLogFile()
	{
		_logWriter = new LogWriter();
		
		Calendar c = Calendar.getInstance();
		String date = c.get(Calendar.YEAR) + "_" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.DAY_OF_MONTH) + "-"
				+ c.get(Calendar.HOUR_OF_DAY) + "_" + c.get(Calendar.MINUTE) + "_" + c.get(Calendar.SECOND);
		
		_logWriter.openFile("debugLog_" + date + ".txt");
	}
	
	public void write(String tag, String message)
	{
		if(_logWriter != null)
		{
			_logWriter.writeToFile(getTimeString() + " " + tag + ": " + message);
		}
	}
	
	private String getTimeString()
	{
		String result = "";
		
		Calendar c = Calendar.getInstance();
		result = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
		
		return result;
	}
}
