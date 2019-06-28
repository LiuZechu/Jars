package com.gmail.liuzechu2013.singapore.jars;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TrainingActivity extends AppCompatActivity {
    private ArrayList<Jar> jarList; // this jarList is the list for TRAINING
    private ArrayList<Jar> userJarList; // this userJarList is the list containing ALL CANDIES the user owns
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
    private int numberGraduated;
    private int expEarned = 0;
    private int sugarEarned = 0;
    // for graduation
    private Jar currentGraduatedJar;
    private ArrayList<Jar> candiesGraduated;

    // CONSTANTS
    public static final String GET_JAR_LIST = "GET_JAR_LIST";
    public static final String EXP_EARNED = "expEarned";
    public static final String SUGAR_EARNED = "sugarEarned";
    public static final String CURRENT_STREAK = "currentStreak";
    public static final String TRAINING_JAR_NAME = "trainingJarName";

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
        numberGraduated = 0;

        // get jar name from intent (to identify whether this trains all the jars or just one jar)
        Intent intent = getIntent();
        String jarName = intent.getStringExtra(TRAINING_JAR_NAME);

        // get user's entire userJarList from saved local file
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        String jsonStringForJarList = loadFromLocalFile(MainActivity.USER_JAR_FILE_NAME);
        userJarList = gson.fromJson(jsonStringForJarList, type);

        processCandiesForTraining(jarName);

        // prevent null pointer exception for jarList
        if (jarList == null) {
            jarList = new ArrayList<>();
        }

        // set up the button for CORRECT
        correctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCorrect();
            }
        });

        // set up the button for WRONG
        wrongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "wrong");
                if (currentCandy != null) {
                    numberWrong++;

                    // get wrong: exp++
                    expEarned++;

                    currentCandy.dropToLevelOne();
                    trainCandy(false);
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
        // Intent intent = getIntent();
        // String jsonString = intent.getStringExtra(GET_JAR_LIST);
        // Gson gson = new Gson();
        // Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        // jarList = gson.fromJson(jsonString, type);

        // instead of getting jarList from intent, we should process it on the spot from User's JarList


        // get current streak from intent (for computation of sugar awarded)
//        Intent intent = getIntent();
//        currentStreak = intent.getIntExtra(CURRENT_STREAK, 1);

        // just get current streak from sharedPrefs directly instead
        currentStreak = loadStreak();



        // this starts the training process
        nextJar(jarList);
    }

    public void nextJar(List<Jar> jars) {

        if (jarList == null || currentJarIndex >= jarList.size()) {
            finishTraining();
        } else {
            currentJar = jars.get(currentJarIndex);
            title.setText(currentJar.getTitle());
            currentJarIndex++;
            trainCandy(false);
        }
    }

    // this method takes in a boolean parameter. True: previous candy is graduated and removed.
    // in this case, the current candy index will be decremented by 1 so as to make up for the
    // loss of one candy
    public void trainCandy(boolean isPrevGraduated) {
        if (isPrevGraduated) {
            currentCandyIndex--;
        }

        ArrayList<Candy> candies = currentJar.getCandies();

        // make the two buttons invisible initially
        trainingBottomBarLayout.setVisibility(View.INVISIBLE);

        if (candies == null || currentCandyIndex >= candies.size() || currentCandyIndex < 0 ) { // by right the last condition shouldn't happen
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

    private void processCandiesForTraining(String jarName) {
        jarList = new ArrayList<>(); // for now, create a new jar for candies, for training purpose

        if (jarName.equals(MainActivity.CODE_FOR_TRAINING_ALL_CANDIES)) {
            for (Jar jar : userJarList) {
                ArrayList<Candy> candyList = jar.getCandies();
                Jar trainingJar = null;
                boolean trainingJarCreated = false;
                for (Candy candy : candyList) {
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
                    jarList.add(trainingJar);
                }
            }
        } else {
            // Train individual jar
            Jar jarToTrain = null;
            for (Jar jar : userJarList) {
                if (jar.getTitle().equals(jarName)) {
                    jarToTrain = jar;
                }
            }

            if (jarToTrain != null) {
                jarList.add(jarToTrain);
            } else {
                // no such jar is found
                Toast.makeText(this, "No such Jar is found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCorrect() {

        if (currentCandy != null) {
            numberCorrect++;


            Log.d("test", "candy level: " + currentCandy.getLevel() + " prompt: " + currentCandy.getPrompt());

            // get correct: exp += 1 * 2^level of candy
            expEarned += (int) Math.pow(2, currentCandy.getLevel());
            // get correct: sugar += (10) * level of candy (1-7) * (100 + (streak * 10))%
            // [e.g. 10 day streak means 200% sugar]
            sugarEarned += (int) (10 * currentCandy.getLevel() * (100 + (currentStreak * 10)) * 0.01);

            // GRADUATION: exp += 500; sugar += 5000
            if (currentCandy.getLevel() >= 7) {
                expEarned += 500;
                sugarEarned += 5000;

                numberGraduated++;
                // check whether current graduated jar is created
                if (currentGraduatedJar == null) {
                    currentGraduatedJar = new Jar(currentJar.getTitle());
                    candiesGraduated.add(currentGraduatedJar);
                }

                addCandyToGraduated(currentCandy);
                currentJar.deleteCandy(currentCandy);
                trainCandy(true);

            } else { // Candy hasn't graduated yet
                currentCandy.levelUp();
                // DEBUG
                Log.d("test", "candy level: " + currentCandy.getLevel() + " prompt: " + currentCandy.getPrompt());

                trainCandy(false);
            }
        }
    }

    public void finishTraining() {
        // save all changes to Jars and Candies into a local file
        Gson gson = new Gson();
        String toSave = gson.toJson(userJarList);
        Log.d("test", toSave);
        saveToLocalFile(MainActivity.USER_JAR_FILE_NAME, toSave);

        // display message of encouragement
        String displayText = "Good Job! You got " + numberCorrect + " correct and "
                + numberWrong + " wrong! You also graduated " + numberGraduated
                + " Candies. Keep it up! Tap to exit Training.";
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

    public void addCandyToGraduated(Candy candy) {
        currentGraduatedJar.addCandy(candy);
    }

    private int loadStreak() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        int currentStreak = sharedPreferences.getInt(ProfileFragment.STREAK, 0);

        return currentStreak;
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
}
