package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class CandyFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ArrayList<Jar> jarList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_candy, null);

        // TESTING: CREATE A TEMP jarList; will be replaced later
        jarList = new ArrayList<>();
        Jar jar1 = new Jar("Discrete Structures");
        jar1.addCandy(new Candy("wheres NYC", "USA"));
        jar1.addCandy(new Candy("whats the capital of singapore", "singapore"));
        jar1.addCandy(new Candy("whats the time complexity of floyd warshall algo", "V^3"));
        jarList.add(jar1);
        jarList.add(new Jar("CS"));
        jarList.add(new Jar("French"));
        jarList.add(new Jar("Design Thinking"));
        jarList.add(new Jar("Computer Organisation"));
        jarList.add(new Jar("PS"));
        jarList.add(new Jar("GAPI"));
        jarList.add(new Jar("Chinese"));
        jarList.add(new Jar("Japanese"));
        // TESTING ENDS

        mRecyclerView = (RecyclerView) view.findViewById(R.id.jar_list_recyclerView);
        JarListAdapter adapter = new JarListAdapter(getContext(), jarList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // menu that pops up when tapping a list item
        //registerForContextMenu(mRecyclerView);
        //mRecyclerView.setLongClickable(false); // menu pops up via short tap instead of long press

        return view;
    }



//    // context menu for individual jar
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.menu_context_jars, menu);
//    }


//    public boolean onContextItemSelected(MenuItem item) {
//        //AdapterView.AdapterContextMenuInfo info =
//        //        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        switch (item.getItemId()) {
//            case R.id.menu_context_jar_train:
//                trainCandies();
//                return true;
//            case R.id.menu_context_jar_view_all:
//                viewAllCandies();
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }
//
//    public void trainCandies() {
//        Intent intent = new Intent(getContext(), TrainingActivity.class);
//        startActivity(intent);
//    }
//
//    public void viewAllCandies() {
//        Intent intent = new Intent(getContext(), ViewAllCandiesActivity.class);
//        startActivity(intent);
//    }
}
