package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;

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
                    case R.id.menu_context_jar_delete:
                        // pop up dialogue to warn user whether to proceed with deleting the jar
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(jarItemView.getContext());
                        alertBuilder.setTitle("DELETE THIS JAR?");
                        alertBuilder.setMessage("Are you sure that you want to delete this Jar? All the Candies inside will be lost and this action CANNOT be undone.");
                        alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteJar();
                            }
                        });
                        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });

                        alertBuilder.show();

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
        // ArrayList<Jar> jarList = new ArrayList<>();
        // jarList.add(currentJar);
        // Gson gson = new Gson();
        // String jsonString = gson.toJson(jarList);
        // intent.putExtra(TrainingActivity.GET_JAR_LIST, jsonString);

        intent.putExtra(TrainingActivity.TRAINING_JAR_NAME, currentJar.getTitle());

        activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_TRAINING);

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

    public void deleteJar() {
        MainActivity.getJarList().remove(currentJar);
        Activity activity = getActivity(jarItemView.getContext());
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.loadFragment(new JarsFragment());

            // save changes to local file
            Gson gson = new Gson();
            String jsonString = gson.toJson(MainActivity.getJarList());
            mainActivity.saveToLocalFile(MainActivity.USER_JAR_FILE_NAME, jsonString);
        }
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
