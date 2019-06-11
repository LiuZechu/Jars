package com.gmail.liuzechu2013.singapore.jars;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

public class JarListAdapter extends RecyclerView.Adapter<JarViewHolder> {
    private LayoutInflater mInflater;
    private List<Jar> jarList;

    public JarListAdapter(Context context, List<Jar> jarList) {
        mInflater = LayoutInflater.from(context);
        this.jarList = jarList;
    }

    @NonNull
    @Override
    public JarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.jar_list_item, viewGroup, false);

        return new JarViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull JarViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Jar jar = jarList.get(i);
        // add date to the view
        viewHolder.jarItemView.setText(jar.getTitle());
        viewHolder.currentJar = jar;
    }

    @Override
    public int getItemCount() {
        return jarList.size();
    }
}
