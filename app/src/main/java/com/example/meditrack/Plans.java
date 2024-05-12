package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.graphics.Color;
import android.util.Log;
import java.text.ParseException;
import java.util.HashMap;
import java.util.stream.Collectors;



public class Plans extends AppCompatActivity {

    private TextView noPlansText;
    private List<Map<String, Object>> planDataList;
    private LinearLayout planContainer;
    private FirebaseFirestore db;
    private Button plusButton;
    private Button plusButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_plans);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        noPlansText = findViewById(R.id.noPlansText);
        planContainer = findViewById(R.id.planContainer);
        plusButton = findViewById(R.id.plusButton);
        plusButton2 = findViewById(R.id.plusButton2);

        retrievePlansFromFirestore();
        setupOnClickListeners();
    }

    private void setupOnClickListeners() {
        plusButton.setOnClickListener(v -> startActivity(new Intent(Plans.this, AddNewPlan.class)));
        plusButton2.setOnClickListener(v -> startActivity(new Intent(Plans.this, AddNewPlan.class)));

        ImageButton todayIcon = findViewById(R.id.todayIcon);
        todayIcon.setOnClickListener(v -> startActivity(new Intent(Plans.this, Today.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)));

        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(v -> startActivity(new Intent(Plans.this, CalendarActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)));

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(v -> startActivity(new Intent(Plans.this, TrackSymptoms.class)));
    }

    private void retrievePlansFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.collection("plans")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            planDataList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> planData = document.getData();
                                planData.put("planId", document.getId());
                                planDataList.add(planData);
                            }
                            displayPlans();
                        } else {
                            Toast.makeText(this, "Failed to fetch plans", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void displayPlans() {
        planContainer.removeAllViews();
        if (planDataList != null && !planDataList.isEmpty()) {
            findViewById(R.id.imgIcon2).setVisibility(View.GONE);
            noPlansText.setVisibility(View.GONE);
            plusButton.setVisibility(View.GONE);
            plusButton2.setVisibility(View.VISIBLE);
            for (Map<String, Object> planData : planDataList) {
                displayPlanEntry(planData);
            }
        } else {
            findViewById(R.id.imgIcon2).setVisibility(View.VISIBLE);
            noPlansText.setVisibility(View.VISIBLE);
            plusButton.setVisibility(View.VISIBLE);
            plusButton2.setVisibility(View.GONE);
        }
    }

    private void displayPlanEntry(Map<String, Object> planData) {
        String name = (String) planData.get("name");
        int dosage = ((Long) planData.get("dosage")).intValue();
        String unit = (String) planData.get("unit");
        String intakeMethod = (String) planData.get("Intake_method");
        Timestamp startTimeStamp = (Timestamp) planData.get("startDate");
        Timestamp endTimeStamp = (Timestamp) planData.get("endDate");
        Date startDate = startTimeStamp.toDate();
        Date endDate = endTimeStamp.toDate();
        int days = calculateDays(startDate, endDate);
        String timesFormatted = extractTimesFormatted(planData);

        LinearLayout planEntryLayout = createPlanEntryLayout(name, dosage, unit, intakeMethod, startDate, days, timesFormatted, planData);
        planContainer.addView(planEntryLayout);
    }

    private LinearLayout createPlanEntryLayout(String name, int dosage, String unit, String intakeMethod, Date startDate, int days, String timesFormatted, Map<String, Object> planData) {
        LinearLayout planEntryLayout = new LinearLayout(this);
        planEntryLayout.setOrientation(LinearLayout.HORIZONTAL);
        planEntryLayout.setPadding(0, 8, 0, 5);

        LinearLayout nameAndIntakeLayout = new LinearLayout(this);
        nameAndIntakeLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        nameAndIntakeLayout.setLayoutParams(layoutParams);

        TextView nameLabel = new TextView(this);
        nameLabel.setText(String.format(Locale.getDefault(), "%s %d %s", name, dosage, unit));
        nameLabel.setTextSize(20);
        nameLabel.setTextColor(Color.WHITE);
        nameAndIntakeLayout.addView(nameLabel);

        TextView intakeMethodLabel = new TextView(this);
        List<String> selectedDays = (List<String>) planData.get("selectedDays");

        // Format day names to abbreviated form
        String daysText = formatDayNames(selectedDays);

        // Conditional display based on intake method
        if (intakeMethod.equals("Specific day of the week")) {
            if (!daysText.isEmpty()) {
                intakeMethodLabel.setText(String.format(Locale.getDefault(), "%s (%s) %d days.", daysText, timesFormatted, days));
            } else {
                intakeMethodLabel.setText(String.format(Locale.getDefault(), "(%s) %d days.", timesFormatted, days));
            }
        } else {
            intakeMethodLabel.setText(String.format(Locale.getDefault(), "%s, (%s) %d days.", intakeMethod, timesFormatted, days));
        }

        intakeMethodLabel.setTextColor(Color.WHITE);
        nameAndIntakeLayout.addView(intakeMethodLabel);
        planEntryLayout.addView(nameAndIntakeLayout);

        Button optionsButton = new Button(this);
        optionsButton.setText("Options");
        optionsButton.setOnClickListener(v -> showOptionsDialog((String) planData.get("planId"), planData));
        planEntryLayout.addView(optionsButton);

        return planEntryLayout;
    }

    private String formatDayNames(List<String> days) {
        if (days == null || days.isEmpty()) {
            return "";
        }

        // Map full day names to their abbreviations
        Map<String, String> dayAbbreviations = new HashMap<>();
        dayAbbreviations.put("Monday", "MON");
        dayAbbreviations.put("Tuesday", "TUE");
        dayAbbreviations.put("Wednesday", "WED");
        dayAbbreviations.put("Thursday", "THU");
        dayAbbreviations.put("Friday", "FRI");
        dayAbbreviations.put("Saturday", "SAT");
        dayAbbreviations.put("Sunday", "SUN");

        // Transform the list of days to their abbreviations
        return days.stream()
                .map(day -> dayAbbreviations.getOrDefault(day, day))
                .collect(Collectors.joining(", "));
    }




    private String extractTimesFormatted(Map<String, Object> planData) {
        List<String> formattedTimes = new ArrayList<>();

        // Check regular time entries first
        for (int i = 1; i <= 5; i++) {
            String timeKey = "time" + (i == 1 ? "" : i);
            String time = (String) planData.get(timeKey);
            if (time != null && !time.isEmpty()) {
                formattedTimes.add(formatTimeWithAmPm(time));
            }
        }

        // If no regular times are available, check nextOccurrences
        if (formattedTimes.isEmpty()) {
            List<String> nextOccurrences = (List<String>) planData.get("nextOccurrences");
            if (nextOccurrences != null && !nextOccurrences.isEmpty()) {
                for (String nextTime : nextOccurrences) {
                    formattedTimes.add(formatTimeWithAmPm(nextTime));
                }
            }
        }

        return String.join(", ", formattedTimes);
    }

    private String formatTimeWithAmPm(String time) {
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date date = parseFormat.parse(time);
            return displayFormat.format(date);
        } catch (ParseException e) {
            Log.e("Plans", "Failed to parse time", e);
            return time; // Return original time if parsing fails
        }
    }

    private void showOptionsDialog(String planId, Map<String, Object> planData) {
        OptionsDialogFragment dialog = OptionsDialogFragment.newInstance(planId, planData);
        dialog.show(getSupportFragmentManager(), "OptionsDialog");
    }

    private int calculateDays(Date startDate, Date endDate) {
        long differenceInMillis = endDate.getTime() - startDate.getTime();
        return (int) (differenceInMillis / (1000 * 60 * 60 * 24));
    }

    public void deletePlan(String planId) {
        if (planId != null && !planId.trim().isEmpty()) {
            db.collection("plans").document(planId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Plans.this, "Plan deleted successfully", Toast.LENGTH_SHORT).show();
                        retrievePlansFromFirestore();  // Refresh the list after delete
                    })
                    .addOnFailureListener(e -> Toast.makeText(Plans.this, "Failed to delete plan: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(Plans.this, "Invalid plan ID", Toast.LENGTH_SHORT).show();
        }
    }
}
