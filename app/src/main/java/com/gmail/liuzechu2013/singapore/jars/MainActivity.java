package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private Button trainingButton;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_files:
                    mTextMessage.setText(R.string.navbar_files);
                    return true;
                case R.id.navigation_candy:
                    mTextMessage.setText(R.string.navbar_candy);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.navbar_profile);
                    return true;
                case R.id.navigation_shop:
                    mTextMessage.setText(R.string.navbar_shop);
                    return true;
            }
            return false;
        }
    };

    private void gotoTraining() {
        Intent training = new Intent(this, TrainingActivity.class);
        startActivity(training);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
