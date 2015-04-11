package com.airbrush.airbrushrecorder.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.airbrush.airbrushrecorder.Flight;
import com.airbrush.airbrushrecorder.LoginHelper;
import com.airbrush.airbrushrecorder.TaskUploadFlight;
import com.airbrush.airbrushrecorder.WebInterface;
import com.airbrush.airbrushrecorder.data.FlightsDataSource;
import com.airbrush.airbrushrecorder.dialog.DialogDeleteFlight;
import com.airbrush.airbrushrecorder.dialog.DialogEditFlight;
import com.airbrush.airbrushrecorder.dialog.DialogUploadFlightResponse;
import com.airbrush.airbrushrecorder.dialog.DialogWifiOff;
import com.airbrush.airbrushrecorder.fragments.FragmentRecorder.OnToggleRecordingListener;
import com.airbrush.airbrushrecorder.R;
import com.airbrush.airbrushrecorder.fragments.InterfaceOnFlightUploadCompleted;
import com.airbrush.airbrushrecorder.DataStorage;

public class FragmentFlightBrowser extends Fragment implements InterfaceOnFlightUploadCompleted
{
	private static String TAG = "FLIGHT_BROWSER";
	private static String UPLOAD_BOOLEAN_NAME = "flightBrowserUploading";
	private Spinner _spinner = null;
	
	private ArrayList<Flight> _flights = null;
	
	private OnFlightBrowserListener m_listener;
	private ProgressBar m_progressBar = null;
	
	public interface OnFlightBrowserListener //well, thats a stupid name...
	{
		public void submitSelectedFlight(View view);
		public void editSelectedFlight(View view);
	}
	
	private final OnItemSelectedListener m_itemSelectedListener = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
		{
			displayMetaData();
	    }
		
