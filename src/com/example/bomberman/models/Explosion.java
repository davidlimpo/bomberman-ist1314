package com.example.bomberman.models;

import com.example.bomberman.thread.BombermanThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Explosion extends Model {
		
	private int explosionFrame = 0;
	
	public static int explosionDuration = 0;
	public static int explosionRange = 0;
	
	private Rect[] middle;
	private Rect[] top;
	private Rect[] bottom;
	private Rect[] left;
	private Rect[] right;
	
	private Player player;
	private int pointsObtained = 0;
	
	public Explosion(Bitmap explosionBitmap, int x, int y, Player player) {
		super(explosionBitmap, 16, 6, 0, 0, x, y);
		
		this.player = player;
		
		this.middle = new Rect[4];
		this.top = new Rect[4];
		this.bottom = new Rect[4];
		this.left = new Rect[4];
		this.right = new Rect[4];

		int srcX = 0;
		int srcY = 9*height;
		
		for(int i=0; i<4; i++){
			this.middle[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;
		}
		
		srcX = 0;
		srcY += height;
		
		for(int i=0; i<4; i++){
			this.top[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;
		}
		
		srcX = 0;
		srcY += height;
		
		for(int i=0; i<4; i++){
			this.bottom[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;
		}
		
		srcX = 0;
		srcY += height;
		
		for(int i=0; i<4; i++){
			this.left[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;
		}
		
		srcX = 0;
		srcY += height;
		
		for(int i=0; i<4; i++){
			this.right[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		
		if (explosionFrame == 1) {
			Drawable middleExplosion, leftExplosion, rightExplosion, topExplosion, bottomExplosion;
			synchronized (BombermanThread.colisionMatrix) {
				middleExplosion = BombermanThread.colisionMatrix.get(this.getY()).get(this.getX());
				leftExplosion = BombermanThread.colisionMatrix.get(this.getY() - 1).get(this.getX());
				rightExplosion = BombermanThread.colisionMatrix.get(this.getY() + 1).get(this.getX());
				topExplosion = BombermanThread.colisionMatrix.get(this.getY()).get(this.getX() - 1);
				bottomExplosion = BombermanThread.colisionMatrix.get(this.getY()).get(this.getX() + 1);
			}
			
			if (middleExplosion instanceof Explosive)
				this.pointsObtained += ((Explosive) middleExplosion).Explode();
			if (leftExplosion instanceof Explosive)
				this.pointsObtained += ((Explosive) leftExplosion).Explode();
			if (rightExplosion instanceof Explosive)
				this.pointsObtained += ((Explosive) rightExplosion).Explode();
			if (topExplosion instanceof Explosive)
				this.pointsObtained += ((Explosive) topExplosion).Explode();
			if (bottomExplosion instanceof Explosive)
				this.pointsObtained += ((Explosive) bottomExplosion).Explode();
		} else if(explosionFrame > 3) {
			this.player.bombExploded(this.pointsObtained);
			return;
		}
		
		drawMiddle(canvas);
		drawTop(canvas);
		drawBottom(canvas);
		drawLeft(canvas);
		drawRight(canvas);
		
		explosionFrame++;
	}
	
	public void drawMiddle(Canvas canvas){
		src = middle[explosionFrame];

		int x = this.absoluteX - camera.getCameraX();
		int y = this.absoluteY - camera.getCameraY();
		
		canvas.drawBitmap(this.bitmap, src, new Rect(x, y, x + this.width, y + this.height), null);
	}
	
	public void drawTop(Canvas canvas){
		src = top[explosionFrame];
		
		int x = this.absoluteX - camera.getCameraX();
		int y = this.absoluteY - height - camera.getCameraY();
		
		canvas.drawBitmap(this.bitmap, src, new Rect(x, y, x + this.width, y + this.height), null);
	}
	
	public void drawBottom(Canvas canvas){
		src = bottom[explosionFrame];
		
		int x = this.absoluteX - camera.getCameraX();
		int y = this.absoluteY + height - camera.getCameraY();
		
		canvas.drawBitmap(this.bitmap, src, new Rect(x, y, x + this.width, y + this.height), null);	
		
	}
	
	public void drawLeft(Canvas canvas){
		src = left[explosionFrame];
		
		int x = this.absoluteX - width - camera.getCameraX();
		int y = this.absoluteY - camera.getCameraY();
		
		canvas.drawBitmap(this.bitmap, src, new Rect(x, y, x + this.width, y + this.height), null);	
	}
	
	public void drawRight(Canvas canvas){
		src = right[explosionFrame];
		
		int x = this.absoluteX + width - camera.getCameraX();
		int y = this.absoluteY - camera.getCameraY();
		
		canvas.drawBitmap(this.bitmap, src, new Rect(x, y, x + this.width, y + this.height), null);	
	}
}
