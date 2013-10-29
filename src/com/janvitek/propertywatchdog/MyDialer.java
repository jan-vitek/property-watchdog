package com.janvitek.propertywatchdog;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MyDialer {
	private static MyDialer instance;
	private String callNumber;
	private String serverAddress;
	private Context context;
	private LocationManager locManager;
	private LocationReceiver locReceiver;
	private SharedPreferences mySettings;

	private MyDialer() {
	}

	public static MyDialer getInstance() {
		if (instance == null) {
			Log.d("MyDialer", "Creating new instance");
			instance = new MyDialer();
		}
		return instance;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void loadSettings(boolean startSending) {
		mySettings = context.getSharedPreferences("mySettingsFile", 0);
		callNumber = mySettings.getString("callNumber", "");
		serverAddress = mySettings.getString("serverAddress", "no-server");
		Log.d("MyDialer", "number: " + callNumber + " address: "
				+ serverAddress);
		if (!isPlugged() && startSending) initSending();
	}
	
	public void loadSettings(){
		loadSettings(true);
	}

	public void call() {
		try {
			Log.d("watchdog service", "dialing" + callNumber);
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + callNumber));
			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			Log.e("helloandroid dialing example", "Call failed", e);
		}
	}

	public void sendLocation(Location loc) {
		new RequestTask().execute("http://" + serverAddress
				+ "/write.php?provider=" + loc.getProvider() + "&lat="
				+ loc.getLatitude() + "&lon=" + loc.getLongitude() + "&time="
				+ loc.getTime() + "&accuracy=" + loc.getAccuracy());
	}

	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}

	public String getCallNumber() {
		return callNumber;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setMailAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public void initSending() {
		locManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locReceiver = new LocationReceiver();
		locManager.requestLocationUpdates("gps", 10000, 3, locReceiver);
		locManager.requestLocationUpdates("network", 30000, 50, locReceiver);
	}

	public void unregisterLocReceiver() {
		if (locManager != null && locManager != null) {
			locManager.removeUpdates(locReceiver);
		}
	}

	private boolean isPlugged() {
		Intent ix = context.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		boolean plugged = (ix != null) && (ix.getIntExtra("plugged", 0) != 0);
		return plugged;
	}
}
