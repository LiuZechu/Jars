package com.xyz.orbital.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class MakerPageFragment extends Fragment {

    int mNum;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static MakerPageFragment newInstance(int num) {
        MakerPageFragment f = new MakerPageFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 0;
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        if (mNum == 0) {
            v = inflater.inflate(R.layout.fragment_maker_ordinary, container, false);
            Button ordinaryButton = v.findViewById(R.id.maker_page_ordinary_button);
            ordinaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGeneratingNewItem(MakerFragment.ORDINARY);
                }
            });
        } else if (mNum == 1) {
            v = inflater.inflate(R.layout.fragment_maker_grand, container, false);
            Button grandButton = v.findViewById(R.id.maker_page_grand_button);
            grandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGeneratingNewItem(MakerFragment.GRAND);
                }
            });
        } else {
            v = inflater.inflate(R.layout.fragment_maker_deluxe, container, false);
            Button deluxeButton = v.findViewById(R.id.maker_page_deluxe_button);
            deluxeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGeneratingNewItem(MakerFragment.DELUXE);
                }
            });
        }
        return v;
    }

    // called when a maker button is clicked
    private void onGeneratingNewItem(int makerType) {
        int[] result = generateNewItem(makerType);
        Log.d("test", result[0] + " AND " + result[1]);

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

        // update sugar
        int cost = 0;
        switch (makerType) {
            case MakerFragment.ORDINARY:
                cost = 200;
                break;
            case MakerFragment.GRAND:
                cost = 2500;
                break;
            case MakerFragment.DELUXE:
                cost = 12500;
                break;
            default:
                break;
        }

        int newSugarAmount = loadSugar() - cost;
        saveSugar(newSugarAmount);

        // update top bar sugar amount
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).loadDataIntoTopBar();
        }

    }


    // returns an array of 2 integers(int). 1st int represents category, 2nd represents item
    private int[] generateNewItem(int makerType) {
        // Random rnd = new Random();
        // int category = rnd.nextInt(4);
        int category = ThreadLocalRandom.current().nextInt(1, 4);
        int item = -1;

        switch (makerType) {
            case MakerFragment.ORDINARY:
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

            case MakerFragment.GRAND:
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

            case MakerFragment.DELUXE:
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
            fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(stringToSave.getBytes());

            // Toast.makeText(getContext(), "changes saved successfully", Toast.LENGTH_SHORT).show();
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
            fis = getActivity().openFileInput(fileName);
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

    public void saveSugar(int amount) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.SUGAR, amount);
        editor.commit();
    }

    public int loadSugar() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        int sugar = sharedPreferences.getInt(ProfileFragment.SUGAR, 0);
        return sugar;
    }
}
