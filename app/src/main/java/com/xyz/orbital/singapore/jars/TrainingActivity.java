package com.xyz.orbital.singapore.jars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.xyz.orbital.singapore.jars.MainActivity.getResourceID;

public class TrainingActivity extends AppCompatActivity {
    private ArrayList<Jar> jarList; // this jarList is the list for TRAINING
    private ArrayList<Jar> userJarList; // this userJarList is the list containing ALL CANDIES the user owns
    private TextView title;
    private TextView mainTextView;
    private ConstraintLayout trainingBottomBarLayout;
    private Button correctButton;
    private Button wrongButton;
    private Button contextButton;
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
    private ArrayList<Jar> candiesGraduated = new ArrayList<>();
    // for expression
    final Random rnd = new Random();

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
        contextButton = findViewById(R.id.training_context_button);
        exitTrainingButton = findViewById(R.id.exit_training_button);
        changeExpression();

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

        if (userJarList != null) {
            processCandiesForTraining(jarName);
        }
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

        // set up button to view CONTEXT i.e. screenshot attached to the candy
        contextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewScreenshot();
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
//        if (isPrevGraduated) {
//            currentCandyIndex--;
//        }

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

            changeColor(currentCandy.getLevel());
            changeExpression();

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

                // changed here 27/07: process candies for individual jar as well
                ArrayList<Candy> candyList = jarToTrain.getCandies();
                Jar trainingJar = null;
                boolean trainingJarCreated = false;
                for (Candy candy : candyList) {
                    // candy.decrementCountDown(); // commented out because this should have been done in background work already
                    if (candy.shouldTrain()) {
                        if (!trainingJarCreated) {
                            trainingJar = new Jar(jarToTrain.getTitle());
                            trainingJarCreated = true;
                        }
                        trainingJar.addCandy(candy);
                    }
                }

                // prevent null pointer exception
                if (trainingJar == null) {
                    trainingJar = new Jar(jarToTrain.getTitle());
                }

                jarList.add(trainingJar);

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


                // delete this candy from its original jar
                // currentJar.deleteCandy(currentCandy);
                for (Jar jar : userJarList) {
                    if (currentJar.getTitle().equals(jar.getTitle())) {
                        jar.deleteCandy(currentCandy);
                    }
                }

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

        // update user's archive graduatedCandies.txt file
        updateUserGraduatedCandies();

        // update the number of candies graduated in SharedPreferences
        int totalGraduated = loadCandiesGraduated() + numberGraduated;
        saveCandiesGraduated(totalGraduated);

        // update the boolean value of whether the current streak is maintained
        // TODO: need to consider the case of whether the user completes all or only part of the daily training
        saveStreakMaintained(true);

        // update and save line graph data
        // 1. line graph of Candies trained
        int numberTrained = numberCorrect + numberWrong;
        addToLineGraphFile(numberTrained, MainActivity.LINE_GRAPH_CANDIES_TRAINED_FILE_NAME);
        // 2. line graph of Candies graduated
        addToLineGraphFile(numberGraduated, MainActivity.LINE_GRAPH_CANDIES_GRADUATED_FILE_NAME);

