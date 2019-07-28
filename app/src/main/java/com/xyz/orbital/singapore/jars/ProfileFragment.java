package com.xyz.orbital.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class ProfileFragment extends Fragment {
    // private RecyclerView mRecyclerView; // not used for now
    // private ArrayList<Achievement> achievementList; // not used for now
    private FloatingActionButton settingsButton; // button to reset user stats
    private TextView usernameTextView;
    // achievements
    private TextView achievementStreakScore;
    private TextView achievementMadeScore;
    private TextView achievementGraduatedScore;
    private TextView achievementJarsScore;
    private TextView achievementSugarScore;
    private TextView achievementLevelScore;
    // achievement stars
    private ImageView achievementStreakStars;
    private ImageView achievementMadeStars;
    private ImageView achievementGraduatedStars;
    private ImageView achievementJarsStars;
    private ImageView achievementSugarStars;
    private ImageView achievementLevelStars;
    // graduated candies button
    private Button viewGraduatedCandiesButton;

    // SharedPreferences tags to save user data
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String USERNAME = "username";
    public static final String LEVEL = "level";
    public static final String EXP = "exp";
    public static final String STREAK = "streak";
    public static final String STREAK_MAINTAINED = "streakMaintained"; // resets to false daily
    public static final String TOTAL_CANDIES_MADE = "totalCandiesMade";
    public static final String TOTAL_CANDIES_GRADUATED = "totalCandiesGraduated";
    public static final String LONGEST_STREAK = "longestStreak";
    public static final String TOTAL_JARS_MADE = "totalJarsMade";
    public static final String TOTAL_SUGAR_SPENT = "totalSugarSpent";
    public static final String SUGAR = "sugar";
    public static final String CANDY_COLOURS = "candyColours";
    public static final String SELECTED_EXPRESSIONS = "selectedExpressions";

    public static final String LEVEL_STAR = "levelStar";
    public static final String TOTAL_CANDIES_MADE_STAR = "totalCandiesMadeStar";
    public static final String TOTAL_CANDIES_GRADUATED_STAR = "totalCandiesGraduatedStar";
    public static final String LONGEST_STREAK_STAR = "longestStreakStar";
    public static final String TOTAL_JARS_MADE_STAR = "totalJarsMadeStar";
    public static final String TOTAL_SUGAR_SPENT_STAR = "totalSugarSpentStar";

    // user stats to be saved in SharedPreferences
    private String username;
    private int exp;
    private int sugar; // current amount of sugar owned by the user
    private int streak; // this is the current streak
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




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_profile, null);

        // initialise achievement views
        achievementStreakScore = view.findViewById(R.id.achievement_streak_score);
        achievementMadeScore = view.findViewById(R.id.achievement_made_score);
        achievementGraduatedScore = view.findViewById(R.id.achievement_graduated_score);
        achievementJarsScore = view.findViewById(R.id.achievement_jars_score);
        achievementSugarScore = view.findViewById(R.id.achievement_sugar_score);
        achievementLevelScore = view.findViewById(R.id.achievement_level_score);

        viewGraduatedCandiesButton = view.findViewById(R.id.view_graduated_candies);
        viewGraduatedCandiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoArchives();
            }
        });

        LineChartView lineChartView = view.findViewById(R.id.profile_chart);

        // load line graph data from saved local file (for now, Candies trained only)
        int[] yAxisData = loadLineGraphData(MainActivity.LINE_GRAPH_CANDIES_TRAINED_FILE_NAME);
        // test values
        // int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
        List<PointValue> yAxisValues = new ArrayList<>();
        LineChartData data = new LineChartData();
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#000000"))
                .setHasLabelsOnlyForSelected(true);
        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, (int) (Math.random() * 100)));
        }
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        data.setLines(lines);
        data.setValueLabelBackgroundAuto(false);
        data.setValueLabelBackgroundColor(Color.TRANSPARENT);
        data.setValueLabelTypeface(Typeface.DEFAULT);
        //data.setValueLabelBackgroundEnabled(false);
        //Typeface jost = Typeface.createFromAsset(getContext().getAssets(), "font/jost_book.otf");
        //data.setValueLabelTypeface(jost);
        data.setValueLabelsTextColor(Color.BLACK);
        data.setValueLabelTextSize(30);
        lineChartView.setLineChartData(data);
        for (Line l : data.getLines()) {
            int count = 0;
            for (PointValue value : l.getValues()) {
                // Here I modify target only for Y values but it is OK to modify X targets as well.
                value.setTarget(value.getX(), yAxisData[count]);
                String label = Integer.toString(yAxisData[count]);
                value.setLabel(label);
                count++;
            }
        }
