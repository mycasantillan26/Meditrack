package com.example.meditrack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.widget.TextView;
import android.util.Log;
import java.text.ParseException;
import java.util.HashMap;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.widget.Button;
import android.view.View;
import com.google.firebase.FirebaseApp;



public class Today extends AppCompatActivity {
    private ListView lvTodayPlans;
    private List<Map<String, Object>> todayPlans = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private SimpleDateFormat dateDisplayFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
    private TextView tvDayOfWeek, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_today);
        lvTodayPlans = findViewById(R.id.lvTodayPlans);
        tvDayOfWeek = findViewById(R.id.tvDayOfWeek);
        tvDate = findViewById(R.id.tvDate);
        Button plusButton = findViewById(R.id.plusButton);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore.setLoggingEnabled(true);


        Date today = new Date();
        tvDayOfWeek.setText(dayFormat.format(today));
        tvDate.setText(dateDisplayFormat.format(today));

        fetchTodayPlans();

        setupButtons();
        plusButton.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, AddNewPlan.class);
            startActivity(intent);
        });
    }

    private void setupButtons() {
        ImageButton profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, Profile.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        ImageButton todayIcon = findViewById(R.id.todayIcon);
        todayIcon.setOnClickListener(v -> {
            // Already on "Today" page
        });

        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, CalendarActivity.class);
            startActivity(intent);
        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, Plans.class);
            startActivity(intent);
        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, TrackSymptoms.class);
            startActivity(intent);
        });
    }

    private void fetchTodayPlans() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.collection("plans")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Task<DocumentSnapshot>> markAsTakenChecks = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> planData = document.getData();
                                planData.put("planId", document.getId()); // Setting the document ID as planId
                                String planId = document.getId();

                                // Check if the plan has been taken
                                Task<DocumentSnapshot> takenCheck = db.collection("MarkAsTaken")
                                        .document(planId)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful() && task1.getResult() != null && task1.getResult().exists()) {
                                                Map<String, Object> takenData = task1.getResult().getData();
                                                planData.put("isTaken", takenData.get("taken").equals("Yes"));
                                            } else {
                                                planData.put("isTaken", false);
                                            }
                                            expandPlanData(planData);
                                        });
                                markAsTakenChecks.add(takenCheck);
                            }

                            Tasks.whenAllComplete(markAsTakenChecks).addOnCompleteListener(tasks -> {
                                updateListView(); // Update your list once all checks are completed
                            });

                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(this, "Failed to fetch plans: " + error, Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }


    private void expandPlanData(Map<String, Object> originalPlanData) {
        List<String> times = extractTimes(originalPlanData);
        for (String time : times) {
            Map<String, Object> planCopy = new HashMap<>(originalPlanData);
            planCopy.put("displayTime", time);
            planCopy.put("isTaken", false);  // Initialize isTaken field
            planCopy.put("planId", originalPlanData.get("planId").toString());
            todayPlans.add(planCopy);
        }
    }



    private List<String> extractTimes(Map<String, Object> planData) {
        List<String> times = new ArrayList<>();
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Check time fields
        for (int i = 1; i <= 5; i++) {
            String timeKey = "time" + (i == 1 ? "" : i);
            String time = (String) planData.get(timeKey);
            if (time != null && !time.isEmpty()) {
                try {
                    Date date = parseFormat.parse(time);
                    times.add(displayFormat.format(date));
                } catch (ParseException e) {
                    Log.e("TimeFormatting", "Failed to parse time: " + time, e);
                }
            }
        }

        // If no regular times, check nextOccurrences
        if (times.isEmpty()) {
            List<String> nextOccurrences = (List<String>) planData.get("nextOccurrences");
            if (nextOccurrences != null) {
                for (String nextTime : nextOccurrences) {
                    try {
                        Date date = parseFormat.parse(nextTime);
                        times.add(displayFormat.format(date));
                    } catch (ParseException e) {
                        Log.e("TimeFormatting", "Failed to parse next occurrence time: " + nextTime, e);
                    }
                }
            }
        }
        return times;
    }


    private void updateListView() {
        if (!todayPlans.isEmpty()) {
            PlanAdapter adapter = new PlanAdapter(this, todayPlans);
            lvTodayPlans.setAdapter(adapter);
            lvTodayPlans.setVisibility(View.VISIBLE);
            findViewById(R.id.imgIcon2).setVisibility(View.GONE);
            findViewById(R.id.noPlansText).setVisibility(View.GONE);
            findViewById(R.id.plusButton).setVisibility(View.GONE);
        } else {
            lvTodayPlans.setVisibility(View.GONE);
            findViewById(R.id.imgIcon2).setVisibility(View.VISIBLE);
            findViewById(R.id.noPlansText).setVisibility(View.VISIBLE);
            findViewById(R.id.plusButton).setVisibility(View.VISIBLE);
        }
    }


    class PlanAdapter extends ArrayAdapter<Map<String, Object>> {
        private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

        public PlanAdapter(Context context, List<Map<String, Object>> plans) {
            super(context, 0, plans);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_plan, parent, false);
            }

            TextView tvPlanName = convertView.findViewById(R.id.tvPlanName);
            TextView tvPlanTime = convertView.findViewById(R.id.tvPlanTime);
            Button redSquareButton = convertView.findViewById(R.id.redSquareButton);

            Map<String, Object> plan = getItem(position);
            String name = (String) plan.get("name");
            String time = (String) plan.get("displayTime");
            Boolean isTaken = (Boolean) plan.get("isTaken");

            tvPlanName.setText(name);
            tvPlanTime.setText(time);
            redSquareButton.setEnabled(!isTaken);  // Disable button if plan is taken

            redSquareButton.setOnClickListener(v -> {
                if (!isTaken) {
                    showPlanOptions(plan, name, time);
                }
            });

            return convertView;
        }



        private String extractTimesFormatted(Map<String, Object> planData) {
            List<String> times = new ArrayList<>();
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            // Extract times from time1 to time5
            for (int i = 1; i <= 5; i++) {
                String timeKey = "time" + (i == 1 ? "" : i); // time1, time2, ... time5
                String time = (String) planData.get(timeKey);
                if (time != null && !time.isEmpty()) {
                    try {
                        Date date = parseFormat.parse(time);
                        times.add(displayFormat.format(date)); // Format each time
                    } catch (ParseException e) {
                        Log.e("TimeFormatting", "Failed to parse time: " + time, e);
                    }
                }
            }

            // Check nextOccurrences if regular times are empty
            if (times.isEmpty()) {
                List<String> nextOccurrences = (List<String>) planData.get("nextOccurrences");
                if (nextOccurrences != null) {
                    for (String nextTime : nextOccurrences) {
                        try {
                            Date date = parseFormat.parse(nextTime);
                            times.add(displayFormat.format(date)); // Format each time
                        } catch (ParseException e) {
                            Log.e("TimeFormatting", "Failed to parse next occurrence time: " + nextTime, e);
                        }
                    }
                }
            }

            return String.join(", ", times);
        }


        private String formatTimeWithAmPm(String time) {
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            try {
                Date date = parseFormat.parse(time);
                return displayFormat.format(date);
            } catch (ParseException e) {
                Log.e("Today", "Failed to parse time", e);
                return time;  // Return original time if parsing fails
            }
        }

        private void showPlanOptions(Map<String, Object> planData, String planName, String time) {
            String planId = (String) planData.get("planId"); // Retrieve the plan ID set earlier
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Plan Options");
            builder.setMessage("Select an action for this plan.");
            builder.setPositiveButton("Mark as Taken", (dialog, which) -> {
                if (planId != null && !planId.isEmpty()) {
                    updateMarkAsTaken(planId, planName, time, "Yes");
                } else {
                    Toast.makeText(getContext(), "Error: Plan ID is missing or invalid", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                if (planId != null && !planId.isEmpty()) {
                    updateMarkAsTaken(planId, planName, time, "No");
                } else {
                    Toast.makeText(getContext(), "Error: Plan ID is missing or invalid", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }


        private void updateMarkAsTaken(String planId, String planName, String time, String taken) {
            if (planId == null || planId.isEmpty()) {
                Toast.makeText(getContext(), "Error: Plan ID is missing or invalid", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, Object> takenData = new HashMap<>();
            takenData.put("planId", planId);
            takenData.put("planName", planName);
            takenData.put("time", time);
            takenData.put("taken", taken);

            db.collection("MarkAsTaken").add(takenData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore Success", "Document added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Success: Data saved", Toast.LENGTH_SHORT).show();
                        // Find and update the plan as taken in local data
                        for (Map<String, Object> plan : todayPlans) {
                            if (planId.equals(plan.get("planId")) && time.equals(plan.get("displayTime"))) {
                                plan.put("isTaken", true);
                                notifyDataSetChanged();  // Refresh adapter to update UI
                                break;
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore Error", "Error adding document", e);
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }



    }
    }


