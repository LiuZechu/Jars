package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class JarViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    protected TextView jarItemView;
    private JarListAdapter mAdapter;
    protected Jar currentJar;
    public static final String JAR_STRING = "JAR STRING";

    public JarViewHolder(View itemView, JarListAdapter adapter) {
        super(itemView);
        jarItemView = (TextView) itemView.findViewById(R.id.jar_item_textView);
        this.mAdapter = adapter;
        itemView.setOnClickListener(this);
        //jarItemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {
        Activity activity = getActivity(jarItemView.getContext());
        PopupMenu popup = new PopupMenu(activity, jarItemView);
        popup.getMenuInflater().inflate(R.menu.menu_context_jars, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_context_jar_train:
                        trainCandies();
                        break;
                    case R.id.menu_context_jar_view_all:
                        viewAllCandies();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        menu.add(this.getAdapterPosition(), R.id.menu_context_jar_train, 0, "Train");
//        menu.add(this.getAdapterPosition(), R.id.menu_context_jar_view_all, 1, "View All");
//    }

    public void trainCandies() {
        Activity activity = getActivity(jarItemView.getContext());
        Intent intent = new Intent(activity, TrainingActivity.class);

        // send candies as an extra
        ArrayList<Jar> jarList = new ArrayList<>();
        jarList.add(currentJar);
        Gson gson = new Gson();
        String jsonString = gson.toJson(jarList);
        intent.putExtra(TrainingActivity.GET_JAR_LIST, jsonString);

        activity.startActivity(intent);
    }

    public void viewAllCandies() {
        Activity activity = getActivity(jarItemView.getContext());
        Intent intent = new Intent(activity, ViewAllCandiesActivity.class);

        // prepare jar to be sent as an extra
        Gson gson = new Gson();
        String jarString = gson.toJson(currentJar);
        intent.putExtra(JAR_STRING, jarString);
        activity.startActivity(intent);
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
