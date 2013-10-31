package com.janvitek.propertywatchdog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
	MyDialer dialer = MyDialer.getInstance();
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            MyDialer.getInstance().call();
            MyDialer.getInstance().initSending();
        }
        if(action.equals(Intent.ACTION_POWER_CONNECTED)){
        	MyDialer.getInstance().unregisterLocReceiver();
        }
	}
	
	
}