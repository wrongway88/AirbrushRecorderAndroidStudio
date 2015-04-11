package com.airbrush.airbrushrecorder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.content.DialogInterface;
import android.os.Bundle;
import com.airbrush.airbrushrecorder.R;

public class DialogDeleteFlight extends DialogFragment
{
	private static String TAG = "DIALOG_DELETE_FLIGHT";
	
	public interface NoticeDialogListener
	{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
	
	private NoticeDialogListener m_listener;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setMessage(R.string.dialog_delete_flight);
        
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   m_listener.onDialogPositiveClick(DialogDeleteFlight.this);
                   }
               });
        
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   m_listener.onDialogNegativeClick(DialogDeleteFlight.this);
                   }
               });
        
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