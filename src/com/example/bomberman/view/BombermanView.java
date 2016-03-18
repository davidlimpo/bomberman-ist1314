package com.example.bomberman.view;

import com.example.bomberman.BombermanActivity;
import com.example.bomberman.thread.BombermanThread;
import com.example.bomberman.thread.ClientThread;
import com.example.bomberman.thread.ComunicateMove;
import com.example.bomberman.thread.ServerThread;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class BombermanView extends SurfaceView implements SurfaceHolder.Callback {

	private BombermanThread thread;
	public static boolean isMultiplayer = false;
	public static boolean isServer = false;
	
	private static final String TAG = "BombermanView";
	
	public BombermanView(Context context, String levelDescription, String levelMap, String serverAddress, boolean isMultiplayer, boolean isServer, BombermanActivity bombermanActivity) {
		super(context);
		getHolder().addCallback(this);
		
		thread = new BombermanThread(getHolder(), context, levelDescription, levelMap, bombermanActivity);
		BombermanView.isMultiplayer = isMultiplayer;
		BombermanView.isServer = isServer;
		
		setFocusable(true);
	}
	
	public void downKey(){
		thread.downKeyEvent();
	}
	
	public void upKey(){
		thread.upKeyEvent();
	}
	
	public void leftKey(){
		thread.leftKeyEvent();
	}

	public void rightKey(){
		thread.rightKeyEvent();
	}
	
	public void bombKey(){
		thread.bombKeyEvent();
	}
	
	public void quitKey(){
		thread.setRunning(false);
		
		try {
			thread.join();
		} catch (InterruptedException e) {}
	}
	
	public void pauseKey(boolean running){
		thread.setPause(running);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	public void noneKey() {
		thread.noneKeyEvent();
	}
}
