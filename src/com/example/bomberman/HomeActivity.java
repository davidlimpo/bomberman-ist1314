package com.example.bomberman;

import com.example.bomberman.dialogs.UserNameDialog;
import com.example.bomberman.dialogs.UserNameDialog.UserNameDialogListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity implements UserNameDialogListener {
	
	private static final int MENU_CHANGEUSERNAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		SharedPreferences settings = getSharedPreferences(Constant.PREFS_NAME, 0);

		String username = settings.getString(Constant.USERNAME_FIELD, Constant.EMPTY_USERNAME);
		
		if(username.equals(Constant.EMPTY_USERNAME)){
			UserNameDialog dialog = new UserNameDialog();
			dialog.show(getFragmentManager(), "AskUsername");
		}
		
		((Button) this.findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, MPActivity.class);
		        startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, MENU_CHANGEUSERNAME, 0, R.string.menu_changeusername);
		return true;
	}
		
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CHANGEUSERNAME:
            	UserNameDialog dialog = new UserNameDialog();
    			dialog.show(getFragmentManager(), "AskUsername");
                return true;
        }

        return false;
    }


	@Override
	public void onUserNameConfirmed(String username) {
		SharedPreferences settings = getSharedPreferences(Constant.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(Constant.USERNAME_FIELD, username);
		editor.commit();
	}
	
    public void startSinglePlayer(View v) {
        Intent intent = new Intent(HomeActivity.this, SPActivity.class);
        startActivity(intent);
    }
}
