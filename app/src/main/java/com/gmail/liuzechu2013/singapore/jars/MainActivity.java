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
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ShopFragment.ViewCurrentItemsListener, FilesFragment.OnFileOpenListener{
    private BottomNavigationView navView;
    private Button trainingButton;
    private ArrayList<Jar> jarListForTraining;
    public final static int REQUEST_CODE = 1;
    // for saving user data using shared preferences
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String USER_STATISTICS = "UserStatistics";

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
        } else if (requestCode == CandyFragment.REQUEST_CODE_FOR_NEW_CANDY) {
            if (resultCode == RESULT_OK) {

                loadFragment(new CandyFragment());

                Menu menu = navView.getMenu();
                MenuItem menuItem = menu.getItem(1); // highlight the Candy tab
                menuItem.setChecked(true);

                // GET DATA FROM data (for the newly created candy)
                String jarTitle = data.getStringExtra(MakeNewCandyActivity.JAR_TITLE);
                String prompt = data.getStringExtra(MakeNewCandyActivity.PROMPT);
                String answer = data.getStringExtra(MakeNewCandyActivity.ANSWER);

            }
        } else {}
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



        // FOR TESTING: MANUALLY ADD JAR LIST FOR TRAINING
        jarListForTraining = new ArrayList<>();
        Jar jar1 = new Jar("general knowledge");
        jar1.addCandy(new Candy("wheres canberra", "australia"));
        jar1.addCandy(new Candy("whos the father of computer science", "alan turing"));
        Jar jar2 = new Jar("french");
        jar2.addCandy(new Candy("hello", "BONJOUR!"));
        jarListForTraining.add(jar1);
        jarListForTraining.add(jar2);
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
}
