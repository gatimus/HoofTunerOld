package com.gatimus.hooftuner;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


public class About extends DialogFragment {

	private static final String TAG = "About:";
	private Builder builder;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.i(TAG, "Create");
		super.onCreateDialog(savedInstanceState);
		builder = new Builder(getActivity());
		builder.setTitle(R.string.action_about);
		builder.setMessage(R.string.msg_about);
		builder.setIcon(R.drawable.square_logo_transparent_black);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				Log.i(TAG, "Ok");
				dialog.dismiss();
			}
		});
		builder.setNeutralButton("Go to site...", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ponyvillelive.com/about"));
				startActivity(browserIntent);
				dialog.dismiss();
			}
		});
		return builder.create();
	} //onCreateDialog

} //class
