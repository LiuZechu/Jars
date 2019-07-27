package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MakerFragment extends Fragment {
    private ViewCurrentItemsListener activityCommander;

    /*
    private RecyclerView superpowerRecyclerView;
    private RecyclerView extensionRecyclerView;
    private RecyclerView jarRecyclerView;
    private RecyclerView candyRecyclerView;
    private RecyclerView candyExpressionRecyclerView;

    private ArrayList<Item> superpowerList;
    private ArrayList<Item> extensionList;
    private ArrayList<Item> jarList;
    private ArrayList<Item> candyList;
    private ArrayList<Item> candyExpressionList;
    */

    private static final int NUM_MAKER = 3;
    private ViewPager makerPager;
    private PagerAdapter makerPagerAdapter;

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
        View view = inflater.inflate(R.layout.fragment_maker, null);

        FloatingActionButton button = view.findViewById(R.id.view_current_items_button);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCommander.viewCurrentItems();
            }
        };
        button.setOnClickListener(listener);

        makerPager = (ViewPager) view.findViewById(R.id.maker_pager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.maker_page_indicator);
        tabLayout.setupWithViewPager(makerPager, true);
        makerPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        makerPager.setAdapter(makerPagerAdapter);

        /*
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
        Type type = new TypeToken<ArrayList<ArrayList<Item>>>(){}.getType();
        ArrayList<ArrayList<Item>> allShopItems = gson.fromJson(jsonString, type);
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
        */

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

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MakerPageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_MAKER;
        }
    }
}


// for TESTING
//        superpowerList = new ArrayList<>();
//        superpowerList.add(new Item(Item.SUPERPOWER, "Life"));
//        superpowerList.add(new Item(Item.SUPERPOWER, "Happiness"));
//        superpowerList.add(new Item(Item.SUPERPOWER, "Progress"));
//        superpowerList.add(new Item(Item.SUPERPOWER, "Democracy"));
//        superpowerList.add(new Item(Item.SUPERPOWER, "Longevity"));
//        superpowerList.add(new Item(Item.SUPERPOWER, "Invisibility"));
//
//        extensionList = new ArrayList<>();
//        extensionList.add(new Item(Item.EXTENSION, "extension"));
//        extensionList.add(new Item(Item.EXTENSION, "extension"));
//        extensionList.add(new Item(Item.EXTENSION, "extension"));
//        extensionList.add(new Item(Item.EXTENSION, "extension"));
//        extensionList.add(new Item(Item.EXTENSION, "extension"));
//        extensionList.add(new Item(Item.EXTENSION, "extension"));
//        extensionList.add(new Item(Item.EXTENSION, "extension"));
//
//        jarList = new ArrayList<>();
//        jarList.add(new Item(Item.JAR, "plastic jar"));
//        jarList.add(new Item(Item.JAR, "porcelain jar"));
//        jarList.add(new Item(Item.JAR, "bronze jar"));
//        jarList.add(new Item(Item.JAR, "jelly jar"));
//        jarList.add(new Item(Item.JAR, "glass jar"));
//        jarList.add(new Item(Item.JAR, "broken jar"));
//
//        candyList = new ArrayList<>();
//        candyList.add(new Item(Item.CANDY, "sweet candy"));
//        candyList.add(new Item(Item.CANDY, "sour candy"));
//        candyList.add(new Item(Item.CANDY, "mala candy"));
//        candyList.add(new Item(Item.CANDY, "chocolate candy"));
//        candyList.add(new Item(Item.CANDY, "lollipop"));
//        candyList.add(new Item(Item.CANDY, "chewing gum"));
//        candyList.add(new Item(Item.CANDY, "jelly"));
//
//        candyExpressionList = new ArrayList<>();
//        candyExpressionList.add(new Item(Item.CANDY_EXPRESSION, "happy"));
//        candyExpressionList.add(new Item(Item.CANDY_EXPRESSION, "sad"));
//        candyExpressionList.add(new Item(Item.CANDY_EXPRESSION, "hesitant"));
//        candyExpressionList.add(new Item(Item.CANDY_EXPRESSION, "angry"));
//        candyExpressionList.add(new Item(Item.CANDY_EXPRESSION, "crying"));
//        candyExpressionList.add(new Item(Item.CANDY_EXPRESSION, "smiley"));
//        candyExpressionList.add(new Item(Item.CANDY_EXPRESSION, "sarcastic"));
//
//        ArrayList<ArrayList<Item>> allShopItems = new ArrayList<>();
//        allShopItems.add(superpowerList);
//        allShopItems.add(extensionList);
//        allShopItems.add(jarList);
//        allShopItems.add(candyList);
//        allShopItems.add(candyExpressionList);
//
//        Gson gson = new Gson();
//        String stringConverted = gson.toJson(allShopItems);