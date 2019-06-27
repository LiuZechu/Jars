package com.gmail.liuzechu2013.singapore.jars;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edmt.dev.advancednestedscrollview.AdvancedNestedScrollView;
import edmt.dev.advancednestedscrollview.MaxHeightRecyclerView;

public class ProfileFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ArrayList<Achievement> achievementList;
    private Button resetButton; // button to reset user stats
    private TextView usernameTextView;
    private TextView levelTextView;
    private TextView expTextView;
    private TextView totalCandiesMadeTextView;
    private TextView totalCandiesGraduatedTextView;

    // SharedPreferences to save user data
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String USERNAME = "username";
    public static final String LEVEL = "level";
    public static final String EXP = "exp";
    public static final String STREAK = "streak";
    public static final String TOTAL_CANDIES_MADE = "totalCandiesMade";
    public static final String TOTAL_CANDIES_GRADUATED = "totalCandiesGraduated";

    // user stats:
    private String username;
    private int level;
    private int exp;
    private int streak;
    private int totalCandiesMade;
    private int totalCandiesGraduated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, null);

        // initialise user stats
        loadAllData();

        usernameTextView = view.findViewById(R.id.profile_username_textView);

        usernameTextView.setText(username);

        // draw achievements
        ImageView achievementStreakBar = (ImageView) view.findViewById(R.id.achievement_streak_bar);
        achievementStreakBar.getDrawable().setLevel(5000);
        ImageView achievementMadeBar = (ImageView) view.findViewById(R.id.achievement_made_bar);
        achievementMadeBar.getDrawable().setLevel(2000);
        ImageView achievementGraduatedBar = (ImageView) view.findViewById(R.id.achievement_graduated_bar);
        achievementGraduatedBar.getDrawable().setLevel(3200);
        ImageView achievementJarsBar = (ImageView) view.findViewById(R.id.achievement_jars_bar);
        achievementJarsBar.getDrawable().setLevel(9040);
        ImageView achievementSugarBar = (ImageView) view.findViewById(R.id.achievement_sugar_bar);
        achievementSugarBar.getDrawable().setLevel(1000);
        ImageView achievementLevelBar = (ImageView) view.findViewById(R.id.achievement_level_bar);
        achievementLevelBar.getDrawable().setLevel(50);





        // button to reset user stats
        resetButton = (Button) view.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUserStats();
            }
        });

        return view;
    }

    public void resetUserStats() {
        // does not reset name
        level = 1;
        exp = 100;
        streak = 1;
        totalCandiesMade = 0;
        totalCandiesGraduated = 0;

        // TODO: Streak number not included here
        levelTextView.setText(level + "");
        expTextView.setText(exp + "");
        totalCandiesMadeTextView.setText(totalCandiesMade + "");
        totalCandiesGraduatedTextView.setText(totalCandiesGraduated + "");

        saveAllData();
        Toast.makeText(getContext(), "User data reset successfully", Toast.LENGTH_SHORT).show();
    }

    public void saveAllData() {
        // does not save username here
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LEVEL, level);
        editor.putInt(EXP, exp);
        editor.putInt(STREAK, streak);
        editor.putInt(TOTAL_CANDIES_MADE, totalCandiesMade);
        editor.putInt(TOTAL_CANDIES_GRADUATED, totalCandiesGraduated);
        editor.commit();
    }

    public void loadAllData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "default username");
        level = sharedPreferences.getInt(LEVEL, -1);
        exp = sharedPreferences.getInt(EXP, -1);
        streak = sharedPreferences.getInt(STREAK, -1);
        totalCandiesMade = sharedPreferences.getInt(TOTAL_CANDIES_MADE, -1);
        totalCandiesGraduated = sharedPreferences.getInt(TOTAL_CANDIES_GRADUATED, -1);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveAllData();
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
}
