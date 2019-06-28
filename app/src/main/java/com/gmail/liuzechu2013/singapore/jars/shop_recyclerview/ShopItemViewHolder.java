package com.gmail.liuzechu2013.singapore.jars.shop_recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gmail.liuzechu2013.singapore.jars.R;
import com.gmail.liuzechu2013.singapore.jars.UserItemListAdapter;

public class ShopItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    protected TextView shopItemView;
    private ShopItemListAdapter mAdapter;
    // protected Item currentUserItem;

    public ShopItemViewHolder(View itemView, ShopItemListAdapter adapter) {
        super(itemView);
        shopItemView = (TextView) itemView.findViewById(R.id.shop_item_description_textView);
        this.mAdapter = adapter;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO: do something


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