//        for (int i = 0; i < yAxisData.length; i++){
//            yAxisValues.add(new PointValue(i, yAxisData[i]));
//        }
        lineChartView.startDataAnimation();
        lineChartView.setValueSelectionEnabled(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL);
        lineChartView.setHorizontalScrollBarEnabled(true);




        // FOR TESTING ONLY! RESETS ALL USER DATA
        //resetAllData();

        // load user stats
        loadAllData();

        // set achievement stats; returns an array of double indicating the levels of achievement bars
        double[] barLevels = setAchievementStats();

        // set achievement stars
        achievementStreakStars = view.findViewById(R.id.achievement_streak_stars);
        achievementMadeStars = view.findViewById(R.id.achievement_made_stars);
        achievementGraduatedStars = view.findViewById(R.id.achievement_graduated_stars);
        achievementJarsStars = view.findViewById(R.id.achievement_jars_stars);
        achievementSugarStars = view.findViewById(R.id.achievement_sugar_stars);
        achievementLevelStars = view.findViewById(R.id.achievement_level_stars);
        calculateAchievementStars();
        setAchievementStars();


        usernameTextView = view.findViewById(R.id.profile_username_textView);

        usernameTextView.setText(username);

        // draw achievement bars
        ImageView achievementStreakBar = (ImageView) view.findViewById(R.id.achievement_streak_bar);
        achievementStreakBar.getDrawable().setLevel((int) (barLevels[0] * 10000));

        ImageView achievementMadeBar = (ImageView) view.findViewById(R.id.achievement_made_bar);
        achievementMadeBar.getDrawable().setLevel((int) (barLevels[1] * 10000));

        ImageView achievementGraduatedBar = (ImageView) view.findViewById(R.id.achievement_graduated_bar);
        achievementGraduatedBar.getDrawable().setLevel((int) (barLevels[2] * 10000));

        ImageView achievementJarsBar = (ImageView) view.findViewById(R.id.achievement_jars_bar);
        achievementJarsBar.getDrawable().setLevel((int) (barLevels[3] * 10000));

        ImageView achievementSugarBar = (ImageView) view.findViewById(R.id.achievement_sugar_bar);
        achievementSugarBar.getDrawable().setLevel((int) (barLevels[4] * 10000));

        ImageView achievementLevelBar = (ImageView) view.findViewById(R.id.achievement_level_bar);
        achievementLevelBar.getDrawable().setLevel((int) (barLevels[5] * 10000));


//        // button to reset user stats
//        resetButton = (Button) view.findViewById(R.id.reset_button);
//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetUserStats();
//            }
//        });

        // go to Settings
        settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettings();
            }
        });


        return view;
    }


    private int[] loadLineGraphData(String fileName) {
        String fromFile = loadFromLocalFile(fileName);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LineGraphPoint>>(){}.getType();
        ArrayList<LineGraphPoint> lineGraphPoints = gson.fromJson(fromFile, type);
        if (lineGraphPoints == null) {
            lineGraphPoints = new ArrayList<>();
        }

        int[]  yAxisData = new int[lineGraphPoints.size()];

        for (int i = 0; i < lineGraphPoints.size(); i++) {
            LineGraphPoint point = lineGraphPoints.get(i);
            yAxisData[i] = point.getQuantity();
        }

        return yAxisData;
    }

    public void gotoSettings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        intent.putExtra(USERNAME, username);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_USERNAME);
    }

    private void gotoArchives() {
        Intent intent = new Intent(getContext(), ArchiveActivity.class);
        startActivity(intent);
    }
