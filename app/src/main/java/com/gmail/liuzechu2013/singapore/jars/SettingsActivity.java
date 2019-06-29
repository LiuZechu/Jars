package com.gmail.liuzechu2013.singapore.jars;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private Button usernameButton;
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
        usernameButton = findViewById(R.id.settings_username_button);
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUsername();
            }
        });

        // view graduated candies
        Button viewGraduatedCandiesButton = findViewById(R.id.settings_view_graduated_candies_button);
        viewGraduatedCandiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoArchives();
            }
        });

        // set alarm so that user will be notified of Candies to train
        timePicker = findViewById(R.id.settings_alarm_time_picker);
        Button alarmButton = findViewById(R.id.settings_alarm_button);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });
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

    private void gotoArchives() {
        Intent intent = new Intent(this, ArchiveActivity.class);
        startActivity(intent);
    }

    private void setAlarm() {
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
        } else {
            calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute(),
                    0
            );
        }

        long timeInMillis = calendar.getTimeInMillis();


        // create the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_FOR_ALARM, intent, 0);

        // use RTC instead of RTC_WAKEUP so as not to wake up the phone when it's sleeping
        alarmManager.setInexactRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Daily training reminder set successfully", Toast.LENGTH_SHORT).show();
    }

}
