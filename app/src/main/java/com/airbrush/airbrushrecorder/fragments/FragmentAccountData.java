package com.airbrush.airbrushrecorder.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.airbrush.airbrushrecorder.Flight;
import com.airbrush.airbrushrecorder.LoginHelper;
import com.airbrush.airbrushrecorder.WebInterface;
import com.airbrush.airbrushrecorder.data.FlightsDataSource;
import com.airbrush.airbrushrecorder.dialog.DialogCreateAccount;
import com.airbrush.airbrushrecorder.dialog.DialogDeleteFlight;
import com.airbrush.airbrushrecorder.dialog.DialogEnterLoginData;
import com.airbrush.airbrushrecorder.dialog.DialogUploadFlightResponse;
import com.airbrush.airbrushrecorder.dialog.DialogWifiOff;
import com.airbrush.airbrushrecorder.fragments.FragmentRecorder.OnToggleRecordingListener;
import com.airbrush.airbrushrecorder.R;

public class FragmentAccountData extends Fragment
{
	private static String TAG = "ACCOUNT_DATA";
	
	private OnAccountDataListener m_listener;
	
	public interface OnAccountDataListener //well, thats a stupid name...
	{
		
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		/*
		if(activity instanceof OnAccountDataListener)
		{
			m_listener = (OnAccountDataListener) activity;
		}
		else
		{
			throw new ClassCastException(activity.toString() + "must implement OnFlightBrowserListener");
		}
		*/
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		return inflater.inflate(R.layout.fragment_account_data, container, false);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		displayAccountData();
	}
	
	public void displayAccountData()
	{
		String name = "";
		
		FlightsDataSource dataSource = new FlightsDataSource(this.getActivity());
		dataSource.open();
		name = dataSource.getUserName();
		dataSource.close();
		
		displayAccountData(name);
	}
	
	private void displayAccountData(String name)
	{
		TextView textView = (TextView)getView().findViewById(R.id.textview_account_data);
		
		String message = getString(R.string.textView_account_data_title);
		
		if(name.length() <= 0)
		{
			message += "\n" + getString(R.string.textView_account_data_default);
		}
		else
		{
			message += "\n" + name;
		}
		
		textView.setText(message);
	}
	
	public void createAccount(View view)
	{
		viewCreateAccount();
	}
	
	public void switchAccount(View view)
	{
		viewSwitchAccount();
	}
	
	public void viewSwitchAccount()
	{
		DialogEnterLoginData dialog = new DialogEnterLoginData(this.getActivity());
		dialog.show(this.getActivity().getSupportFragmentManager(), TAG);
	}
	
	public void viewCreateAccount()
	{
		if(WebInterface.wifiAvailable(this.getActivity()))
		{
			DialogCreateAccount createAccountDialog = new DialogCreateAccount(this.getActivity());
			createAccountDialog.show(this.getActivity().getSupportFragmentManager(), TAG);
		}
		else
		{
			DialogWifiOff dialog = new DialogWifiOff();
			dialog.show(this.getActivity().getSupportFragmentManager(), TAG);
		}
	}
}