package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Plans extends AppCompatActivity {

    private TextView noPlansText;
    private TextView plansTextView;
    private TextView nameTextView; // Declare TextView for Name
    private TextView dosageTextView; // Declare TextView for Dosage
    private TextView unitTextView; // Declare TextView for Unit
    private TextView intakeMethodTextView; // Declare TextView for Intake Method
    private TextView timeTextView; // Declare TextView for Time
    private TextView daysTextView; // Declare TextView for Days
    private List<String> planNames; // List to hold plan names
    private FirebaseFirestore db;
    private List<Map<String, Object>> planDataList; // List to hold plan data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_plans);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        noPlansText = findViewById(R.id.noPlansText);
        plansTextView = findViewById(R.id.plansTextView);

        // Initialize TextViews
        nameTextView = findViewById(R.id.nameTextView);
        dosageTextView = findViewById(R.id.dosageTextView);
        unitTextView = findViewById(R.id.unitTextView);
        intakeMethodTextView = findViewById(R.id.intakeMethodTextView);
        timeTextView = findViewById(R.id.timeTextView);
        daysTextView = findViewById(R.id.daysTextView);

        // Retrieve plans from Firestore
        retrievePlansFromFirestore();

        // Set up OnClickListener for other buttons (profileButton, todayIcon, etc.)
        setupOnClickListeners();
    }

    private void setupOnClickListeners() {
        // PlusButton OnClickListener
        Button plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddNewPlan activity when plusButton is clicked
                Intent intent = new Intent(Plans.this, AddNewPlan.class);
                startActivity(intent);
            }
        });

        // TodayIcon OnClickListener
        ImageButton todayIcon = findViewById(R.id.todayIcon);
        todayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Today activity when todayIcon is clicked
                Intent intent = new Intent(Plans.this, Today.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // CalendarIcon OnClickListener
        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the CalendarActivity when calendarIcon is clicked
                Intent intent = new Intent(Plans.this, CalendarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // ImgIcon OnClickListener (Refresh Plans)
        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh the current activity by restarting it
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        // TrackerIcon OnClickListener
        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the TrackSymptoms activity when trackerIcon is clicked
                Intent intent = new Intent(Plans.this, TrackSymptoms.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    private void retrievePlansFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("plans")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            planDataList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> planData = document.getData();
                                planDataList.add(planData);
                            }
                            displayPlans(); // Once plans are retrieved, display them
                        } else {
                            Toast.makeText(this, "Failed to fetch plans", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void displayPlans() {
        if (planDataList != null && !planDataList.isEmpty()) {
            // Plans exist, hide the "No Plans" text
            findViewById(R.id.imgIcon2).setVisibility(View.GONE);
            noPlansText.setVisibility(View.GONE);

            // Iterate through each plan data
            for (Map<String, Object> planData : planDataList) {
                // Extract relevant information from the plan data
                String name = (String) planData.get("name");
                Object dosageObject = planData.get("dosage");
                int dosage;
                if (dosageObject instanceof Long) {
                    dosage = ((Long) dosageObject).intValue();
                } else if (dosageObject instanceof Integer) {
                    dosage = (Integer) dosageObject;
                } else {
                    dosage = 0; // Default value or handle it as per your requirement
                }
                String unit = (String) planData.get("unit");
                String intakeMethod = (String) planData.get("Intake_method");
                com.google.firebase.Timestamp startTimeStamp = (com.google.firebase.Timestamp) planData.get("startDate");
                String startTime = startTimeStamp.toDate().toString();

                com.google.firebase.Timestamp endTimeStamp = (com.google.firebase.Timestamp) planData.get("endDate");
                String endTime = endTimeStamp.toDate().toString();


                // Format the start and end date times into readable format (e.g., days user takes the medicine)
                // Here you need to implement the logic to calculate days based on start and end time
                // For now, let's assume you have a method called calculateDays() to calculate days

                // Calculate the days
                String days = calculateDays(startTime, endTime);

                // Display the information in respective TextViews
                nameTextView.setText(name);
                dosageTextView.setText(String.valueOf(dosage));
                unitTextView.setText(unit);
                intakeMethodTextView.setText(intakeMethod);
                timeTextView.setText(startTime); // Assuming timeTextView displays only start time
                daysTextView.setText(days);

                // Set TextViews visibility to visible
                nameTextView.setVisibility(View.VISIBLE);
                dosageTextView.setVisibility(View.VISIBLE);
                unitTextView.setVisibility(View.VISIBLE);
                intakeMethodTextView.setVisibility(View.VISIBLE);
                timeTextView.setVisibility(View.VISIBLE);
                daysTextView.setVisibility(View.VISIBLE);
            }
        } else {
            // No plans exist, display the "No Plans" text
            findViewById(R.id.imgIcon2).setVisibility(View.VISIBLE);
            noPlansText.setVisibility(View.VISIBLE);
            // Hide all TextViews
            nameTextView.setVisibility(View.GONE);
            dosageTextView.setVisibility(View.GONE);
            unitTextView.setVisibility(View.GONE);
            intakeMethodTextView.setVisibility(View.GONE);
            timeTextView.setVisibility(View.GONE);
            daysTextView.setVisibility(View.GONE);
        }
    }

    // Method to calculate days
    private String calculateDays(String startTime, String endTime) {
        // Implement your logic to calculate days
        return ""; // Placeholder, replace with your implementation
    }
}
