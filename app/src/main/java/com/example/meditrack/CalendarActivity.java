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
        import androidx.annotation.NonNull;
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
        import android.widget.ImageView;
        import java.lang.reflect.Field;
        import java.util.Date;
        import java.util.HashSet;
        import android.view.Gravity;
        import java.text.ParseException;




public class CalendarActivity extends AppCompatActivity {

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CalendarView calendarView;
    private HashSet<Date> planDates;
    private List<Map<String, Object>> planDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(currentUser == null) {
            startActivity(new Intent(CalendarActivity.this, GetStarted.class));
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                updateMonthYear(year, month);
            }
        });

        Calendar now = Calendar.getInstance();
        updateMonthYear(now.get(Calendar.YEAR), now.get(Calendar.MONTH)); // Use current date for initialization

        setCalendarHeaderColor(calendarView, Color.WHITE);
        setHeaderTextColor(calendarView, Color.WHITE);

        setupButtons();
        retrievePlansFromFirestore();
    }


    private void updateMonthYear(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        TextView monthYearText = findViewById(R.id.monthYearText);
        if (monthYearText != null) {
            monthYearText.setText(monthYearFormat.format(calendar.getTime()));
        }
    }

    private void setHeaderTextColor(CalendarView calendarView, int color) {
        try {
            Field field = CalendarView.class.getDeclaredField("mMonthName");
            field.setAccessible(true);
            TextView monthName = (TextView) field.get(calendarView);
            if (monthName != null) {
                monthName.setTextColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCalendarHeaderColor(CalendarView view, int color) {
        try {
            int headerId = getResources().getIdentifier("month_name", "id", "android");
            if (headerId != 0) {
                View headerView = view.findViewById(headerId);
                if (headerView instanceof TextView) {
                    ((TextView) headerView).setTextColor(color);
                }
            }

            int prevButtonId = getResources().getIdentifier("prev", "id", "android");
            int nextButtonId = getResources().getIdentifier("next", "id", "android");
            if (prevButtonId != 0) {
                View prevButton = view.findViewById(prevButtonId);
                if (prevButton instanceof ImageView) {
                    ((ImageView) prevButton).setColorFilter(color);
                }
            }
            if (nextButtonId != 0) {
                View nextButton = view.findViewById(nextButtonId);
                if (nextButton instanceof ImageView) {
                    ((ImageView) nextButton).setColorFilter(color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            // Already on "Calendar" page
        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, Plans.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, TrackSymptoms.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void retrievePlansFromFirestore() {
         currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("plans")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            planDataList = new ArrayList<>();
                            planDates = new HashSet<>();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> planData = document.getData();
                                Timestamp startTimestamp = (Timestamp) planData.get("startDate");
                                Timestamp endTimestamp = (Timestamp) planData.get("endDate");
                                Calendar startCal = Calendar.getInstance();
                                startCal.setTime(startTimestamp.toDate());
                                Calendar endCal = Calendar.getInstance();
                                endCal.setTime(endTimestamp.toDate());
                                while (!startCal.after(endCal)) {
                                    try {
                                        Date dateToAdd = formatter.parse(formatter.format(startCal.getTime()));
                                        planDates.add(dateToAdd);
                                        startCal.add(Calendar.DATE, 1);
                                    } catch (ParseException e) {
                                        e.printStackTrace(); // Handle parsing errors here
                                    }
                                }
                            }
                            markCalendarDates();
                        } else {
                            Toast.makeText(this, "Failed to fetch plans", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void markCalendarDates() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1); // Start from the first of the month
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        LinearLayout dateMarkers = findViewById(R.id.dateMarkersLayout);
        dateMarkers.removeAllViews();

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            Date dateToCheck = null;
            try {
                dateToCheck = sdf.parse(sdf.format(cal.getTime())); // Normalize date
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the exception
            }
            TextView dateView = new TextView(this);
            dateView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            dateView.setGravity(Gravity.CENTER);
            if (planDates.contains(dateToCheck)) {
                dateView.setText("•");
                dateView.setTextColor(Color.RED);
            } else {
                dateView.setText("");
            }
            dateMarkers.addView(dateView);
        }
    }



}