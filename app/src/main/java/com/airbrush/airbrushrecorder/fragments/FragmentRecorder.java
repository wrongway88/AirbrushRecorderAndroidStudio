package com.airbrush.airbrushrecorder.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import com.airbrush.airbrushrecorder.ServicePathLog;
import com.airbrush.airbrushrecorder.dialog.DialogGPSOff;
import com.airbrush.airbrushrecorder.R;

public class FragmentRecorder extends Fragment
{
	public interface OnToggleRecordingListener
	{
		public void togglePathLogging(View view);
		public void onLoggingSuccess();
		public void onLoggingFail();
	}
	
	private Boolean m_logging = false;
	private OnToggleRecordingListener m_listener;
	private static String TAG = "FragmentRecorder";
	private static final String PREFERENCES = "airbrush_preferences";
	private static final String B_LOGGING = "airbrush_isLogging";
	
	private Handler _pathLogHandler = new Handler()
	{
	    @Override
	    public void handleMessage(Message msg)
	    {
	    	if(msg.arg1 == Activity.RESULT_OK)
	    	{
	    		m_listener.onLoggingSuccess();
	    	}
	    	else if(msg.arg1 == Activity.RESULT_CANCELED)
	    	{
	    		m_listener.onLoggingFail();
	    	}
	    }
	};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		this.setRetainInstance(true); //so that the fragment is not recreated on orientation change (or other events), needed to keep track of logging state
		
		return inflater.inflate(R.layout.fragment_recorder, container, false);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		if(activity instanceof OnToggleRecordingListener)
		{
			m_listener = (OnToggleRecordingListener) activity;
		}
		else
		{
			throw new ClassCastException(activity.toString() + "must implement OnToggleRecordingListener");
		}
	}
	
	@Override
	public void onResume()
	{
        Log.d(TAG, "onResume");

		super.onResume();
		
		SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCES, 0);
		if(settings != null)
		{
			m_logging = settings.getBoolean(B_LOGGING, false);
		}
		
		setLayoutLogging(m_logging);
	}
	
	public void togglePathLogging(View view)
	{
        Log.d(TAG, "togglePathLogging");

		if(m_logging)
		{
			stopPositionLogging();
		}
		else
		{
			startPositionLogging();
		}
	}
	
	private void startPositionLogging()
    {
        Log.d(TAG, "startPositionLogging");

		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			String departure = "";
			String arrival = "";
			String airplaneType = "";
			
	    	Intent intent = new Intent(getActivity(), ServicePathLog.class);
	    	intent.putExtra(getString(R.string.log_departure), departure);
			intent.putExtra(getString(R.string.log_destination), arrival);
			intent.putExtra(getString(R.string.log_airplane_type), airplaneType);
			intent.putExtra(getString(R.string.log_path_log_handler), new Messenger(_pathLogHandler));
			getActivity().startService(intent);
			
			setLayoutLogging(true);
			
			m_logging = !m_logging;
			SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCES, 0);
			if(settings != null)
			{
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(B_LOGGING, m_logging);
				editor.commit();
			}
		}
		else
		{
			DialogGPSOff dialog = new DialogGPSOff();
			dialog.show(this.getFragmentManager(), TAG);
		}
    }
	
	private void stopPositionLogging()
	{
		setLayoutLogging(false);
		
		m_logging = !m_logging;
		SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCES, 0);
		if(settings != null)
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(B_LOGGING, m_logging);
			editor.commit();
		}
		
		Intent intent = new Intent(getActivity(), ServicePathLog.class);
		getActivity().stopService(intent);
	}
	
	private void setLayoutLogging(Boolean logging)
	{
		LinearLayout indicatorLayout = (LinearLayout) getView().findViewById(R.id.layout_log_indicator);
		TextView indicator = (TextView) getView().findViewById(R.id.textview_log_indicator);
		
		Button button = (Button) getView().findViewById(R.id.button_toggle_logging);
		
		if(logging)
		{
			indicator.setText("Recording");
			indicator.setVisibility(View.VISIBLE);
			indicatorLayout.setVisibility(View.VISIBLE);
			
			button.setText(R.string.button_stop_logging);
			
			int grey = 12763842; // #c2c2c2
			getView().getRootView().setBackgroundColor(grey);
			getView().setBackgroundColor(grey);
		}
		else
		{
			indicator.setVisibility(View.GONE);
			indicatorLayout.setVisibility(View.GONE);
			
			button.setText(R.string.button_start_logging);
			
			int white = 16777215; // #ffffff
			getView().getRootView().setBackgroundColor(white);
			getView().setBackgroundColor(white);
		}
	}
}
