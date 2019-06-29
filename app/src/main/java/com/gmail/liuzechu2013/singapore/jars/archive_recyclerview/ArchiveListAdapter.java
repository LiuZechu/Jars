package com.gmail.liuzechu2013.singapore.jars.archive_recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.liuzechu2013.singapore.jars.Jar;
import com.gmail.liuzechu2013.singapore.jars.JarViewHolder;
import com.gmail.liuzechu2013.singapore.jars.R;

import java.util.List;

public class ArchiveListAdapter extends RecyclerView.Adapter<ArchiveViewHolder> {
    private LayoutInflater mInflater;
    private List<Jar> archiveList;

    public ArchiveListAdapter(Context context, List<Jar> archiveList) {
        mInflater = LayoutInflater.from(context);
        this.archiveList = archiveList;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.archive_list_item, viewGroup, false);

        return new ArchiveViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Jar jar = archiveList.get(i);
        // add date to the view
        viewHolder.archiveItemView.setText(jar.getTitle());
        viewHolder.currentJar = jar;
    }

    @Override
    public int getItemCount() {
        return archiveList.size();
    }
}
