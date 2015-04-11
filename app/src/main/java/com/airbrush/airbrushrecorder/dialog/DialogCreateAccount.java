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

public class DialogCreateAccount extends DialogFragment
{
	public interface NoticeDialogListener
	{
        public void onSuccess(DialogFragment dialog, String email, String password);
    }
	
	private NoticeDialogListener m_listener;
	
	private static String TAG = "DIALOG_CREATE_ACCOUNT";
	
	//private NoticeDialogListener m_listener;
	private FragmentActivity m_activity = null;
	
	public DialogCreateAccount(FragmentActivity activity)
	{
		m_activity = activity;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		this.setRetainInstance(true);
		DataStorage.getInstance().setBoolean(getString(R.string.data_view_create_account), true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View v = inflater.inflate(R.layout.dialog_create_account, null);
		
		builder.setTitle(R.string.dialog_create_account);
		builder.setMessage(R.string.dialog_enter_account_data);
		
		builder.setView(v)
			.setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {}})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {
				DataStorage.getInstance().setBoolean(getString(R.string.data_view_create_account), false);
				DataStorage.getInstance().setString(getString(R.string.data_email), "");
				DataStorage.getInstance().setString(getString(R.string.data_name), "");
				DataStorage.getInstance().setString(getString(R.string.data_surname), "");
			}});
		
		final AlertDialog d = builder.create();
		
		d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog)
            {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                    	EditText inputName = (EditText) v.findViewById(R.id.edit_text_name);
                    	EditText inputSurname = (EditText) v.findViewById(R.id.edit_text_surname);
                    	EditText inputMail = (EditText) v.findViewById(R.id.edit_text_email);
                    	EditText inputPassword = (EditText) v.findViewById(R.id.edit_text_password);
                    	
                    	String name = inputName.getText().toString();
                    	String surname = inputSurname.getText().toString();
                    	String mail = inputMail.getText().toString();
                    	String password = inputPassword.getText().toString();
                    	
                    	//validate stuff
                    	Boolean invalid = false;
                    	
                    	if(name.length() <= 0)
                    	{
                    		inputName.setError(getString(R.string.edit_text_name_error_missing));
                    		invalid = true;
                    	}
                    	
                    	if(surname.length() <= 0)
                    	{
                    		inputSurname.setError(getString(R.string.edit_text_surname_error_missing));
                    		invalid = true;
                    	}
                    	
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
                    	
                    	WebInterface webInterface = new WebInterface(m_activity);
                    	int response = webInterface.createAccount(name, surname, mail, password);
                    	
                    	Boolean success = false;
                    	if(response >= 200 && response < 300)
                    	{
                    		success = true;
                    	}
                    	
                    	String info = "";
                    	
                    	if(response == 409)
                    	{
                    		info = m_activity.getString(R.string.dialog_create_account_response_email_taken);
                    		inputMail.setError(getString(R.string.edit_text_mail_error_taken));
                    		inputMail.requestFocus();
                    	}
                    	
                    	DialogCreateAccountResponse dialogResponse = new DialogCreateAccountResponse();
                		Bundle bundle = new Bundle();
                		bundle.putBoolean("success", success);
                		bundle.putString("info", info);
                		dialogResponse.setArguments(bundle);
                		dialogResponse.show(m_activity.getSupportFragmentManager(), TAG);
                		
                		if(success)
                		{
                			m_listener.onSuccess(DialogCreateAccount.this, mail, password);
                			DataStorage.getInstance().setBoolean(getString(R.string.data_view_create_account), false);
                			DataStorage.getInstance().setString(getString(R.string.data_email), "");
            				DataStorage.getInstance().setString(getString(R.string.data_name), "");
            				DataStorage.getInstance().setString(getString(R.string.data_surname), "");
                			d.dismiss();
                		}
                		else
                		{
                			return;
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
