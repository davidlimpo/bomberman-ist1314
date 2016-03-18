package com.example.bomberman.models;

import java.util.Random;

import com.example.bomberman.thread.BombermanThread;
import com.example.bomberman.thread.ServerThread;
import com.example.bomberman.view.BombermanView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bot extends Player {
	
	Random randomGenerator = new Random();
	
	public static float robotSpeed = 0;
	public static int pointsPerRobotKilled = 0;
	
	public Bot(int id, Bitmap bitmap, int x, int y, BombermanThread drawer) {
		super(id, bitmap, 4, 24, 0, 1, x, y, drawer);
	}
	
	@Override
	public Model collider() {
		Model possibleColider = super.collider();
		
		if (possibleColider instanceof Player)
			((Player) possibleColider).Explode();
		
		return (Model) possibleColider;
	}
	
	@Override
	public int Explode() {
		this.killed = true;
		
		synchronized (BombermanThread.colisionMatrix) {
			BombermanThread.colisionMatrix.get(this.getY()).set(this.getX(), new NullModel());
		}
		
		return Bot.pointsPerRobotKilled;
	}
	
	public void randomlyMove() {
		int randomInt = this.randomGenerator.nextInt(4);
		
		switch (randomInt) {
			case 0:
				super.moveDown();
				if (BombermanView.isMultiplayer && BombermanView.isServer) ServerThread.thread.sendMoveDown(this);
				break;
			case 1:
				super.moveLeft();
				if (BombermanView.isMultiplayer && BombermanView.isServer) ServerThread.thread.sendMoveLeft(this);
				break;
			case 2:
				super.moveRight();
				if (BombermanView.isMultiplayer && BombermanView.isServer) ServerThread.thread.sendMoveRight(this);
				break;
			case 3:
				super.moveUp();
				if (BombermanView.isMultiplayer && BombermanView.isServer) ServerThread.thread.sendMoveUp(this);
				break;
		}
	}
}
