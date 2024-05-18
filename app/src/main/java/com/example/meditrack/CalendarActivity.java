package com.example.meditrack;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import androidx.annotation.NonNull;



public class CalendarActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix


    private CalendarView calendarView;
    private TextView monthYearText;
    private LinearLayout planContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);
        setupButtons();

        calendarView = findViewById(R.id.calendarView);
        monthYearText = findViewById(R.id.monthYearText);
        planContainer = findViewById(R.id.planContainer);

        // Update the month and year text when the activity is created
        updateMonthYearText(Calendar.getInstance().getTime());

        // Fetch plans for the current month initially
        fetchPlansForMonth(Calendar.getInstance().getTime());

        // Handle changes in the selected date on the calendar
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            private int lastDisplayedMonth = -1; // to keep track of the last displayed month

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Check if the month has changed
                if (month != lastDisplayedMonth) {
                    lastDisplayedMonth = month; // Update the last displayed month
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);

                    updateMonthYearText(calendar.getTime());
                    fetchPlansForMonth(calendar.getTime());
                }
            }
        });
    }

    private void setupButtons() {
        ImageButton profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        ImageButton todayIcon = findViewById(R.id.todayIcon);
        todayIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, Today.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, Plans.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(v -> {
            // Already on "Tracker" page
        });
    }




    private void fetchPlansForMonth(Date date) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String monthYear = monthFormat.format(date);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.collection("plans")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        planContainer.removeAllViews(); // Clear previous entries
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> planData = document.getData();
                                processPlanData(planData, monthYear, monthFormat);
                            }
                        } else {
                            Toast.makeText(this, "Failed to fetch plans.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void processPlanData(Map<String, Object> planData, String targetMonthYear, SimpleDateFormat monthFormat) {
        Timestamp startTimestamp = (Timestamp) planData.get("startDate");
        Timestamp endTimestamp = (Timestamp) planData.get("endDate");
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startTimestamp.toDate());
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endTimestamp.toDate());

        while (!startCal.after(endCal)) {
            String currentMonthYear = monthFormat.format(startCal.getTime());
            if (currentMonthYear.equals(targetMonthYear)) {
                addPlanToView(planData, startCal.getTime());
            }
            startCal.add(Calendar.DATE, 1); // Increment the day
        }
    }

    private void addPlanToView(Map<String, Object> planData, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d", Locale.getDefault());
        String displayText = planData.get("name") + " - " + dateFormat.format(date);
        TextView planView = new TextView(this);
        planView.setText(displayText);
        planView.setTextSize(16f);
        planView.setTextColor(Color.WHITE);
        planContainer.addView(planView);
    }

    private void updateMonthYearText(Date date) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearText.setText(monthYearFormat.format(date));
    }
}
