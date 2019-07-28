package com.xyz.orbital.singapore.jars;

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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CandyViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
    protected TextView candyItemView;
    protected View candyColorView;
    private CandyListAdapter mAdapter;
    // the following two attributes are for toggling prompt/answer when tapping a candy item; can change later
    protected Candy currentCandy;
    private boolean isPrompt;
    protected Jar currentJar; // this is used for Candy deletion

    public CandyViewHolder(View itemView, CandyListAdapter adapter) {
        super(itemView);
        candyItemView = (TextView) itemView.findViewById(R.id.jar_item_textView);
        candyColorView = itemView.findViewById(R.id.jar_item_colorView);
        this.mAdapter = adapter;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

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

    @Override
    public boolean onLongClick(View v) {
        Activity activity = getActivity(candyItemView.getContext());
        PopupMenu popup = new PopupMenu(activity, candyItemView);
        popup.getMenuInflater().inflate(R.menu.menu_context_candies, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_context_candy_delete:
                        // pop up dialogue to warn user whether to proceed with deleting the jar
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(candyItemView.getContext());
                        alertBuilder.setTitle("DELETE THIS CANDY?");
                        alertBuilder.setMessage("Are you sure that you want to delete this Candy? This action CANNOT be undone.");
                        alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCandy();
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

        return false;
    }

    private void deleteCandy() {

        Gson gson = new Gson();
        Activity currentActivity = getActivity(candyItemView.getContext());

        // local jar list from local file
        String jarListString = loadFromLocalFile(MainActivity.USER_JAR_FILE_NAME, currentActivity);
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        ArrayList<Jar> jarList = gson.fromJson(jarListString, type);
        Jar jarAfterDeletion = null; // for reloading current page

        // process jar list to delete the correct candy
        for (Jar jar : jarList) {
            if (jar.getTitle().equals(currentJar.getTitle())) {
                // correct jar is found
                jar.getCandies().remove(currentCandy);
                jarAfterDeletion = jar; // for reloading current page
            }
        }

        // save changes to local file
        String jsonString = gson.toJson(jarList);
        saveToLocalFile(MainActivity.USER_JAR_FILE_NAME, jsonString, currentActivity);

        // reload current page
        currentActivity.finish();
        Intent newIntent = currentActivity.getIntent();
        newIntent.putExtra(JarViewHolder.JAR_STRING, gson.toJson(jarAfterDeletion));
        currentActivity.startActivity(newIntent);
    }

    // save a String into local text file on phone
    public void saveToLocalFile(String fileName, String stringToSave, Activity activity) {
        FileOutputStream fos = null;
        try {
            fos = activity.openFileOutput(fileName, MainActivity.MODE_PRIVATE);
            fos.write(stringToSave.getBytes());

            Toast.makeText(activity, "changes saved successfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // read a string out from local text file
    public String loadFromLocalFile(String fileName, Activity activity) {
        FileInputStream fis = null;
        String output = null;

        try {
            fis = activity.openFileInput(fileName);
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
}
