package com.gatimus.hooftuner;

import android.app.Application;
import android.graphics.Typeface;
import android.widget.ListView;
import android.widget.TextView;

public class CustomApp extends Application {
	
	private Typeface normalFont;
	
	public CustomApp(){
		super();
	}

	public void setTypeface(TextView view){
		normalFont = Typeface.createFromAsset(getAssets(),"fonts/regular.ttf");
		if(view != null) {
			view.setTypeface(normalFont);
		}
	}

}
