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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Plans extends AppCompatActivity {
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix
    private TextView noPlansText;
    private List<Map<String, Object>> planDataList;
    private LinearLayout planContainer;
    private FirebaseFirestore db;

    private Button plusButton;
    private Button plusButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(currentUser == null) {
            startActivity(new Intent(Plans.this, GetStarted.class));
        }
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
        ImageButton todayIcon = findViewById(R.id.todayIcon);
        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        ImageButton profileButton = findViewById(R.id.profileButton);


        retrievePlansFromFirestore();
        setupButtons();
    }

    private void setupButtons() {
        plusButton.setOnClickListener(v -> startActivity(new Intent(Plans.this, AddNewPlan.class)));
        plusButton2.setOnClickListener(v -> startActivity(new Intent(Plans.this, AddNewPlan.class)));

        ImageButton profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Plans.this, Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish(); // Close the current activity
        });

        ImageButton todayIcon = findViewById(R.id.todayIcon);
        todayIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Plans.this, Plans.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Plans.this, CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> {
            // Already on "plans" page
        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Plans.this, TrackSymptoms.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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
        if (planDataList != null && !planDataList.isEmpty()) {
            findViewById(R.id.imgIcon2).setVisibility(View.GONE);
            noPlansText.setVisibility(View.GONE);
            plusButton.setVisibility(View.GONE);
            plusButton2.setVisibility(View.VISIBLE);

            planContainer.removeAllViews();
            for (Map<String, Object> planData : planDataList) {
                // Extract relevant information from the plan data
                String name = (String) planData.get("name");
                int dosage = ((Long) planData.get("dosage")).intValue(); // Cast long to int safely
                String unit = (String) planData.get("unit");
                String intakeMethod = (String) planData.get("Intake_method");
                Timestamp startTimeStamp = (Timestamp) planData.get("startDate");
                Timestamp endTimeStamp = (Timestamp) planData.get("endDate");

                Date startDate = startTimeStamp.toDate();
                Date endDate = endTimeStamp.toDate();

                int days = calculateDays(startDate, endDate);

                // Now include the planData when calling createPlanEntryLayout
                LinearLayout planEntryLayout = createPlanEntryLayout(name, dosage, unit, intakeMethod, startDate, days, planData);
                planContainer.addView(planEntryLayout);
            }
        } else {
            findViewById(R.id.imgIcon2).setVisibility(View.VISIBLE);
            noPlansText.setVisibility(View.VISIBLE);
            plusButton.setVisibility(View.VISIBLE);
            plusButton2.setVisibility(View.GONE);
            planContainer.removeAllViews();
        }
    }



    private void displayPlanEntry(Map<String, Object> planData) {
        String name = (String) planData.get("name");
        long dosage = (long) planData.get("dosage");  // Cast to long then convert to int
        String unit = (String) planData.get("unit");
        String intakeMethod = (String) planData.get("Intake_method");
        Timestamp startTimeStamp = (Timestamp) planData.get("startDate");
        Timestamp endTimeStamp = (Timestamp) planData.get("endDate");
        Date startDate = startTimeStamp.toDate();
        Date endDate = endTimeStamp.toDate();
        int days = calculateDays(startDate, endDate);

        LinearLayout planEntryLayout = createPlanEntryLayout(name, dosage, unit, intakeMethod, startDate, days, planData);
        planContainer.addView(planEntryLayout);
    }

    private LinearLayout createPlanEntryLayout(String name, long dosage, String unit, String intakeMethod, Date startDate, int days, Map<String, Object> planData) {
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
        intakeMethodLabel.setText(String.format(Locale.getDefault(), "%s (%s), %d days", intakeMethod, formatTime(startDate), days));
        intakeMethodLabel.setTextColor(Color.WHITE); // Set text color to white
        nameAndIntakeLayout.addView(intakeMethodLabel);
        planEntryLayout.addView(nameAndIntakeLayout);

        Button optionsButton = new Button(this);
        optionsButton.setText("Options");
        optionsButton.setOnClickListener(v -> showOptionsDialog((String) planData.get("planId"), planData));
        planEntryLayout.addView(optionsButton);

        return planEntryLayout;
    }


    private void showOptionsDialog(String planId, Map<String, Object> planData) {
        OptionsDialogFragment dialog = OptionsDialogFragment.newInstance(planId, planData);
        dialog.show(getSupportFragmentManager(), "OptionsDialog");
    }

    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return timeFormat.format(date);
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
