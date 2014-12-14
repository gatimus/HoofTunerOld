package com.gatimus.hooftuner;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gatimus.hooftuner.BackgroundAudio.LocalBinder;

public class Main extends ActionBarActivity implements OnItemClickListener{
	
	private static final String TAG = "Main:";
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private JSONArray stations;
    private JSONObject currentStation;
    private Service bgAudio;
    boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bundle extras = getIntent().getExtras();
			if (extras != null) {
					if (extras.getString("stations")!= null) {
						try {
							this.stations = new JSONArray(extras.getString("stations"));
						} catch (JSONException e) {
							Log.e(TAG, e.toString());
						}
					}
			}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        mDrawerList.setAdapter(adapter);
        for (int i=0; i<stations.length(); i++) {
        	String name = "";
        	try {
				name = stations.getJSONObject(i).getString("name");
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
        	Log.v(TAG, "adding " + name);
        	adapter.add(name);
        }
        mDrawerList.setOnItemClickListener(this);
	} //onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "create menu");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} //onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, item.getTitle().toString() + " selected");
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	} //onOptionsItemSelected
	
	@Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BackgroundAudio.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			if (mBound) {
				((BackgroundAudio)bgAudio).setStation(stations.getJSONObject(position));
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		mDrawerLayout.closeDrawers();
	}
	
	@Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
	
	private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            bgAudio = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
} //class
