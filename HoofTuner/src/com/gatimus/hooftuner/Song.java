package com.gatimus.hooftuner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Song {
	
	private static final String TAG = "Song:";
	private Bitmap image;
	
	//API variables
	private int id;
	private String text;
	private String artist;
	private String title;
	private String album; //external>bronytunes
	private String description; //external>bronytunes
	private URL imageUrl; //external>bronytunes
	
	public Song(JSONObject jObject){
		Log.v(TAG, "construct");
		try {
			id = jObject.getInt("id");
			text = jObject.getString("text");
			artist = jObject.getString("artist");
			title = jObject.getString("title");
			JSONObject bronytunes = jObject.getJSONObject("external").getJSONObject("bronytunes");
			album = bronytunes.getString("album"); //external>bronytunes
			description = bronytunes.getString("description"); //external>bronytunes
			imageUrl = new URL(bronytunes.getString("image_url")); //external>bronytunes
			if(imageUrl != null){
				image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
			}
		} catch (JSONException | IOException e) {
			Log.e(TAG, e.toString());
		}
	} //constructor

} //class
