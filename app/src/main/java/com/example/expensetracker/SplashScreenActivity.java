package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private static  int SPLASH = 3000; // Duration of the splash screen in milliseconds
    Animation animation; // Animation object to hold the animation used for the splash screen
    private ImageView imageView; // ImageView to display the app logo
    private TextView appName; // TextView to display the app name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity full screen, hiding the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the layout for the splash screen
        setContentView(R.layout.activity_splash_screen);

        // Load the animation from the "animation" resource folder
        animation = AnimationUtils.loadAnimation(this, R.anim.animation);

        // Get references to the ImageView and TextView from the layout
        imageView = findViewById(R.id.imageView);
        appName = findViewById(R.id.appName);

        // Apply the animation to the ImageView and TextView
        imageView.setAnimation(animation);
        appName.setAnimation(animation);

        // Use a Handler to delay the transition to the LoginActivity for a certain duration
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start the LoginActivity
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);

                // Start the LoginActivity
                startActivity(intent);

                // Finish the SplashScreenActivity, preventing the user from navigating back to it
                finish();
            }
        }, SPLASH); // Delay the transition for the specified duration
    }
}
