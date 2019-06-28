package com.gmail.liuzechu2013.singapore.jars;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class UserItemListAdapter extends RecyclerView.Adapter<UserItemViewHolder> {
    private LayoutInflater mInflater;
    private List<Item> userItemList;

    public UserItemListAdapter(Context context, List<Item> userItemList) {
        mInflater = LayoutInflater.from(context);
        this.userItemList = userItemList;
    }

    @NonNull
    @Override
    public UserItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.user_item_list_item, viewGroup, false);

        return new UserItemViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull UserItemViewHolder viewHolder, int i) {
        // retrieve data for that position i
        Item userItem = userItemList.get(i);
        // add date to the view
        viewHolder.userItemView.setText(userItem.getName());
        int itemType = userItem.getType();
        String typeName;
        switch (itemType) {
            case 1:
                typeName = "Superpower"; break;
            case 2:
                typeName = "Extension"; break;
            case 3:
                typeName = "Jar"; break;
            case 4:
                typeName = "Candy"; break;
            case 5:
                typeName = "Candy Expression"; break;
            default:
                typeName = "Error";
        }
        viewHolder.userItemTypeView.setText(typeName);
    }

    @Override
    public int getItemCount() {
        return userItemList.size();
    }
}
