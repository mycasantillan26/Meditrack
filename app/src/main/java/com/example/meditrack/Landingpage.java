package com.example.meditrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class Landingpage extends AppCompatActivity {

    private static final int DELAY_TIME_MS = 2000; // 2 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_landingpage);

        // Find the ProgressBar
        ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Hide the ProgressBar after a delay and start the GetStarted activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide the ProgressBar
                loadingProgressBar.setVisibility(android.view.View.GONE);

                // Start the GetStarted activity
                startActivity(new Intent(Landingpage.this, GetStarted.class));
                finish(); // Finish the Landingpage activity
            }
        }, DELAY_TIME_MS);
    }
}
