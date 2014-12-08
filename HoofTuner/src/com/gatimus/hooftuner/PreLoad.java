package com.gatimus.hooftuner;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

public class PreLoad extends AsyncTask<Void, Void, Void> {
	
	private static final String TAG = "PreLoad:";
	private Context context;
	private Intent intent;
	private Resources res;
	private PAO pao;
	private ArrayList<Station> stations;
	
	public PreLoad(Context c){
		super();
		Log.v(TAG, "construct");
		this.context = c;
		this.intent = new Intent(context, Main.class);
		this.res = context.getResources();
		this.pao = new PAO(res);
		stations = new ArrayList<Station>();
	} //constructor
	
	@Override
    protected void onPreExecute() {
		Log.v(TAG, "pre execute");
		super.onPreExecute();
	} //onPreExecute

	@Override
	protected Void doInBackground(Void... params) {
		Log.v(TAG, "execute");
		if(pao.getStatus()){
			stations = pao.getStations();
		} else {
			//no access
		}
		return null;
	} //doInBackground
	
	@Override
    protected void onPostExecute(Void result) {
		Log.v(TAG, "post execute");
		super.onPostExecute(result);
		intent.putParcelableArrayListExtra("stations", stations);
		context.startActivity(intent);
	} //onPostExecute

} //class
