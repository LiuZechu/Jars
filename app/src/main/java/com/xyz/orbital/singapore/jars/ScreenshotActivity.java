package com.xyz.orbital.singapore.jars;

import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ScreenshotActivity extends AppCompatActivity {

    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection sMediaProjection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);

        Button button = findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisible(false);
            }
        });

//        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
//        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
//        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
//
//
//        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
//
//        sMediaProjection.stop();
//
//        //Process the media capture
//        image = mImageReader.acquireLatestImage();
//        Image.Plane[] planes = image.getPlanes();
//        ByteBuffer buffer = planes[0].getBuffer();
//        int pixelStride = planes[0].getPixelStride();
//        int rowStride = planes[0].getRowStride();
//        int rowPadding = rowStride - pixelStride * mWidth;
//        //Create bitmap
//        bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
//        bitmap.copyPixelsFromBuffer(buffer);
//        //Write Bitmap to file in some path on the phone
//        fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
//        bitmap.compress(CompressFormat.PNG, 100, fos);
//        fos.close();
    }
}
