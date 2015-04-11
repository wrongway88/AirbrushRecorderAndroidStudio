package com.airbrush.airbrushrecorder.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;

import com.airbrush.airbrushrecorder.Flight;
import com.airbrush.airbrushrecorder.R;
import com.airbrush.airbrushrecorder.data.FlightsDataSource;

@SuppressLint("ValidFragment")
public class DialogEditFlight extends DialogFragment
{
	private static String TAG = "DIALOG_EDIT_FLIGHT";
	
	public interface NoticeDialogListener
	{
        public void onDialogSaveClicked();
    }
	
	private NoticeDialogListener m_listener;
	
	private FragmentActivity m_activity = null;
	private int m_fightId = -1;
	
	public DialogEditFlight(FragmentActivity activity)
	{
		m_activity = activity;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		super.onCreateDialog(savedInstanceState);
		
		Bundle bundle = getArguments();
		m_fightId = bundle.getInt("flightId");
        FlightsDataSource dataSource = new FlightsDataSource(m_activity);
        dataSource.open();
		Flight flight = dataSource.getFlight(m_fightId);
		dataSource.close();
        
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View v = inflater.inflate(R.layout.dialog_edit_flight, null);
		
		final AlertDialog d = builder.create();
		d.setView(v);
		
		EditText inputDeparture = (EditText) v.findViewById(R.id.edit_text_departure);
    	EditText inputDestination = (EditText) v.findViewById(R.id.edit_text_destination);
    	EditText inputAirplaneType = (EditText) v.findViewById(R.id.edit_text_airplane_type);
    	if(inputDeparture != null)
    		inputDeparture.setText(flight.getDeparture());
		if(inputDestination != null)
			inputDestination.setText(flight.getDestination());
		if(inputAirplaneType != null)
			inputAirplaneType.setText(flight.getAirplaneType());
    	
		Button saveButton = (Button) v.findViewById(R.id.button_save_flight); //d.getButton(R.id.button_save_flight);
		
		if(saveButton != null)
		{
			saveButton.setOnClickListener(new View.OnClickListener()
	        {
	            @Override
	            public void onClick(View view)
	            {
	            	EditText inputDeparture = (EditText) v.findViewById(R.id.edit_text_departure);
	            	EditText inputDestination = (EditText) v.findViewById(R.id.edit_text_destination);
	            	EditText inputAirplaneType = (EditText) v.findViewById(R.id.edit_text_airplane_type);
	            	
	            	String departure = inputDeparture.getText().toString();
	            	String destination = inputDestination.getText().toString();
	            	String airplaneType = inputAirplaneType.getText().toString();
	            	
	            	FlightsDataSource dataSource = new FlightsDataSource(m_activity);
	                dataSource.open();
	        		Flight flight = dataSource.getFlight(m_fightId);
	        		
	        		if(flight != null)
	        		{
		        		if(departure.length() > 0)
		        		{
		        			flight.setDeparture(departure);
		        		}
		        		if(destination.length() > 0)
		        		{
		        			flight.setDestination(destination);
		        		}
		        		if(airplaneType.length() > 0)
		        		{
		        			flight.setAirplaneType(airplaneType);
		        		}
		        		dataSource.updateFlight(flight);
	        		}
	        		
	        		dataSource.close();
	        		
	        		m_listener.onDialogSaveClicked();
	        		d.dismiss();
	            }
	        });
		}
		
		Button deleteButton = (Button) v.findViewById(R.id.button_delete_flight);
		if(deleteButton != null)
		{
			deleteButton.setOnClickListener(new View.OnClickListener()
			{	
				@Override
				public void onClick(View v)
				{
					DialogDeleteFlight dialog = new DialogDeleteFlight();
					dialog.show(m_activity.getSupportFragmentManager(), TAG);
					
					d.dismiss();
				}
			});
		}
		
		return d;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		try
		{
			m_listener = (NoticeDialogListener)activity;
		}
		catch (Exception e)
		{
			Log.e(TAG, activity.toString() + " must implement NoticeDialogListener");
		}
	}
}
