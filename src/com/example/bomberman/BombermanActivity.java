package com.example.bomberman;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.bomberman.thread.BombermanThread;
import com.example.bomberman.view.BombermanView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BombermanActivity extends Activity {
	
	private TextView usernameTextView;
	private TextView timerTextView;
	private BombermanView bombermanView;
	
	private boolean running = false;
	
	private CountDownTimer timer;
	public static int timeLeft = 180; //in seconds
	private boolean isMultiplayer = false;
	private boolean isServer = false;
	private String level = null;
	
	private TextView playerScoreView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_level_one);
		
		Intent intent = getIntent();
		this.isMultiplayer = intent.getBooleanExtra(Constant.IS_MULTIPLAYER, false);
		this.isServer = intent.getBooleanExtra(Constant.IS_SERVER, false);
		this.level = intent.getStringExtra(Constant.LEVEL_PATH);
		String serverAddress = intent.getStringExtra(Constant.SERVER_ADDRESS);
		
		
		bombermanView = new BombermanView(this, loadFile(this.level, "dsc"), loadFile(this.level, "map"), serverAddress, isMultiplayer, isServer, this);
		((FrameLayout) findViewById(R.id.frame1)).addView(bombermanView);
		Button down = (Button) findViewById(R.id.button5);
		Button up = (Button) findViewById(R.id.button4);
		Button left = (Button) findViewById(R.id.button3);
		Button right = (Button) findViewById(R.id.button6);
		Button bomb = (Button) findViewById(R.id.button7);
		Button quit = (Button) findViewById(R.id.button2);
		Button pause = (Button) findViewById(R.id.hostGame);
		this.playerScoreView = (TextView) findViewById(R.id.textView2);
		
		SharedPreferences settings = getSharedPreferences(Constant.PREFS_NAME, 0);
		String username = settings.getString(Constant.USERNAME_FIELD, Constant.EMPTY_USERNAME);
		
		usernameTextView = (TextView) findViewById(R.id.textView1);
		usernameTextView.setText(username);
		
		timerTextView = (TextView) findViewById(R.id.textView3);
		createTimer();

		down.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch ( event.getAction() ) {
				    case MotionEvent.ACTION_DOWN:
				    	bombermanView.downKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendMoveDown(BombermanThread.myPlayer);
				    	break;
				    case MotionEvent.ACTION_UP:
				    	bombermanView.noneKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendStop(BombermanThread.myPlayer);
				    	break;
			    }
				
			    return true;
			}
		});
		
		up.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch ( event.getAction() ) {
				    case MotionEvent.ACTION_DOWN:
				    	bombermanView.upKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendMoveUp(BombermanThread.myPlayer);
				    	break;
				    case MotionEvent.ACTION_UP:
				    	bombermanView.noneKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendStop(BombermanThread.myPlayer);
				    	break;
			    }
				
			    return true;
			}
		});
		
		left.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch ( event.getAction() ) {
				    case MotionEvent.ACTION_DOWN:
				    	bombermanView.leftKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendMoveLeft(BombermanThread.myPlayer);
				    	break;
				    case MotionEvent.ACTION_UP:
				    	bombermanView.noneKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendStop(BombermanThread.myPlayer);
				    	break;
			    }
				
			    return true;
			}
		});
		
		right.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch ( event.getAction() ) {
				    case MotionEvent.ACTION_DOWN:
				    	bombermanView.rightKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendMoveRight(BombermanThread.myPlayer);
				    	break;
				    case MotionEvent.ACTION_UP:
				    	bombermanView.noneKey();
				    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendStop(BombermanThread.myPlayer);
				    	break;
			    }
				
			    return true;
			}
		});
		
		bomb.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch ( event.getAction() ) {
			    case MotionEvent.ACTION_DOWN:
			    	bombermanView.bombKey();
			    	if (BombermanActivity.this.isMultiplayer) MPActivity.multiplayerThread.sendPlaceBomb(BombermanThread.myPlayer);
			    	break;
			    case MotionEvent.ACTION_UP:
			    	break;
				}
				
				return false;
			}
		});
		
		quit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bombermanView.quitKey();
				finish();
			}
		});
		
		pause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				running = !running;
				timer.cancel();
				bombermanView.pauseKey(running);
				
				if(running){
					Toast.makeText(getApplicationContext(), "Continued",
									Toast.LENGTH_SHORT).show();
					createTimer();
					
				} else
					Toast.makeText(getApplicationContext(), "Paused",
									Toast.LENGTH_SHORT).show();
			}
		});
	}
	

	@Override
	public void onPause(){
		bombermanView.quitKey();
		finish();
		super.onPause();
	}
	
	@Override
	public void onStop(){
		bombermanView.quitKey();
		finish();
		super.onStop();
	}
	
	
	@Override
	public void onBackPressed() {
		bombermanView.quitKey();
		finish();
		super.onBackPressed();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.level_one, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	    
    public String loadFile(String level, String file){
    	
    	AssetManager assetManager = getAssets();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		
		try{
	    	InputStream is = assetManager.open("levels/" + level + "/" + file);
			
			i = is.read();
			while(i != -1){
				byteArrayOutputStream.write(i);
				i = is.read();
			}
			is.close();
			
		} catch (IOException e){
			e.printStackTrace();
		}
		
		return byteArrayOutputStream.toString();
    }
    
    public void createTimer(){
	    timer = new CountDownTimer(timeLeft*1000, 1000) {

	        public void onTick(long millisUntilFinished) {
	        	timeLeft--;
	        	timerTextView.setText(millisUntilFinished / 1000 + " secs");
	        }

	        public void onFinish() {
	        	timerTextView.setText("Time's Up!");
	        	Toast.makeText(BombermanActivity.this, "Time's Up!, you have " + BombermanThread.myPlayer.getScore() + " points.", Toast.LENGTH_LONG).show();
	        	BombermanActivity.this.onBackPressed();
	        }
	     }.start();
    }
    
    public void updatePlayerScore(int newScore) {
    	this.playerScoreView.setText(String.valueOf(newScore));
    }
 }
