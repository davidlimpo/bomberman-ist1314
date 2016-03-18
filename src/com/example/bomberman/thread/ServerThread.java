package com.example.bomberman.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.example.bomberman.models.Bot;
import com.example.bomberman.models.Player;

import android.util.Log;
import android.util.SparseArray;

public class ServerThread extends Thread implements ServerClientsHandlerThread.CallBack, ComunicateMove {
	private static final String TAG = "ServerThread";
	public static final int SERVER_PORT = 8988;
	
	public static ServerThread thread = null;
	
	private HashMap<Integer, ServerClientsHandlerThread> clientsThreads = new HashMap<Integer, ServerClientsHandlerThread>();
	private boolean terminate = false;
	private String level = null;

	public ServerThread(String level) {
		this.level  = level;
		ServerThread.thread = this;
	}

	public int getAvailableId() {
		for(int i = 0; i < BombermanThread.players.size(); i++){
		    int key = BombermanThread.players.keyAt(i);
		    Player player = BombermanThread.players.valueAt(i);
		    if (player.isAvailable()) {
		    	player.setUnavailable();
		    	return player.getId();
		    }
		}
		
		return -1;
	}
	
	@Override
	public void run() {
		try {
            ServerSocket serverSocket = new ServerSocket(ServerThread.SERVER_PORT);
            Log.d(ServerThread.TAG, "Server: Socket opened");
            
            while (!terminate) {
            	Socket client = serverSocket.accept();
                Log.d(ServerThread.TAG, "Server: connection done");
                
                int availablePlayerId = this.getAvailableId();
                
                ServerClientsHandlerThread handlerClientThread = new ServerClientsHandlerThread(availablePlayerId, client, this.level, this);
                this.clientsThreads.put(availablePlayerId, handlerClientThread);
                
                Log.d(TAG, "SOCKET server"+ServerThread.SERVER_PORT);
    			
                handlerClientThread.start();
            }
            
            for (Map.Entry<Integer, ServerClientsHandlerThread> clientThread : this.clientsThreads.entrySet()) {
            	clientThread.getValue().terminate();
            	try {
					clientThread.getValue().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            
            serverSocket.close();
        } catch (IOException e) {
            Log.e(ServerThread.TAG, e.getMessage());
        }
	}
	
	@Override
	public void sendToOtherClients(ServerClientsHandlerThread client, String command) {
		for (Map.Entry<Integer, ServerClientsHandlerThread> clientThread : this.clientsThreads.entrySet())
			if (client != null && clientThread.getKey().equals(client.getPlayerId())) continue;
			else clientThread.getValue().sendCommand(command);
	}
	
	public void sendMoveLeft(Player player) {
		this.sendToOtherClients(null, String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('L') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
	}
	
	public void sendMoveRight(Player player) {
		this.sendToOtherClients(null, String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('R') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
	}

	public void sendMoveUp(Player player) {
		this.sendToOtherClients(null, String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('U') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
	}
	
	public void sendMoveDown(Player player) {
		this.sendToOtherClients(null, String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('D') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
	}
	
	public void sendPlaceBomb(Player player) {
		this.sendToOtherClients(null, String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('B') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
	}

	@Override
	public void sendStop(Player player) {
		this.sendToOtherClients(null, String.valueOf('P') + String.valueOf('|') + String.valueOf(player.getId()) + String.valueOf('|') + String.valueOf('S') + String.valueOf('|') + String.valueOf(player.getX()) + String.valueOf('|') + String.valueOf(player.getY()));
	}
	
	public void sendMoveLeft(Bot bot) {
		this.sendToOtherClients(null, String.valueOf('B') + String.valueOf('|') + String.valueOf(bot.getId()) + String.valueOf('|') + String.valueOf('L') + String.valueOf('|') + String.valueOf(bot.getX()) + String.valueOf('|') + String.valueOf(bot.getY()));
	}
	
	public void sendMoveRight(Bot bot) {
		this.sendToOtherClients(null, String.valueOf('B') + String.valueOf('|') + String.valueOf(bot.getId()) + String.valueOf('|') + String.valueOf('R') + String.valueOf('|') + String.valueOf(bot.getX()) + String.valueOf('|') + String.valueOf(bot.getY()));
	}

	public void sendMoveUp(Bot bot) {
		this.sendToOtherClients(null, String.valueOf('B') + String.valueOf('|') + String.valueOf(bot.getId()) + String.valueOf('|') + String.valueOf('U') + String.valueOf('|') + String.valueOf(bot.getX()) + String.valueOf('|') + String.valueOf(bot.getY()));
	}
	
	public void sendMoveDown(Bot bot) {
		this.sendToOtherClients(null, String.valueOf('B') + String.valueOf('|') + String.valueOf(bot.getId()) + String.valueOf('|') + String.valueOf('D') + String.valueOf('|') + String.valueOf(bot.getX()) + String.valueOf('|') + String.valueOf(bot.getY()));
	}

	@Override
	public void terminate() {
		this.terminate  = true;
	}
}
