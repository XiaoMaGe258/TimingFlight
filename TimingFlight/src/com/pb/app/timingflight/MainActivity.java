package com.pb.app.timingflight;

import java.io.DataOutputStream;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pb.app.timingflight.airplanemode.ShellUtils;
import com.pb.app.timingflight.receiver.Config;


public class MainActivity extends Activity {

	Context context;
	
    Switch switcher;
    LinearLayout timeSettingLayout;
    RelativeLayout switchRelativeLayout;
    RelativeLayout openLayout;
    RelativeLayout closeLayout;
    TextView openText;
    TextView closeText;
    Config mConfig;
    AlarmManager mAlarmManager;

    boolean isAbove17 = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
        mConfig = new Config(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        
        switcher = (Switch) findViewById(R.id.switch_on);
        switchRelativeLayout = (RelativeLayout) findViewById(R.id.switch_layout);

        timeSettingLayout = (LinearLayout) findViewById(R.id.time_setting_layout);
        openLayout = (RelativeLayout) findViewById(R.id.time_setting_open_layout);
        closeLayout = (RelativeLayout) findViewById(R.id.time_setting_close_layout);
        openText = (TextView) findViewById(R.id.time_setting_open_text);
        closeText = (TextView) findViewById(R.id.time_setting_close_text);
        
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                	if(isAbove17 && !ShellUtils.checkRooted()){
                		upgradeRootPermission(getPackageCodePath());
                	}else{
                		mConfig.setTurnOnAirplaneModeEnabled(isChecked);
                		mConfig.setTurnOffAirplaneModeEnabled(isChecked);
                		timeSettingLayout.setVisibility(View.VISIBLE);
                		mConfig.registerOnAlarm(getApplicationContext(), mAlarmManager);
                		mConfig.registerOffAlarm(getApplicationContext(), mAlarmManager);
                	}
                }else{
                	mConfig.setTurnOnAirplaneModeEnabled(isChecked);
                	mConfig.setTurnOffAirplaneModeEnabled(isChecked);
                    timeSettingLayout.setVisibility(View.INVISIBLE);
                    mConfig.cancelOnAlarm(getApplicationContext(), mAlarmManager);
                    mConfig.cancelOffAlarm(getApplicationContext(), mAlarmManager);
                }
            }
        });
        switchRelativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switcher.setChecked(!switcher.isChecked());
            }
        });
        openLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Time onTime = mConfig.getTurnOnTime();
                new TimePickerDialog(context,
                        TimePickerDialog.THEME_DEVICE_DEFAULT_DARK,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mConfig.cancelOnAlarm(getApplicationContext(), mAlarmManager);
                                mConfig.setTurnOnHourMinute(hourOfDay, minute);
                                
                                if (mConfig.isTurnOnAirplaneModeEnabled()) {
                                	Log.d("xmg", "re register  121");
                                    mConfig.registerOnAlarm(getApplicationContext(), mAlarmManager);
                                }
                                openText.setText(getText(R.string.open_flight_time) + ":  " + 
                                		mConfig.getTurnOnTime().format("%H:%M"));
                            }
                        }, onTime.hour, onTime.minute, true).show();
            }
        });
        closeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Time offTime = mConfig.getTurnOffTime();
                new TimePickerDialog(context,
                        TimePickerDialog.THEME_DEVICE_DEFAULT_DARK,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mConfig.cancelOffAlarm(getApplicationContext(), mAlarmManager);
                                mConfig.setTurnOffHourMinute(hourOfDay, minute);
                                if (mConfig.isTurnOffAirplaneModeEnabled()) {
                                    mConfig.registerOffAlarm(getApplicationContext(), mAlarmManager);
                                }
                                closeText.setText(getText(R.string.close_flight_time) + ":  "+
                                		mConfig.getTurnOffTime().format("%H:%M"));
                            }
                        }, offTime.hour, offTime.minute, true).show();
            }
        });
	}
	@Override
	protected void onResume() {
		super.onResume();
		checkRootState();
		initUI();
	}

	public void checkRootState(){
		if(Build.VERSION.SDK_INT >= 17){
			isAbove17 = true;
			upgradeRootPermission(getPackageCodePath());
		} else {
			isAbove17 = false;
		}
	}

	public void initUI(){
		if(isAbove17 && !ShellUtils.checkRooted()){
			upgradeRootPermission(getPackageCodePath());
			switcher.setChecked(false);
		}else{
			if(mConfig.isTurnOnAirplaneModeEnabled()){
				switcher.setChecked(true);
				openText.setText(getText(R.string.open_flight_time) + ":  "+mConfig.getTurnOnTime().format("%H:%M"));
				closeText.setText(getText(R.string.close_flight_time) + ":  "+mConfig.getTurnOffTime().format("%H:%M"));
			}
		}
	}
	
	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * @param pkgCodePath context.getPackageCodePath();
	 * @return应用程序是/否获取Root权限
	 */
	public static boolean upgradeRootPermission(String pkgCodePath) {
	    Process process = null;
	    DataOutputStream os = null;
	    Log.d("xmg", "pkgCodePath="+pkgCodePath);
	    try {
	        String cmd="chmod 777 " + pkgCodePath;
	        process = Runtime.getRuntime().exec("su"); //切换到root帐号
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes(cmd + "\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
	    } catch (Exception e) {
	        return false;
	    } finally {
	        try {
	            if (os != null) {
	                os.close();
	            }
	            process.destroy();
	        } catch (Exception e) {
	        }
	    }
	    return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {

            new AlertDialog.Builder(context)
                    .setTitle(getText(R.string.action_about))
                    .setMessage(getText(R.string.action_about_text))
                    .setPositiveButton(getText(R.string.action_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
