package com.xyz.orbital.singapore.jars;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class AchievementViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    protected TextView achievementItemView;
    private AchievementListAdapter mAdapter;

    public AchievementViewHolder(View itemView, AchievementListAdapter adapter) {
        super(itemView);
        achievementItemView = itemView.findViewById(R.id.achievement_description_textView);

        //Failed attempt at using AdvancedNestedScrollView
        //achievementItemView = itemView.findViewById(R.id.achievement_text_view);

        this.mAdapter = adapter;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       // TODO: do something


    }
}
