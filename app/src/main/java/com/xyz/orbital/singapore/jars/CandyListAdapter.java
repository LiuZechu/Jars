package com.xyz.orbital.singapore.jars;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CandyListAdapter extends RecyclerView.Adapter<CandyViewHolder> {
    private LayoutInflater mInflater;
    private List<Candy> candyList;
    private Jar jar;

    public CandyListAdapter(Context context, List<Candy> candyList, Jar jar) {
        mInflater = LayoutInflater.from(context);
        this.candyList = candyList;
        this.jar = jar;
    }

    @NonNull
    @Override
    public CandyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.jar_list_item, viewGroup, false);

        return new CandyViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CandyViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Candy candy = candyList.get(i);
        // add date to the view
        // added level number for testing; will be removed later
        viewHolder.candyItemView.setText(candy.getPrompt() + " LEVEL: " + candy.getLevel());
        String[] arr = {"#ef6256", "#f99c1c", "#fec41b", "#47b585", "#5bc4bf", "#825ca4", "#e96ca9"};
        viewHolder.candyColorView.getBackground().setColorFilter(Color.parseColor(arr[candy.getLevel() - 1]), PorterDuff.Mode.SRC_OVER);
        viewHolder.currentCandy = candy;
        viewHolder.currentJar = jar;
    }

    @Override
    public int getItemCount() {
        return candyList.size();
    }
}
