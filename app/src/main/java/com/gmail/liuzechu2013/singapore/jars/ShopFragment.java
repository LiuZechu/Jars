package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gmail.liuzechu2013.singapore.jars.shop_recyclerview.ShopItemListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShopFragment extends Fragment {
    private ViewCurrentItemsListener activityCommander;

    private RecyclerView superpowerRecyclerView;
    private RecyclerView extensionRecyclerView;
    private RecyclerView jarRecyclerView;
    private RecyclerView candyRecyclerView;
    private RecyclerView candyExpressionRecyclerView;

    private ArrayList<ShopItem> superpowerList;
    private ArrayList<ShopItem> extensionList;
    private ArrayList<ShopItem> jarList;
    private ArrayList<ShopItem> candyList;
    private ArrayList<ShopItem> candyExpressionList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            activityCommander = (ViewCurrentItemsListener) getActivity(context);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_shop, null);

        Button button = (Button) view.findViewById(R.id.view_current_items_button);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCommander.viewCurrentItems();
            }
        };
        button.setOnClickListener(listener);

        // Get all shop items from a .txt file from assets
        Activity currentActivity = getActivity(getContext());
        String jsonString = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(currentActivity.getAssets().open("all_shop_items")));

            // read file content
            StringBuilder sb = new StringBuilder();
            String mLine = null;
            while ((mLine = reader.readLine()) != null) {
                //process line
                sb.append(mLine).append("\n");
            }
            jsonString = sb.toString();
        } catch (IOException e) {
            //log the exception
            Log.d("error", e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    Log.d("error", e.toString());
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ArrayList<ShopItem>>>(){}.getType();
        ArrayList<ArrayList<ShopItem>> allShopItems = gson.fromJson(jsonString, type);
        superpowerList = allShopItems.get(0);
        extensionList = allShopItems.get(1);
        jarList = allShopItems.get(2);
        candyList = allShopItems.get(3);
        candyExpressionList = allShopItems.get(4);


        // superpowers
        superpowerRecyclerView = (RecyclerView) view.findViewById(R.id.shop_superpowers_recyclerView);
        ShopItemListAdapter adapter1 = new ShopItemListAdapter(getContext(), superpowerList);
        superpowerRecyclerView.setAdapter(adapter1);
        superpowerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));

        // extensions
        extensionRecyclerView = (RecyclerView) view.findViewById(R.id.shop_extensions_recyclerView);
        ShopItemListAdapter adapter2 = new ShopItemListAdapter(getContext(), extensionList);
        extensionRecyclerView.setAdapter(adapter2);
        extensionRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));

        // jars
        jarRecyclerView = (RecyclerView) view.findViewById(R.id.shop_jars_recyclerView);
        ShopItemListAdapter adapter3 = new ShopItemListAdapter(getContext(), jarList);
        jarRecyclerView.setAdapter(adapter3);
        jarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));

        // candies
        candyRecyclerView = (RecyclerView) view.findViewById(R.id.shop_candy_recyclerView);
        ShopItemListAdapter adapter4 = new ShopItemListAdapter(getContext(), candyList);
        candyRecyclerView.setAdapter(adapter4);
        candyRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));

        // candy expressions
        candyExpressionRecyclerView = (RecyclerView) view.findViewById(R.id.shop_candy_expressions_recyclerView);
        ShopItemListAdapter adapter5 = new ShopItemListAdapter(getContext(), candyExpressionList);
        candyExpressionRecyclerView.setAdapter(adapter5);
        candyExpressionRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));


        return view;
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

    // listener interface to view current items
    public interface ViewCurrentItemsListener {
        public void viewCurrentItems();
    }
}


// for TESTING
//        superpowerList = new ArrayList<>();
//        superpowerList.add(new ShopItem(ShopItem.SUPERPOWER, "Life"));
//        superpowerList.add(new ShopItem(ShopItem.SUPERPOWER, "Happiness"));
//        superpowerList.add(new ShopItem(ShopItem.SUPERPOWER, "Progress"));
//        superpowerList.add(new ShopItem(ShopItem.SUPERPOWER, "Democracy"));
//        superpowerList.add(new ShopItem(ShopItem.SUPERPOWER, "Longevity"));
//        superpowerList.add(new ShopItem(ShopItem.SUPERPOWER, "Invisibility"));
//
//        extensionList = new ArrayList<>();
//        extensionList.add(new ShopItem(ShopItem.EXTENSION, "extension"));
//        extensionList.add(new ShopItem(ShopItem.EXTENSION, "extension"));
//        extensionList.add(new ShopItem(ShopItem.EXTENSION, "extension"));
//        extensionList.add(new ShopItem(ShopItem.EXTENSION, "extension"));
//        extensionList.add(new ShopItem(ShopItem.EXTENSION, "extension"));
//        extensionList.add(new ShopItem(ShopItem.EXTENSION, "extension"));
//        extensionList.add(new ShopItem(ShopItem.EXTENSION, "extension"));
//
//        jarList = new ArrayList<>();
//        jarList.add(new ShopItem(ShopItem.JAR, "plastic jar"));
//        jarList.add(new ShopItem(ShopItem.JAR, "porcelain jar"));
//        jarList.add(new ShopItem(ShopItem.JAR, "bronze jar"));
//        jarList.add(new ShopItem(ShopItem.JAR, "jelly jar"));
//        jarList.add(new ShopItem(ShopItem.JAR, "glass jar"));
//        jarList.add(new ShopItem(ShopItem.JAR, "broken jar"));
//
//        candyList = new ArrayList<>();
//        candyList.add(new ShopItem(ShopItem.CANDY, "sweet candy"));
//        candyList.add(new ShopItem(ShopItem.CANDY, "sour candy"));
//        candyList.add(new ShopItem(ShopItem.CANDY, "mala candy"));
//        candyList.add(new ShopItem(ShopItem.CANDY, "chocolate candy"));
//        candyList.add(new ShopItem(ShopItem.CANDY, "lollipop"));
//        candyList.add(new ShopItem(ShopItem.CANDY, "chewing gum"));
//        candyList.add(new ShopItem(ShopItem.CANDY, "jelly"));
//
//        candyExpressionList = new ArrayList<>();
//        candyExpressionList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "happy"));
//        candyExpressionList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "sad"));
//        candyExpressionList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "hesitant"));
//        candyExpressionList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "angry"));
//        candyExpressionList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "crying"));
//        candyExpressionList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "smiley"));
//        candyExpressionList.add(new ShopItem(ShopItem.CANDY_EXPRESSION, "sarcastic"));
//
//        ArrayList<ArrayList<ShopItem>> allShopItems = new ArrayList<>();
//        allShopItems.add(superpowerList);
//        allShopItems.add(extensionList);
//        allShopItems.add(jarList);
//        allShopItems.add(candyList);
//        allShopItems.add(candyExpressionList);
//
//        Gson gson = new Gson();
//        String stringConverted = gson.toJson(allShopItems);