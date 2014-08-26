package com.pb.app.timingflight.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pb.app.timingflight.airplanemode.AirplaneModeUtils;

/**
 * BroadcastReceiver that handles auto-on/off broadcasts, and turns on/off
 * airplane mode.
 * @author XMG
 */
public class FlightIntentReceiver extends BroadcastReceiver {
	
	static final String TURN_ON_AIRPLANE = "com.pb.app.timingflight.TURN_ON_AIRPLANE";
	static final String TURN_OFF_AIRPLANE = "com.pb.app.timingflight.TURN_OFF_AIRPLANE";

	@Override
	public void onReceive(final Context context, Intent intent) {

		String action = intent.getAction();
//		Log.d("napandroid", action);
		Log.d("xmg","in onReceive   action="+action);
		if (TURN_ON_AIRPLANE.equals(action)) {
            Log.d("xmg","on");
            new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						AirplaneModeUtils.openFlight(context);
					} catch (Exception e) {
					}
				}
			}).start();
		} else if (TURN_OFF_AIRPLANE.equals(action)) {
            Log.d("xmg","off");
            new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						AirplaneModeUtils.closeFlight(context);
					} catch (Exception e) {
					}
				}
			}).start();
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			Config config = new Config(context);
//			Log.d("napandroid", config.toString());
			AlarmManager alarmManager = (AlarmManager) 
					context.getSystemService(Context.ALARM_SERVICE);
//			Log.d("napandroid", alarmManager.toString());
			if (config.isTurnOnAirplaneModeEnabled()) {
				config.registerOnAlarm(context, alarmManager);
			}
			if (config.isTurnOffAirplaneModeEnabled()) {
				config.registerOffAlarm(context, alarmManager);
			}
		}
	}

}
