package com.example.android.cannongame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GamingActivity extends AppCompatActivity {

    private DrawView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.activity_gaming);
        view = new DrawView(this);
        //放在这里，当状态栏出现时，一些标志位会被清除，而没法被重新置上
//        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | view.SYSTEM_UI_FLAG_FULLSCREEN | view.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(view);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
