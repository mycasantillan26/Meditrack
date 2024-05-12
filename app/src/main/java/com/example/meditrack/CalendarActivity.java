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

public class CalendarActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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

        calendarView = findViewById(R.id.calendarView);
        monthYearText = findViewById(R.id.monthYearText);
        planContainer = findViewById(R.id.planContainer);

        // Update the month and year text when the activity is created
        updateMonthYearText(Calendar.getInstance().getTime());

        // Fetch plans for the current month initially
        fetchPlansForMonth(Calendar.getInstance().getTime());

        // Handle changes in the selected date on the calendar
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            updateMonthYearText(selectedDate.getTime());
            fetchPlansForMonth(selectedDate.getTime());
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
