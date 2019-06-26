package com.gmail.liuzechu2013.singapore.jars;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

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

    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // when job is cancelled
        jobCancelled = true;

        return true;
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
        ArrayList<Jar> candiesToTrain = new ArrayList<>(); // for now, create a new jar for candies, for training purpose
        numberOfCandiesToTrain = 0;

        for (Jar jar : jarList) {
            ArrayList<Candy> candyList = jar.getCandies();
            Jar trainingJar = null;
            boolean trainingJarCreated = false;
            for (Candy candy: candyList) {
                candy.decrementCountDown();
                if (candy.shouldTrain()) {
                    if (!trainingJarCreated) {
                        trainingJar = new Jar(jar.getTitle());
                        trainingJarCreated = true;
                    }
                    trainingJar.addCandy(candy);
                    numberOfCandiesToTrain++;
                }
            }

            if (trainingJar != null) {
                candiesToTrain.add(trainingJar);
            }
        }


        // save the list of candies to train into a local file
        String toSave = gson.toJson(candiesToTrain);
        saveToLocalFile(MainActivity.CANDY_TRAINING_FILE_NAME, toSave);
        jsonStringForTraining = toSave;

        // fire up notification
        sendNotification();

        jobFinished(params, true);
    }

    public void sendNotification() {
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, "Training Notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_navbar_candy_24dp) // placeholder icon; TODO: change this!!
                .setContentTitle("Train your Candies now!")
                .setContentText("You have " + numberOfCandiesToTrain + " Candies waiting for you!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.GET_JAR_LIST, jsonStringForTraining);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);


        // deliver the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
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
