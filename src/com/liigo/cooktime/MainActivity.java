package com.liigo.cooktime;

import java.util.Calendar;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class MainActivity extends Activity {

	private TextView   textViewShowTime;
	private TimePicker timePickerMealTime;
	private Button     buttonCalcTime;
	SharedPreferences  pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textViewShowTime   = (TextView)   findViewById(R.id.textViewShowTime);
		timePickerMealTime = (TimePicker) findViewById(R.id.timePickerMealTime);
		buttonCalcTime     = (Button)     findViewById(R.id.buttonCalcTime);
		
		// 初始化开饭时间，恢复上次运行时存储的开饭时间
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		timePickerMealTime.setIs24HourView(true);
		timePickerMealTime.setCurrentHour(pref.getInt("MealTime.Hour", 8));
		timePickerMealTime.setCurrentMinute(pref.getInt("MealTime.Minute", 0));
		
		calcTime(); // 计算并显示距离开饭时间的间隔时长

		buttonCalcTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				calcTime();
			}
		});
		
		timePickerMealTime.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
				calcTime();
				// 存储开饭时间
				Editor editor = pref.edit();
				editor.putInt("MealTime.Hour", hour);
				editor.putInt("MealTime.Minute", minute);
				editor.commit();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * 计算并显示当前时间与开饭时间之间间隔的时间差
	 */
	private void calcTime() {
		// 现在时间(本地时区)
		Calendar calendar = Calendar.getInstance();
		int fromHour   = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制
		int fromMinute = calendar.get(Calendar.MINUTE);
		// 开饭时间
		int endHour   = timePickerMealTime.getCurrentHour(); // 24小时制
		int endMinute = timePickerMealTime.getCurrentMinute();
		Log.d("cooktime", "from "+fromHour+":"+fromMinute+" to "+endHour+":"+endMinute);
		
		// 取二者时间间隔
		int hour, minute;
		if(fromHour > endHour || (fromHour == endHour && fromMinute > endMinute)) {
			endHour += 24;
		}
		hour = endHour - fromHour;
		minute = endMinute - fromMinute;
		if(minute < 0) {
			minute += 60;
			hour--;
			if(hour < 0) hour += 24;
		}

		String showtimeStr = "";
		showtimeStr += hour + "小时" + minute + "分钟";
		textViewShowTime.setText(showtimeStr);
	}

}
