package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

public class CandyViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    protected TextView candyItemView;
    private CandyListAdapter mAdapter;
    // the following two attributes are for toggling prompt/answer when tapping a candy item; can change later
    protected Candy currentCandy;
    private boolean isPrompt;

    public CandyViewHolder(View itemView, CandyListAdapter adapter) {
        super(itemView);
        candyItemView = (TextView) itemView.findViewById(R.id.candy_item_textView);
        this.mAdapter = adapter;
        itemView.setOnClickListener(this);

        // initialise isPrompt
        isPrompt = true;
    }

    @Override
    public void onClick(View v) {
        if (isPrompt) {
            candyItemView.setText(currentCandy.getAnswer());
        } else {
            candyItemView.setText(currentCandy.getPrompt());
        }
        isPrompt = !isPrompt;
    }

    // how to get activity from context
    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }
}
