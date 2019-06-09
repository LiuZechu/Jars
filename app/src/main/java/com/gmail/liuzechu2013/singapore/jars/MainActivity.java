package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button trainingButton;

    // switch between different screens using bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_files:
                    //fragment = getSupportFragmentManager().findFragmentById(R.layout.fragment_files);
                    fragment = new FilesFragment();
                    break;
                case R.id.navigation_candy:
                    //fragment = getSupportFragmentManager().findFragmentById(R.layout.fragment_candy);
                    fragment = new CandyFragment();
                    break;
                case R.id.navigation_profile:
                    //fragment = getSupportFragmentManager().findFragmentById(R.layout.fragment_profile);
                    fragment = new ProfileFragment();
                    break;
                case R.id.navigation_shop:
                    //fragment = getSupportFragmentManager().findFragmentById(R.layout.fragment_shop);
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
        startActivity(training);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // load default fragment; need to change to last saved later
        loadFragment(new FilesFragment());

        // topbar training button
        trainingButton = (Button) findViewById(R.id.training_button);
        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTraining();
            }
        });
    }

}
