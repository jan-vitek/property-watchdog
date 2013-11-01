package com.janvitek.propertywatchdog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class InternetStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("InternetStateReceiver", "Network connectivity changed");

		if (intent.getExtras() != null) {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

			if (ni != null && ni.isConnectedOrConnecting()) {
				List<String> list = new ArrayList<String>();
				Log.d("InternetStateReceiver", "Network " + ni.getTypeName()
						+ " connected");
				String path = Environment.getExternalStorageDirectory()
						+ "/PropertyWatchdog/" + "failedUri.txt";
				File file = new File(path);
				if (file.exists()) {
					try {
						FileInputStream fstream = new FileInputStream(path);
						DataInputStream in = new DataInputStream(fstream);
						BufferedReader br = new BufferedReader(new InputStreamReader(in));
						String strLine;

						while ((strLine = br.readLine()) != null) {
							list.add(strLine);
						}

						in.close();
						
						file.delete();
						
					} catch (Exception e) {
						System.err.println("Error: " + e.getMessage());
					}
					for(String line : list){
						new RequestTask().execute(line);
					}
				}
			} else if (intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
				Log.d("InternetStateReceiver",
						"There's no network connectivity");
			}
		}
	}

}
