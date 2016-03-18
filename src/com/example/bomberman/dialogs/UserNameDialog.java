package com.example.bomberman.dialogs;

import com.example.bomberman.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class UserNameDialog extends DialogFragment {
	
	public interface UserNameDialogListener {
        public void onUserNameConfirmed(String username);
    }
	
	private UserNameDialogListener mListener = null;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	final EditText input = new EditText(getActivity());
    	input.setInputType(InputType.TYPE_CLASS_TEXT);
    	input.setText("Username");
    	input.setTextColor(color.Pink);
    	input.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				input.setText("");
			}
		});

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Insert Username")
        	   .setView(input)
               .setPositiveButton("Confirm" , new DialogInterface.OnClickListener() {
            	   
                   public void onClick(DialogInterface dialog, int id) {
                	   	mListener.onUserNameConfirmed(input.getText().toString());
                   }
               });

        return builder.create();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (UserNameDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}