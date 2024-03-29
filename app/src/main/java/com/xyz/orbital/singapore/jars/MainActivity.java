package com.xyz.orbital.singapore.jars;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.ProgressBar;
import android.widget.TextView;
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
        implements MakerFragment.ViewCurrentItemsListener, FilesFragment.OnFileOpenListener{
    private BottomNavigationView navView;
    private Button trainingButton;
    private ArrayList<Jar> jarListForTraining;
    public static final int REQUEST_CODE_FOR_TRAINING = 1;
    public static final int REQUEST_CODE_FOR_NEW_CANDY = 2;
    public static final int REQUEST_CODE_FOR_USERNAME = 3;
    public static final int REQUEST_CODE_FOR_VIEW_CANDIES = 4;
    // for saving user data using shared preferences
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String USER_STATISTICS = "UserStatistics";
    public static final String USER_JAR_NAME_ARRAY = "UserJarNameArray"; // for easy access when making candy from other apps
    // for Candy Tab
    private static ArrayList<Jar> jarList;
    public static final String USER_JAR_FILE_NAME = "userJars.txt";
    public static final String CANDY_TRAINING_FILE_NAME = "candyTraining.txt";
    // for Profile Tab line graph
    public static final String LINE_GRAPH_CANDIES_GRADUATED_FILE_NAME = "lineGraphCandiesGraduated.txt";
    public static final String LINE_GRAPH_CANDIES_TRAINED_FILE_NAME = "lineGraphCandiesTrained.txt";
    // for Training Button
    final Random rnd = new Random();
    public static final String CODE_FOR_TRAINING_ALL_CANDIES = "codeForTrainingAllCandies";

    // top bar
    private TextView topbarLevelText;
    private TextView topbarStreaklText;
    private TextView topbarSugarText;
    private ProgressBar topbarLevelRing;

    // user stats to be saved in SharedPreferences
    private String username;
    private int exp;
    private int sugar; // current amount of sugar owned by the user
    private int streak; // this is the current streak
    private boolean streakMaintained; // resets to false everyday during background processing;
                                      // turns to true after training; used to indicate whether streak is maintained
    private int selectedCandyColours;
    private boolean[] selectedExpressions;

    private int level;
    private int levelStar;

    private int longestStreak;
    private int longestStreakStar;

    private int totalCandiesMade;
    private int totalCandiesMadeStar;

    private int totalCandiesGraduated;
    private int totalCandiesGraduatedStar;

    private int totalJarsMade;
    private int totalJarsMadeStar;

    private int totalSugarSpent;
    private int totalSugarSpentStar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // topbar training button
        trainingButton = (Button) findViewById(R.id.training_button);
        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTraining();
            }
        });

        final ImageView img = findViewById(R.id.training_expression);
        final String str = "ic_expression" + rnd.nextInt(62);
        // TODO: need to select the correct expression.
        img.setImageDrawable
                (
                        getResources().getDrawable(getResourceID(str, "drawable",
                                getApplicationContext()))
                );

        String[] arr = {"#ef6256", "#f99c1c", "#fec41b", "#47b585", "#5bc4bf", "#825ca4", "#e96ca9"};
        trainingButton.getBackground().setColorFilter(Color.parseColor(arr[new Random().nextInt(7)]), PorterDuff.Mode.SRC_OVER);

        // TODO: check whether there's unnecessary loading
        loadAllData();


        // load data into topbar
        topbarLevelText = findViewById(R.id.topbar_level_text);
        topbarStreaklText = findViewById(R.id.topbar_streak_text);
        topbarSugarText = findViewById(R.id.topbar_sugar_text);
        topbarLevelRing = findViewById(R.id.topbar_level_ring);
        loadDataIntoTopBar();

        // load data into jarList
        loadDataIntoJarList();

        // prevent null pointer exception for jarList
        if (jarList == null) {
            jarList = new ArrayList<>();
            Log.d("test", "jar list not null");
        }

