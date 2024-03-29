package com.xyz.orbital.singapore.jars;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DailyBackgroundJobService extends JobService {
    private boolean jobCancelled = false;
    private int numberOfCandiesToTrain; // used for notification
    private String jsonStringForTraining;
    public static final int JOB_ID = 123;
    public static final String CHANNEL_ID = "channelID";
    public static final int NOTIFICATION_ID = 2019;
    private final Handler workHandler = new Handler();
    private Runnable workRunnable;

    @Override
    public boolean onStartJob(final JobParameters params) {
        workRunnable = new Runnable() {
            @Override
            public void run() {
                doBackgroundWork(params);

                boolean reschedule = false;
                jobFinished(params, reschedule);
            }
        };

        workHandler.post(workRunnable);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        workHandler.removeCallbacks(workRunnable);

        // CHANGED TO RETURN FALSE
        return false;
    }

    // updates and checks all Candies; collates all Candies to be trained today and notifies user
    private void doBackgroundWork(JobParameters params) {
        // read user Jars data from local file
        Gson gson = new Gson();
        String jsonString = loadFromLocalFile(MainActivity.USER_JAR_FILE_NAME);
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        ArrayList<Jar> jarList = gson.fromJson(jsonString, type);

        // create a new list inside USER_JAR_FILE_NAME if no such list exists
        if (jarList == null) {
            jarList = new ArrayList<>();
        }

        // process Candies
        // ArrayList<Jar> candiesToTrain = new ArrayList<>(); // for now, create a new jar for candies, for training purpose
        numberOfCandiesToTrain = 0;

        for (Jar jar : jarList) {
            ArrayList<Candy> candyList = jar.getCandies();
            // Jar trainingJar = null;
            // boolean trainingJarCreated = false;
            for (Candy candy: candyList) {
                Log.d("test", candy.getPrompt() + " candy level decremented!");
                candy.decrementCountDown();
                if (candy.shouldTrain()) {
//                    if (!trainingJarCreated) {
//                        trainingJar = new Jar(jar.getTitle());
//                        trainingJarCreated = true;
//                    }
//                    trainingJar.addCandy(candy);
                    numberOfCandiesToTrain++;
                }
            }

//            if (trainingJar != null) {
//                candiesToTrain.add(trainingJar);
//            }
        }

        String toSave = gson.toJson(jarList);
        saveToLocalFile(MainActivity.USER_JAR_FILE_NAME, toSave);


        // update the longest streak maintained
        int currentStreak = loadStreak();

        if (currentStreak > loadLongestStreak()) {
            saveLongestStreak(currentStreak);
        }


        // check whether the current streak can be maintained
        boolean isMaintained = loadStreakMaintained();
        Log.d("test", "streak maintained? : " + isMaintained);

        if (isMaintained) {
            // streak increment by one
            saveStreak(currentStreak + 1);

        } else {
            // streak drops to one
            saveStreak(1);
        }
        // reset isMaintained to FALSE
        saveStreakMaintained(false);



        // save the list of candies to train into a local file
//        String toSave = gson.toJson(candiesToTrain);
//        saveToLocalFile(MainActivity.CANDY_TRAINING_FILE_NAME, toSave);
//        jsonStringForTraining = toSave;

        // fire up notification
        sendNotification();

        // jobFinished(params, false);
    }

    public void sendNotification() {
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, "Training Notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
        }


        // add username to notification text
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(ProfileFragment.USERNAME, "username");
        String toAddressUser = (username == null || username.equals("username")) ? "" : "Hi " + username + "! ";


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mini_icon)
                .setContentTitle("Train your Candies now!")
                .setContentText(toAddressUser + "You have " + numberOfCandiesToTrain + " Candies waiting for you!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // open up Training Activity when the user taps the notification
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.TRAINING_JAR_NAME, MainActivity.CODE_FOR_TRAINING_ALL_CANDIES);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);


        // deliver the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // true indicates current streak is maintained
    private void saveStreakMaintained(boolean isMaintained) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ProfileFragment.STREAK_MAINTAINED, isMaintained);
        editor.commit();
    }

    // true indicates current streak is maintained
    private boolean loadStreakMaintained() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        boolean isMaintained = sharedPreferences.getBoolean(ProfileFragment.STREAK_MAINTAINED, true);

        return isMaintained;
    }

    private void saveStreak(int numberOfDays) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.STREAK, numberOfDays);
        editor.commit();
    }

    private int loadStreak() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        int numberOfDays = sharedPreferences.getInt(ProfileFragment.STREAK, 1);

        return numberOfDays;
    }

    private void saveLongestStreak(int numberOfDays) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.LONGEST_STREAK, numberOfDays);
        editor.commit();
    }

    private int loadLongestStreak() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        int numberOfDays = sharedPreferences.getInt(ProfileFragment.LONGEST_STREAK, 1);

        return numberOfDays;
    }

    // save a String into local text file on phone
    public void saveToLocalFile(String fileName, String stringToSave) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(stringToSave.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // read a string out from local text file
    public String loadFromLocalFile(String fileName) {
        FileInputStream fis = null;
        String output = null;

        try {
            fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            output = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output;
    }
}
