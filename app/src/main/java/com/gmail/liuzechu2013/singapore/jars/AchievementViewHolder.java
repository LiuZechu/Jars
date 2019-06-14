package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import edmt.dev.advancednestedscrollview.AdvancedNestedScrollView;

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

//    // how to get activity from context
//    public Activity getActivity(Context context)
//    {
//        if (context == null)
//        {
//            return null;
//        }
//        else if (context instanceof ContextWrapper)
//        {
//            if (context instanceof Activity)
//            {
//                return (Activity) context;
//            }
//            else
//            {
//                return getActivity(((ContextWrapper) context).getBaseContext());
//            }
//        }
//
//        return null;
//    }
}
