package com.xyz.orbital.singapore.jars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;

public class JarShelfViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    protected TextView jarItemView;
    protected ImageView jarImageView;
    protected View jarColorView;
    private JarShelfAdapter mAdapter;
    protected Jar currentJar;
    public static final String JAR_STRING = "JAR STRING";

    public JarShelfViewHolder(View itemView, JarShelfAdapter adapter) {
        super(itemView);
        jarItemView = (TextView) itemView.findViewById(R.id.jar_shelf_textView);
        jarImageView = itemView.findViewById(R.id.jar_shelf_imageView);
        jarColorView = itemView.findViewById(R.id.jar_shelf_colorView);
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

            // update last accessed jar index for spinner in jars fragment
            saveLastAccessJarIndex(0);

            // update jar name array string in main activity
            SharedPreferences sharedPreferences = getActivity(jarItemView.getContext()).getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
            String jarNameArrayString = sharedPreferences.getString(MainActivity.USER_JAR_NAME_ARRAY, "");
            String[] jarNameArray = gson.fromJson(jarNameArrayString, String[].class);
            String[] newJarNameArray;
            if (jarNameArray.length > 1) {
                newJarNameArray = new String[jarNameArray.length - 1];
                int counter = 0;
                for (int i = 0; i < jarNameArray.length; i++) {
                    if (!jarNameArray[i].equals(currentJar.getTitle())) {
                        newJarNameArray[counter] = jarNameArray[i];
                        counter++;
                    }
                }
            } else {
                newJarNameArray = new String[0];
            }
            String toSave = gson.toJson(newJarNameArray);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(MainActivity.USER_JAR_NAME_ARRAY, toSave);
            Log.d("test", toSave);

            editor.commit();
        }
    }

    // to reset last accessed jar index to zero
    private void saveLastAccessJarIndex(int index) {
        SharedPreferences sharedPreferences = getActivity(jarItemView.getContext()).getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MakeNewCandyFromFloatingActivity.LAST_ACCESS_JAR_INDEX, index);
        editor.commit();
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
