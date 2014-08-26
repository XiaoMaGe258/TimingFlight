package com.pb.app.timingflight;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
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
            	mConfig.setTurnOnAirplaneModeEnabled(isChecked);
            	mConfig.setTurnOffAirplaneModeEnabled(isChecked);
                if(isChecked){
                    timeSettingLayout.setVisibility(View.VISIBLE);
                    mConfig.registerOnAlarm(getApplicationContext(), mAlarmManager);
                    mConfig.registerOffAlarm(getApplicationContext(), mAlarmManager);
                }else{
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
        
        initUI();
//		registerCheckingService();
	}
	public void initUI(){
		if(mConfig.isTurnOnAirplaneModeEnabled()){
        	switcher.setChecked(true);
        	 openText.setText(getText(R.string.open_flight_time) + ":  "+mConfig.getTurnOnTime().format("%H:%M"));
        	 closeText.setText(getText(R.string.close_flight_time) + ":  "+mConfig.getTurnOffTime().format("%H:%M"));
        }
	}
	
//	public void registerCheckingService(){
//	    IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK); 
//	    CheckingReceiver receiver = new CheckingReceiver(); 
//	    registerReceiver(receiver, filter); 
//	}
	
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
