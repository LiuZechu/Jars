package com.gmail.liuzechu2013.singapore.jars;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class FloatingWindowService extends Service {
    private WindowManager windowManager;
    private LinearLayout linearLayout;
    // TEST
    private TextView textView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialise floating window and its layout
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        linearLayout = (LinearLayout) inflater.inflate(R.layout.floating_window_layout, null);

        // stop button to remove floating window from screen
        Button stopButton = linearLayout.findViewById(R.id.stop_floating_window_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(linearLayout);
                stopSelf();
            }
        });

        // button to make a candy from clipboard
        Button makeCandyButton = linearLayout.findViewById(R.id.floating_window_make_candy_button);
        makeCandyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCandyFromClipBoard();
            }
        });

        // TEST
        textView = linearLayout.findViewById(R.id.test);

        // set parameters of linear layout inside the floating window
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setBackgroundColor(Color.argb(66, 255, 0, 0));
        linearLayout.setLayoutParams(linearLayoutParams);

        // for compatibility with different API levels
        int layoutParams;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams = WindowManager.LayoutParams.TYPE_PHONE;
        }

        // TODO: figure what these do
        final WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams(600, 300,
                layoutParams, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        windowManagerParams.x = 0;
        windowManagerParams.y = 0;
        windowManagerParams.gravity = Gravity.CENTER | Gravity.CENTER;

        windowManager.addView(linearLayout, windowManagerParams);

        // make the window movable on screen
        linearLayout.setOnTouchListener(new View.OnTouchListener() {

            private WindowManager.LayoutParams updatedParams = windowManagerParams;
            int x, y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParams.x;
                        y = updatedParams.y;

                        touchedX = event.getRawX();
                        touchedY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        updatedParams.x = (int) (x + (event.getRawX() - touchedX));
                        updatedParams.y = (int) (y + (event.getRawY() - touchedY));

                        windowManager.updateViewLayout(linearLayout, updatedParams);
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }

    private void makeCandyFromClipBoard() {

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        String text = (String) getText(clipboardManager);

        // TEST
        textView.setText(text);

        // start make candy activity
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        String jarNameArrayString = sharedPreferences.getString(MainActivity.USER_JAR_NAME_ARRAY, "");

        Intent intent = new Intent(getApplicationContext(), MakeNewCandyActivity.class);
        intent.putExtra(JarsFragment.JAR_NAME_ARRAY, jarNameArrayString);
        // startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_NEW_CANDY);
        startActivity(intent);
    }

    // get data from clip board
    public CharSequence getText(ClipboardManager clipboardManager) {
        ClipData clip = clipboardManager.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(getApplicationContext());
        }
        return null;
    }
}