//    // not used for now
//    public void resetUserStats() {
//        // does not reset name
//        level = 1;
//        exp = 100;
//        streak = 1;
//        totalCandiesMade = 0;
//        totalCandiesGraduated = 0;
//
//        levelTextView.setText(level + "");
//        expTextView.setText(exp + "");
//        totalCandiesMadeTextView.setText(totalCandiesMade + "");
//        totalCandiesGraduatedTextView.setText(totalCandiesGraduated + "");
//
//        saveAllData();
//        Toast.makeText(getContext(), "User data reset successfully", Toast.LENGTH_SHORT).show();
//    }

    public void saveAllData() {
        // does not save username here
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putInt(EXP, exp);
        //editor.putInt(STREAK, streak);

        editor.putString(USERNAME, username);

        editor.putInt(LONGEST_STREAK, longestStreak);
        editor.putInt(LONGEST_STREAK_STAR, longestStreakStar);

        editor.putInt(TOTAL_CANDIES_MADE, totalCandiesMade);
        editor.putInt(TOTAL_CANDIES_MADE_STAR, totalCandiesMadeStar);

        editor.putInt(TOTAL_CANDIES_GRADUATED, totalCandiesGraduated);
        editor.putInt(TOTAL_CANDIES_GRADUATED_STAR, totalCandiesGraduatedStar);

        editor.putInt(TOTAL_JARS_MADE, totalJarsMade);
        editor.putInt(TOTAL_JARS_MADE_STAR, totalJarsMadeStar);

        editor.putInt(TOTAL_SUGAR_SPENT, totalSugarSpent);
        editor.putInt(TOTAL_SUGAR_SPENT_STAR, totalSugarSpentStar);

        editor.putInt(LEVEL, level);
        editor.putInt(LEVEL_STAR, levelStar);

        editor.commit();
    }

    // loads all saved sharedPrefs to be used on this screen
    public void loadAllData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "username");

        //streak = sharedPreferences.getInt(STREAK, 1);

        longestStreak = sharedPreferences.getInt(LONGEST_STREAK, 1);
        longestStreakStar = sharedPreferences.getInt(LONGEST_STREAK_STAR, 0);

        totalCandiesMade = sharedPreferences.getInt(TOTAL_CANDIES_MADE, 0);
        totalCandiesMadeStar = sharedPreferences.getInt(TOTAL_CANDIES_MADE_STAR, 0);

        totalCandiesGraduated = sharedPreferences.getInt(TOTAL_CANDIES_GRADUATED, 0);
        totalCandiesGraduatedStar = sharedPreferences.getInt(TOTAL_CANDIES_GRADUATED_STAR, 0);

        totalJarsMade = sharedPreferences.getInt(TOTAL_JARS_MADE, 0);
        totalJarsMadeStar = sharedPreferences.getInt(TOTAL_JARS_MADE_STAR, 0);

        totalSugarSpent = sharedPreferences.getInt(TOTAL_SUGAR_SPENT, 0);
        totalSugarSpentStar = sharedPreferences.getInt(TOTAL_SUGAR_SPENT_STAR, 0);

        level = sharedPreferences.getInt(LEVEL, 1);
        levelStar = sharedPreferences.getInt(LEVEL_STAR, 0);
    }

    // individual methods for shared preferences
    public void saveUsername() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, username);
        editor.commit();
    }

    public void loadUsername() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "your username");
    }


    // return an array of doubles, corresponding to the levels of the bars
    private double[] setAchievementStats() {
        // maximum streak
        int longestStreakNextStar = 0;
        switch (longestStreakStar) {
            case 0:
                longestStreakNextStar = 5;
                break;
            case 1:
                longestStreakNextStar = 10;
                break;
            case 2:
                longestStreakNextStar = 50;
                break;
            case 3:
                longestStreakNextStar = 100;
                break;
            case 4:
                longestStreakNextStar = 500;
                break;
            case 5:
                longestStreakNextStar = 500;
                break;
        }
        achievementStreakScore.setText(longestStreak + "/" + longestStreakNextStar);

        // candies made
        int candiesMadeNextStar = 0;
        switch (totalCandiesMadeStar) {
            case 0:
                candiesMadeNextStar = 10;
                break;
            case 1:
                candiesMadeNextStar = 100;
                break;
            case 2:
                candiesMadeNextStar = 1000;
                break;
            case 3:
                candiesMadeNextStar = 5000;
                break;
            case 4:
                candiesMadeNextStar = 10000;
                break;
            case 5:
                candiesMadeNextStar = 10000;
                break;
        }
        achievementMadeScore.setText(totalCandiesMade + "/" + candiesMadeNextStar);

        // candies graduated
        int candiesGraduatedNextStar = 0;
        switch (totalCandiesGraduatedStar) {
            case 0:
                candiesGraduatedNextStar = 5;
                break;
            case 1:
                candiesGraduatedNextStar = 50;
                break;
            case 2:
                candiesGraduatedNextStar = 500;
                break;
            case 3:
                candiesGraduatedNextStar = 2500;
                break;
            case 4:
                candiesGraduatedNextStar = 5000;
                break;
            case 5:
                candiesGraduatedNextStar = 5000;
                break;
        }
        achievementGraduatedScore.setText(totalCandiesGraduated + "/" + candiesGraduatedNextStar);

        // jars made
        int jarsMadeNextStar = 0;
        switch (totalJarsMadeStar) {
            case 0:
                jarsMadeNextStar = 3;
                break;
            case 1:
                jarsMadeNextStar = 8;
                break;
            case 2:
                jarsMadeNextStar = 20;
                break;
            case 3:
                jarsMadeNextStar = 50;
                break;
            case 4:
                jarsMadeNextStar = 100;
                break;
            case 5:
                jarsMadeNextStar = 100;
                break;
        }
        achievementJarsScore.setText(totalJarsMade + "/" + jarsMadeNextStar);

        // sugar spent
        int sugarSpentNextStar = 0;
        switch (totalSugarSpentStar) {
            case 0:
                sugarSpentNextStar = 1000;
                break;
            case 1:
                sugarSpentNextStar = 5000;
                break;
            case 2:
                sugarSpentNextStar = 10000;
                break;
            case 3:
                sugarSpentNextStar = 100000;
                break;
            case 4:
                sugarSpentNextStar = 1000000;
                break;
            case 5:
                sugarSpentNextStar = 1000000;
                break;
        }
        achievementSugarScore.setText(totalSugarSpent + "/" + sugarSpentNextStar);

        // current
        int levelNextStar = 0;
        switch (levelStar) {
            case 0:
                levelNextStar = 5;
                break;
            case 1:
                levelNextStar = 10;
                break;
            case 2:
                levelNextStar = 50;
                break;
            case 3:
                levelNextStar = 100;
                break;
            case 4:
                levelNextStar = 300;
                break;
            case 5:
                levelNextStar = 300;
                break;
        }
        achievementLevelScore.setText(level + "/" + levelNextStar);


        // return an array of doubles to indicate achievement bar levels
        double[] barLevels = new double[6];
        barLevels[0] = (double) longestStreak / longestStreakNextStar;
        barLevels[1] = (double) totalCandiesMade / candiesMadeNextStar;
        barLevels[2] = (double) totalCandiesGraduated / candiesGraduatedNextStar;
        barLevels[3] = (double) totalJarsMade / jarsMadeNextStar;
        barLevels[4] = (double) totalSugarSpent / sugarSpentNextStar;
        barLevels[5] = (double) level / levelNextStar;

        return barLevels;
    }


    // display the correct number of stars for the achievement levels
    private void setAchievementStars() {
        achievementStreakStars.setImageResource(getStarResource(longestStreakStar));
        achievementMadeStars.setImageResource(getStarResource(totalCandiesMadeStar));
        achievementGraduatedStars.setImageResource(getStarResource(totalCandiesGraduatedStar));
        achievementJarsStars.setImageResource(getStarResource(totalJarsMadeStar));
        achievementSugarStars.setImageResource(getStarResource(totalSugarSpentStar));
        achievementLevelStars.setImageResource(getStarResource(levelStar));
    }

    // used in the above setAchievementStars() method
    private int getStarResource(int numberOfStars) {
        int resourceID = 0;
        switch(numberOfStars) {
            case 0:
                resourceID = R.drawable.ic_stars0;
                break;
            case 1:
                resourceID = R.drawable.ic_stars1;
                break;
            case 2:
                resourceID = R.drawable.ic_stars2;
                break;
            case 3:
                resourceID = R.drawable.ic_stars3;
                break;
            case 4:
                resourceID = R.drawable.ic_stars4;
                break;
            case 5:
                resourceID = R.drawable.ic_stars5;
                break;
        }

        return resourceID;
    }

    // calculates and updates achievement stars; rewards Sugar when getting an additional star
    private void calculateAchievementStars() {
        // maximum streak
        int prevLongestStreakStar = longestStreakStar;
        if (longestStreak < 5) {
            longestStreakStar = 0;
        } else if (longestStreak < 10) {
            longestStreakStar = 1;
        } else if (longestStreak < 50) {
            longestStreakStar = 2;
        } else if (longestStreak < 100) {
            longestStreakStar = 3;
        } else if (longestStreak < 500) {
            longestStreakStar = 4;
        } else {
            longestStreakStar = 5;
        }
        if (longestStreakStar - prevLongestStreakStar == 1) {
            addSugarToUser(longestStreakStar);
        }

        // candies made
        int prevTotalCandiesMadeStar = totalCandiesMadeStar;
        if (totalCandiesMade < 10) {
            totalCandiesMadeStar = 0;
        } else if (totalCandiesMade < 100) {
            totalCandiesMadeStar = 1;
        } else if (totalCandiesMade < 1000) {
            totalCandiesMadeStar = 2;
        } else if (totalCandiesMade < 5000) {
            totalCandiesMadeStar = 3;
        } else if (totalCandiesMade < 10000) {
            totalCandiesMadeStar = 4;
        } else {
            totalCandiesMadeStar = 5;
        }
        if (totalCandiesMadeStar - prevTotalCandiesMadeStar == 1) {
            addSugarToUser(totalCandiesMadeStar);
        }

        // candies graduated
        int prevTotalCandiesGraduatedStar = totalCandiesGraduatedStar;
        if (totalCandiesGraduated < 5) {
            totalCandiesGraduatedStar = 0;
        } else if (totalCandiesGraduated < 50) {
            totalCandiesGraduatedStar = 1;
        } else if (totalCandiesGraduated < 500) {
            totalCandiesGraduatedStar = 2;
        } else if (totalCandiesGraduated < 2500) {
            totalCandiesGraduatedStar = 3;
        } else if (totalCandiesGraduated < 5000) {
            totalCandiesGraduatedStar = 4;
        } else {
            totalCandiesGraduatedStar = 5;
        }
        if (totalCandiesGraduatedStar - prevTotalCandiesGraduatedStar == 1) {
            addSugarToUser(totalCandiesGraduatedStar);
        }

        // jars made
        int prevTotalJarsMadeStar = totalJarsMadeStar;
        if (totalJarsMade < 3) {
            totalJarsMadeStar = 0;
        } else if (totalJarsMade < 8) {
            totalJarsMadeStar = 1;
        } else if (totalJarsMade < 20) {
            totalJarsMadeStar = 2;
        } else if (totalJarsMade < 50) {
            totalJarsMadeStar = 3;
        } else if (totalJarsMade < 100) {
            totalJarsMadeStar = 4;
        } else {
            totalJarsMadeStar = 5;
        }
        if (totalJarsMadeStar - prevTotalJarsMadeStar == 1) {
            addSugarToUser(totalJarsMadeStar);
        }

        // sugar spent
        int prevTotalSugarSpentStar = totalSugarSpentStar;
        if (totalSugarSpent < 1000) {
            totalSugarSpentStar = 0;
        } else if (totalSugarSpent < 5000) {
            totalSugarSpentStar = 1;
        } else if (totalSugarSpent < 10000) {
            totalSugarSpentStar = 2;
        } else if (totalSugarSpent < 100000) {
            totalSugarSpentStar = 3;
        } else if (totalSugarSpent < 1000000) {
            totalSugarSpentStar = 4;
        } else {
            totalSugarSpentStar = 5;
        }
        if (totalSugarSpentStar - prevTotalSugarSpentStar == 1) {
            addSugarToUser(totalSugarSpentStar);
        }

        // current level
        int prevLevelStar = levelStar;
        if (level < 5) {
            levelStar = 0;
        } else if (level < 10) {
            levelStar = 1;
        } else if (level < 50) {
            levelStar = 2;
        } else if (level < 100) {
            levelStar = 3;
        } else if (level < 300) {
            levelStar = 4;
        } else {
            levelStar = 5;
        }
        if (levelStar - prevLevelStar == 1) {
            addSugarToUser(levelStar);
        }
    }

    // used in the above method
    private void addSugarToUser(int stars) {
        int sugarAdded = 0;
        switch (stars) {
            case 1:
                sugarAdded = 1000;
                break;
            case 2:
                sugarAdded = 2000;
                break;
            case 3:
                sugarAdded = 5000;
                break;
            case 4:
                sugarAdded = 10000;
                break;
            case 5:
                sugarAdded = 50000;
                break;
        }

        // increase user's sugar count
        Activity mainActivity = getActivity();
        if (mainActivity instanceof MainActivity) {
            MainActivity ma = (MainActivity) mainActivity;
            ma.increaseSugar(sugarAdded);
        }
    }

    // read a string out from local text file
    public String loadFromLocalFile(String fileName) {
        FileInputStream fis = null;
        String output = null;

        try {
            fis = getContext().openFileInput(fileName);
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

    @Override
    public void onPause() {
        super.onPause();
        saveAllData();
    }



    // FOR TESTING ONLY!
    public void resetAllData() {
        // comment/uncomment as needed

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putInt(EXP, 1);
        //editor.putInt(STREAK, 2);

        editor.putString(USERNAME, username);

        editor.putInt(LONGEST_STREAK, 1);
        editor.putInt(LONGEST_STREAK_STAR, 0);

        editor.putInt(TOTAL_CANDIES_MADE, 0);
        editor.putInt(TOTAL_CANDIES_MADE_STAR, 0);

        editor.putInt(TOTAL_CANDIES_GRADUATED, 0);
        editor.putInt(TOTAL_CANDIES_GRADUATED_STAR, 0);

        editor.putInt(TOTAL_JARS_MADE, 0);
        editor.putInt(TOTAL_JARS_MADE_STAR, 0);

        editor.putInt(TOTAL_SUGAR_SPENT, 0);
        editor.putInt(TOTAL_SUGAR_SPENT_STAR, 0);

        editor.putInt(LEVEL, 1);
        editor.putInt(LEVEL_STAR, 0);

        editor.commit();
    }
}














    //          Failed attempt at using AdvancedNestedScrollView
//        AchievementListAdapter adapter = new AchievementListAdapter(getContext(), achievementList);
//        LinearLayoutManager lm = new LinearLayoutManager(getContext());
//        final MaxHeightRecyclerView rv = (MaxHeightRecyclerView) view.findViewById(R.id.card_recycler_view);
//        rv.setLayoutManager(lm);
//        rv.setAdapter(adapter);
//        rv.addItemDecoration(new DividerItemDecoration(getContext(), lm.getOrientation()));
//
//        // OMIT THE LAST PART ABOUT ONSCROLL LISTENER AND CARD HEADER SHADOW
//
//        AdvancedNestedScrollView advancedNestedScrollView = (AdvancedNestedScrollView) view.findViewById(R.id.achievement_nested_scroll_view);
//        advancedNestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        advancedNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY == 0 && oldScrollY > 0) {
//                    // Reset the RecyclerView's scroll position each time the card returns to its start position.
//                    rv.scrollToPosition(0);
//
//                    // OMIT cardShadowHeader
//                }
//            }
//        });

//        return view;
