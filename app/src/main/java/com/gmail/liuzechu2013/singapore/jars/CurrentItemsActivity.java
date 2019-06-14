package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class CurrentItemsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<ShopItem> userItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_items);

        // TESTING: CREATE A TEMP jarList; will be replaced later
        userItemList = new ArrayList<>();
        userItemList.add(new ShopItem(ShopItem.CANDY, "Mint Candy"));
        userItemList.add(new ShopItem(ShopItem.SUPERPOWER, "Keep Streak Alive for 2 days"));
        userItemList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "Smiley Face"));
        userItemList.add(new ShopItem(ShopItem.CANDY, "Chocolate Candy"));
        userItemList.add(new ShopItem(ShopItem.SUPERPOWER, "Keep Streak Alive for 3 days"));
        userItemList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "Angry Face"));
        userItemList.add(new ShopItem(ShopItem.CANDY, "Lollipop"));
        userItemList.add(new ShopItem(ShopItem.SUPERPOWER, "Keep Streak Alive for 5 days"));
        userItemList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "Cute Face"));
        // TESTING ENDS

        mRecyclerView = (RecyclerView) findViewById(R.id.user_item_list_recyclerView);
        UserItemListAdapter adapter = new UserItemListAdapter(this, userItemList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
