package com.example.bomberman;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.example.bomberman.thread.BombermanThread;
import com.example.bomberman.thread.ClientThread;
import com.example.bomberman.thread.ComunicateMove;
import com.example.bomberman.thread.ServerThread;
import com.example.bomberman.wifidirect.WiFiDirectBroadcastReceiver;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MPActivity extends Activity  implements PeerListListener, ConnectionInfoListener {

	private static final String TAG = "MPActivity";
	private ListView gamesList = null;
	private ArrayAdapter<String> adapter = null;
	
	private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
	
    private ArrayList<WifiP2pDevice> availableDevices = null;
    private String level = null;
    
    private boolean isMultiplayer = false;
	protected boolean isServer = false;
    
	public static ComunicateMove multiplayerThread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mp);
		
		this.gamesList = (ListView) findViewById(R.id.gameList);
		
		this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		this.manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		this.channel = this.manager.initialize(this, getMainLooper(), null);
		
		this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		this.gamesList.setAdapter(this.adapter);
		
		((Button) findViewById(R.id.hostGame)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MPActivity.this.manager.createGroup(MPActivity.this.channel, new ActionListener() {
					
					@Override
					public void onSuccess() {
						Toast.makeText(MPActivity.this, "Group created", Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onFailure(int reason) {
						Toast.makeText(MPActivity.this, "Group Failed", Toast.LENGTH_LONG).show();
					}
				});
				
				Intent intent = new Intent(MPActivity.this, SPActivity.class);
				intent.putExtra(Constant.IS_MULTIPLAYER, true);
				intent.putExtra(Constant.IS_SERVER, true);
				intent.putExtra(Constant.SERVER_ADDRESS, "");
				startActivity(intent);
			}
		});
		
		((Button) findViewById(R.id.refresh)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(MPActivity.this, "Discovery Initiated",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(MPActivity.this, "Discovery Failed : " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
			}
		});
		
		this.gamesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = MPActivity.this.availableDevices.get(position).deviceAddress;
                config.wps.setup = WpsInfo.PBC;
				
                MPActivity.this.isMultiplayer = true;
                MPActivity.this.isServer = false;
                
				manager.connect(channel, config, new ActionListener() {

		            @Override
		            public void onSuccess() {
		            	Toast.makeText(MPActivity.this, "Connected",
		                        Toast.LENGTH_SHORT).show();
		            }

		            @Override
		            public void onFailure(int reason) {
		                Toast.makeText(MPActivity.this, "Connect failed. Retry.",
		                        Toast.LENGTH_SHORT).show();
		            }
		        });
			}
			
		});
	}

	public void startMultiplayerGame(String serverAddress, boolean isServer, String level) {
		Intent intent = new Intent(MPActivity.this, BombermanActivity.class);
		intent.putExtra(Constant.IS_MULTIPLAYER, true);
		intent.putExtra(Constant.IS_SERVER, isServer);
		intent.putExtra(Constant.SERVER_ADDRESS, serverAddress);
		intent.putExtra(Constant.LEVEL_PATH, level);
		startActivity(intent);
	}
	
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }
	
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public void setIsWifiP2pEnabled(boolean b) {
		Toast.makeText(getApplicationContext(), "Wi-Fi Status: " + b, Toast.LENGTH_LONG).show();
	}

	public void resetData() {
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		this.adapter.clear();
		this.availableDevices = new ArrayList<WifiP2pDevice>();
		
		for (WifiP2pDevice device : peers.getDeviceList()) {
			this.adapter.add(device.deviceName);
			this.availableDevices.add(device);
		}
		
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		if (isMultiplayer) {
			Log.d(TAG, "It is multiplayer.");
			MPActivity.multiplayerThread = new ClientThread(info.groupOwnerAddress.getHostAddress(), this);
			
			((Thread) MPActivity.multiplayerThread).start();
			Log.d(TAG, "Thread iniciadated.");
		} else Log.d(TAG, "It is !!!!!NOT!!!!! multiplayer.");
	}

}
