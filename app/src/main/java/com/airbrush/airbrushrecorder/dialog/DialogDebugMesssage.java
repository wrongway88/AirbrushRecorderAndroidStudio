package com.airbrush.airbrushrecorder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class DialogDebugMesssage extends DialogFragment
{
	//private static String TAG = "DIALOG_CREATE_ACCOUNT_RESPONSE";
	
	public static void write(String message, FragmentActivity activity)
	{
		DialogDebugMesssage dialogDebug = new DialogDebugMesssage();
		Bundle bundle = new Bundle();
		bundle.putString("message", message);
		dialogDebug.setArguments(bundle);
		dialogDebug.show(activity.getSupportFragmentManager(), "DEBUG_MESSAGE");
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Debug Output");
        
        Bundle bundle = getArguments();
        String message = bundle.getString("message");
        
        builder.setMessage(message);
        
        return builder.create();
    }

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
}
