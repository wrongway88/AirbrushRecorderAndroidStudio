package com.airbrush.airbrushrecorder;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.airbrush.airbrushrecorder.data.FlightsDataSource;
import com.airbrush.airbrushrecorder.dialog.DialogEnterLoginData;
import com.airbrush.airbrushrecorder.dialog.DialogWifiOff;

import com.airbrush.airbrushrecorder.LogWriter;

public class LoginHelper
{
	private static String TAG = "LOGIN_HELPER";
	
	private LogWriter _logWriter = new LogWriter();
	
	public boolean setLoginData(FragmentActivity activity, String mail, String password)
	{
		try
		{
			_logWriter.openFile("log.txt");
			
			if(WebInterface.wifiAvailable(activity))
			{
				_logWriter.writeToFile("setLoginData: set login data online");
				
				return setLoginDataOnline(activity, mail, password);
			}
			else
			{
				_logWriter.writeToFile("setLoginData: set login data offline");
				
				setLoginDataOffline(activity, mail, password);
			}
			
			_logWriter.closeFile();
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}

		return true;
	}
	
	private void setLoginDataOffline(Activity activity, String mail, String password)
	{
		FlightsDataSource dataSource = new FlightsDataSource(activity);
		dataSource.open();
		dataSource.updateUserMail(mail);
		dataSource.updatePassword(password);
		dataSource.updateCookie(""); //force login on next upload
		dataSource.close();
	}
	
	private boolean setLoginDataOnline(FragmentActivity activity, String mail, String password)
	{
		_logWriter.openFile("log.txt");
		
		_logWriter.writeToFile("setLoginDataOnline");
		
		if(WebInterface.wifiAvailable(activity))
		{
			String ip = WebInterface.getIPAddress(false);
			
			//this checks whether login data is correct
			String userId = getUserId(mail, activity);
			if(userId.length() <= 0)
			{
				_logWriter.writeToFile("failed to get user id: " + userId);
				_logWriter.closeFile();
				
				Log.e(TAG, "setLoginDataOnline: failed to get id");
				return false;
			}
			
			Log.d(TAG, "id: " + userId);
			
			String sessionData = getSessionData(userId, password, activity);
			if(sessionData.length() <= 0)
			{
				_logWriter.writeToFile("failed to get session data: " + userId + ", " + password);
				_logWriter.closeFile();
				
				Log.e(TAG, "setLoginDataOnline: failed to get session data - " + userId + " // " + password);
				return false;
			}
			
			_logWriter.writeToFile("writing to database now: " + userId + ", " + password);
			_logWriter.closeFile();
			
			FlightsDataSource dataSource = new FlightsDataSource(activity);
			dataSource.open();
			dataSource.updateUserMail(mail);
			dataSource.updatePassword(password);
			dataSource.updateIp(ip);
			dataSource.updateCookie(sessionData); //force login on next upload
			dataSource.close();
			
			return true;
		}
		
		_logWriter.writeToFile("couldnt log in, no internet");
		_logWriter.closeFile();
		
		return false;
	}
	
	public boolean login(FragmentActivity activity)
	{
		try
		{
			if(WebInterface.wifiAvailable(activity) == false)
			{
				DialogWifiOff dialog = new DialogWifiOff();
				dialog.show(activity.getSupportFragmentManager(), TAG);
				return false;
			}
			
			FlightsDataSource dataSource = new FlightsDataSource(activity);
			dataSource.open();
			
			String mail = dataSource.getUserName();
			String password = dataSource.getPassWord();
			
			dataSource.close();
			
			if(mail.length() == 0 || password.length() == 0)
			{
				DialogEnterLoginData dialog = new DialogEnterLoginData(activity);
				dialog.show(activity.getSupportFragmentManager(), TAG);
				
				//Log.e(TAG, "mail or password is not set");
				return false;
			}
			
			String userId = getUserId(mail, activity);
			
			String sessionData = getSessionData(userId, password, activity);
			
			dataSource.open();
			dataSource.updateCookie(sessionData);
			dataSource.close();
			
			if(sessionData.length() > 0)
				return false;
			else
				return true;
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		return false;
	}
	
	private String getUserId(String mailAddress, FragmentActivity activity)
	{
		String userId = "";
		
		if(mailAddress.length() == 0)
		{
			Log.e(TAG, "mail is not set");
			return userId;
		}
		
		WebInterface webInterface = new WebInterface(activity);
		userId = webInterface.requestUserId(mailAddress, activity);
		
		return userId;
	}
	
	private String getSessionData(String userId, String password, Activity activity)
	{
		String sessionData = "";
		
		if(userId.length() <= 0 || password.length() == 0)
		{
			Log.e(TAG, "userId and/or password not set");
			return sessionData;
		}
		
		WebInterface webInterface = new WebInterface(activity);
		sessionData = webInterface.login(userId,  password);
		
		return sessionData;
	}
	
	public boolean ipChanged(Activity activity)
	{
		String ip = WebInterface.getIPAddress(false);
		
		FlightsDataSource dataSource = new FlightsDataSource(activity);
		dataSource.open();
		String oldIp = dataSource.getIp();
		dataSource.close();
		
		return (ip != oldIp);
	}
	
	public String getSessionData(Activity activity)
	{
		String result = "";
		
		FlightsDataSource dataSource = new FlightsDataSource(activity);
		dataSource.open();
		result = dataSource.getCookie();
		dataSource.close();
		
		return result;
	}

	public boolean onDialogPositiveClick(DialogFragment dialog, String mailAddress, String password)
	{
		return setLoginData(dialog.getActivity(), mailAddress, password);
	}

	public void onDialogNegativeClick(DialogFragment dialog)
	{
		
	}
}
