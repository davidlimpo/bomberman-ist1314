package com.example.bomberman.models;

import com.example.bomberman.thread.BombermanThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

public class Player extends Model implements Camera, Explosive {

	public static int pointsPerOpponentKilled = 0;
	
	private static final String TAG = "Player";
	protected int currentFrame = 0;
	protected int speedX = 0;
	protected int speedY = 0;
	protected boolean centered = true;
	
	protected int speed = 15;
	
	protected boolean stopped = true;
	private Bomb bombPlaced = null;

	private int halfWidth = 0;
	private int halfHeight = 0;
	
	protected Rect[] down;
	protected Rect[] up;
	protected Rect[] right;
	protected Rect[] left;
	protected Rect[] currentAnimation;
	
	protected BombermanThread drawer;
	protected boolean killed = false;
	private int id;
	
	private int score = 0;

	private boolean isAvailable = true;
	
	public Player(int id, Bitmap bitmap, int bmpRows, int bmpCols, int bitmapX, int bitmapY, int x, int y, BombermanThread drawer){
		super(bitmap, bmpRows, bmpCols, bitmapX, bitmapY, x, y);
		
		this.halfWidth = this.width / 2;
		this.halfHeight = this.height / 2;
		this.id = id;
		
		if (this.id != 1) this.centered = false;
		
		this.drawer = drawer;
		
		this.down = new Rect[3];
		this.up = new Rect[3];
		this.right = new Rect[3];
		this.left = new Rect[3];
		
		currentAnimation = down;
		
		int srcX = bitmapX * this.width;
		int srcY = bitmapY * this.height;
		
		for(int i=0; i<3; i++){
			this.down[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;	
		}
		
		for(int i=0; i<3; i++){
			this.right[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;	
		}
		
		for(int i=0; i<3; i++){
			this.left[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;	
		}
	
		for(int i=0; i<3; i++){
			this.up[i] = new Rect(srcX, srcY, srcX+width, srcY+height);
			srcX += this.width;	
		}
	}
	
	public Player(int id, Bitmap bitmap, int x, int y, BombermanThread drawer){
		this(id, bitmap, 4, 24, 0, 0, x, y, drawer);
	}
	
	public void moveDown(){
		speedY = this.speed;
		speedX = 0;
		this.stopped = false;

		currentAnimation = down;
	}
	
	public void moveUp() {
		speedY = -this.speed;
		speedX = 0;		
		this.stopped = false;

		currentAnimation = up;
	}

	public void moveLeft() {
		speedY = 0;
		speedX = -this.speed;
		this.stopped = false;

		currentAnimation = left;
	}

	public void moveRight() {
		speedY = 0;
		speedX = this.speed;
		this.stopped = false;

		currentAnimation = right;
	}
	
	public void placeBomb() {
		if(this.bombPlaced == null) {
			this.bombPlaced = new Bomb(BombermanThread.bombsBitmap, BombermanThread.explosionsBitmap, this.getAbsoluteX() / Model.squareSize, this.getAbsoluteY() / Model.squareSize, this);
			this.drawer.addDrawable(this.bombPlaced);
		}
	}
	
	public void stop() {
		this.speedX = 0;
		this.speedY = 0;
		this.stopped = true;
	}
	
	public Model collider() {
		int valueToY = (this.speedY > 0?Model.squareSize:0);
		int valueToX = (this.speedX > 0?Model.squareSize:0);
		
		int firstY = ((super.absoluteY += this.speedY) + valueToY) / Model.squareSize;
		int firstX = ((super.absoluteX += this.speedX)  + valueToX) / Model.squareSize;
		
		int secondY = (super.absoluteY + valueToY + (this.speedX != 0?Model.squareSize - 1:0)) / Model.squareSize;
		int secondX = (super.absoluteX  + valueToX + (this.speedY != 0?Model.squareSize - 1:0)) / Model.squareSize;
		
		Drawable possibleColider1 = null;
		Drawable possibleColider2 = null;
		
		possibleColider1 = BombermanThread.colisionMatrix.get(firstY).get(firstX);
		possibleColider2 = BombermanThread.colisionMatrix.get(secondY).get(secondX);
		
		
		Drawable possibleColider = null;
		
		if((possibleColider = possibleColider1) instanceof NullModel && (possibleColider = possibleColider2) instanceof NullModel) {
			return null;
		}
		
		return (Model) possibleColider;
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (this.killed) this.drawer.removeDrawable();
		int xToAdd = (this.speedX > 0 ? -Model.squareSize : (this.speedX < 0 ? Model.squareSize : 0));
		int yToAdd = (this.speedY > 0 ? -Model.squareSize : (this.speedY < 0 ? Model.squareSize : 0));
		
		BombermanThread.colisionMatrix.get(super.getY()).set(super.getX(), new NullModel());
		
		Model collider = this.collider();
		
		if (collider != null) {
			if (this.speedX != 0) super.absoluteX = collider.getAbsoluteX() + xToAdd;
			else if (this.speedY != 0) super.absoluteY = collider.getAbsoluteY() + yToAdd;
			this.speedX = 0;
			this.speedY = 0;
			this.stopped = true;
		}
		
		BombermanThread.colisionMatrix.get(super.getY()).set(super.getX(), this);
		
		if(stopped)
			super.src = currentAnimation[0];
		else
			super.src = currentAnimation[currentFrame=((++currentFrame)%currentAnimation.length)];
		
		if (this.centered) {
			int screenCenterX = Model.canvasCenterX - this.halfWidth;
			int screenCenterY = Model.canvasCenterY - this.halfHeight;
			       
			Rect dst = new Rect(screenCenterX, screenCenterY, screenCenterX + this.height, screenCenterY + this.width);
			canvas.drawBitmap(bitmap, super.src, dst, null);
		} else super.draw(canvas);
	}

	public void bombExploded(int points) {
		this.bombPlaced = null;
		this.drawer.removeDrawable();
		
		this.score += points;
		
		this.drawer.updatePlayerScore(this.score);
	}
	
	@Override
	public int getCameraX() {
		return this.absoluteX - Model.canvasCenterX + this.halfWidth;
	}

	@Override
	public int getCameraY() {
		return this.absoluteY - Model.canvasCenterY + this.halfHeight;
	}

	@Override
	public int Explode() {
		this.killed = true;
		
		synchronized (BombermanThread.colisionMatrix) {
			BombermanThread.colisionMatrix.get(this.getY()).set(this.getX(), new NullModel());
		}
		
		if (BombermanThread.myPlayer == this)
			this.drawer.showToast("You are dead, GAME OVER, you have " + this.score + " points.");
		
		return Player.pointsPerOpponentKilled;
	}

	public int getId() {
		return this.id;
	}
	
	public void setCentered(boolean centered) {
		if (this.centered != centered) {
			this.centered = centered;
			
			if (centered) {
				this.drawer.removeDrawable(this);
				this.drawer.addDrawableOnEnd(this);
			}
		}
	}
	
	public void executeAction(String action, String absoluteX, String absoluteY) {
		synchronized (BombermanThread.colisionMatrix) {
			BombermanThread.colisionMatrix.get(this.getY()).set(this.getX(), new NullModel());
		}
		int X = Integer.parseInt(absoluteX);
		int Y = Integer.parseInt(absoluteY);
		
		this.absoluteX = X * Model.squareSize;
		this.absoluteY = Y * Model.squareSize;
		synchronized (BombermanThread.colisionMatrix) {
			BombermanThread.colisionMatrix.get(Y).set(X, this);
		}
		
		
		if (action.equals("L"))
			this.moveLeft();
		else if (action.equals("R"))
			this.moveRight();
		else if (action.equals("U"))
			this.moveUp();
		else if (action.equals("D"))
			this.moveDown();
		else if (action.equals("B"))
			this.placeBomb();
		else if (action.equals("S"))
			this.stop();
	}
	
	public boolean isAvailable() {
		return this.isAvailable ;
	}
	
	public void setUnavailable() {
		this.isAvailable = false;
		this.drawer.addDrawable(this);
		BombermanThread.colisionMatrix.get(this.getY()).set(this.getX(), this);
	}
	
	public int getScore() {
		return this.score;
	}
}
