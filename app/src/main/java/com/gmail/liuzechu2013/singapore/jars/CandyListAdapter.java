package com.gmail.liuzechu2013.singapore.jars;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CandyListAdapter extends RecyclerView.Adapter<CandyViewHolder> {
    private LayoutInflater mInflater;
    private List<Candy> candyList;

    public CandyListAdapter(Context context, List<Candy> candyList) {
        mInflater = LayoutInflater.from(context);
        this.candyList = candyList;
    }

    @NonNull
    @Override
    public CandyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.candy_list_item, viewGroup, false);

        return new CandyViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CandyViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Candy candy = candyList.get(i);
        // add date to the view
        viewHolder.candyItemView.setText(candy.getPrompt());
        viewHolder.currentCandy = candy;
    }

    @Override
    public int getItemCount() {
        return candyList.size();
    }
}
