package com.gatimus.hooftuner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Station implements Parcelable {
	
	private static final String TAG = "Station:";
	private PAO pao;
	private Bitmap image;
	
	//API variables
	private int id;
    private String name;
    private String shortcode;
    private String genre;
    private String category;
    private URL imageUrl;
    private URL webUrl;
    private URL twitterUrl;
    private String irc;
    private ArrayList<Stream> streams;
    private int defaultStreamId;
    private URL streamUrl;
    private URL playerUrl;
    private URL requestUrl;
    
    public Station(Parcel in){
    	this.name = in.readString();
    	this.image = (Bitmap) in.readValue(getClass().getClassLoader());
    	//everything
    }
	
	public Station(JSONObject jObject, PAO pao){
		Log.v(TAG, "construct");
		this.pao = pao;
		try {
			id = jObject.getInt("id");
			name = jObject.getString("name");
			shortcode = jObject.getString("shortcode");
			genre = jObject.getString("genre");
			category = jObject.getString("category");
			imageUrl = new URL(jObject.getString("image_url"));
			webUrl = new URL(jObject.getString("web_url"));
			twitterUrl = new URL(jObject.getString("twitter_url"));
			irc = jObject.getString("irc");
			streams = new ArrayList<Stream>();
			JSONArray array = jObject.getJSONArray("streams");
			for (int i=0; i<array.length(); i++) {
				streams.add(new Stream(array.getJSONObject(i)));
			}
			defaultStreamId = jObject.getInt("default_stream_id");
			streamUrl = new URL(jObject.getString("stream_url"));
			playerUrl = new URL(jObject.getString("player_url"));
			requestUrl = new URL(jObject.getString("request_url"));
			if(imageUrl != null){
				image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
			}
		} catch (JSONException | IOException e) {
			Log.e(TAG, e.toString());
		}
	} //constructor
	
	public String getName(){
		return name;
	} //getName
	
	public Bitmap getImage(){
		return image;
	}
	
	public Stream getStream(){
		return getStream(defaultStreamId);
	} //getStream()
	
	public Stream getStream(int streamId){
		Log.v(TAG, "get stream " + String.valueOf(streamId));
		Stream stream = null;
		for(Stream i : streams){
			if(streamId == i.getId()){
				stream = i;
			}
		}
		return stream;
	} //getStream(int streamId)
	
	public Song getCurrentSong() {
		return getCurrentSong(defaultStreamId);
	} //getCurrentSong()
	
	public Song getCurrentSong(int streamId) {
		Song currentSong = null;
		JSONObject jObject = pao.getNowPlaying(id);
		try {
			JSONArray array = jObject.getJSONArray("streams");
			for (int i=0; i<array.length(); i++) {
				if(array.getJSONObject(i).getInt("id") == streamId){
					currentSong = new Song(array.getJSONObject(i));
				}
			} 
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
		return currentSong;
	} //getCurrentSong(int streamId)

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeValue(image);
		//everything
	}
	
	public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {

		@Override
		public Station createFromParcel(Parcel source) {
			return new Station(source);
		}

		@Override
		public Station[] newArray(int size) {
			return new Station[size];
		}
		
	};

} //class
