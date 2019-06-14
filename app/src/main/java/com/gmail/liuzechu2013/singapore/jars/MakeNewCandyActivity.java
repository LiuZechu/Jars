package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MakeNewCandyActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    private Spinner chooseJarSpinner;
    private Button doneButton;
    private String jarTitleSelected;
    private EditText promptEditText;
    private EditText answerEditText;

    public static final String JAR_TITLE = "jarTitle";
    public static final String PROMPT = "prompt";
    public static final String ANSWER = "answer";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_candy);

        // get jarNameArray from intent
        String jsonString = getIntent().getStringExtra(CandyFragment.JAR_NAME_ARRAY);
        Gson gson = new Gson();
        String[] jarNameArray = gson.fromJson(jsonString, String[].class);

        chooseJarSpinner = findViewById(R.id.make_candy_choose_jar_spinner);
        // Create an ArrayAdapter using a Jar array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jarNameArray);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        chooseJarSpinner.setAdapter(adapter);

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
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void doneMakingCandy() {
        Intent intent = new Intent(this, MainActivity.class);
        // put the candy created by user into intent
        intent.putExtra(JAR_TITLE, jarTitleSelected);
        intent.putExtra(PROMPT, promptEditText.getText().toString());
        intent.putExtra(ANSWER, answerEditText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
