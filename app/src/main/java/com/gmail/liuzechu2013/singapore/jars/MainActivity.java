package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
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
    private static final String USER_JAR_FILE_NAME = "userJars.txt";

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
                case R.id.navigation_candy:
                    fragment = new CandyFragment();
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;
                case R.id.navigation_shop:
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
    private boolean loadFragment(Fragment fragment) {
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

            Toast.makeText(this, "saved successfully", Toast.LENGTH_SHORT).show();
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

        // load data into jarList
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        String jsonStringForJarList = loadFromLocalFile(USER_JAR_FILE_NAME);
        jarList = gson.fromJson(jsonStringForJarList, type);


        // FOR TESTING: MANUALLY ADD JAR LIST FOR TRAINING
        jarListForTraining = new ArrayList<>();
        Jar jar1 = new Jar("general knowledge");
        jar1.addCandy(new Candy("wheres canberra", "australia"));
        jar1.addCandy(new Candy("whos the father of computer science", "alan turing"));
        Jar jar2 = new Jar("french");
        jar2.addCandy(new Candy("hello", "BONJOUR!"));
        jarListForTraining.add(jar1);
        jarListForTraining.add(jar2);

        if (jarList == null) {
            jarList = new ArrayList<>();
        }

//        jarList.add(new Jar("French"));
//        jarList.add(new Jar("Design Thinking"));
//        jarList.add(new Jar("Computer Organisation"));
//        jarList.add(new Jar("PS"));
//        jarList.add(new Jar("GAPI"));
//        jarList.add(new Jar("Chinese"));
//        jarList.add(new Jar("Japanese"));
        // TESTING ENDS



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
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // put stuff into it as key value pairs
        // editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        // sharedPreferences.getString(CONSTANT, default value)
        // etc
    }

    public static ArrayList<Jar> getJarList() {
        return jarList;
    }
}
