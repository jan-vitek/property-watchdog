package com.janvitek.propertywatchdog;

import com.janvitek.propertywatchdog.IMyPhoneDisconnectedService;
import com.janvitek.propertywatchdog.R;

import android.R.bool;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ServiceConnection;


public class MainActivity extends Activity {

	static Button setButton;
	static TextView dialNumberView;
	static MyDialer dialer ;
	static TextView callNumberInfo;
	static TextView serverAddressInfo;
	boolean started = false;
	static Button startService;
	static Button stopService;
	static Button stateButton;
	static RemoteServiceConnection conn = null;
	private IMyPhoneDisconnectedService remoteService;
	static TextView serverAddress;
	SharedPreferences mySettings;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        
        
        
        setButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//dialer.setCallNumber(dialNumberView.getText().toString());
				//dialer.setMailAddress(serverAddress.getText().toString());
				SharedPreferences.Editor editor = mySettings.edit();
			    editor.putString("callNumber", dialNumberView.getText().toString());
			    editor.putString("serverAddress", serverAddress.getText().toString());
			    editor.commit();
			    dialer.loadSettings(false);
			    refreshCurrentSettings();
			}
		});
        
        startService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startService();
			}
		});
        
        stopService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopService();
			}
		});
        
        stateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String ans = isMyServiceRunning() ? "Running" : "Stopped";
				Toast.makeText(MainActivity.this, ans, Toast.LENGTH_SHORT).show();	
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    void initView() {
      dialNumberView = (TextView) findViewById(R.id.dialNumber);
      dialer = MyDialer.getInstance();
      dialer.setContext(this.getApplicationContext());
      setButton = (Button) findViewById(R.id.setButton);
      callNumberInfo = (TextView) findViewById(R.id.callNumberInfo);
      serverAddressInfo = (TextView) findViewById(R.id.serverAddressInfo);
      startService = (Button) findViewById(R.id.startService);
      stopService = (Button) findViewById(R.id.stopService);
      stateButton = (Button) findViewById(R.id.state);
      serverAddress = (TextView) findViewById(R.id.serverAddress);
      mySettings = getSharedPreferences("mySettingsFile", 0);
      
      dialNumberView.setText(mySettings.getString("callNumber", ""));
      serverAddress.setText(mySettings.getString("serverAddress", ""));
      
      refreshCurrentSettings();
    }

    private void refreshCurrentSettings(){
      callNumberInfo.setText("Tel: " + mySettings.getString("callNumber", ""));
      serverAddressInfo.setText("Server: http://" + mySettings.getString("serverAddress", "") + "/");
    }
    
    private void startService(){
    	if (isMyServiceRunning()) {
   			Toast.makeText(MainActivity.this, "Service already started", Toast.LENGTH_SHORT).show();
   		} else {
   			Intent i = new Intent();
   			i.setClassName("com.janvitek.propertywatchdog", "com.janvitek.propertywatchdog.PhoneDisconnectedService");
   			//i.putExtra("dialNumber", dialNumberView.getText().toString());
   			startService(i);
   			started = true;
   			Log.d( getClass().getSimpleName(), "startService() " + dialNumberView.getText().toString() );
   			//bindService();
   			//releaseService();
   		}
    }
    
    private void stopService(){
    	if (!isMyServiceRunning()) {
   			Toast.makeText(MainActivity.this, "Service not yet started", Toast.LENGTH_SHORT).show();
  		} else {
   			Intent i = new Intent();
   			i.setClassName("com.janvitek.propertywatchdog", "com.janvitek.propertywatchdog.PhoneDisconnectedService");
   			stopService(i);
   			started = false;
   			Log.d( getClass().getSimpleName(), "stopService()" );
  		}
    }
    
    private void bindService() {
		if(conn == null) {
			conn = new RemoteServiceConnection();
			Intent i = new Intent();
			i.setClassName("com.janvitek.propertywatchdog", "com.janvitek.propertywatchdog.PhoneDisconnectedService");
			bindService(i, conn, Context.BIND_AUTO_CREATE);
			Log.d( getClass().getSimpleName(), "bindService()" );
		} else {
	        Toast.makeText(getApplicationContext(), "Cannot bind - service already bound", Toast.LENGTH_SHORT).show();
		}
    }
    
    private void releaseService() {
		if(conn != null) {
			unbindService(conn);
			conn = null;
			Log.d( getClass().getSimpleName(), "releaseService()" );
		} else {
			Toast.makeText(getApplicationContext(), "Cannot unbind - service not bound", Toast.LENGTH_SHORT).show();
		}
     }
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PhoneDisconnectedService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    class RemoteServiceConnection implements ServiceConnection {
    	@Override
    	public void onServiceConnected(ComponentName className, 
			IBinder boundService ) {
          remoteService = IMyPhoneDisconnectedService.Stub.asInterface((IBinder)boundService);
          try {
			remoteService.setCallNumber(dialNumberView.getText().toString());
		} catch (RemoteException e) {
			Toast.makeText(getApplicationContext(), "error while setting up daemon", Toast.LENGTH_SHORT);
		}
          Log.d( getClass().getSimpleName(), "onServiceConnected()" );
        }

    	@Override
        public void onServiceDisconnected(ComponentName className) {
          remoteService = null;
		   Log.d( getClass().getSimpleName(), "onServiceDisconnected" );
        }
    };
}



