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

import com.google.gson.Gson;

import java.util.ArrayList;

public class UserItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    protected TextView userItemView;
    protected TextView userItemTypeView;
    private UserItemListAdapter mAdapter;
    // protected ShopItem currentUserItem;

    public UserItemViewHolder(View itemView, UserItemListAdapter adapter) {
        super(itemView);
        userItemView = (TextView) itemView.findViewById(R.id.user_item_textView);
        userItemTypeView = (TextView) itemView.findViewById(R.id.user_item_type_textView);
        this.mAdapter = adapter;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO: do something


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
