package com.gmail.liuzechu2013.singapore.jars;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ViewAllCandiesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<Candy> candyList;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_candies);

        // get a jar from intent
        String jsonString = getIntent().getStringExtra(JarViewHolder.JAR_STRING);
        Gson gson = new Gson();
        Jar jar = gson.fromJson(jsonString, Jar.class);
        candyList = jar.getCandies();

        // set title of the screen
        title = findViewById(R.id.view_all_candies_title_textView);
        title.setText(jar.getTitle());

        // set up recycler view
        mRecyclerView = findViewById(R.id.view_all_candies_recyclerView);
        CandyListAdapter adapter = new CandyListAdapter(this, candyList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
