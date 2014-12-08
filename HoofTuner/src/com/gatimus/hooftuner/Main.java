package com.gatimus.hooftuner;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Main extends ActionBarActivity {
	
	private static final String TAG = "Main:";
	private ArrayList<Station> stations;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<Station> adapter;
    //private ArrayList<String> arrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bundle extras = getIntent().getExtras();
			if (extras != null) {
				extras.getParcelableArrayList("stations");
					if (extras.getParcelableArrayList("stations")!= null) {
						this.stations = extras.getParcelableArrayList("stations");
					}
			}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        //arrayList = new ArrayList<String>();
        adapter = new StationAdapter<Station>(this, stations);
        mDrawerList.setAdapter(adapter);
        //for(Station station: stations){
        	//Log.v(TAG, "adding " + station.getName());
        	//arrayList.add(station.getName());
        	//adapter.notifyDataSetChanged();
        //}
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
	
} //class
