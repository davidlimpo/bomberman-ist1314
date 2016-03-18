package com.example.bomberman.models;

import com.example.bomberman.thread.BombermanThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class Model implements Drawable {
	public static Camera camera = null;
	public static int squareSize = 0;
	
	public static int canvasCenterX = 0;
	public static int canvasCenterY = 0;
	
	protected int absoluteX = 0;
	protected int absoluteY = 0;
	
	protected int width = 0;
	protected int height = 0;
	
	protected Rect src = null;
	
	protected Bitmap bitmap = null;
	
	protected boolean killed = false;
	
	public Model(Bitmap bitmap, int bmpRows, int bmpCols, int bitmapX, int bitmapY, int x, int y) {
		this.absoluteX = x * Model.squareSize;
		this.absoluteY = y * Model.squareSize;
		
		this.bitmap = bitmap;
		this.width = bitmap.getWidth() / bmpCols;
		this.height = bitmap.getHeight() / bmpRows;
		
		if (this.width != Model.squareSize || this.height != Model.squareSize) {
			this.width = Model.squareSize;
			this.height = Model.squareSize;
			
			this.bitmap = Bitmap.createScaledBitmap(this.bitmap, Model.squareSize * bmpCols, Model.squareSize * bmpRows, false);
		}
				
		
		int srcX = bitmapX * this.width;
		int srcY = bitmapY * this.height;
		
		this.src = new Rect(srcX, srcY, srcX + this.width, srcY + this.height);		
	}
	
	public void draw(Canvas canvas) {
		int x = this.absoluteX - camera.getCameraX();
		int y = this.absoluteY - camera.getCameraY();
		
		canvas.drawBitmap(this.bitmap, this.src, new Rect(x, y, x + this.width, y + this.height), null);
	}
	
	public int getAbsoluteX() {
		return this.absoluteX;
	}
	
	public int getAbsoluteY() {
		return this.absoluteY;
	}
	
	public int getX() {
		return this.absoluteX / Model.squareSize;
	}
	
	public int getY() {
		return this.absoluteY / Model.squareSize;
	}
}
