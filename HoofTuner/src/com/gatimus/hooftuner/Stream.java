package com.gatimus.hooftuner;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Stream {
	
	private static final String TAG = "Stream:";
	private int id;
    private String name;
    private URL url;
    private String type;
    private boolean isDefault;
	
	public Stream(JSONObject jObject){
		Log.v(TAG, "construct");
		try {
			id = jObject.getInt("id");
			name = jObject.getString("name");
			url = new URL(jObject.getString("url"));
			type = jObject.getString("type");
			isDefault = jObject.getBoolean("is_default");
		} catch (JSONException | MalformedURLException e) {
			Log.e(TAG, e.toString());
		}
	} //constructor
	
	public int getId(){
		return id;
	} //getId

} //class
