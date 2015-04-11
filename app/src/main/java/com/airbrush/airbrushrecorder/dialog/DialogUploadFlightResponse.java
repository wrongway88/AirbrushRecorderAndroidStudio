package com.airbrush.airbrushrecorder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.airbrush.airbrushrecorder.R;

public class DialogUploadFlightResponse extends DialogFragment
{
	private static String TAG = "DIALOG_CREATE_ACCOUNT_RESPONSE";
	
	public interface NoticeDialogListener
	{
        public void onDialogDeleteClick(DialogFragment dialog);
    }
	
	private NoticeDialogListener m_listener;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_upload);
        
        Bundle bundle = getArguments();
        Boolean success = bundle.getBoolean("success");
        Boolean loginSuccess = true;
        loginSuccess = bundle.getBoolean("loginSuccess");
        String additionalMessage = "";
        additionalMessage = bundle.getString("message");
        
        String message = "";
        
        if(success)
        {
        	message += getString(R.string.dialog_upload_response_positive) + "\n" + getString(R.string.dialog_upload_response_keep_local);
        	
        	builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                	m_listener.onDialogDeleteClick(DialogUploadFlightResponse.this);
                }
            });
        	
        	builder.setNegativeButton(R.string.dialog_keep, new DialogInterface.OnClickListener()
        	{
				public void onClick(DialogInterface dialog, int id) {}
			});
        }
        else
        {
        	message += getString(R.string.dialog_upload_response_negative);
        	
        	if(loginSuccess == false)
        	{
        		message += "\n" + getString(R.string.dialog_upload_response_check_login_failed) + " " + getString(R.string.dialog_upload_response_check_login_data);
        	}
        }
        
        if(additionalMessage.length() > 0)
        {
        	message += "\n" + additionalMessage;
        }
        
        builder.setMessage(message);
        
        return builder.create();
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
