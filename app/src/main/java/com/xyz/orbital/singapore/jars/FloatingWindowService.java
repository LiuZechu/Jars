package com.xyz.orbital.singapore.jars;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;

public class FloatingWindowService extends Service {
//    private WindowManager windowManager;
//    private LinearLayout linearLayout;
    private WindowManager windowManager;
    private LinearLayout floatingView;

    public static final String CANDY_ANSWER = "candyAnswer";
    public static final String FLOATING_WINDOW_FLAG = "floatingWindowFlag";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

//        // initialise floating window and its layout
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        linearLayout = (LinearLayout) inflater.inflate(R.layout.floating_window_layout, null);

        //Inflate the floating view layout we created
//        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window_layout, null);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        floatingView = (LinearLayout) inflater.inflate(R.layout.floating_window_layout, null);
// for compatibility with different API levels
        int layoutParams;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams = WindowManager.LayoutParams.TYPE_PHONE;
        }

//        // TODO: figure what these do
//        final WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams(1000, 200,
//                layoutParams, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//        windowManagerParams.x = 0;
//        windowManagerParams.y = 0;
//        windowManagerParams.gravity = Gravity.CENTER | Gravity.CENTER;
        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParams,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        //The root element of the collapsed view layout
        final View collapsedView = floatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = floatingView.findViewById(R.id.expanded_container);

    // stop button to remove floating window from screen
        Button stopButton = floatingView.findViewById(R.id.stop_floating_window_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(floatingView);
                stopSelf();
            }
        });
        Button closeButton = floatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });
        // button to make a candy from clipboard
        Button makeCandyButton = floatingView.findViewById(R.id.floating_window_make_candy_button);
        makeCandyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCandyFromClipBoard();
            }
        });

        // button for user to go back to app (view candies tab)
        Button viewCandiesButton = floatingView.findViewById(R.id.floating_window_view_candies_button);
        viewCandiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCandies();
            }
        });

        floatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
//        // stop button to remove floating window from screen
//        Button stopButton = linearLayout.findViewById(R.id.stop_floating_window_button);
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                windowManager.removeView(linearLayout);
//                stopSelf();
//            }
//        });
//
//        // button to make a candy from clipboard
//        Button makeCandyButton = linearLayout.findViewById(R.id.floating_window_make_candy_button);
//        makeCandyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                makeCandyFromClipBoard();
//            }
//        });
//
//        // button for user to go back to app (view candies tab)
//        Button viewCandiesButton = linearLayout.findViewById(R.id.floating_window_view_candies_button);
//        viewCandiesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewCandies();
//            }
//        });

//        // button to take a screenshot and make a candy at the same time
//        Button screenshotButton = linearLayout.findViewById(R.id.floating_window_screenshot_button);
//        screenshotButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takeScreenshot();
//            }
//        });


//        // set parameters of linear layout inside the floating window
//        LinearLayout.LayoutParams linearLayoutParams =
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        linearLayout.setBackgroundColor(Color.argb(66, 255, 0, 0));
//        linearLayout.setLayoutParams(linearLayoutParams);
//
//        // for compatibility with different API levels
//        int layoutParams;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            layoutParams = WindowManager.LayoutParams.TYPE_PHONE;
//        }

//        // TODO: figure what these do
//        final WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams(1000, 200,
//                layoutParams, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//        windowManagerParams.x = 0;
//        windowManagerParams.y = 0;
//        windowManagerParams.gravity = Gravity.CENTER | Gravity.CENTER;
//
//        windowManager.addView(linearLayout, windowManagerParams);
//
//        // OnTouchListener that tracks finger movement
//        View.OnTouchListener listener = new View.OnTouchListener() {
//
//            private WindowManager.LayoutParams updatedParams = windowManagerParams;
//            int x, y;
//            float touchedX, touchedY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        x = updatedParams.x;
//                        y = updatedParams.y;
//
//                        touchedX = event.getRawX();
//                        touchedY = event.getRawY();
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//                        updatedParams.x = (int) (x + (event.getRawX() - touchedX));
//                        updatedParams.y = (int) (y + (event.getRawY() - touchedY));
//
//                        windowManager.updateViewLayout(linearLayout, updatedParams);
//
//                        break;
//                    default:
//                        break;
//                }
//
//                return false;
//
//            }
//        };
//
//        // make the window movable on screen
//        linearLayout.setOnTouchListener(listener);
//        // TODO: resolve the problem of touch = click for the following buttons
////        stopButton.setOnTouchListener(listener);
////        makeCandyButton.setOnTouchListener(listener);
////        viewCandiesButton.setOnTouchListener(listener);
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return floatingView == null || floatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (floatingView != null) windowManager.removeView(floatingView);
//    }


    private void makeCandyFromClipBoard() {

        ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(CLIPBOARD_SERVICE);

        ClipData clipData = clipboardManager.getPrimaryClip();


        String text = (String) getText(clipboardManager);

        // start make candy activity
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        String jarNameArrayString = sharedPreferences.getString(MainActivity.USER_JAR_NAME_ARRAY, "");

        // Intent intent = new Intent(getApplicationContext(), MakeNewCandyActivity.class);
        Intent intent = new Intent(getApplicationContext(), MakeNewCandyFromFloatingActivity.class);
        intent.putExtra(JarsFragment.JAR_NAME_ARRAY, jarNameArrayString);
        // startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_NEW_CANDY);
        intent.putExtra(CANDY_ANSWER, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


        // stop this floating window
        windowManager.removeView(floatingView);
        stopSelf();
    }

    // get data from clip board
    public CharSequence getText(ClipboardManager clipboardManager) {
        ClipData clip = clipboardManager.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            Log.d("test", "message is: " + clip.getItemAt(0).coerceToText(getApplicationContext()));
            return clip.getItemAt(0).coerceToText(getApplicationContext());
//            String result = clip.getItemAt(0).getText().toString();
//            return result;
        }

        return null;
    }

    // go back to app from floating window
    private void viewCandies() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(FLOATING_WINDOW_FLAG, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

}
