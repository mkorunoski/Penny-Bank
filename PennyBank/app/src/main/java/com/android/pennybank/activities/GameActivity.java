package com.android.pennybank.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pennybank.R;
import com.android.pennybank.opengl.GameRenderer;
import com.android.pennybank.util.Logger;

public class GameActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private GameRenderer gameRenderer;
    private boolean rendererSet = false;

    private RelativeLayout relativeLayout;
    private TextView score;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = 0.0f;
        float y = 0.0f;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        }

        gameRenderer.setTapedPosition(new PointF(x, y));

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        gameRenderer = new GameRenderer(this);

        setupUI();

        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();

        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new GameRenderer(this));
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

//        setContentView(glSurfaceView);
    }

    private void setupUI() {
        relativeLayout = new RelativeLayout(this);
        relativeLayout.addView(glSurfaceView);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 8, 8, 8);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP);

        score = new TextView(this);
        score.setTextSize(32);
        score.setTextColor(ContextCompat.getColor(this, R.color.blue));
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/SCOREBOARD.ttf");
        score.setTypeface(type);
        score.setLayoutParams(layoutParams);
        score.setText("Score: 0");

        relativeLayout.addView(score);

        setContentView(relativeLayout);
    }

    public void setScore(int scoredPoints) {
        score.setText("Score: " + scoredPoints);
        if (Logger.ENABLED) {
            Log.i(Logger.TAG, "New score: " + scoredPoints);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}
