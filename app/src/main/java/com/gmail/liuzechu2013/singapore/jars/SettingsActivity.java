package com.gmail.liuzechu2013.singapore.jars;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Templates;

public class SettingsActivity extends AppCompatActivity {
    public static final String ALARM_HOUR = "alarmhour";
    public static final String ALARM_MINUTE= "alarmminute";

    private EditText usernameEditText;
    private TimePicker timePicker;

    public static final int REQUEST_CODE_FOR_ALARM = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameEditText = findViewById(R.id.settings_username_edit_text);
        // pre-fill edit text with current username
        Intent intent = getIntent();
        usernameEditText.setText(intent.getStringExtra(ProfileFragment.USERNAME));

        // set alarm so that user will be notified of Candies to train
        timePicker = findViewById(R.id.settings_alarm_time_picker);

        FloatingActionButton saveButton = findViewById(R.id.settings_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
                editUsername();
            }
        });

        //load clock data
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(sharedPreferences.getInt(ALARM_HOUR, 0));
            timePicker.setMinute(sharedPreferences.getInt(ALARM_MINUTE, 0));
        } else {
            timePicker.setCurrentHour(sharedPreferences.getInt(ALARM_HOUR, 0));
            timePicker.setCurrentMinute(sharedPreferences.getInt(ALARM_MINUTE, 0));
        }

    }

    private void editUsername() {
        String username = usernameEditText.getText().toString();

        if (username == null || username.length() == 0) {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ProfileFragment.USERNAME, username);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void setAlarm() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= 23) {
            calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.getHour(),
                    timePicker.getMinute(),
                    0
            );
            editor.putInt(ALARM_HOUR, timePicker.getHour());
            editor.putInt(ALARM_MINUTE, timePicker.getMinute());
        } else {
            calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute(),
                    0
            );
            editor.putInt(ALARM_HOUR, timePicker.getCurrentHour());
            editor.putInt(ALARM_MINUTE, timePicker.getCurrentMinute());
        }
        editor.apply();

        // EDITED HERE: to prevent alarm from firing off immediately when an earlier time is set
        long timeInMillis;
        long rawTimeInMillis = calendar.getTimeInMillis();
        long difference = Calendar.getInstance().getTimeInMillis() - rawTimeInMillis;
        if (difference > 0) {
            // the set time is EARLIER than current time
            timeInMillis = rawTimeInMillis + TimeUnit.DAYS.toMillis(1);
            Log.d("test", "time too early");
        } else {
            // the set time is LATER than current time; as per normal
            timeInMillis = rawTimeInMillis;
            Log.d("test", "set time later than current time");
        }

        // create the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_FOR_ALARM, intent, 0);

        // use RTC instead of RTC_WAKEUP so as not to wake up the phone when it's sleeping
        alarmManager.setInexactRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
