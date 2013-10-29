package com.janvitek.propertywatchdog;

import com.janvitek.propertywatchdog.IMyPhoneDisconnectedService;
import com.janvitek.propertywatchdog.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class PhoneDisconnectedService extends Service {

	private BroadcastReceiver serviceReceiver;
	private NotificationManager mNotificationManager;
	private MyDialer dialer;
	private Notification not;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return myPhoneDisconnectedServiceStub;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
        dialer = MyDialer.getInstance();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		dialer.setContext(getApplicationContext());
		dialer.loadSettings();
		showNotification();
		this.serviceReceiver = new MyReceiver();
		this.registerReceiver(this.serviceReceiver, new IntentFilter("android.intent.action.ACTION_POWER_DISCONNECTED"));
		this.registerReceiver(this.serviceReceiver, new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED"));
	}
	
	@Override
	public void onDestroy(){
		mNotificationManager.cancel(1);
		unregisterReceiver(serviceReceiver);
		dialer.unregisterLocReceiver();
		super.onDestroy();
	}
	
	private IMyPhoneDisconnectedService.Stub myPhoneDisconnectedServiceStub = new IMyPhoneDisconnectedService.Stub() {

		@Override
		public String getRunningConfig() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setCallNumber(String number) throws RemoteException {
			dialer.setCallNumber(number);
		}
	  
	};
	
	private void showNotification(){
	    not = new Notification(R.drawable.ic_launcher, "Application started", System.currentTimeMillis());
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, PhoneDisconnectedService.class), Notification.FLAG_ONGOING_EVENT);        
	    not.flags = Notification.FLAG_ONGOING_EVENT;
	    not.setLatestEventInfo(this, "Property Watchdog", "tel: " + dialer.getCallNumber() + " server: " + dialer.getServerAddress(), contentIntent);
	    mNotificationManager.notify(1, not);
	}
	
	

}
