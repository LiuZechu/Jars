package com.xyz.orbital.singapore.jars;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.xyz.orbital.singapore.jars.archive_recyclerview.ArchiveListAdapter;
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
import java.util.Collection;
import java.util.HashMap;

public class ArchiveActivity extends AppCompatActivity {
    public final static String GRADUATED_CANDIES_FILE_NAME = "graduatedCandies.txt"; // this file saves a HashMap, not an ArrayList!
    private ArrayList<Jar> graduatedJarList;
    private static HashMap<String, Jar> graduatedJarHash;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        String jsonStringForGraduatedCandies = loadFromLocalFile(GRADUATED_CANDIES_FILE_NAME);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Jar>>(){}.getType();
        graduatedJarHash = gson.fromJson(jsonStringForGraduatedCandies, type);

        // to avoid null pointer exception
        if (graduatedJarHash == null) {
            graduatedJarHash = new HashMap<>();
        }

        Collection<Jar> collectionOfJars = graduatedJarHash.values();
        graduatedJarList = new ArrayList<>(collectionOfJars);

        mRecyclerView = findViewById(R.id.archive_recycler_view);
        ArchiveListAdapter adapter = new ArchiveListAdapter(this, graduatedJarList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public static HashMap<String, Jar> getGraduatedJarHash() {
        return graduatedJarHash;
    }

    // save a String into local text file on phone
    public void saveToLocalFile(String fileName, String stringToSave) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(stringToSave.getBytes());

            Toast.makeText(this, "changes saved successfully", Toast.LENGTH_SHORT).show();
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
            fis = openFileInput(fileName);
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
