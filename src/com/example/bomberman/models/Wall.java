package com.example.bomberman.models;

import android.graphics.Bitmap;

public class Wall extends Model {
		
	public Wall(Bitmap bitmap, int x, int y){
		super(bitmap, 3, 6, 1, 0, x, y);
	}
}
