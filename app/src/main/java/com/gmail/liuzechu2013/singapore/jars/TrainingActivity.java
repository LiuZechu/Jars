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
    private int currentStreak;
    private int numberCorrect;
    private int numberWrong;
    private int expEarned = 0;
    private int sugarEarned = 0;

    // CONSTANTS
    public static final String GET_JAR_LIST = "GET_JAR_LIST";
    public static final String EXP_EARNED = "expEarned";
    public static final String SUGAR_EARNED = "sugarEarned";
    public static final String CURRENT_STREAK = "currentStreak";

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

        // set up the buttons for CORRECT and WRONG
        correctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCandy != null) {
                    numberCorrect++;

                    // get correct: exp += 1 * 2^level of candy
                    expEarned += (int) Math.pow(2, currentCandy.getLevel());
                    // get correct: sugar += (10) * level of candy (1-7) * (100 + (streak * 10))%
                    // [e.g. 10 day streak means 200% sugar]
                    sugarEarned += (int) (10 * currentCandy.getLevel() * (100 + (currentStreak * 10)) * 0.01);

                    // if graduate: exp += 500; sugar += 5000
                    if (currentCandy.getLevel() == 7) {
                        expEarned += 500;
                        sugarEarned += 5000;
                    }

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

                    // get wrong: exp++
                    expEarned++;

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

        // get current streak from intent (for computation of sugar awarded)
        currentStreak = intent.getIntExtra(CURRENT_STREAK, 1);
    }

    public void nextJar(List<Jar> jars) {

        if (jarList == null || currentJarIndex >= jarList.size()) {
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

        if (candies == null || currentCandyIndex >= candies.size()) {
            // reset to zero
            currentCandyIndex = 0;
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXP_EARNED, expEarned);
        intent.putExtra(SUGAR_EARNED, sugarEarned);
        setResult(RESULT_OK, intent);
        finish();
    }
}
