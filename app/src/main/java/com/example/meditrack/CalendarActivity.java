package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);

        // Find the calendarView
        CalendarView calendarView = findViewById(R.id.calendarView);



        // Find the profile button ImageButton
        ImageButton profileButton = findViewById(R.id.profileButton);

        // Set OnClickListener for the profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Profile activity when the profile button is clicked
                Intent intent = new Intent(CalendarActivity.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Add animation
            }
        });

        // Find the today icon ImageButton
        ImageButton todayIcon = findViewById(R.id.todayIcon);

        // Set OnClickListener for the today icon
        todayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(CalendarActivity.this, Today.class);
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
                Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // Find the image icon ImageButton
        ImageButton imgIcon = findViewById(R.id.imgIcon);

        // Set OnClickListener for the image icon
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(CalendarActivity.this, Plans.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // Find the tracker icon ImageButton
        ImageButton trackerIcon = findViewById(R.id.trackerIcon);

        // Set OnClickListener for the tracker icon
        trackerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Calendar activity when the calendar icon is clicked
                Intent intent = new Intent(CalendarActivity.this, TrackSymptoms.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

      

        // Highlight today's date initially
        highlightSelectedDate(calendarView, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    // Method to highlight selected date
    private void highlightSelectedDate(CalendarView calendarView, int year, int month, int dayOfMonth) {
        // Set the style for the selected date
        calendarView.setDateTextAppearance(R.style.SelectedDateStyle);

        // Set the selected date in the calendar
        calendarView.setDate(getSelectedDateInMillis(year, month, dayOfMonth));
    }


    // Method to get selected date in milliseconds
    private long getSelectedDateInMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calendar.getTimeInMillis();
    }

}
