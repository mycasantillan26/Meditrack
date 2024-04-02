package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageButton;

public class Plans extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_plans);


        ImageButton profileButton = findViewById(R.id.profileButton);

        // Set OnClickListener for the profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Profile activity when the profile button is clicked
                Intent intent = new Intent(Plans.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Add animation
            }
        });

        ImageButton todayIcon = findViewById(R.id.todayIcon);

        // Set OnClickListener for the calendar icon
        todayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(Plans.this, Today.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        // Find the calendar icon ImageButton
        ImageButton calendarIcon = findViewById(R.id.calendarIcon);

        // Set OnClickListener for the calendar icon
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(Plans.this, CalendarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);

        // Set OnClickListener for the calendar icon
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(Plans.this, Plans.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);

        // Set OnClickListener for the calendar icon
        trackerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(Plans.this, TrackSymptoms.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        Button plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddNewPlan activity when the plusButton is clicked
                Intent intent = new Intent(Plans.this, AddNewPlan.class);
                startActivity(intent);
            }
        });
    }
}
