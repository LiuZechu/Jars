package com.xyz.orbital.singapore.jars;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

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

    // for generating new items
    public static final int ORDINARY = 0;
    public static final int GRAND = 1;
    public static final int DELUXE = 2;


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

        // to get references to buttons
        View viewOrdinary = inflater.inflate(R.layout.fragment_maker_ordinary, null);
        View viewGrand = inflater.inflate(R.layout.fragment_maker_grand, null);
        View viewDeluxe = inflater.inflate(R.layout.fragment_maker_deluxe, null);

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

        // maker buttons
//        Button ordinaryButton = viewOrdinary.findViewById(R.id.maker_page_ordinary_button);
//        ordinaryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onGeneratingNewItem(ORDINARY);
//            }
//        });
//
//        Button grandButton = viewGrand.findViewById(R.id.maker_page_grand_button);
//        grandButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onGeneratingNewItem(GRAND);
//            }
//        });
//
//        Button deluxeButton = viewDeluxe.findViewById(R.id.maker_page_deluxe_button);
//        deluxeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onGeneratingNewItem(DELUXE);
//            }
//        });


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


    // called when a maker button is clicked
    private void onGeneratingNewItem(int makerType) {
        int[] result = generateNewItem(makerType);
        Log.d("test", result[0] + " AND " + result[1]);
        Toast.makeText(getContext(), result[0] + " AND " + result[1], Toast.LENGTH_LONG).show();
        String fromFile = loadFromLocalFile(CurrentItemsActivity.USER_INVENTORY_FILE_NAME);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ArrayList<Integer>>>(){}.getType();
        ArrayList<ArrayList<Integer>> inventory = gson.fromJson(fromFile, type);
        if (inventory == null) {
            inventory = new ArrayList<>();
            inventory.add(new ArrayList<Integer>());
            inventory.add(new ArrayList<Integer>());
            inventory.add(new ArrayList<Integer>());
            inventory.add(new ArrayList<Integer>());
        }
        int category = result[0];
        int item = result[1];
        ArrayList<Integer> items = inventory.get(category);
        items.add(item);

        // save changes
        String toSave = gson.toJson(inventory);
        saveToLocalFile(CurrentItemsActivity.USER_INVENTORY_FILE_NAME, toSave);
    }

    // returns an array of 2 integers(int). 1st int represents category, 2nd represents item
    private int[] generateNewItem(int makerType) {
        // Random rnd = new Random();
        // int category = rnd.nextInt(4);
        int category = ThreadLocalRandom.current().nextInt(1, 4);
        int item = -1;

        switch (makerType) {
            case ORDINARY:
                switch (category) {
//                    case 0: // colors
//                        item = ThreadLocalRandom.current().nextInt(min, max + 1);
//                        break;
                    case 1: // expressions
                        item = ThreadLocalRandom.current().nextInt(0, 22 + 1);
                        break;
                    case 2: // jars
                        item = ThreadLocalRandom.current().nextInt(0, 2 + 1);
                        break;
                    case 3: // power-ups
                        item = ThreadLocalRandom.current().nextInt(0, 1 + 1) * 3 + 0;
                        break;
                    default:
                        break;
                }
                break;

            case GRAND:
                switch (category) {
//                    case 0: // colors
//                        item = ThreadLocalRandom.current().nextInt(min, max + 1);
//                        break;
                    case 1: // expressions
                        item = ThreadLocalRandom.current().nextInt(23, 44 + 1);
                        break;
                    case 2: // jars
                        item = ThreadLocalRandom.current().nextInt(3, 5 + 1);
                        break;
                    case 3: // power-ups
                        item = ThreadLocalRandom.current().nextInt(0, 1 + 1) * 3 + 1;
                        break;
                    default:
                        break;
                }
                break;

            case DELUXE:
                switch (category) {
//                    case 0: // colors
//                        item = ThreadLocalRandom.current().nextInt(min, max + 1);
//                        break;
                    case 1: // expressions
                        item = ThreadLocalRandom.current().nextInt(45, 61 + 1);
                        break;
                    case 2: // jars
                        item = ThreadLocalRandom.current().nextInt(6, 8 + 1);
                        break;
                    case 3: // power-ups
                        item = ThreadLocalRandom.current().nextInt(0, 1 + 1) * 3 + 2;
                        break;
                    default:
                        break;
                }
                break;

             default:
                 break;
        }

        int[] result = {category, item};

        return result;
    }



    // save a String into local text file on phone
    public void saveToLocalFile(String fileName, String stringToSave) {
        FileOutputStream fos = null;
        try {
            fos = getActivity(getContext()).openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(stringToSave.getBytes());

            Toast.makeText(getContext(), "changes saved successfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // read a string out from local text file
    public String loadFromLocalFile(String fileName) {
        FileInputStream fis = null;
        String output = null;

        try {
            fis = getActivity(getContext()).openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            output = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output;
    }
}
