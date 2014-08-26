package com.pb.app.timingflight;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FlightService extends Service{
	
	AlarmManager am ;
	@Override
	public void onCreate() {
		Log.d("xmg", "FlightService  onCreate()");
		super.onCreate();
		am = (AlarmManager)getSystemService(ALARM_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("xmg", "FlightService  onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.d("xmg", "FlightService  onDestroy()");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
//	@Override
//	public IBinder onBind(Intent intent) {
//		Log.d("xmg", "FlightService  onBind()");
//		return null;
//	}
	
//	@Override
//	public boolean onUnbind(Intent intent) {
//		Log.d("xmg", "FlightService  onUnbind()");
//		return super.onUnbind(intent);
//	}

//	@Override
//	public void onStart(Intent intent, int startId) {
//		Log.d("xmg", "FlightService  onStart()");
//		super.onStart(intent, startId);
//	}

//	@Override
//	public void onRebind(Intent intent) {
//		Log.d("xmg", "FlightService  onRebind()");
//		super.onRebind(intent);
//	}
	
}