//        // get list of candies to train from saved local file
//        String jsonStringForTrainingList = loadFromLocalFile(CANDY_TRAINING_FILE_NAME);
//        jarListForTraining = gson.fromJson(jsonStringForTrainingList, type);

        // process which candies need to be trained
        // processCandiesForTraining();

        // FOR MAKING CANDY OUTSIDE THE APP
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra(FloatingWindowService.FLOATING_WINDOW_FLAG, false)) {
            loadFragment(new JarsFragment());

            Menu menu = navView.getMenu();
            MenuItem menuItem = menu.getItem(1); // highlight the Candy tab
            menuItem.setChecked(true);

            Log.d("test", "if block");
        } else {
            // load default fragment; TODO: need to change to last saved later
            loadFragment(new FilesFragment());
            Log.d("test", "else block");
        }


        // loadFragment(new FilesFragment());

        /*
        Fragment existing = getSupportFragmentManager().findFragmentById(R.id.content);
        if (existing == null) {
            Fragment newFragment = new FilesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, newFragment)
                    .commit();
        }
        */



        // background work using jobScheduler
        // scheduleJob();
    }

    public void loadDataIntoJarList() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        String jsonStringForJarList = loadFromLocalFile(USER_JAR_FILE_NAME);
        //Log.d("test fromMain", jsonStringForJarList);
        jarList = gson.fromJson(jsonStringForJarList, type);
    }

    // moved to training activity
    public void processCandiesForTraining() {
        // process Candies
        jarListForTraining = new ArrayList<>(); // for now, create a new jar for candies, for training purpose

        for (Jar jar : jarList) {
            ArrayList<Candy> candyList = jar.getCandies();
            Jar trainingJar = null;
            boolean trainingJarCreated = false;
            for (Candy candy: candyList) {
                // candy.decrementCountDown(); // commented out because this should have been done in background work already
                if (candy.shouldTrain()) {
                    if (!trainingJarCreated) {
                        trainingJar = new Jar(jar.getTitle());
                        trainingJarCreated = true;
                    }
                    trainingJar.addCandy(candy);
                }
            }

            if (trainingJar != null) {
                jarListForTraining.add(trainingJar);
            }
        }

//        // save the list of candies to train into a local file
//        Gson gson = new Gson();
//        String toSave = gson.toJson(candiesToTrain);
//        saveToLocalFile(CANDY_TRAINING_FILE_NAME, toSave);

    }


    public void loadDataIntoTopBar() {
        if (topbarLevelText != null && topbarStreaklText != null && topbarSugarText != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            level = sharedPreferences.getInt(ProfileFragment.LEVEL, 1);
            streak = sharedPreferences.getInt(ProfileFragment.STREAK, 1);
            sugar = sharedPreferences.getInt(ProfileFragment.SUGAR, 0);
            exp = sharedPreferences.getInt(ProfileFragment.EXP, 0);

            topbarLevelText.setText("" + level);
            topbarStreaklText.setText("" + streak);
            topbarSugarText.setText("" + sugar);
            topbarLevelRing.setProgress(exp * 100 / getExpToLevelUp(level));

        } else {
            Log.d("Loading Topbar", "Topbar views absent!");
        }
    }


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
                    fragment = new JarsFragment();
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;
                case R.id.navigation_maker:
                    fragment = new MakerFragment();
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
        // Gson gson = new Gson();
        // String jsonString = gson.toJson(jarListForTraining);
        // training.putExtra(TrainingActivity.GET_JAR_LIST, jsonString);
        // training.putExtra(TrainingActivity.CURRENT_STREAK, streak);
        training.putExtra(TrainingActivity.TRAINING_JAR_NAME, CODE_FOR_TRAINING_ALL_CANDIES);
        startActivityForResult(training, REQUEST_CODE_FOR_TRAINING);
    }

    // come back to Candy tab from training
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FOR_TRAINING) {
            if (resultCode == RESULT_OK) {
                int expEarned = data.getIntExtra(TrainingActivity.EXP_EARNED, 0);
                int sugarEarned = data.getIntExtra(TrainingActivity.SUGAR_EARNED, 0);

                // adjust user's exp and sugar accordingly
                increaseExp(expEarned);
                increaseSugar(sugarEarned);

                // DEBUG
                Log.d("help", "exp: " + exp);
                Log.d("help", "sugar: " + sugar);
                Log.d("help", "level: " + level);

                // return back to Candy Tab
                loadFragment(new JarsFragment());

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
                String imageUri = data.getStringExtra(MakeNewCandyActivity.SCREENSHOT_IMAGE_URI);
                int jarIndex = data.getIntExtra(MakeNewCandyActivity.JAR_INDEX,-1);

                // changed here
                if (jarList == null) {
                    jarList = new ArrayList<>();
                    jarList.add(new Jar(jarTitle));
                }

                Jar jar = jarList.get(jarIndex);
                Candy newCandy = new Candy(prompt, answer);
                // attach screenshot URI to the candy
                if (imageUri != null) {
                    newCandy.setImageUri(Uri.parse(imageUri));
                }
                jar.addCandy(newCandy);

                //increment "total candies made" count
                incrementTotalCandiesMade();

                //increase user's exp
                increaseExp(1);

                // save and load newly updated jar data
                Gson gson = new Gson();
                String jsonString = gson.toJson(jarList);
                saveToLocalFile(USER_JAR_FILE_NAME, jsonString);

                loadFragment(new JarsFragment());

                Menu menu = navView.getMenu();
                MenuItem menuItem = menu.getItem(1); // highlight the Candy tab
                menuItem.setChecked(true);

            }
        } else if (requestCode == REQUEST_CODE_FOR_USERNAME) {
            if (resultCode == RESULT_OK) {
                username = data.getStringExtra(ProfileFragment.USERNAME);
                saveUsername();

                loadFragment(new ProfileFragment());

                Menu menu = navView.getMenu();
                MenuItem menuItem = menu.getItem(2); // highlight the Profile tab
                menuItem.setChecked(true);
            }
        } else if (requestCode == REQUEST_CODE_FOR_VIEW_CANDIES) {
            if (resultCode == RESULT_OK) {
                // reload Jars page
                loadFragment(new JarsFragment());

                Menu menu = navView.getMenu();
                MenuItem menuItem = menu.getItem(1); // highlight the Candy/Jar tab
                menuItem.setChecked(true);
            }
        } else {
            Log.d("test", "this part is called");
        }
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

    // view current items from MakerFragment
    @Override
    public void viewCurrentItems(){
        Intent intent = new Intent(this, CurrentItemsActivity.class);
        startActivity(intent);
    }



    @Override
    public void openFile() {
        // creates a floating window

        // TODO: edit hardcoding of this part
        // check permission to overlay
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        } else {

            // creates a floating window
            moveTaskToBack(true);
            startService(new Intent(MainActivity.this, FloatingWindowService.class));
        }


    }




    // SHARED PREFERENCES METHODS:

    // saves all top bar data
    public void saveAllData() {
        // does not save username here
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.LEVEL, level);
        editor.putInt(ProfileFragment.EXP, exp);
        editor.putInt(ProfileFragment.STREAK, streak);
        editor.putInt(ProfileFragment.SUGAR, sugar);
        editor.putInt(ProfileFragment.LONGEST_STREAK, longestStreak);
        editor.putInt(ProfileFragment.TOTAL_CANDIES_MADE, totalCandiesMade);
        editor.putInt(ProfileFragment.TOTAL_CANDIES_GRADUATED, totalCandiesGraduated);
        editor.commit();
    }

    // loads all top bar data
    public void loadAllData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        // username = sharedPreferences.getString(USERNAME, "default username");
        level = sharedPreferences.getInt(ProfileFragment.LEVEL, 1);
        exp = sharedPreferences.getInt(ProfileFragment.EXP, 0);
        streak = sharedPreferences.getInt(ProfileFragment.STREAK, 1);
        longestStreak = sharedPreferences.getInt(ProfileFragment.LONGEST_STREAK, 1);
        sugar = sharedPreferences.getInt(ProfileFragment.SUGAR, 0);
        totalCandiesMade = sharedPreferences.getInt(ProfileFragment.TOTAL_CANDIES_MADE, 0);
        totalCandiesGraduated = sharedPreferences.getInt(ProfileFragment.TOTAL_CANDIES_GRADUATED, 0);
    }

    // individual methods for shared preferences
    public void saveUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ProfileFragment.USERNAME, username);
        editor.commit();
    }

    public void loadUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(ProfileFragment.USERNAME, "your username");
    }

    public void saveExp() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.EXP, exp);
        editor.commit();
    }

    public void loadExp() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        exp = sharedPreferences.getInt(ProfileFragment.EXP, 0);
    }

    public void saveSugar() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.SUGAR, sugar);
        editor.commit();
    }

    public void loadSugar() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        sugar = sharedPreferences.getInt(ProfileFragment.SUGAR, 0);
    }

    public void saveLevel() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.LEVEL, level);
        editor.commit();
    }

    public void loadLevel() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        level = sharedPreferences.getInt(ProfileFragment.LEVEL, 1);
    }

    public void saveTotalSugarSpent() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.TOTAL_SUGAR_SPENT, totalSugarSpent);
        editor.commit();
    }

    public void loadTotalSugarSpent() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        totalSugarSpent = sharedPreferences.getInt(ProfileFragment.TOTAL_SUGAR_SPENT, 0);
    }

    public static int getExpToLevelUp(int level) {
        if (level <= 100) {
            return 3 * (level + 1) * (level + 1);
        } else {
            return 30000;
        }
    }

    // use negative values when exp is reduced
    public void increaseExp(int amount) {
        this.exp += amount;

        // check whether next level is reached; update if necessary
        // Exp needed to reach next level = 3 * (next level)^2, if next level <= 100
        // Exp needed to reach next level = 30 000, if next level > 100
        int expNeededToLevelUp = getExpToLevelUp(level);

        if (exp >= expNeededToLevelUp) {
            // level up!
            level++;
            exp -= expNeededToLevelUp;
            saveLevel();

            // award sugar
            // Leveling up: sugar += floor[level^1.5] * 100
            int sugarToAward = (int) Math.floor(Math.pow(level, 1.5)) * 100;
            increaseSugar(sugarToAward);
        }

        saveExp();
        loadDataIntoTopBar();
    }

    // use negative values when sugar is spent/reduced
    public void increaseSugar(int amount) {
        this.sugar += amount;
        saveSugar();
        loadDataIntoTopBar();
    }

    public void increaseTotalSugarSpent(int amount) {
        this.totalSugarSpent += amount;
        saveTotalSugarSpent();
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