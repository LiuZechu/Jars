package com.xyz.orbital.singapore.jars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CurrentItemsActivity extends AppCompatActivity {

    private ArrayList<ArrayList<Integer>> userInventory;
    ImageButton expressionButton;
    ImageButton powerupButton;

    // internal file to store current inventory
    public static final String USER_INVENTORY_FILE_NAME = "userInventory.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_items);
        expressionButton = findViewById(R.id.user_item_expression);
        expressionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openExpression();
            }
        });
        powerupButton = findViewById(R.id.user_item_powerup);
        powerupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPowerup();
            }
        });

        // initialise user inventory
        Gson gson = new Gson();
        String jsonStringFromFile = loadFromLocalFile(USER_INVENTORY_FILE_NAME);
        if (jsonStringFromFile == null || jsonStringFromFile.equals("")) {
            // save default empty inventory into the file
            userInventory = new ArrayList<>();
            userInventory.add(new ArrayList<Integer>()); // colors
            userInventory.add(new ArrayList<Integer>()); // expressions
            userInventory.add(new ArrayList<Integer>()); // jars
            userInventory.add(new ArrayList<Integer>()); // power-ups

            String toSave = gson.toJson(userInventory);
            saveToLocalFile(toSave, USER_INVENTORY_FILE_NAME);
        }

    }

    private void openExpression() {
        Intent intent = new Intent(this, ExpressionActivity.class);
        startActivity(intent);
    }

    private void openPowerup() {
        Intent intent = new Intent(this, PowerupActivity.class);
        startActivity(intent);
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
