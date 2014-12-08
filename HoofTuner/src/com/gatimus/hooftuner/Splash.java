package com.gatimus.hooftuner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class Splash extends Activity {
	
	private static final String TAG = "Splash:";
	private AsyncTask preLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preLoad = new PreLoad(this);
        ((PreLoad)preLoad).execute();
    } //onCreate
    
} //class
