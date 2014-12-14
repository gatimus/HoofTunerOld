package com.gatimus.hooftuner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Splash extends Activity {
	
	private static final String TAG = "Splash:";
	private AsyncTask preLoad;
	private Intent intent;
	private Resources res;
	private ProgressBar pBar;
	private TextView pText;
	private int pPercent;
	private String pMsg;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pText = (TextView) findViewById(R.id.progressText);
        pPercent = 0;
        this.res = getResources();
        this.intent = new Intent(this, Main.class);
        preLoad = new PreLoad(this);
        ((PreLoad)preLoad).execute(res.getString(R.string.url_api_status), res.getString(R.string.url_api_stations));
    } //onCreate
    
    public class PreLoad extends AsyncTask<String, Integer, JSONArray> {
    	
    	
    	private PAO pao;
    	private ArrayList<Station> stations;
    	private boolean online;
    	
    	
    	public PreLoad(Context c){
    		super();
    		Log.v(TAG, "construct");
    		online = false;
    		
    		this.pao = new PAO(res);
    		stations = new ArrayList<Station>();
    	} //constructor
    	
    	@Override
        protected void onPreExecute() {
    		pMsg = "Sending letters to Celestia...";
    		Log.v(TAG, pMsg);
            pText.setText(pMsg);
    		super.onPreExecute();
    	} //onPreExecute

    	@Override
    	protected JSONArray doInBackground(String... params) {
    		Log.v(TAG, "doInBackground");
    		JSONObject json = null;
    		JSONArray jArray = null;
    		try {
    			json = getJSON(params[0]).getJSONObject("result");
    			online = json.getBoolean("online");
    		} catch (Exception e) {
    			Log.e(TAG, e.toString());
    		}
    		if(online){
    			pMsg = "Friendship channel open";
    			publishProgress(pPercent);
    			try {
    				jArray = getJSON(params[1]).getJSONArray("result");
        		} catch (Exception e) {
        			Log.e(TAG, e.toString());
        		}
    		} else {
    			pMsg = "To the MOOOOOOOON!!!!!";
    			publishProgress(pPercent);
    		}
    		publishProgress(pPercent);
    		return jArray;
    	} //doInBackground
    	
    	@Override
    	protected void onProgressUpdate(Integer... progress) {
    		pBar.setProgress(progress[0]);
    		Log.v(TAG, pMsg);
			pText.setText(pMsg);
    	}
    	
    	@Override
        protected void onPostExecute(JSONArray result) {
    		Log.v(TAG, "post execute");
    		super.onPostExecute(result);
    		intent.putExtra("stations", result.toString());
    		startActivity(intent);
    		finish();
    	} //onPostExecute
    	
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
    	    	    Log.v(TAG, result);
    	    	    pPercent += pBar.getMax()/2;
    	    	    publishProgress(pPercent);
    	    	    jObject = new JSONObject(result);
    	    	}
    		} catch (Exception e) {
    	    	Log.e(TAG, e.toString());
    	    }
    		return jObject;
    	} //getJSON

    } //inner class
    
} //class
