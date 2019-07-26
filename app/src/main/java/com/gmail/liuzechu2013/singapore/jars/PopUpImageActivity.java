package com.gmail.liuzechu2013.singapore.jars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

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

        // getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));
        getWindow().setLayout(width, height);

        // get image URI from intent
        Intent intent = getIntent();
        Uri imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI));

        // set image view
        ImageView imageView = findViewById(R.id.pop_up_image_view);
        // imageView.setImageURI(imageUri);

        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(imageUri);
            // get a bitmap from the stream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            // String path = saveImage(bitmap);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unable to load the image", Toast.LENGTH_SHORT);
        }

        Log.d("test", imageUri.toString());
    }
}
