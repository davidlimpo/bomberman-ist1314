package com.example.bomberman.models;

import com.example.bomberman.thread.BombermanThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bomb extends Model {
	
	private Explosion explosion = null;
	
	public static float explosionTimeout = 30;
	
	private int timeToExplode = 30;
	private int currentFrame = 0;
	
	private Rect[] animation;
	
	public Bomb(Bitmap bombBitmap, Bitmap explosionBitmap, int x, int y, Player player){
		super(bombBitmap, 4, 4, 0, 0, x, y);
		
		explosion = new Explosion(explosionBitmap, x, y, player);
		
		this.animation = new Rect[4];
		
		int srcX = 0;
		int srcY = 0;
		
		for(int i=0; i<3; i++){
			this.animation[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		
		if(timeToExplode > 0){
			super.draw(canvas);
	
			this.src = animation[currentFrame=((++currentFrame)%2)];
			timeToExplode--;
		} else explosion.draw(canvas);
	}
}
