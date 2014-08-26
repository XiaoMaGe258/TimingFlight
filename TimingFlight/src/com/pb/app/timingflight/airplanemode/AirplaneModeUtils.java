package com.pb.app.timingflight.airplanemode;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AirplaneModeUtils {
	
	/**toggle Flight Mode*/
    public static void toggle(Context context) throws Exception {
        if (Build.VERSION.SDK_INT >= 17) {
            toggleAboveApiLevel17();
        } else {
            toggleBelowApiLevel17(context);
        }
    }
    /**open Flight Mode*/
    public static void openFlight(Context context) throws Exception {
    	if (Build.VERSION.SDK_INT >= 17) {
    		openFlightAboveApiLevel17();
    	} else {
    		openBelowApiLevel17(context);
    	}
    }
    /**close Flight Mode*/
    public static void closeFlight(Context context) throws Exception {
    	if (Build.VERSION.SDK_INT >= 17) {
    		closeFlightAboveApiLevel17();
    	} else {
    		closeBelowApiLevel17(context);
    	}
    }
    
    private static void toggleAboveApiLevel17() throws Exception {
        // Android 4.2 and above
        try {
            ShellUtils.sudo("ndc", "resolver", "flushdefaultif");
        } catch (Exception e) {
            ShellUtils.sudo("settings", "put", "global", "airplane_mode_on", "1");
            ShellUtils.sudo("am", "broadcast", "-a", "android.intent.action.AIRPLANE_MODE", "--ez state", "true");
            ShellUtils.sudo("settings", "put", "global", "airplane_mode_on", "0");
            ShellUtils.sudo("am", "broadcast", "-a", "android.intent.action.AIRPLANE_MODE", "--ez state", "false");
        }
    }
    
    @SuppressWarnings("deprecation")
	private static void toggleBelowApiLevel17(Context context) throws Exception {
        // Android 4.2 below
        Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 1);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", true);
        context.sendBroadcast(intent);
        Thread.sleep(3000);
        Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0);
        intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", false);
        context.sendBroadcast(intent);
    }
    
    // Android 4.2 and above open Flight Mode
    private static void openFlightAboveApiLevel17() throws Exception {
        try {
            ShellUtils.sudo("settings", "put", "global", "airplane_mode_on", "1");
            ShellUtils.sudo("am", "broadcast", "-a", "android.intent.action.AIRPLANE_MODE", "--ez state", "true");
            Log.e("xmg", "sudo success");
        } catch (Exception e) {
        	Log.e("xmg", "sudo fail");
        	ShellUtils.sudo("ndc", "resolver", "flushdefaultif");
        }
    }
    
    // Android 4.2 and above close Flight Mode
    private static void closeFlightAboveApiLevel17() throws Exception {
    	try {
            ShellUtils.sudo("settings", "put", "global", "airplane_mode_on", "0");
            ShellUtils.sudo("am", "broadcast", "-a", "android.intent.action.AIRPLANE_MODE", "--ez state", "false");
    		Log.e("xmg", "sudo success");
    	} catch (Exception e) {
    		Log.e("xmg", "sudo fail");
    		ShellUtils.sudo("ndc", "resolver", "flushdefaultif");
    	}
    }
    
    // Android 4.2 below open Flight Mode
    @SuppressWarnings("deprecation")
	private static void openBelowApiLevel17(Context context) throws Exception {
        Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 1);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", true);
        context.sendBroadcast(intent);
    }
    
    // Android 4.2 below close Flight Mode
    @SuppressWarnings("deprecation")
	private static void closeBelowApiLevel17(Context context) throws Exception {
    	Settings.System.putInt(
    			context.getContentResolver(),
    			Settings.System.AIRPLANE_MODE_ON, 0);
    	Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    	intent.putExtra("state", false);
    	context.sendBroadcast(intent);
    }
    
}
