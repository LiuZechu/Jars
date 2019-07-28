package com.xyz.orbital.singapore.jars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

public class JarsFragment extends Fragment {
    private RecyclerView mListRecyclerView;
    private RecyclerView mShelfRecyclerView;
    private static ArrayList<Jar> jarList;
    private FloatingActionButton makeCandyButton;
    private String[] jarNameArray;

    public static final String JAR_NAME_ARRAY = "JarNameArray";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_jars, null);

        // Load list of jars
        // get main activity to update its jarList in case its content changes
        Activity activity = getActivity();
        MainActivity mainActivity = null;
        if (activity instanceof MainActivity) {
            mainActivity = (MainActivity) activity;
        }
        mainActivity.loadDataIntoJarList();
        jarList = MainActivity.getJarList();


        // create jar list if there is none
        if (jarList == null) {
            jarList = new ArrayList<>();
        }

        mListRecyclerView = (RecyclerView) view.findViewById(R.id.jar_list_recyclerView);
        JarListAdapter adapter = new JarListAdapter(getContext(), jarList);
        mListRecyclerView.setAdapter(adapter);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mShelfRecyclerView = view.findViewById(R.id.jar_shelf_recyclerView);
        JarShelfAdapter shelfAdapter = new JarShelfAdapter(getContext(), jarList);
        mShelfRecyclerView.setAdapter(shelfAdapter);
        mShelfRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        makeCandyButton = view.findViewById(R.id.make_candy_floating_action_button);
        makeCandyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewCandy();
            }
        });

        jarNameArray = new String[jarList.size()];
        for (int i = 0; i < jarList.size(); i++) {
            jarNameArray[i] = jarList.get(i).getTitle();
        }

        // menu that pops up when tapping a list item
        //registerForContextMenu(mListRecyclerView);
        //mListRecyclerView.setLongClickable(false); // menu pops up via short tap instead of long press

        return view;
    }

    public void makeNewCandy() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(jarNameArray);

        Intent intent = new Intent(getContext(), MakeNewCandyActivity.class);
        intent.putExtra(JAR_NAME_ARRAY, jsonString);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_NEW_CANDY);
    }

    public static ArrayList<Jar> getJarList() {
        return jarList;
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

// TESTING: CREATE A TEMP jarList; will be replaced later
//        jarList = new ArrayList<>();
//        Jar jar1 = new Jar("Discrete Structures");
//        jar1.addCandy(new Candy("wheres NYC", "USA"));
//        jar1.addCandy(new Candy("whats the capital of singapore", "singapore"));
//        jar1.addCandy(new Candy("whats the time complexity of floyd warshall algo", "V^3"));
//        jarList.add(jar1);
//        jarList.add(new Jar("CS"));
//        jarList.add(new Jar("French"));
//        jarList.add(new Jar("Design Thinking"));
//        jarList.add(new Jar("Computer Organisation"));
//        jarList.add(new Jar("PS"));
//        jarList.add(new Jar("GAPI"));
//        jarList.add(new Jar("Chinese"));
//        jarList.add(new Jar("Japanese"));
