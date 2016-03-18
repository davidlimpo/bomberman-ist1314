package com.example.bomberman.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import android.util.Log;
import android.util.SparseArray;

import com.example.bomberman.MPActivity;
import com.example.bomberman.models.Bot;
import com.example.bomberman.models.Model;
import com.example.bomberman.models.Player;

public class ClientThread extends Thread implements ComunicateMove {
	private static final int SOCKET_TIMEOUT = 5000;
	private static final String TAG = "ClientThread";
	
	public static ClientThread thread = null;
	
	private Socket socket = null;
	private BufferedReader bufferedReader = null;
	private PrintWriter printWriter= null;
	private String host = null;
	private boolean terminate = false;
	private MPActivity mpActivity = null;
	
	public ClientThread(String host, MPActivity mpActivity) {
		this.host = host;
		this.mpActivity  = mpActivity;
		ClientThread.thread = this;
	}
	
	@Override
	public void run() {
		this.socket = new Socket();
		
		try {
			this.socket.setKeepAlive(true);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			this.socket.bind(null);
			
			Log.d(TAG, "SOCKET server" + ServerThread.SERVER_PORT+"");
			Log.d(TAG, "HOST server" + host);
			
			this.socket.connect((new InetSocketAddress(host, ServerThread.SERVER_PORT)), ClientThread.SOCKET_TIMEOUT);
			
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.printWriter = new PrintWriter(socket.getOutputStream());
			
			int id = Integer.parseInt(this.bufferedReader.readLine());
			this.mpActivity.startMultiplayerGame(this.host, false, this.bufferedReader.readLine());
			
			synchronized (MPActivity.class) {
				try {
					MPActivity.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Log.d(TAG, "ID Received: " + id);
			Log.d(TAG, "Lista antes: " + BombermanThread.players.toString());
			Player myPlayer = BombermanThread.myPlayer;
			myPlayer.setCentered(false);
			BombermanThread.players.put(myPlayer.getId(), myPlayer);
			BombermanThread.myPlayer = BombermanThread.players.get(id);
			BombermanThread.players.remove(id);
			
			Model.camera = BombermanThread.myPlayer;
			BombermanThread.myPlayer.setCentered(true);
			Log.d(TAG, "Lista depois: " + BombermanThread.players.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (!this.terminate) {
			String commandReceived;
			try {
				commandReceived = bufferedReader.readLine();
				Log.d(TAG, "Command Received: " + commandReceived);
				
				String[] info = commandReceived.split("\\|");
				String objectType = info[0];
				String playerId = info[1];
				String action = info[2];
				String absoluteX = info[3];
				String absoluteY = info[4];
				
				if (objectType.equals("P")) {
					Player player = BombermanThread.players.get(Integer.parseInt(playerId));
					player.executeAction(action, absoluteX, absoluteY);
				} else {
					Bot bot = BombermanThread.bots.get(Integer.parseInt(playerId));
					bot.executeAction(action, absoluteX, absoluteY);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		try {
			this.bufferedReader.close();
			this.printWriter.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMoveLeft(Player player) {
		this.printWriter.println(String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('L') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
		this.printWriter.flush();
	}
	
	public void sendMoveRight(Player player) {
		this.printWriter.println(String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('R') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
		this.printWriter.flush();
	}

	public void sendMoveUp(Player player) {
		this.printWriter.println(String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('U') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
		this.printWriter.flush();
	}
	
	public void sendMoveDown(Player player) {
		this.printWriter.println(String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('D') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
		this.printWriter.flush();
	}
	
	public void sendPlaceBomb(Player player) {
		this.printWriter.println(String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('B') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
		this.printWriter.flush();
	}

	@Override
	public void sendStop(Player player) {
		this.printWriter.println(String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('S') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
		this.printWriter.flush();
	}

	@Override
	public void terminate() {
		this.terminate  = true;
	}
}
