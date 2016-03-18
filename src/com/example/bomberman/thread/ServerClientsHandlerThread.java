package com.example.bomberman.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import com.example.bomberman.models.Player;

import android.util.Log;
import android.util.SparseArray;

public class ServerClientsHandlerThread extends Thread {
	private static final String TAG = "ServerClientsHandlerThread";
	private Socket clientSocket = null;
	private BufferedReader bufferedReader = null;
	private PrintWriter printWriter= null;
	private CallBack callBack = null;
	private int id = 0;
	private Player player = null;
	private boolean terminate = false;
	
	public interface CallBack {
		void sendToOtherClients(ServerClientsHandlerThread client, String command);
	}
	
	public ServerClientsHandlerThread(int clientId, Socket clientSocket, String level, CallBack callBack) {
		this.callBack = callBack;
		this.clientSocket = clientSocket;
		this.id = clientId;
		this.player = BombermanThread.players.get(this.id);
		
		Log.d(TAG, "IDIER: " + this.id);
		
		try {
			this.clientSocket.setKeepAlive(true);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.printWriter = new PrintWriter(clientSocket.getOutputStream());
			
			this.printWriter.println(this.id);
			this.printWriter.println(level);
			this.printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		while (!this.terminate) {
			String commandReceived;
			try {
				commandReceived = bufferedReader.readLine();
				Log.d(TAG, "Command Received: " + commandReceived);
				
				String[] info = commandReceived.split("\\|");
				String action = info[2];
				String absoluteX = info[3];
				String absoluteY = info[4];
				
				this.player.executeAction(action, absoluteX, absoluteY);
				
				this.callBack.sendToOtherClients(this, commandReceived);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		try {
			this.bufferedReader.close();
			this.printWriter.close();
			this.clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendCommand(String command) {
		this.printWriter.println(command);
		this.printWriter.flush();
	}
	
	public int getPlayerId() {
		return this.id ;
	}

	public void terminate() {
		this.terminate  = true;
	}
}
