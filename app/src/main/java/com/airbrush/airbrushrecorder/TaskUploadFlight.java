package com.airbrush.airbrushrecorder;

import com.airbrush.airbrushrecorder.dialog.DialogUploadFlightResponse;
import com.airbrush.airbrushrecorder.fragments.InterfaceOnFlightUploadCompleted;
import com.airbrush.airbrushrecorder.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class TaskUploadFlight extends AsyncTask<Flight, Integer, Integer>
{
	private static String TAG = "TASK_UPLOAD_FLIGHT";
	
	private FragmentActivity m_activity = null;
	private String m_sessionData = "";
	private int m_responseCode = 400;
	private String m_response = "";
	
	private InterfaceOnFlightUploadCompleted m_completedListener = null;
	
	public TaskUploadFlight(FragmentActivity activity, String sessionData, InterfaceOnFlightUploadCompleted listener)
	{
		m_activity = activity;
		m_sessionData = sessionData;
		m_completedListener = listener;
	}
	
	@Override
	protected Integer doInBackground(Flight... params)
	{
		try
		{
			if(m_activity != null && m_sessionData.length() > 0)
			{
				int flightCount = params.length;
				
				for(int i = 0; i < flightCount; i++)
				{
					if(WebInterface.wifiAvailable(m_activity))
					{
						WebInterface wf = new WebInterface(m_activity);
						m_responseCode = wf.postFlight(params[i], m_sessionData);
						m_response = wf.getHttpResponse();
					}
				}
			}
			
			createFlightUploadResponse(m_responseCode, m_response);
		}
		catch(Exception e)
		{
			Log.e(TAG, "" + e);
		}
		
		return m_responseCode;
	}

	@Override
	protected void onPostExecute(Integer result)
	{
		if(m_completedListener != null)
		{
			m_completedListener.onUploadCompleted();
		}
    }
	
	private void createFlightUploadResponse(int responseCode, String response)
	{
		if(responseCode >= 200 && responseCode < 300)
		{
			DialogUploadFlightResponse dialog = new DialogUploadFlightResponse();
			Bundle bundle = new Bundle();
			bundle.putBoolean("success", true);
			bundle.putBoolean("loginSuccess", true);
			bundle.putString("message", "");
			dialog.setArguments(bundle);
			dialog.show(m_activity.getSupportFragmentManager(), TAG);
			
			return;
		}
		
		//bad request
		if(responseCode == 400)
		{
			DialogUploadFlightResponse dialog = new DialogUploadFlightResponse();
			Bundle bundle = new Bundle();
			bundle.putBoolean("success", false);
			bundle.putBoolean("loginSuccess", true);
			bundle.putString("message", "");
			dialog.setArguments(bundle);
			dialog.show(m_activity.getSupportFragmentManager(), TAG);
			
			return;
		}
		
		//forbidden
		if(responseCode == 403)
		{
			DialogUploadFlightResponse dialog = new DialogUploadFlightResponse();
			Bundle bundle = new Bundle();
			bundle.putBoolean("success", false);
			bundle.putBoolean("loginSuccess", false);
			bundle.putString("message", "");
			dialog.setArguments(bundle);
			dialog.show(m_activity.getSupportFragmentManager(), TAG);
			
			return;
		}
		
		//other errors...
		if(responseCode > 300)
		{
			DialogUploadFlightResponse dialog = new DialogUploadFlightResponse();
			Bundle bundle = new Bundle();
			bundle.putBoolean("success", false);
			bundle.putBoolean("loginSuccess", true);
			bundle.putString("message", m_activity.getResources().getString(R.string.dialog_upload_response_flight_corrupted));
			dialog.setArguments(bundle);
			dialog.show(m_activity.getSupportFragmentManager(), TAG);
			
			return;
		}
	}
}
