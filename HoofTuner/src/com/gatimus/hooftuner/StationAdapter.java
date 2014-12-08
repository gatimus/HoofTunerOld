package com.gatimus.hooftuner;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationAdapter<Station> extends ArrayAdapter<Station> {
	
	private final Context context;
	private final List<Station> stations;
	

	public StationAdapter(Context context, List<Station> stations) {
		super(context, R.layout.list_row, stations);
		this.context = context;
		this.stations = stations;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_row, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.listImage);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.listText);
		textView.setText(((com.gatimus.hooftuner.Station)stations.get(position)).getName());
		imageView.setImageBitmap(((com.gatimus.hooftuner.Station)stations.get(position)).getImage());
		return rowView;
	}

}
