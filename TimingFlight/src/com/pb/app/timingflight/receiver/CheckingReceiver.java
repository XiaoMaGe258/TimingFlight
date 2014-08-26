package com.pb.app.timingflight.receiver;

import com.pb.app.timingflight.FlightService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/*
    调用方法，需要动态注册一个Receiver
    public void registerCheckingService(){
	    IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
	    CheckingReceiver receiver = new CheckingReceiver();
	    registerReceiver(receiver, filter);
	}
    * */
/**
 * 自动检测Service状态，如果被关闭了，就自动启动。 
 * @author MaYong
 */
public class CheckingReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			// 检查Service状态
			boolean isServiceRunning = false;
			ActivityManager manager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager
					.getRunningServices(Integer.MAX_VALUE)) {
				if ("com.pb.app.timingflight.FlightService"// Service的类名
						.equals(service.service.getClassName())) {
					isServiceRunning = true;
				}

			}
			if (!isServiceRunning) {
				Intent service = new Intent(context, FlightService.class);
				context.startService(service);
			}
		}
	}

}
