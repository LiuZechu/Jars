package com.gmail.liuzechu2013.singapore.jars;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class JarShelfAdapter extends RecyclerView.Adapter<JarShelfViewHolder> {
    private LayoutInflater mInflater;
    private List<Jar> jarList;

    public JarShelfAdapter(Context context, List<Jar> jarList) {
        mInflater = LayoutInflater.from(context);
        this.jarList = jarList;
    }

    @NonNull
    @Override
    public JarShelfViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.jar_shelf_item, viewGroup, false);

        return new JarShelfViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull JarShelfViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Jar jar = jarList.get(i);
        // add date to the view
        viewHolder.jarItemView.setText(jar.getTitle());

        String resName = "ic_jar" + jar.getJarType();
        int resId = viewHolder.itemView.getContext().getResources()
                .getIdentifier(resName, "drawable",
                        viewHolder.itemView.getContext().getPackageName());
        viewHolder.jarImageView.setImageResource(resId);

        viewHolder.jarColorView.getBackground().setColorFilter(Color.parseColor(jar.getJarColor()), PorterDuff.Mode.SRC_OVER);

        viewHolder.currentJar = jar;
    }

    @Override
    public int getItemCount() {
        return jarList.size();
    }
}
