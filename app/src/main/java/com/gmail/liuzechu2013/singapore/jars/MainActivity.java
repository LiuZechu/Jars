package com.gmail.liuzechu2013.singapore.jars;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements ShopFragment.ViewCurrentItemsListener, FilesFragment.OnFileOpenListener{
    private BottomNavigationView navView;
    private Button trainingButton;
    private ArrayList<Jar> jarListForTraining;
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_FOR_NEW_CANDY = 2;
    // for saving user data using shared preferences
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String USER_STATISTICS = "UserStatistics";
    // for Candy Tab
    private static ArrayList<Jar> jarList;
    public static final String USER_JAR_FILE_NAME = "userJars.txt";
    public static final String CANDY_TRAINING_FILE_NAME = "candyTraining.txt";
    // for Training Button
    final Random rnd = new Random();

    // user stats:
    // private String username;
    private int level;
    private int exp;
    private int streak;
    private int totalCandiesMade;
    private int totalCandiesGraduated;

    // switch between different screens using bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_files:
                    fragment = new FilesFragment();
                    break;
                case R.id.navigation_jars:
                    fragment = new CandyFragment();
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;
                case R.id.navigation_maker:
                    fragment = new ShopFragment();
                    break;
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }

            return false;
        }
    };

    // to load a fragment in Main Activity
    protected boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }

        return false;
    }

    private void gotoTraining() {
        Intent training = new Intent(this, TrainingActivity.class);
        Gson gson = new Gson();
        String jsonString = gson.toJson(jarListForTraining);
        training.putExtra(TrainingActivity.GET_JAR_LIST, jsonString);
        startActivityForResult(training, REQUEST_CODE);
    }

    // come back to Candy tab from training
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                loadFragment(new CandyFragment());

                Menu menu = navView.getMenu();
                MenuItem menuItem = menu.getItem(1); // highlight the Candy tab
                menuItem.setChecked(true);
            }
        } else if (requestCode == REQUEST_CODE_FOR_NEW_CANDY) {
            if (resultCode == RESULT_OK) {

                // GET DATA FROM data (for the newly created candy)
                String jarTitle = data.getStringExtra(MakeNewCandyActivity.JAR_TITLE);
                String prompt = data.getStringExtra(MakeNewCandyActivity.PROMPT);
                String answer = data.getStringExtra(MakeNewCandyActivity.ANSWER);
                int jarIndex = data.getIntExtra(MakeNewCandyActivity.JAR_INDEX,-1);

                Jar jar = jarList.get(jarIndex);
                jar.addCandy(new Candy(prompt, answer));

                //increment "total candies made" count
                incrementTotalCandiesMade();

                // save and load newly updated jar data
                Gson gson = new Gson();
                String jsonString = gson.toJson(jarList);
                saveToLocalFile(USER_JAR_FILE_NAME, jsonString);

                loadFragment(new CandyFragment());

                Menu menu = navView.getMenu();
                MenuItem menuItem = menu.getItem(1); // highlight the Candy tab
                menuItem.setChecked(true);

            }
        } else {}
    }

    // save a String into local text file on phone
    public void saveToLocalFile(String fileName, String stringToSave) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(stringToSave.getBytes());

            Toast.makeText(this, "changes saved successfully", Toast.LENGTH_SHORT).show();
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

    // view current items from ShopFragment
    @Override
    public void viewCurrentItems(){
        Intent intent = new Intent(this, CurrentItemsActivity.class);
        startActivity(intent);
    }

    // TEST: open PDF file
    @Override
    public void openFile() {
        Intent intent = new Intent(this, ViewFileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final ImageView img = findViewById(R.id.training_expression);
        final String str = "ic_expression" + rnd.nextInt(17);
        img.setImageDrawable
                (
                        getResources().getDrawable(getResourceID(str, "drawable",
                                getApplicationContext()))
                );

        // load data into jarList
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        String jsonStringForJarList = loadFromLocalFile(USER_JAR_FILE_NAME);
        jarList = gson.fromJson(jsonStringForJarList, type);

        // get list of candies to train from saved local file
        String jsonStringForTrainingList = loadFromLocalFile(CANDY_TRAINING_FILE_NAME);
        jarListForTraining = gson.fromJson(jsonStringForTrainingList, type);


        // load default fragment; TODO: need to change to last saved later
        loadFragment(new FilesFragment());
        /*
        Fragment existing = getSupportFragmentManager().findFragmentById(R.id.content);
        if (existing == null) {
            Fragment newFragment = new FilesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, newFragment)
                    .commit();
        }
        */

        // topbar training button
        trainingButton = (Button) findViewById(R.id.training_button);
        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTraining();
            }
        });

        // background work using jobScheduler
        scheduleJob();
    }

    public void saveAllData() {
        // does not save username here
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.LEVEL, level);
        editor.putInt(ProfileFragment.EXP, exp);
        editor.putInt(ProfileFragment.STREAK, streak);
        editor.putInt(ProfileFragment.TOTAL_CANDIES_MADE, totalCandiesMade);
        editor.putInt(ProfileFragment.TOTAL_CANDIES_GRADUATED, totalCandiesGraduated);
        editor.commit();
    }

    public void loadAllData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        // username = sharedPreferences.getString(USERNAME, "default username");
        level = sharedPreferences.getInt(ProfileFragment.LEVEL, -1);
        exp = sharedPreferences.getInt(ProfileFragment.EXP, -1);
        streak = sharedPreferences.getInt(ProfileFragment.STREAK, -1);
        totalCandiesMade = sharedPreferences.getInt(ProfileFragment.TOTAL_CANDIES_MADE, -1);
        totalCandiesGraduated = sharedPreferences.getInt(ProfileFragment.TOTAL_CANDIES_GRADUATED, -1);
    }

    public static ArrayList<Jar> getJarList() {
        return jarList;
    }

    public void incrementTotalCandiesMade() {
        loadAllData();
        this.totalCandiesMade++;
        saveAllData();
        // TODO: only save relevant data, not everything
    }

    public void incrementTotalCandiesGraduated() {
        loadAllData();
        this.totalCandiesGraduated++;
        saveAllData();
        // TODO: only save relevant data, not everything
    }

    // for background work
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, DailyBackgroundJobService.class);
        JobInfo info = new JobInfo.Builder(DailyBackgroundJobService.JOB_ID, componentName)
                .setPeriodic((long) 24 * 60 * 60 * 1000) // One day in milliseconds
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("JobScheduler result", "Job scheduled successfully");
        } else {
            Log.d("JobScheduler result", "Job scheduling failed");
        }
    }

    protected static int getResourceID(final String resName, final String resType, final Context ctx) {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType,
                ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {
            throw new IllegalArgumentException(
                    "No resource string found with name " + resName);
        } else {
            return ResourceID;
        }
    }

    // currently not used
    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(DailyBackgroundJobService.JOB_ID);
        Log.d("JobScheduler result", "Job scheduling cancelled");
    }
}



// FOR TESTING: MANUALLY ADD JAR LIST FOR TRAINING
//        jarListForTraining = new ArrayList<>();
//        Jar jar1 = new Jar("general knowledge");
//        jar1.addCandy(new Candy("wheres canberra", "australia"));
//        jar1.addCandy(new Candy("whos the father of computer science", "alan turing"));
//        Jar jar2 = new Jar("french");
//        jar2.addCandy(new Candy("hello", "BONJOUR!"));
//        jarListForTraining.add(jar1);
//        jarListForTraining.add(jar2);
//
//        if (jarList == null) {
//            jarList = new ArrayList<>();
//        }

//        jarList.add(new Jar("French"));
//        jarList.add(new Jar("Design Thinking"));
//        jarList.add(new Jar("Computer Organisation"));
//        jarList.add(new Jar("PS"));
//        jarList.add(new Jar("GAPI"));
//        jarList.add(new Jar("Chinese"));
//        jarList.add(new Jar("Japanese"));
// TESTING ENDS