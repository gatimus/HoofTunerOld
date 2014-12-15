package com.gatimus.hooftuner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import com.gatimus.hooftuner.BackgroundAudio.LocalBinder;

public class Main extends ActionBarActivity implements OnItemClickListener, OnSharedPreferenceChangeListener, MediaPlayerControl{
	
	private static final String TAG = "Main:";
	private static final int RESULT_SETTINGS = 1;
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    
    private JSONArray stations;
    private JSONObject currentStation;
    
    private Service bgAudio;
    private boolean mBound = false;
    private MediaController mcontroller;
    private ImageView albumArt;
    private TextView songText;
    SongLoad songLoad;
    private BroadcastReceiver msg;
    
    private DialogFragment help;
    private DialogFragment about;
    private FragmentManager fragMan;
    private Resources res;
    private SharedPreferences sharedPrefs;
    
    //private View view;
    
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "create");
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
		String theme = sharedPrefs.getString("theme", "Twilight");
		 Log.v(TAG, theme);
		 if(theme == "Twilight"){
			 getApplication().setTheme(R.style.Twilight);
		 } else if (theme == "Applejack"){
			 getApplication().setTheme(R.style.Applejack);
		 }
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bundle extras = getIntent().getExtras();
			if (extras != null) {
					if (extras.getString("stations")!= null) {
						try {
							this.stations = new JSONArray(extras.getString("stations"));
							Log.v(TAG, "got stations");
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
        help = new Help();
        about = new About();
        res = getResources();
        fragMan = getFragmentManager();
        albumArt = (ImageView)findViewById(R.id.albumArt);
        //CustomApp app = (CustomApp) getApplication();
        songText = (TextView)findViewById(R.id.songText);
        //app.setTypeface(songText);
        //view = findViewById(R.id.view);
        //view = new BoxView(this);
        mcontroller = new MediaController(this);
        mcontroller.setMediaPlayer(this);
        mcontroller.setAnchorView(findViewById(R.id.controller));
        mcontroller.setEnabled(false);
        msg = new BroadcastReceiver() {
    		@Override
    		public void onReceive(Context context, Intent intent) { 
    			Bundle b = intent.getExtras();
    			if(b.getBoolean("prepared")){
    				mcontroller.setEnabled(true);
    				mcontroller.show();
    				Log.v(TAG, "prepared");
    			}
    	   }
    	};
        registerReceiver(msg, new IntentFilter("com.gatimus.hooftuner"));
	} //onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "create menu");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} //onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, item.getTitle().toString() + " Option Selected");
		int id = item.getItemId();
		switch(id){
		case R.id.action_settings :
			Intent intent = new Intent(this, Settings.class);
			startActivityForResult(intent, RESULT_SETTINGS);
			break;
		case R.id.action_about :
			about.show(fragMan, res.getString(R.string.action_about));
			break;
		case R.id.action_help :
			help.show(fragMan, res.getString(R.string.action_help));
			break;
		case R.id.action_quit : System.exit(0);
			break;
		default : 
			break;
		}
		return super.onOptionsItemSelected(item);
	} //onOptionsItemSelected
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        switch (requestCode) {
	        case RESULT_SETTINGS:
	        	String theme = sharedPrefs.getString("theme", "Twilight");
	        	Log.v(TAG, theme);
	        	if(theme == "Twilight"){
	        		getApplication().setTheme(R.style.Twilight);
	        	} else if (theme == "Applejack"){
	        		getApplication().setTheme(R.style.Applejack);
	        	}
	            break;
	        }
	    }
	 
	 @Override
	 public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		 if(key == "theme"){
			 String theme = pref.getString("theme", "Twilight");
			 ((ListPreference)pref).setSummary(theme);
			 Log.v(TAG, theme);
			 if(theme == "Twilight"){
				 getApplication().setTheme(R.style.Twilight);
			 } else if (theme == "Applejack"){
				 getApplication().setTheme(R.style.Applejack);
			 }
		 }
	 }
	
	@Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BackgroundAudio.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		URL imageUrl;
		try {
			currentStation = stations.getJSONObject(position);
			imageUrl = new URL(currentStation.getString("image_url"));
			songLoad = new SongLoad(currentStation.getInt("id"));
			if (mBound) {
				mcontroller.setEnabled(false);
				((BackgroundAudio)bgAudio).setStation(currentStation);
			}
			if(imageUrl != null){
				AsyncTask<URL, Void, Bitmap> loader = new ImageLoad();
				loader.execute(imageUrl);
			}
		} catch (JSONException | IOException e) {
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
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(event.getAction() != MotionEvent.EDGE_LEFT){
			mcontroller.show();
		}
		Log.v(TAG, "dispatch touch event");
        return super.dispatchTouchEvent(event);
	}
	/*
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		mcontroller.show();
		Log.v(TAG, "touch event");
        return true;
    }
	*/
	@Override
	public void start() {
		((BackgroundAudio)bgAudio).mMediaPlayer.start();
	}

	@Override
	public void pause() {
		((BackgroundAudio)bgAudio).mMediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		return ((BackgroundAudio)bgAudio).mMediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return ((BackgroundAudio)bgAudio).mMediaPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		((BackgroundAudio)bgAudio).mMediaPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return ((BackgroundAudio)bgAudio).mMediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	@Override
	public int getAudioSessionId() {
		return ((BackgroundAudio)bgAudio).mMediaPlayer.getAudioSessionId();
	}
	
	public class ImageLoad extends AsyncTask<URL, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(URL... params) {
			Bitmap image = null;
			try {
				image = BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
			return image;
		}
		
		@Override
        protected void onPostExecute(Bitmap result){
			albumArt.setImageBitmap(result);
		}
		
	} //inner class
	
	public class SongLoad implements Runnable{
		
		
		int stationId;
		Handler h;
		
		public SongLoad(int Id){
			this.stationId = Id;
			h = new Handler();
			h.post(this);
		}
		
		public JSONObject getJSON(String url){
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			JSONObject jObject = null;
			try {
		    	HttpResponse response = client.execute(httpGet);
		    	StatusLine statusLine = response.getStatusLine();
		    	int statusCode = statusLine.getStatusCode();
		    	Log.i(TAG, String.valueOf(statusCode));
		    	if (statusCode == 200) {
		    		HttpEntity entity = response.getEntity();
		    		InputStream content = entity.getContent();
		    		BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"), 8);
		    	    StringBuilder sb = new StringBuilder();
		    	    String line = null;
		    	    while ((line = reader.readLine()) != null)
		    	    {
		    	        sb.append(line + "\n");
		    	    }
		    	    String result = sb.toString();

		    	    jObject = new JSONObject(result);
		    	}
			} catch (Exception e) {
		    	Log.e(TAG, e.toString());
		    }
			return jObject;
		} //getJSON

		@Override
		public void run() {
			BackGroundTask task = new BackGroundTask();
			task.execute();
			h.postDelayed(this, 1000);
		}
		
		public class BackGroundTask extends AsyncTask{

			@Override
			protected Object doInBackground(Object... params) {
				JSONObject json = null;
				try {
					json = getJSON(res.getString(R.string.url_api_nowplaying) + String.valueOf(stationId)).getJSONObject("result").getJSONObject("current_song");
				} catch (NotFoundException | JSONException e) {
					Log.e(TAG, e.toString());
				}
				if(json != null){
					Song song = new Song(json);
					if(song.imageUrl != null){
						AsyncTask<URL, Void, Bitmap> loader = new ImageLoad();
						loader.execute(song.imageUrl);
					}
					if(song.text != null){
						songText.setText(song.text);
			    	    Log.v(TAG, song.text);
					}
				}
				return null;
			}
			
		}
		
	} //inner class
	
} //class
