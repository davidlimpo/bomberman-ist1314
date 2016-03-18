package com.example.bomberman;

import java.io.IOException;

import com.example.bomberman.thread.ServerThread;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SPActivity extends Activity {
	private ListView levelsList = null;
	private ArrayAdapter<String> adapter = null;
	private boolean isMultiplayer = false;
	private boolean isServer = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sp);
		
		Intent intent = getIntent();
		this.isMultiplayer = intent.getBooleanExtra(Constant.IS_MULTIPLAYER, false);
		this.isServer = intent.getBooleanExtra(Constant.IS_SERVER, false);
		
		this.levelsList = (ListView) findViewById(R.id.levelsListView);
		this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		AssetManager assetManager = getAssets();
		try {
			this.adapter.addAll(assetManager.list("levels"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.levelsList.setAdapter(this.adapter);
		this.levelsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (SPActivity.this.isMultiplayer) {
					MPActivity.multiplayerThread = new ServerThread(SPActivity.this.adapter.getItem(position));
					((Thread) MPActivity.multiplayerThread).start();
				}
				
				Intent intent = new Intent(SPActivity.this, BombermanActivity.class);
				intent.putExtra(Constant.LEVEL_PATH, SPActivity.this.adapter.getItem(position));
		        intent.putExtra(Constant.IS_MULTIPLAYER, SPActivity.this.isMultiplayer);
		        intent.putExtra(Constant.IS_SERVER, SPActivity.this.isServer);
		        startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
}
