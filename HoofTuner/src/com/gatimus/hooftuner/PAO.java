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

import android.content.res.Resources;
import android.util.Log;

public class PAO {
	
	private static final String TAG = "PAO:";
	private Resources res;
	
	public PAO(Resources res){
		Log.v(TAG, "construct");
		this.res = res;
	} //constructor
	
	protected JSONObject getJSON(String url){
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
	    	    jObject = new JSONObject(result);
	    	}
		} catch (Exception e) {
	    	Log.e(TAG, e.toString());
	    }
		return jObject;
	} //getJSON
	
	public boolean getStatus() {
		boolean online = false;
		JSONObject json;
		try {
			json = getJSON(res.getString(R.string.url_api_status)).getJSONObject("result");
			online = json.getBoolean("online");
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		Log.v(TAG, "online: " + String.valueOf(online));
	    return online;
	} //getStatus
	
	public ArrayList<Station> getStations(){
		ArrayList<Station> stations = new ArrayList<Station>();
		JSONArray array;
		try {
			array = getJSON(res.getString(R.string.url_api_stations)).getJSONArray("result");
			for (int i=0; i<array.length(); i++) {
				stations.add(new Station(array.getJSONObject(i), this));
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stations;
	}
	
	
	public JSONObject getNowPlaying(int stationId){
		JSONObject json = null;
		try {
			json = getJSON(res.getString(R.string.url_api_nowplaying) + String.valueOf(stationId)).getJSONObject("result");
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return json;
	}

} //class
