package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private String[] jarNameArray;

    public static final String JAR_TITLE = "jarTitle";
    public static final String PROMPT = "prompt";
    public static final String ANSWER = "answer";
    public static final String JAR_INDEX = "jarIndex";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_candy);

        makeNewJarButton = findViewById(R.id.make_candy_new_jar_button);
        makeNewJarEditText = findViewById(R.id.make_candy_new_jar_name_edit_text);
        makeNewJarSaveButton = findViewById(R.id.make_candy_new_jar_save_button);
        makeNewJarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewJarEditText.setVisibility(View.VISIBLE);
                makeNewJarSaveButton.setVisibility(View.VISIBLE);
                makeNewJarButton.setVisibility(View.GONE);
            }
        });

        makeNewJarSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewJarSaveButton.setVisibility(View.GONE);
                makeNewJarButton.setVisibility(View.VISIBLE);
                String newJarName = makeNewJarEditText.getText().toString();
                makeNewJarEditText.getText().clear();
                makeNewJarEditText.setVisibility(View.GONE);
                createJar(newJarName);
            }
        });

        // get jarNameArray from intent
        String jsonString = getIntent().getStringExtra(CandyFragment.JAR_NAME_ARRAY);
        Gson gson = new Gson();
        jarNameArray = gson.fromJson(jsonString, String[].class);

        chooseJarSpinner = findViewById(R.id.make_candy_choose_jar_spinner);
        // Create an ArrayAdapter using a Jar array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jarNameArray);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        chooseJarSpinner.setAdapter(adapter);
        chooseJarSpinner.setOnItemSelectedListener(this);

        promptEditText = findViewById(R.id.make_candy_prompt_editText);
        answerEditText = findViewById(R.id.make_candy_answer_editText);

        doneButton = findViewById(R.id.make_candy_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneMakingCandy();
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
        Jar jar = new Jar(name);
        MainActivity.getJarList().add(jar);
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

        Toast.makeText(this, "New Jar created successfully! Put in the first candy now!", Toast.LENGTH_SHORT).show();
    }

    public void doneMakingCandy() {
        Intent intent = new Intent(this, MainActivity.class);

        // put the candy created by user into intent
        intent.putExtra(JAR_TITLE, jarTitleSelected);
        intent.putExtra(PROMPT, promptEditText.getText().toString());
        intent.putExtra(ANSWER, answerEditText.getText().toString());
        intent.putExtra(JAR_INDEX, jarIndex);
        setResult(RESULT_OK, intent);
        finish();
    }
}