		@Override
	    public void onNothingSelected(AdapterView<?> parentView)
		{
			 
	    }
	};
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		if(activity instanceof OnToggleRecordingListener)
		{
			m_listener = (OnFlightBrowserListener) activity;
		}
		else
		{
			throw new ClassCastException(activity.toString() + "must implement OnFlightBrowserListener");
		}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		return inflater.inflate(R.layout.fragment_flight_browser, container, false);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		listFiles();
		
		m_progressBar = (ProgressBar)getView().findViewById(R.id.progress_bar);
	}
	
	@Override
	public void onResume()
	{
		Log.d(TAG, "" + DataStorage.getInstance().getBoolean(UPLOAD_BOOLEAN_NAME));
		
		if(DataStorage.getInstance().getBoolean(UPLOAD_BOOLEAN_NAME) != null)
		{
			indicateUpload(DataStorage.getInstance().getBoolean(UPLOAD_BOOLEAN_NAME));
		}
		
		super.onResume();
	}
	
	public void updateFlightList()
	{
		listFiles();
	}
	
	private void listFiles()
	{
		try
		{
			FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
			dataSource.open();
			_flights = dataSource.getFlights();
			//dataSource.printAllWaypoints();
			dataSource.close();
			
			List<String> list = new ArrayList<String>();
			for(int i = 0; i < _flights.size(); i++)
			{
				if(_flights.get(i).getRecording() == false)
				{
					list.add(_flights.get(i).getDate().toString());
				}
			}
			
			_spinner = (Spinner) getView().findViewById(R.id.dropdown_file);
			Button submitButton = (Button) getView().findViewById(R.id.button_submit_flight);
			Button editButton = (Button) getView().findViewById(R.id.button_edit_flight);
			
			if(list.isEmpty() == false)
			{
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				//_spinner.setVisibility(View.VISIBLE);
				_spinner.setAdapter(dataAdapter);
				_spinner.setOnItemSelectedListener(m_itemSelectedListener);
				
				submitButton.setVisibility(View.VISIBLE);
				editButton.setVisibility(View.VISIBLE);
			}
			else
			{
				//_spinner.setVisibility(View.GONE);
				submitButton.setVisibility(View.GONE);
				editButton.setVisibility(View.GONE);
			}
			
			if(list.size() > 1)
			{
				_spinner.setVisibility(View.VISIBLE);
			}
			else
			{
				_spinner.setVisibility(View.GONE);
			}
			
			displayMetaData();
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	public void editSelectedFlight(View view)
	{
		try
		{
			DialogEditFlight dialog = new DialogEditFlight(this.getActivity());
			Bundle bundle = new Bundle();
			bundle.putInt("flightId", getSelectedFlight().getId());
			dialog.setArguments(bundle);
			dialog.show(this.getActivity().getSupportFragmentManager(), TAG);
		}
		catch(Exception e)
		{
			Log.d(TAG, e.toString());
		}
	}

	public void deleteSelectedFlight(View view)
	{
		try
		{
			DialogDeleteFlight dialog = new DialogDeleteFlight();
			dialog.show(this.getActivity().getSupportFragmentManager(), TAG);
		}
		catch(Exception e)
		{
			Log.d(TAG, e.toString());
		}
	}
	
	public void deleteSelectedFlight()
	{
		try
		{
			Flight flight = getSelectedFlight();
			if(flight != null)
			{
				FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
				dataSource.open();
				dataSource.deleteFlight(flight.getId(), true);
				dataSource.close();
				
				listFiles();
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	public void submitSelectedFlight(View view)
	{
		try
		{
			Flight flight = getSelectedFlight();
			if(flight != null)
			{
				if(WebInterface.wifiAvailable(getActivity()))
				{
					FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
					dataSource.open();
					String sessionData = dataSource.getCookie();
					dataSource.close();
					
					LoginHelper loginHelper = new LoginHelper();
					boolean loginSuccess = false;
					if(sessionData.length() <= 0 || loginHelper.ipChanged(this.getActivity()))
					{
						loginSuccess = loginHelper.login(getActivity());
						
						dataSource.open();
						sessionData = dataSource.getCookie();
						dataSource.close();
					}
					
					if(sessionData.length() > 0)
					{
						indicateUpload(true);
						submitSelectedFlight(flight, sessionData);
					}
					else
					{
						DialogUploadFlightResponse dialog = new DialogUploadFlightResponse();
						Bundle bundle = new Bundle();
						bundle.putBoolean("success", false);
						bundle.putBoolean("loginSuccess", loginSuccess);
						bundle.putString("message", "");
						dialog.setArguments(bundle);
						dialog.show(getFragmentManager(), TAG);
					}
				}
				else
				{
					DialogWifiOff dialog = new DialogWifiOff();
					dialog.show(getChildFragmentManager(), TAG);
				}
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	@Override
	public void onUploadCompleted()
	{
		indicateUpload(false);
	}
	
	private void submitSelectedFlight(Flight flight, String sessionData)
	{	
		if(WebInterface.wifiAvailable(getActivity()))
		{
			/*
			WebInterface wf = new WebInterface(getActivity());
			int responseCode = wf.postFlight(flight, sessionData);
			String response = wf.getHttpResponse();
			
			createFlightUploadResponse(responseCode, response);
			/**/
			
			TaskUploadFlight task = new TaskUploadFlight(getActivity(), sessionData, this);
			try
			{
				task.execute(flight);
			}
			catch (Exception e)
			{
				Log.d(TAG, e.toString());
			}
		}
	}
	
	private void createFlightUploadResponse(int responseCode, String response)
	{
		indicateUpload(false);
		
		if(responseCode >= 200 && responseCode < 300)
		{
			DialogUploadFlightResponse dialog = new DialogUploadFlightResponse();
			Bundle bundle = new Bundle();
			bundle.putBoolean("success", true);
			bundle.putBoolean("loginSuccess", true);
			bundle.putString("message", "");
			dialog.setArguments(bundle);
			dialog.show(getFragmentManager(), TAG);
			
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
			dialog.show(getFragmentManager(), TAG);
			
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
			dialog.show(getFragmentManager(), TAG);
			
			return;
		}
		
		//other errors...
		if(responseCode > 300)
		{
			DialogUploadFlightResponse dialog = new DialogUploadFlightResponse();
			Bundle bundle = new Bundle();
			bundle.putBoolean("success", false);
			bundle.putBoolean("loginSuccess", true);
			bundle.putString("message", getString(R.string.dialog_upload_response_flight_corrupted));
			dialog.setArguments(bundle);
			dialog.show(getFragmentManager(), TAG);
			
			return;
		}
	}
	
	private Flight getSelectedFlight()
	{
		if(_spinner != null && _flights != null)
		{
			Object selectedItem = _spinner.getSelectedItem();
			if(selectedItem == null)
				return null;
			
			String fileName = selectedItem.toString();
			
			for(int i = 0; i < _flights.size(); i++)
			{
				Flight flight = _flights.get(i);
				String date = flight.getDate().toString();
				
				if(fileName.equals(date))
				{
					return flight;
				}
			}
		}
		
		return null;
	}
	
	private void displayMetaData()
	{
		if(_spinner != null)
		{
			TextView textView = (TextView)getView().findViewById(R.id.textview_flight_data);
			
			if(_flights.isEmpty())
			{
				textView.setText(getString(R.string.textView_flight_data_no_records));
				
				return;
			}
			
			Flight flight = getSelectedFlight();
			
			if(flight == null)
			{
				textView.setText(getString(R.string.textView_flight_data_default));
				return;
			}
			
			try
			{
				String date = flight.getDate().toString();
				String departure = flight.getDeparture(); //getString(R.string.textView_logging_na) + 
				String destination = flight.getDestination();
				
				if(date.length() <= 0)
					date = getString(R.string.textView_logging_na);
				if(departure.length() <= 0)
					departure = getString(R.string.textView_logging_na);
				if(destination.length() <= 0)
					destination = getString(R.string.textView_logging_na);
					
				date = getString(R.string.textView_flightBrowser_date) + date;
				departure = getString(R.string.textView_flightBrowser_departure) + departure;
				destination = getString(R.string.textView_flightBrowser_destination) + destination;
				
				textView.setText(date + "\n" + departure + "\n" + destination + "\n" + getString(R.string.textView_flightBrowser_waypoints) + flight.getWaypointCount());
			}
			catch(Exception e)
			{
				Log.e(TAG, e.getMessage());
			}
		}
	}
	
	public void onDialogPositiveClick(DialogFragment dialog)
	{
		deleteSelectedFlight();
	}
	
	public void indicateUpload(boolean uploading)
	{
		DataStorage.getInstance().setBoolean(UPLOAD_BOOLEAN_NAME, uploading);
		
		_spinner = (Spinner) getView().findViewById(R.id.dropdown_file);
		Button submitButton = (Button) getView().findViewById(R.id.button_submit_flight);
		Button editButton = (Button) getView().findViewById(R.id.button_edit_flight);
		
		if(uploading)
		{
			if(_spinner != null)
			{
				_spinner.setEnabled(false);
			}
			if(submitButton != null)
			{
				submitButton.setEnabled(false);
			}
			if(editButton != null)
			{
				editButton.setEnabled(false);
			}
			if(m_progressBar != null)
			{
				m_progressBar.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			if(_spinner != null)
			{
				_spinner.setEnabled(true);
			}
			if(submitButton != null)
			{
				submitButton.setEnabled(true);
			}
			if(editButton != null)
			{
				editButton.setEnabled(true);
			}
			if(m_progressBar != null)
			{
				m_progressBar.setVisibility(View.GONE);
			}
		}
	}
}