        title.setText("");
        String displayText;
        // display message of encouragement
        if (numberCorrect == 0 && numberWrong == 0 && numberGraduated == 0) {
            displayText = "You have no candies to train!";
        } else {
            displayText = "Good Job! You got " + numberCorrect + " correct and "
                    + numberWrong + " wrong! You also graduated " + numberGraduated
                    + " Candies. Keep it up! Tap to exit Training.";
        }
        mainTextView.setText(displayText);
        mainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitTraining();
            }
        });
    }

    // view screenshot attached to the candy
    private void viewScreenshot() {
        Uri imageUri = currentCandy.getImageUri();

        if (imageUri == null) {
            Toast.makeText(this, "No screenshot is available!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, PopUpImageActivity.class);
            intent.putExtra(PopUpImageActivity.IMAGE_URI, imageUri.toString());

            startActivity(intent);
        }
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

    // true indicates current streak is maintained
    private void saveStreakMaintained(boolean isMaintained) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ProfileFragment.STREAK_MAINTAINED, isMaintained);
        editor.commit();
    }

    private void saveCandiesGraduated(int amount) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.TOTAL_CANDIES_GRADUATED, amount);
        editor.commit();
    }

    private int loadCandiesGraduated() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(ProfileFragment.TOTAL_CANDIES_GRADUATED, 0);
    }

    private int loadStreak() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        int currentStreak = sharedPreferences.getInt(ProfileFragment.STREAK, 0);

        return currentStreak;
    }

    public void updateUserGraduatedCandies() {
        Gson gson = new Gson();
        String stringFromFile = loadFromLocalFile(ArchiveActivity.GRADUATED_CANDIES_FILE_NAME);
        // note: Jars are stored in a HashMap in the file, with Key: String, Value: Jar
        Type type = new TypeToken<HashMap<String, Jar>>(){}.getType();
        HashMap<String, Jar> graduatedHash = gson.fromJson(stringFromFile, type);

        // prevent null pointer exception
        if (graduatedHash == null) {
            graduatedHash = new HashMap<>();
        }

        // loop through candiesGraduated ArrayList can add candies to their corresponding Jars in the HashMap
        for (Jar jar: candiesGraduated) {
            String jarTitle = jar.getTitle();
            if (graduatedHash.containsKey(jarTitle)) {
                Jar graduatedJar = graduatedHash.get(jarTitle);
                graduatedJar.addAllCandies(jar.getCandies());
            } else {
                Jar graduatedJar = new Jar(jarTitle);
                graduatedJar.addAllCandies(jar.getCandies());
                graduatedHash.put(jarTitle, graduatedJar);
            }
        }

        // save changes to file
        String toSave = gson.toJson(graduatedHash);
        saveToLocalFile(ArchiveActivity.GRADUATED_CANDIES_FILE_NAME, toSave);

    }

    private void changeExpression() {
        ImageView img = findViewById(R.id.training_expression);
        String str = "ic_expression" + rnd.nextInt(62);
        // TODO: need to select the correct expression.
        img.setImageDrawable
                (
                        getResources().getDrawable(getResourceID(str, "drawable",
                                getApplicationContext()))
                );
    }

    private void changeColor(int level) {
        View img = findViewById(R.id.training_candy_color);
        String[] arr = {"#ef6256", "#f99c1c", "#fec41b", "#47b585", "#5bc4bf", "#825ca4", "#e96ca9"};
        img.getBackground().setColorFilter(Color.parseColor(arr[level - 1]), PorterDuff.Mode.SRC_OVER);
        // TODO: need to select the correct color.
    }

    // for line graph: add total candies trained in this training to local file
    private void addToLineGraphFile(int quantity, String fileName) {
        String fromFile = loadFromLocalFile(fileName);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LineGraphPoint>>(){}.getType();
        ArrayList<LineGraphPoint> lineGraphPoints = gson.fromJson(fromFile, type);

        if (lineGraphPoints == null) {
            lineGraphPoints = new ArrayList<>();
        }

        LineGraphPoint lineGraphPoint = new LineGraphPoint(quantity);

        // check whether this point has the same date as the previous point. If so, merge them
        if (lineGraphPoints.size() > 0) {
            LineGraphPoint previousPoint = lineGraphPoints.get(lineGraphPoints.size() - 1);
            String previousDate = previousPoint.getDate();

            if (lineGraphPoint.getDate().equals(previousDate)) {
                previousPoint.addQuantity(quantity);
            } else { // different dates

                Calendar previousCalendar = previousPoint.getCalendar();

                do {
                    // check whether there's any empty point/date between the current point and the previous point
                    Calendar nextCalendar = (Calendar) previousCalendar.clone();
                    nextCalendar.set(Calendar.DAY_OF_MONTH, previousCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                    String next = LineGraphPoint.getDateFromCalendar(nextCalendar);
                    String current = lineGraphPoint.getDate();
                    // Log.d("test", next);
                    if (!next.equals(current)) {
                        lineGraphPoints.add(new LineGraphPoint(0, nextCalendar));
                    } else {
                        break;
                    }

                    previousCalendar = nextCalendar;

                } while (true);

                lineGraphPoints.add(lineGraphPoint);

            }
        }


        String toSave = gson.toJson(lineGraphPoints);
        saveToLocalFile(fileName, toSave);
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
