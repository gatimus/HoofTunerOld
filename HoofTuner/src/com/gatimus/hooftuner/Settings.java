package com.gatimus.hooftuner;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	private static final String TAG = "Settings:";
	private FragmentManager fragMan;
	private SharedPreferences sharedPrefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Create Activity");
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
		fragMan = this.getFragmentManager();
		fragMan.beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	} //onCreate
	
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
	
	public class SettingsFragment extends PreferenceFragment {
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			Log.i(TAG, "Create Fragment");
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
		} //onCreate
		
	} //inner class

} //class