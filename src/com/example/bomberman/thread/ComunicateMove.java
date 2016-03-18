package com.example.bomberman.thread;

import com.example.bomberman.models.Player;

public interface ComunicateMove {
	void sendMoveLeft(Player player);
	void sendMoveRight(Player player);
	void sendMoveUp(Player player);
	void sendMoveDown(Player player);
	void sendPlaceBomb(Player player);
	void sendStop(Player myPlayer);
	
	void terminate();
}
