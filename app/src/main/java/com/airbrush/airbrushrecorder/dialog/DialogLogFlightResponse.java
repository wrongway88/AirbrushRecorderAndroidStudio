package com.airbrush.airbrushrecorder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import com.airbrush.airbrushrecorder.R;

public class DialogLogFlightResponse extends DialogFragment
{
	//private static String TAG = "DIALOG_LOG_FLIGHT_RESPONSE";
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_log_flight_response);
        
        String message = getActivity().getString(R.string.dialog_log_flight_response_no_waypoints);
        message += "\n" + getString(R.string.dialog_log_flight_response_move_around);
        
        builder.setMessage(message);

        builder.setNeutralButton("OK",
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