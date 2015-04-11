package com.airbrush.airbrushrecorder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import com.airbrush.airbrushrecorder.R;

public class DialogEnterLoginDataResponse extends DialogFragment
{
	//private static String TAG = "DIALOG_CREATE_ACCOUNT_RESPONSE";
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_set_login_data);
        
        Bundle bundle = getArguments();
        Boolean success = bundle.getBoolean("success");
        
        if(success)
        {
        	builder.setMessage(R.string.dialog_enter_login_data_response_positive);
        }
        else
        {
        	builder.setMessage(R.string.dialog_enter_login_data_response_negative);
        }
        
        return builder.create();
    }

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
}