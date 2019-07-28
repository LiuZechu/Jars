package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class CurrentItemsActivity extends AppCompatActivity {

    ImageButton expressionButton;

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
    }

    private void openExpression() {
        Intent intent = new Intent(this, ExpressionActivity.class);
        startActivity(intent);
    }
}
