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


public class Today extends AppCompatActivity {

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix
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
        setContentView(R.layout.activity_today);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        Date today = new Date();
        lvTodayPlans = findViewById(R.id.lvTodayPlans);
        tvDayOfWeek = findViewById(R.id.tvDayOfWeek);
        tvDate = findViewById(R.id.tvDate);
        Button plusButton = findViewById(R.id.plusButton);

        tvDayOfWeek.setText(dayFormat.format(today));
        tvDate.setText(dateDisplayFormat.format(today));
        if(currentUser == null) {
            startActivity(new Intent(Today.this, GetStarted.class));
        }
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
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish(); // Close the current activity
        });

        ImageButton todayIcon = findViewById(R.id.todayIcon);
        todayIcon.setOnClickListener(v -> {
            // Already on "Today" page
        });

        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, Plans.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Today.this, TrackSymptoms.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void fetchTodayPlans() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.collection("plans")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String todayStr = dateFormat.format(new Date());
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> planData = document.getData();
                                Timestamp timestamp = (Timestamp) planData.get("startDate");
                                String planDateStr = dateFormat.format(timestamp.toDate());
                                if (planDateStr.equals(todayStr)) {
                                    todayPlans.add(planData);
                                }
                            }
                            if (!todayPlans.isEmpty()) {
                                updateListView();
                            } else {
                                Toast.makeText(this, "No plans for today.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to fetch plans.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
            tvPlanName.setText((String) plan.get("name"));
            Timestamp startTimestamp = (Timestamp) plan.get("startDate");
            String timeStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTimestamp.toDate());
            tvPlanTime.setText(timeStr);

            // Optional: Set onClick listener for redSquareButton
            redSquareButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(Today.this);
                builder.setTitle("What do you want to do?");
                builder.setMessage("Select an option for the medication.");
                builder.setPositiveButton("Mark as Taken", (dialog, which) -> {
                    // Handle marking as taken
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            });


            return convertView;
        }
    }

}