package com.example.garbagefinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    View whiteScreen;
    View logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        whiteScreen = findViewById(R.id.whiteScreen);
        logo = findViewById(R.id.logo);

        // Check if the views are null
        if (whiteScreen == null || logo == null) {
            Log.e("SplashActivity", "One or more views are not properly initialized!");
            return;
        }

        Log.d("check", "Views initialized properly");

        // Animate the white screen and logo to slide down
        animateSplashScreen();
    }

    private void animateSplashScreen() {
        // Get the height of the screen
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Create a translation animation
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, screenHeight);
        animation.setDuration(1000); // Set duration to 1 second
        animation.setFillAfter(true);

        // Start the animation
        whiteScreen.startAnimation(animation);
        logo.startAnimation(animation);

        // Add a listener to transition to the login screen after the animation
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Transition to the login screen
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);
                finish(); // Finish the splash screen activity so it can't be returned to
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
    }
}
