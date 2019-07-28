package com.gmail.liuzechu2013.singapore.jars;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import static com.gmail.liuzechu2013.singapore.jars.MainActivity.getResourceID;

public class ExpressionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expression);

        Gson gson = new Gson();
        String fromFile = loadFromLocalFile(CurrentItemsActivity.USER_INVENTORY_FILE_NAME);
        Type type = new TypeToken<ArrayList<ArrayList<Integer>>>(){}.getType();
        ArrayList<ArrayList<Integer>> inventory = gson.fromJson(fromFile, type);

        // for null pointer
        if (inventory == null) {
            // save default empty inventory into the file
            inventory = new ArrayList<>();
            inventory.add(new ArrayList<Integer>()); // colors
            inventory.add(new ArrayList<Integer>()); // expressions
            inventory.add(new ArrayList<Integer>()); // jars
            inventory.add(new ArrayList<Integer>()); // power-ups

            String toSave = gson.toJson(inventory);
            saveToLocalFile(toSave, CurrentItemsActivity.USER_INVENTORY_FILE_NAME);
        }

        // get the list for expressions
        ArrayList<Integer> expressions = inventory.get(1);
        for (int i : expressions) {
            String str = "face_" + i;
            int resID = getResources().getIdentifier(str, "id", getPackageName());
            findViewById(resID).setVisibility(View.VISIBLE);
            str = "exp" + i;
            resID = getResources().getIdentifier(str, "id", getPackageName());
            findViewById(resID).setAlpha(1);
        }
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
