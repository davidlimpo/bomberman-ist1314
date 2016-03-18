package com.example.bomberman.models;

import com.example.bomberman.thread.BombermanThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstacle extends Model implements Explosive{	
	
	private Rect[] explosionAnimation = new Rect[6];
	private int explosionFrame = 0;
	
	private boolean explode = false;
	BombermanThread drawer;
	
	public Obstacle(Bitmap bitmap, int x, int y, BombermanThread drawer){
		super(bitmap, 16, 6, 0, 0, x, y);
		
		this.drawer = drawer;
		
		int srcX = 0;
		int srcY = 0;
		
		for(int i = 0; i < 6; i++){
			this.explosionAnimation[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;
		}
	}
	
	public int Explode() {
		this.explode = true;
		
		return 0;
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(this.explode){
			if (this.explosionFrame > 4) {
				drawer.removeDrawable();
				synchronized (BombermanThread.colisionMatrix) {
					BombermanThread.colisionMatrix.get(this.getY()).set(this.getX(), new NullModel());
				}
			}
			super.src = explosionAnimation[explosionFrame++];
		}
		
		super.draw(canvas);
	}
}
