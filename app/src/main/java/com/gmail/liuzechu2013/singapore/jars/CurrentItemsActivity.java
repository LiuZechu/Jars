package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CurrentItemsActivity extends AppCompatActivity {
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_items);

        backButton = (Button) findViewById(R.id.current_items_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoShop();
            }
        });
    }

    // need to change to go back to shop instead of just main activity
    private void gotoShop() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
