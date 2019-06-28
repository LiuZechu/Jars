package com.gmail.liuzechu2013.singapore.jars.shop_recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.liuzechu2013.singapore.jars.Item;
import com.gmail.liuzechu2013.singapore.jars.R;

import java.util.List;

public class ShopItemListAdapter extends RecyclerView.Adapter<ShopItemViewHolder> {
    private LayoutInflater mInflater;
    private List<Item> itemList;

    public ShopItemListAdapter(Context context, List<Item> itemList) {
        mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ShopItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.shop_list_item, viewGroup, false);

        return new ShopItemViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopItemViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Item superpower = itemList.get(i);
        // add date to the view
        viewHolder.shopItemView.setText(superpower.getName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
