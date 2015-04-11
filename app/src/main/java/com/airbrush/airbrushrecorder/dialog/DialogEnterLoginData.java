package com.airbrush.airbrushrecorder.dialog;

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
import android.content.DialogInterface;
import android.os.Bundle;

import com.airbrush.airbrushrecorder.DataStorage;
import com.airbrush.airbrushrecorder.WebInterface;
import com.airbrush.airbrushrecorder.R;

import com.airbrush.airbrushrecorder.LoginHelper;

public class DialogEnterLoginData extends DialogFragment
{
	private static String TAG = "DIALOG_ENTER_LOGIN_DATA";
	
	private FragmentActivity m_activity = null;
	
	public interface NoticeDialogListener
	{
		public void onAccountDataSet();
    }
	
	private NoticeDialogListener m_listener;
	
	public DialogEnterLoginData(FragmentActivity activity)
	{
		m_activity = activity;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		this.setRetainInstance(true);
		DataStorage.getInstance().setBoolean(getString(R.string.data_view_change_account), true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View v = inflater.inflate(R.layout.dialog_enter_login_data, null);
		
		builder.setTitle(R.string.dialog_set_login_data);
		builder.setMessage(R.string.dialog_set_login_data_message);
		
		builder.setView(v)
			.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {}})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {
				DataStorage.getInstance().setBoolean(getString(R.string.data_view_change_account), false);
				DataStorage.getInstance().setString(getString(R.string.data_email), "");
			}});
		
		final AlertDialog d = builder.create();
		
		d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog)
            {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                    	EditText inputMail = (EditText) v.findViewById(R.id.edit_text_email);
                    	EditText inputPassword = (EditText) v.findViewById(R.id.edit_text_password);
                    	
                    	String mail = inputMail.getText().toString();
                    	String password = inputPassword.getText().toString();
                    	
                    	//validate stuff
                    	Boolean invalid = false;
                    	
                    	if(mail.length() <= 0)
                    	{
                    		inputMail.setError(getString(R.string.edit_text_mail_error_missing));
                    		invalid = true;
                    	}
                    	else if(WebInterface.validateMailAddress(mail) == false)
                    	{
                    		inputMail.setError(getString(R.string.edit_text_mail_error_invalid));
                    		invalid = true;
                    	}
                    	
                    	if(password.length() < 3)
                    	{
                    		inputPassword.setError(getString(R.string.edit_text_password_error_short));
                    		invalid = true;
                    	}
                    	
                    	if(invalid)
                    	{
                    		return;
                    	}
                    	
                    	password = WebInterface.saltPassword(password);
                    	password = WebInterface.toHash(password);
                    	
                    	LoginHelper loginHelper = new LoginHelper();
                    	if(loginHelper.setLoginData(m_activity, mail, password) == false)
                    	{
                    		DialogEnterLoginDataResponse dialogResponse = new DialogEnterLoginDataResponse();
                			Bundle bundle = new Bundle();
                			bundle.putBoolean("success", false);
                			dialogResponse.setArguments(bundle);
                			dialogResponse.show(m_activity.getSupportFragmentManager(), TAG);
                			
                			return;
                    	}
                    	else
                    	{
                    		DataStorage.getInstance().setBoolean(getString(R.string.data_view_change_account), false);
                    		DataStorage.getInstance().setString(getString(R.string.data_email), "");
                    		m_listener.onAccountDataSet();
                    		d.dismiss();
                    	}
                    }
                });
            }
		});
		
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
