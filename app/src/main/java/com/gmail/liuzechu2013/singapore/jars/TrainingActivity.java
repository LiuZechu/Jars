package com.gmail.liuzechu2013.singapore.jars;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TrainingActivity extends AppCompatActivity {
    private ArrayList<Jar> jarList;
    private TextView title;
    private TextView mainTextView;
    private LinearLayoutCompat trainingBottomBarLayout;
    private Button correctButton;
    private Button wrongButton;
    private Button exitTrainingButton;
    // moved here as class fields
    private int currentJarIndex = 0;
    private Jar currentJar;
    private int currentCandyIndex = 0;
    private Candy currentCandy;
    // statistics
    private int numberCorrect;
    private int numberWrong;

    // CONSTANTS
    public static final String GET_JAR_LIST = "GET_JAR_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        title = findViewById(R.id.training_title_textView);
        mainTextView = findViewById(R.id.training_main_textView);
        trainingBottomBarLayout = findViewById(R.id.training_bottom_bar_layout);
        correctButton = findViewById(R.id.training_correct_button);
        wrongButton = findViewById(R.id.training_wrong_button);
        exitTrainingButton = findViewById(R.id.exit_training_button);

        // set stats to zero
        numberCorrect = 0;
        numberWrong = 0;

        // set up the buttons
        correctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCandy != null) {
                    numberCorrect++;
                    currentCandy.levelUp();
                    trainCandy();
                }
            }
        });

        wrongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCandy != null) {
                    numberWrong++;
                    currentCandy.dropToLevelOne();
                    trainCandy();
                }
            }
        });

        exitTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitTraining();
            }
        });

        // get jarList out from intent
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra(GET_JAR_LIST);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        jarList = gson.fromJson(jsonString, type);

        nextJar(jarList);
    }

    public void nextJar(List<Jar> jars) {

        if (currentJarIndex >= jarList.size()) {
            finishTraining();
        } else {
            currentJar = jars.get(currentJarIndex);
            title.setText(currentJar.getTitle());
            currentJarIndex++;
            trainCandy();
        }
    }

    public void trainCandy() {
        ArrayList<Candy> candies = currentJar.getCandies();

        // make the two buttons invisible initially
        trainingBottomBarLayout.setVisibility(View.INVISIBLE);

        if (currentCandyIndex >= candies.size()) {
            nextJar(jarList);
        } else {

            currentCandy = candies.get(currentCandyIndex);

            String prompt = currentCandy.getPrompt();
            final String answer = currentCandy.getAnswer();

            mainTextView.setText(prompt);
            mainTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainTextView.setText(answer);
                    trainingBottomBarLayout.setVisibility(View.VISIBLE);
                    currentCandyIndex++;
                }
            });
        }
    }

    public void finishTraining() {
        String displayText = "Good Job! You got " + numberCorrect + " correct and "
                + numberWrong + " wrong! Keep it up! Tap to exit Training.";
        mainTextView.setText(displayText);
        mainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitTraining();
            }
        });
    }

    public void exitTraining() {
        // TODO: change this to return to CandyFragment instead of just MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.FRAGMENT_LOAD, MainActivity.CANDY);
        startActivity(intent);
    }
}
