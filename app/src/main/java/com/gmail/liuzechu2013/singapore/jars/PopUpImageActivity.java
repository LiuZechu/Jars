package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class PopUpImageActivity extends AppCompatActivity {

    public static final String IMAGE_URI = "imageURI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_image);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));


        // get image URI from intent
        Intent intent = getIntent();
        Uri imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI));

        // set image view
        ImageView imageView = findViewById(R.id.pop_up_image_view);
        imageView.setImageURI(imageUri);

        Log.d("test", imageUri.toString());
    }
}
