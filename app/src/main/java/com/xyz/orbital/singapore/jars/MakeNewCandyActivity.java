package com.xyz.orbital.singapore.jars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MakeNewCandyActivity extends AppCompatActivity
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
    private boolean newJarMade = false; // to check whether a new jar is created in this session
    private Uri imageUri;

    public static final String JAR_TITLE = "jarTitle";
    public static final String PROMPT = "prompt";
    public static final String ANSWER = "answer";
    public static final String JAR_INDEX = "jarIndex";
    public static final String LAST_ACCESS_JAR_INDEX = "lastAccessJarIndex"; // so that the last access one will stay on top
    public static final String SCREENSHOT_IMAGE_URI = "screenshotImageUri"; // for screenshot


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_candy);

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
            ArrayList<Jar> jarList = MainActivity.getJarList();
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
                || answerEditText == null || answerEditText.length() == 0) {
            Toast.makeText(this, "Prompt or Answer cannot be empty!", Toast.LENGTH_SHORT).show();
        } else if (makeNewJarBar.getVisibility() == View.VISIBLE) {
            // makeNewJarBar.setBackgroundColor(Color.RED);
            // makeNewJarBar.getBackground().setColorFilter(Color.parseColor("#ef6256"), PorterDuff.Mode.SRC_OVER);
            // makeNewJarEditText.setTextColor(Color.parseColor("#940000"));
            makeNewJarEditText.setBackgroundColor(Color.parseColor("#ff9c9c"));
            Toast.makeText(this, "Remember to save your new Jar!", Toast.LENGTH_SHORT).show();
        } else {

            // update jar name array IF a jar is created
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


            Intent intent = new Intent(this, MainActivity.class);


            // UPDATE LAST ACCESSED JAR INDEX
            saveLastAccessJarIndex(jarIndex);


            // put the candy created by user into intent
            intent.putExtra(JAR_TITLE, jarTitleSelected);
            intent.putExtra(PROMPT, promptEditText.getText().toString());
            intent.putExtra(ANSWER, answerEditText.getText().toString());
            if (imageUri != null) {
                intent.putExtra(SCREENSHOT_IMAGE_URI, imageUri.toString());
            }
            intent.putExtra(JAR_INDEX, jarIndex);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    // ADD A SCREENSHOT
    private void addScreenshot() {
        // open phone photo gallery
        // Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        // NOTE: changed to ACTION_OPEN_DOCUMENT so that the URI obtained has permission even outside this activity
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, MakeNewCandyFromFloatingActivity.PICK_IMAGE);
    }

    // SAVE A SCREENSHOT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == MakeNewCandyFromFloatingActivity.PICK_IMAGE) {
            imageUri = data.getData();
            // imageView.setImageURI(imageUri);

            Toast.makeText(this, "Screenshot chosen successfully", Toast.LENGTH_SHORT).show();
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
}
