package com.gmail.liuzechu2013.singapore.jars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.PriorityQueue;

public class MakeNewCandyFromFloatingActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    private Spinner chooseJarSpinner;
    private Button doneButton;
    private String jarTitleSelected;
    private int jarIndex;
    private EditText promptEditText;
    private EditText answerEditText;
    private Button makeNewJarButton;
    private EditText makeNewJarEditText;
    private Button makeNewJarSaveButton;
    private Button makeNewJarCancelButton;
    private LinearLayout oldJarBar;
    private LinearLayout makeNewJarBar;
    private String[] jarNameArray;
    private ArrayList<Jar> jarList;
    private Uri imageUri;
    private boolean newJarMade = false; // to check whether a new jar is created in this session

    public static final String JAR_TITLE = "jarTitle";
    public static final String PROMPT = "prompt";
    public static final String ANSWER = "answer";
    public static final String JAR_INDEX = "jarIndex";
    public static final String FLAG_FOR_FLOATING_WINDOW = "flagForFloatingWindow";
    public static final int PICK_IMAGE = 100;
    public static final String LAST_ACCESS_JAR_INDEX = "lastAccessJarIndex"; // so that the last access one will stay on top


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_candy);

        // TODO: APPLY THIS TO THE OTHER ACTIVITY AS WELL?
        // make screenshot button visible
        FloatingActionButton screenshotFloatingActionButton = findViewById(R.id.add_screenshot_floating_action_button);
        //screenshotFloatingActionButton.setVisibility(View.VISIBLE);
        screenshotFloatingActionButton.show();

        // load jar list from local file
        loadDataIntoJarList();

        // make a new jar by entering jar's name

        makeNewJarButton = findViewById(R.id.make_candy_new_jar_button);
        makeNewJarEditText = findViewById(R.id.make_candy_new_jar_name_edit_text);
        makeNewJarSaveButton = findViewById(R.id.make_candy_new_jar_save_button);
        makeNewJarCancelButton = findViewById(R.id.make_candy_new_jar_cancel_button);
        oldJarBar = findViewById(R.id.make_candy_old_jar_bar);
        makeNewJarBar = findViewById(R.id.make_candy_new_jar_bar);
        makeNewJarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldJarBar.setVisibility(View.GONE);
                makeNewJarBar.setVisibility(View.VISIBLE);
            }
        });

        makeNewJarSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newJarName = makeNewJarEditText.getText().toString();

                if (newJarName != null && newJarName.length() != 0) {

                    makeNewJarEditText.getText().clear();
                    oldJarBar.setVisibility(View.VISIBLE);
                    makeNewJarBar.setVisibility(View.GONE);
                }

                createJar(newJarName);
            }
        });

        makeNewJarCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewJarEditText.getText().clear();
                oldJarBar.setVisibility(View.VISIBLE);
                makeNewJarBar.setVisibility(View.GONE);
            }
        });

        // get jarNameArray from intent
        String jsonString = getIntent().getStringExtra(JarsFragment.JAR_NAME_ARRAY);
        Gson gson = new Gson();
        jarNameArray = gson.fromJson(jsonString, String[].class);

        // prevent null pointer exception
        if (jarNameArray == null) {
            jarNameArray = new String[0];
        }

        // spinner
        chooseJarSpinner = findViewById(R.id.make_candy_choose_jar_spinner);
        // Create an ArrayAdapter using a Jar array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jarNameArray);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        chooseJarSpinner.setAdapter(adapter);
        chooseJarSpinner.setOnItemSelectedListener(this);
        // LAST ACCESSED JAR IS SELECTED
        chooseJarSpinner.setSelection(loadLastAccessJarIndex());

        // initialise edit text fields
        promptEditText = findViewById(R.id.make_candy_prompt_editText);
        answerEditText = findViewById(R.id.make_candy_answer_editText);

        // get candy answer from intent if it is launched outside the app
        String answer = getIntent().getStringExtra(FloatingWindowService.CANDY_ANSWER);
        answerEditText.setText(answer);

        doneButton = findViewById(R.id.make_candy_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneMakingCandy();
            }
        });

        // ADD A SCREENSHOT
        FloatingActionButton addScreenshotButton = findViewById(R.id.add_screenshot_floating_action_button);
        addScreenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addScreenshot();
            }
        });
    }

    // callback methods for spinner menu
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        jarTitleSelected = (String) parent.getItemAtPosition(pos);
        jarIndex = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void createJar(String name) {
        // throw a Toast if the jar name entered is empty
        if (name == null || name.length() == 0) {
            Toast.makeText(this, "Jar's name cannot be empty!", Toast.LENGTH_SHORT).show();

        } else if (checkDuplicateName(name)) {
            // check whether the same Jar name already exists
            // if there are duplicates, this block of code will run
            Toast.makeText(this,
                    "A Jar with the same name already exists! Please enter another name.",
                    Toast.LENGTH_SHORT);

        } else {
            Jar jar = new Jar(name);
            // changed here
            if (jarList != null) {
                jarList.add(jar);
            } else {
                jarList = new ArrayList<>();
                jarList.add(jar);
            }

            // update spinner
            String[] temp = jarNameArray;
            int len = jarNameArray.length;
            jarNameArray = new String[len + 1];
            for (int i = 0; i < len; i++) {
                jarNameArray[i] = temp[i];
            }
            jarNameArray[len] = name;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jarNameArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            chooseJarSpinner.setAdapter(adapter);
            chooseJarSpinner.setOnItemSelectedListener(this);
            chooseJarSpinner.setSelection(adapter.getPosition(name));

            // move AFTER creating a candy successfully
            // update Total Jars Made
//            int totalJarsMade = loadTotalJarsMade();
//            saveTotalJarsMade(totalJarsMade + 1);

            newJarMade = true;

            // move AFTER creating a candy successfully to prevent mismatch between jarList and jarNameArray
            // update USER_JAR_NAME_ARRAY in shared preferences
//            Gson gson = new Gson();
//            String jarNameArrayString = gson.toJson(jarNameArray);
//            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString(MainActivity.USER_JAR_NAME_ARRAY, jarNameArrayString);
//            editor.commit();

            Toast.makeText(this, "New Jar created successfully! Put in the first candy now!", Toast.LENGTH_SHORT).show();
        }
    }

    // checks name against jarNameArray to make sure that there won't be two Jars of the same name
    // return TRUE when there are DUPLICATES
    private boolean checkDuplicateName(String name) {
        boolean result = false;
        for (String jarName : jarNameArray) {
            if (jarName.equals(name)) {
                result = true;
            }
        }

        return result;
    }

    public void doneMakingCandy() {
        if (jarTitleSelected == null) {
            // no Jar is selected
            if (jarNameArray == null || jarNameArray.length == 0) {
                // There is no existing Jar
                Toast.makeText(this, "Please create a new Jar first.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please select a Jar.", Toast.LENGTH_SHORT).show();
            }
        } else if (promptEditText == null || promptEditText.length() == 0
                    || answerEditText == null || answerEditText.length() == 0 ) {
            Toast.makeText(this, "Prompt or Answer cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {

            // SAVE CANDY MADE HERE INSTEAD OF MAIN ACTIVITY
            saveCandy();

            // SAVE JAR MADE, IF ANY
            //if (newJarMade) {
                Gson gson = new Gson();
                String jarNameArrayString = gson.toJson(jarNameArray);
                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MainActivity.USER_JAR_NAME_ARRAY, jarNameArrayString);
                editor.commit();
            //}


            if (newJarMade) {
                int totalJarsMade = loadTotalJarsMade();
                saveTotalJarsMade(totalJarsMade + 1);
            }


            // UPDATE LAST ACCESSED JAR INDEX
            saveLastAccessJarIndex(jarIndex);

//            Intent intent = new Intent(this, MainActivity.class);

//            // put the candy created by user into intent
//            intent.putExtra(JAR_TITLE, jarTitleSelected);
//            intent.putExtra(PROMPT, promptEditText.getText().toString());
//            intent.putExtra(ANSWER, answerEditText.getText().toString());
//            intent.putExtra(JAR_INDEX, jarIndex);
//           intent.putExtra(FLAG_FOR_FLOATING_WINDOW, true);
//            startActivity(intent);

            // GO BACK TO THE PREVIOUS ACTIVITY
            launchFloatingWindow();
            moveTaskToBack(true);
            finish();
        }
    }

    private void saveTotalJarsMade(int amount) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.TOTAL_JARS_MADE, amount);
        editor.commit();
    }

    private int loadTotalJarsMade() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(ProfileFragment.TOTAL_JARS_MADE, 0);
    }

    // for accessing jar list
    public void loadDataIntoJarList() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Jar>>(){}.getType();
        String jsonStringForJarList = loadFromLocalFile(MainActivity.USER_JAR_FILE_NAME);
        //Log.d("test fromMain", jsonStringForJarList);
        jarList = gson.fromJson(jsonStringForJarList, type);
    }

    // for saving candy here instead of main activity
    public void saveCandy() {

        String jarTitle = jarTitleSelected;
        String prompt = promptEditText.getText().toString();
        String answer = answerEditText.getText().toString();

        // changed here
        if (jarList == null) {
            jarList = new ArrayList<>();
            jarList.add(new Jar(jarTitle));
        }

        Jar jar = jarList.get(jarIndex);
        Candy newCandy = new Candy(prompt, answer);
        jar.addCandy(newCandy);

        // attach screenshot URI to the candy
        if (imageUri != null) {
            newCandy.setImageUri(imageUri);
        }

        // save and load newly updated jar data
        Gson gson = new Gson();
        String jsonString = gson.toJson(jarList);
        saveToLocalFile(MainActivity.USER_JAR_FILE_NAME, jsonString);

        //increment "total candies made" count
        incrementTotalCandiesMade();

        //increase user's exp
        increaseExp(1);
    }

    // launch floating window again
    public void launchFloatingWindow() {
        // creates a floating window

        // TODO: edit hardcoding of this part
        // check permission to overlay
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        } else {

            // creates a floating window
            moveTaskToBack(true);
            startService(new Intent(MakeNewCandyFromFloatingActivity.this, FloatingWindowService.class));
        }
    }

    // ADD A SCREENSHOT
    private void addScreenshot() {
        // open phone photo gallery
        // Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        // NOTE: changed to ACTION_OPEN_DOCUMENT so that the URI obtained has permission even outside this activity
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    // SAVE A SCREENSHOT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            // imageView.setImageURI(imageUri);

            Toast.makeText(this, "Screenshot chosen successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementTotalCandiesMade() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        int totalCandies = sharedPreferences.getInt(ProfileFragment.TOTAL_CANDIES_MADE, 0);
        totalCandies++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ProfileFragment.TOTAL_CANDIES_MADE, totalCandies);
        editor.commit();
    }

    private void increaseExp(int amount) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int exp = sharedPreferences.getInt(ProfileFragment.EXP, 0);
        exp = exp + amount;

        int level = sharedPreferences.getInt(ProfileFragment.LEVEL, 1);

        // check whether next level is reached; update if necessary
        // Exp needed to reach next level = 3 * (next level)^2, if next level <= 100
        // Exp needed to reach next level = 30 000, if next level > 100
        int expNeededToLevelUp = MainActivity.getExpToLevelUp(level);

        if (exp >= expNeededToLevelUp) {
            // level up!
            level++;
            exp -= expNeededToLevelUp;

            // save level
            editor.putInt(ProfileFragment.LEVEL, level);

            // award sugar
            // Leveling up: sugar += floor[level^1.5] * 100
            int sugarToAward = (int) Math.floor(Math.pow(level, 1.5)) * 100;
            int totalSugar = sharedPreferences.getInt(ProfileFragment.SUGAR, 0);
            totalSugar += sugarToAward;
            editor.putInt(ProfileFragment.SUGAR, totalSugar);
        }

        editor.putInt(ProfileFragment.EXP, exp);
        editor.commit();
    }

    // the following two methods are for the spinner, so that the last accessed jar will be shown first
    private int loadLastAccessJarIndex() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        int index = sharedPreferences.getInt(LAST_ACCESS_JAR_INDEX, 0);
        return index;
    }

    private void saveLastAccessJarIndex(int index) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LAST_ACCESS_JAR_INDEX, index);
        editor.commit();
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
