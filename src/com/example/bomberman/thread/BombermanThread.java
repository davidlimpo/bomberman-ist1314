package com.example.bomberman.thread;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bomberman.BombermanActivity;
import com.example.bomberman.MPActivity;
import com.example.bomberman.R;
import com.example.bomberman.R.color;
import com.example.bomberman.models.Bomb;
import com.example.bomberman.models.Bot;
import com.example.bomberman.models.Drawable;
import com.example.bomberman.models.Explosion;
import com.example.bomberman.models.Model;
import com.example.bomberman.models.NullModel;
import com.example.bomberman.models.Obstacle;
import com.example.bomberman.models.Player;
import com.example.bomberman.models.Wall;
import com.example.bomberman.view.BombermanView;

public class BombermanThread extends Thread {
	public final static int BMP_COLS = 6;
	public final static int BMP_ROWS = 16;
	
	public static Bitmap bombsBitmap = null;
	public static Bitmap explosionsBitmap = null;
	
    static final long FPS = 10;
    
	//private Bitmap backgroundImage;
	private SurfaceHolder surfaceHolder;
	
	private Context context;
	
	private boolean running;
	
	public static ArrayList<ArrayList<Drawable>> colisionMatrix;
	
	private LinkedList<Drawable> drawables = new LinkedList<Drawable>();
	
	private ListIterator<Drawable> drawablesIterator;
	
	public static Player myPlayer;
	public static SparseArray<Player> players = null;
	
	public static SparseArray<Bot> bots = new SparseArray<Bot>();
	private BombermanActivity bombermanActivity = null;
	private boolean paused = false;
	
	public BombermanThread(SurfaceHolder surfaceHolder, Context context, String levelDescription, String levelMap, BombermanActivity bombermanActivity){
			super();
			this.surfaceHolder = surfaceHolder;
			this.context = context;
			this.bombermanActivity = bombermanActivity;
			
			Resources res = context.getResources();
			//backgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);
			bombsBitmap = BitmapFactory.decodeResource(res, R.drawable.bombs);
			explosionsBitmap = BitmapFactory.decodeResource(res, R.drawable.animate);
			colisionMatrix = readMapFromFile(res, levelMap);
			this.readDescriptionFromFile(levelDescription);
	}
	
	private void readDescriptionFromFile(String levelDescription) {
		String[] description = levelDescription.split("\n");
		
		for (String pair : description) {
			String[] keyPair = pair.split("\\:");
			String key = keyPair[0];
			String value = keyPair[1];
			
			if (key.equals("GD"))
				BombermanActivity.timeLeft = Integer.valueOf(value);
			else if (key.equals("ET"))
				Bomb.explosionTimeout = Float.valueOf(value);
			else if (key.equals("ED"))
				Explosion.explosionDuration = Integer.valueOf(value);
			else if (key.equals("ER"))
				Explosion.explosionRange = Integer.valueOf(value);
			else if (key.equals("RS"))
				Bot.robotSpeed = Float.valueOf(value);
			else if (key.equals("PR"))
				Bot.pointsPerRobotKilled = Integer.valueOf(value);
			else if (key.equals("PO"))
				Player.pointsPerOpponentKilled = Integer.valueOf(value);
		}
	}

	public void setPause(boolean pause) {
		this.paused = pause;
	}
	
	public void downKeyEvent(){
		myPlayer.moveDown();
	}
	
	public void upKeyEvent() {
		myPlayer.moveUp();
	}

	public void leftKeyEvent() {
		myPlayer.moveLeft();
	}

	public void rightKeyEvent() {
		myPlayer.moveRight();
	}

	public void bombKeyEvent() {
		myPlayer.placeBomb();
	}
	
	public void noneKeyEvent() {
		myPlayer.stop();
	}
	
	public void addDrawable(Drawable drawable) {
		synchronized (this.drawables) {
			this.drawables.addFirst(drawable);
		}
	}
	
	public void removeDrawable() {
		synchronized (this.drawables) {
			this.drawablesIterator.remove();
		}
	}
	
	public void addDrawableOnEnd(Drawable drawable) {
		synchronized (this.drawables) {
			this.drawables.addLast(drawable);
		}
	}
	
