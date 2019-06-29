package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private Button usernameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameEditText = findViewById(R.id.settings_username_edit_text);
        // pre-fill edit text with current username
        Intent intent = getIntent();
        usernameEditText.setText(intent.getStringExtra(ProfileFragment.USERNAME));
        usernameButton = findViewById(R.id.settings_username_button);
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUsername();
            }
        });

        // view graduated candies
        Button viewGraduatedCandiesButton = findViewById(R.id.settings_view_graduated_candies_button);
        viewGraduatedCandiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoArchives();
            }
        });
    }

    private void editUsername() {
        String username = usernameEditText.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ProfileFragment.USERNAME, username);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void gotoArchives() {
        Intent intent = new Intent(this, ArchiveActivity.class);
        startActivity(intent);
    }
}
