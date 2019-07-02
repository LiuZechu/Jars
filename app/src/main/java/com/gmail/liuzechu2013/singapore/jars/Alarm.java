package com.gmail.liuzechu2013.singapore.jars;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // RingTone for testing purpose!
//        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
//        mediaPlayer.start();
        Log.d("test", "ALARM FIRED");

        scheduleJob();

    }


    // for background work
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(context, DailyBackgroundJobService.class);
        JobInfo info = new JobInfo.Builder(DailyBackgroundJobService.JOB_ID, componentName)
                .setMinimumLatency(1)
                .setOverrideDeadline(1)
                .setPersisted(false)
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("JobScheduler result", "Job scheduled successfully");
        } else {
            Log.d("JobScheduler result", "Job scheduling failed");
        }
    }

    // currently not used
    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(DailyBackgroundJobService.JOB_ID);
        Log.d("JobScheduler result", "Job scheduling cancelled");
    }
}