	public void removeDrawable(Drawable drawable) {
		synchronized (this.drawables) {
			this.drawables.remove(drawable);
		}
	}
	
	public ArrayList<ArrayList<Drawable>> readMapFromFile(Resources res, String stringLevel){
		
		Bitmap originalWall = BitmapFactory.decodeResource(res, R.drawable.inanimate);
		Bitmap originalObstacle = BitmapFactory.decodeResource(res, R.drawable.animate);
		Bitmap originalPlayer = BitmapFactory.decodeResource(res, R.drawable.players);
		
		Model.squareSize = originalWall.getWidth() / BMP_COLS;
		
		ArrayList<ArrayList<Drawable>> matrix = new ArrayList<ArrayList<Drawable>>();
		
		int x = 0;
		int y = 0;
		int botsIndex = 0;
		
		ArrayList<Drawable> row = new ArrayList<Drawable>();
		BombermanThread.players = new SparseArray<Player>();
		
		for(char c : stringLevel.toCharArray()) {
			Drawable model = null;
			switch (c) {
				case 'W':
					model = new Wall(originalWall, x, y);
					break;
				case '1':
					model = BombermanThread.myPlayer = new Player(1, originalPlayer, x, y, this);
					Model.camera = BombermanThread.myPlayer;
					break;
				case '2':
					model = new Player(2, originalPlayer, x, y, this);
					BombermanThread.players.put(2, (Player) model);
					break;
				case '3':
					model = new Player(3, originalPlayer, x, y, this);
					BombermanThread.players.put(3, (Player) model);
					break;
				case '4':
					model = new Player(4, originalPlayer, x, y, this);
					BombermanThread.players.put(4, (Player) model);
					break;
				case 'R':
					Bot bot = new Bot(botsIndex, originalPlayer, x, y, this);
					BombermanThread.bots.put(botsIndex++, bot);
					model = bot;
					break;
				case '\n':
					matrix.add(row);
					row = new ArrayList<Drawable>();
					x = 0;
					++y;
				case '\r':
					continue;
				case 'O':
					model = new Obstacle(originalObstacle, x, y, this);
					break;
				case '-':
				default:
					model = new NullModel();
					break;
			}
			++x;
			row.add(model);
			if (model instanceof NullModel || (model instanceof Player && !(model instanceof Bot))) continue;
			this.drawables.add(model);
		}
		
		this.drawables.add(BombermanThread.myPlayer);
		
		synchronized (MPActivity.class) {
			MPActivity.class.notifyAll();
		}
		
		return matrix;
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}

	@Override
	public void run(){
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        long botsTime = System.currentTimeMillis();
		while(running){
			if (this.paused ) continue;
			Canvas canvas;
			canvas = null;
			
			startTime = System.currentTimeMillis();
			
			if (!BombermanView.isMultiplayer || (BombermanView.isMultiplayer && BombermanView.isServer)) {
				if (startTime - botsTime > 5000) {
					for (int i = 0; i < BombermanThread.bots.size() ; ++i)
						BombermanThread.bots.get(BombermanThread.bots.keyAt(i)).randomlyMove();
					botsTime = System.currentTimeMillis();
				}
			}
            
			try{
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					doDraw(canvas);
				}
			} finally {
				if(canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
			
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                   if (sleepTime > 0)
                	   sleep(sleepTime);
                   else
                	   sleep(10);
            } catch (Exception e) {}
		}
	}
	
	public void doDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		canvas.drawColor(Color.GREEN);
		
        Model.canvasCenterX = canvas.getWidth() / 2;
		Model.canvasCenterY = canvas.getHeight() / 2;
        
		synchronized (this.drawables) {
			this.drawablesIterator = this.drawables.listIterator();
			while (this.drawablesIterator.hasNext())
				this.drawablesIterator.next().draw(canvas);
		}
   }
	
	public void updatePlayerScore(final int newScore) {
		this.bombermanActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				BombermanThread.this.bombermanActivity.updatePlayerScore(newScore);
			}
		});
	}
	
	public void showToast(final String text) {
		this.bombermanActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(BombermanThread.this.bombermanActivity, text, Toast.LENGTH_LONG).show();
				BombermanThread.this.bombermanActivity.onBackPressed();
			}
		});
	}
}
