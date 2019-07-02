package com.gmail.liuzechu2013.singapore.jars;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class CurrentItemsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Item> userItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_items);

        /*// TESTING: CREATE A TEMP jarList; will be replaced later
        userItemList = new ArrayList<>();
        userItemList.add(new Item(Item.CANDY, "Mint Candy"));
        userItemList.add(new Item(Item.SUPERPOWER, "Keep Streak Alive for 2 days"));
        userItemList.add(new Item(Item.CANDY_EXPRESSION, "Smiley Face"));
        userItemList.add(new Item(Item.CANDY, "Chocolate Candy"));
        userItemList.add(new Item(Item.SUPERPOWER, "Keep Streak Alive for 3 days"));
        userItemList.add(new Item(Item.CANDY_EXPRESSION, "Angry Face"));
        userItemList.add(new Item(Item.CANDY, "Lollipop"));
        userItemList.add(new Item(Item.SUPERPOWER, "Keep Streak Alive for 5 days"));
        userItemList.add(new Item(Item.CANDY_EXPRESSION, "Cute Face"));
        // TESTING ENDS*/
    }
}
