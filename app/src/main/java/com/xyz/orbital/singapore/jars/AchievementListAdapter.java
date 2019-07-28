package com.xyz.orbital.singapore.jars;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AchievementListAdapter extends RecyclerView.Adapter<AchievementViewHolder> {
    private LayoutInflater mInflater;
    private List<Achievement> achievementList;

    public AchievementListAdapter(Context context, List<Achievement> achievementList) {
        mInflater = LayoutInflater.from(context);
        this.achievementList = achievementList;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.achievement_list_item, viewGroup, false);

        // Failed attempt at AdvancedNestScrollView
        // View view = mInflater.inflate(R.layout.achievement_view_holder_item, viewGroup, false);

        return new AchievementViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Achievement achievement = achievementList.get(i);
        // add date to the view
        viewHolder.achievementItemView.setText(achievement.getName());
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }
}
