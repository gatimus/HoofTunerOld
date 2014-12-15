package com.gatimus.hooftuner;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class BackgroundAudio extends Service implements OnPreparedListener, OnErrorListener{
	
	private static final String TAG = "BackgroundAudio";
	public static final String NOTIFICATION = "com.gatimus.hooftuner";
	private final IBinder mBinder = new LocalBinder();
	public MediaPlayer mMediaPlayer = null;
	
	private Context context;
	//private WifiLock wifiLock = null;
	private JSONObject mStation = null;
	
	public BackgroundAudio() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		//mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		//wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
	}
	
	public class LocalBinder extends Binder {
		BackgroundAudio getService() {
            return BackgroundAudio.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public void setStation(JSONObject station){
		this.mStation = station;
		try{
			String url = station.getString("stream_url");
			mMediaPlayer.reset();
			//wifiLock.acquire();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
		} catch(Exception e){
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		Intent i = new Intent("com.gatimus.hooftuner");
		Bundle b = new Bundle();
		b.putBoolean("prepared", true);
		i.putExtras(b);
		sendBroadcast(i);
		Log.v(TAG, "prepared");
	}
	
	@Override
	public void onDestroy() {
       if (mMediaPlayer != null) mMediaPlayer.release();
       //if (wifiLock != null) wifiLock.release();
   }

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.e(TAG, String.valueOf(what));
		//wifiLock.release();
		mp.reset();
		return false;
	}

	

}
