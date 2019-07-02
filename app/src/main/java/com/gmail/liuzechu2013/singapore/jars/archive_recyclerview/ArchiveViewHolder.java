package com.gmail.liuzechu2013.singapore.jars.archive_recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.app.AlertDialog;

import com.gmail.liuzechu2013.singapore.jars.ArchiveActivity;
import com.gmail.liuzechu2013.singapore.jars.Jar;
import com.gmail.liuzechu2013.singapore.jars.R;
import com.gmail.liuzechu2013.singapore.jars.ViewAllCandiesActivity;
import com.google.gson.Gson;

public class ArchiveViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    protected TextView archiveItemView;
    private ArchiveListAdapter mAdapter;
    protected Jar currentJar;
    public static final String JAR_STRING = "JAR STRING";

    public ArchiveViewHolder(View itemView, ArchiveListAdapter adapter) {
        super(itemView);
        archiveItemView = (TextView) itemView.findViewById(R.id.archive_item_textView);
        this.mAdapter = adapter;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Activity activity = getActivity(archiveItemView.getContext());
        PopupMenu popup = new PopupMenu(activity, archiveItemView);

        popup.getMenuInflater().inflate(R.menu.menu_context_archives, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_context_archive_jar_view_all:
                        viewAllCandies();
                        break;
                    case R.id.menu_context_archive_jar_delete:
                        // pop up dialogue to warn user whether to proceed with deleting the jar
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(archiveItemView.getContext());
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



    public void viewAllCandies() {
        Activity activity = getActivity(archiveItemView.getContext());
        Intent intent = new Intent(activity, ViewAllCandiesActivity.class);

        // prepare jar to be sent as an extra
        Gson gson = new Gson();
        String jarString = gson.toJson(currentJar);
        intent.putExtra(JAR_STRING, jarString);
        activity.startActivity(intent);
    }

    public void deleteJar() {
        ArchiveActivity.getGraduatedJarHash().remove(currentJar.getTitle());

        Activity activity = getActivity(archiveItemView.getContext());
        if (activity instanceof ArchiveActivity) {
            ArchiveActivity archiveActivity = (ArchiveActivity) activity;

            // save changes to local file
            Gson gson = new Gson();
            String jsonString = gson.toJson(ArchiveActivity.getGraduatedJarHash());
            archiveActivity.saveToLocalFile(ArchiveActivity.GRADUATED_CANDIES_FILE_NAME, jsonString);
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
