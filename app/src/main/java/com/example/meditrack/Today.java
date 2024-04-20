package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class Today extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_today);

        ImageButton profileButton = findViewById(R.id.profileButton);

        // Set OnClickListener for the profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Profile activity when the profile button is clicked
                Intent intent = new Intent(Today.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Add animation
            }
        });

        ImageButton todayIcon = findViewById(R.id.todayIcon);
        // Set OnClickListener for the today icon (optional)
        todayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You are already on the "Today" page, so no action is needed.
            }
        });

        // Find the calendar icon ImageButton
        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        // Set OnClickListener for the calendar icon
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(Today.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        // Set OnClickListener for the image icon
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Plans activity when the image icon is clicked
                Intent intent = new Intent(Today.this, Plans.class);
                startActivity(intent);
            }
        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        // Set OnClickListener for the tracker icon
        trackerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the TrackSymptoms activity when the tracker icon is clicked
                Intent intent = new Intent(Today.this, TrackSymptoms.class);
                startActivity(intent);
            }
        });
    }
}
