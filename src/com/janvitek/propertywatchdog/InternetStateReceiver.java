package com.janvitek.propertywatchdog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
            File file = MyDialer.getInstance().getContext().getFileStreamPath("failedUri.txt");
			if (ni != null && ni.isConnectedOrConnecting() && file.exists()) {
				List<String> list = new ArrayList<String>();
				
				/**
				 * this code was used when failed uri was saved to external storage
				 * 
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
					*/
				
				    FileInputStream fin;
					try {
						Log.d("InternetStateReceiver", "Sending failed uri");
						fin = MyDialer.getInstance().getContext().openFileInput("failedUri.txt");
						DataInputStream in = new DataInputStream(fin);
						BufferedReader br = new BufferedReader(new InputStreamReader(in));
						String strLine;

						while ((strLine = br.readLine()) != null) {
							list.add(strLine);
						}

						in.close();
						fin.close();
					} catch (FileNotFoundException e) {
						Log.i("InternetStateReceiver", "File with failed uri was not found.");
					} catch (IOException e) {
						Log.e("InternetStateReceiver", "IOException while reading file with failed uri");
					}
				    
				
					for(String line : list){
						new RequestTask().execute(line);
					}
			} else if (intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
				Log.d("InternetStateReceiver",
						"There's no network connectivity");
			}
		}
	}

}
