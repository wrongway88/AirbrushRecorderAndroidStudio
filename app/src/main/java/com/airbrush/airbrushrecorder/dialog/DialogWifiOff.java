package com.airbrush.airbrushrecorder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.os.Bundle;
import android.provider.Settings;

import com.airbrush.airbrushrecorder.R;

public class DialogWifiOff extends DialogFragment
{
	private static String TAG = "DIALOG_CREATE_ACCOUNT_RESPONSE";
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.d(TAG, "create wifi off dialog");
		
		final Activity activity = getActivity();
		
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_wifi_off);
        builder.setMessage(R.string.dialog_wifi_off_message);
        builder.setPositiveButton("OK", 
        			new DialogInterface.OnClickListener()
        			{
		        		public void onClick(DialogInterface d, int id)
		        		{
		        			activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		        			d.dismiss();
		        		}
        			});
        
        builder.setNegativeButton("Cancel", 
	        		new DialogInterface.OnClickListener()
	        		{
	        			public void onClick(DialogInterface d, int id)
	        			{
	        				d.cancel();
	        			}
	        		});
        
        return builder.create();
    }

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
}