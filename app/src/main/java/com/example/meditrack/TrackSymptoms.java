package com.example.meditrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TrackSymptoms extends AppCompatActivity {
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix
    private FirebaseFirestore db;
    private TextView selectedSymptomsTextView;
    private Button selectSymptomsButton;
    private Set<Integer> selectedSymptomIndices;
    private String[] symptomOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(currentUser == null) {
            startActivity(new Intent(TrackSymptoms.this, GetStarted.class));
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tracksymptoms);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();

        // Initialize TextView and Buttons
        selectedSymptomsTextView = findViewById(R.id.selected_symptoms_display);
        selectSymptomsButton = findViewById(R.id.selectSymptomsButton);
        setupButtons();
        Button saveButton = findViewById(R.id.savebutton);
        Button generateGraph = findViewById(R.id.generateGraph);
        generateGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackSymptoms.this, TrackSymptomsGraph.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


        // Load the checklist items from a resource array
        symptomOptions = getResources().getStringArray(R.array.checklist_items);
        selectedSymptomIndices = new HashSet<>();

        // Set OnClickListener for the selectSymptomsButton
        selectSymptomsButton.setOnClickListener(view -> showChecklistDialog());

        // Set OnClickListener for the save button
        saveButton.setOnClickListener(view -> {
            List<String> selectedSymptoms = getSelectedSymptoms();
            saveSymptomsToFirestore(selectedSymptoms);
        });

    }

    private void setupButtons() {
        ImageButton profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrackSymptoms.this, Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        ImageButton todayIcon = findViewById(R.id.todayIcon);
        todayIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TrackSymptoms.this, Today.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        ImageButton calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TrackSymptoms.this, CalendarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        ImageButton imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TrackSymptoms.this, Plans.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

        ImageButton trackerIcon = findViewById(R.id.trackerIcon);
        trackerIcon.setOnClickListener(v -> {
            // Already on "Tracker" page
        });
    }

    private void showChecklistDialog() {
        boolean[] checkedItems = new boolean[symptomOptions.length];
        for (Integer index : selectedSymptomIndices) {
            checkedItems[index] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Symptoms")
                .setMultiChoiceItems(symptomOptions, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedSymptomIndices.add(which);
                        } else {
                            selectedSymptomIndices.remove(which);
                        }
                    }
                })
                .setPositiveButton("OK", (dialog, id) -> displaySelectedSymptoms())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<String> getSelectedSymptoms() {
        List<String> symptoms = new ArrayList<>();
        for (Integer index : selectedSymptomIndices) {
            symptoms.add(symptomOptions[index]);
        }
        return symptoms;
    }

    private void displaySelectedSymptoms() {
        String selectedSymptomsText = getString(R.string.selected_symptoms_prefix) + String.join(", ", getSelectedSymptoms());
        selectedSymptomsTextView.setText(selectedSymptomsText);
    }

    private void saveSymptomsToFirestore(List<String> selectedSymptoms) {
        // Check if the selectedSymptoms list is empty
        if (selectedSymptoms.isEmpty()) {
            Toast.makeText(this, "No symptoms selected. Please select at least one symptom.", Toast.LENGTH_SHORT).show();
            return;
        }

        String user = getCurrentUser();
        if (user != null) {
            String userId = user;
            Date now = new Date();
            Map<String, Object> data = new HashMap<>();
            data.put("SelectSymptoms", selectedSymptoms);
            data.put("date", now);
            data.put("userId", userId);

            db.collection("Symptoms").add(data)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Symptoms Recorded.", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Intent intent = new Intent(TrackSymptoms.this, TrackSymptomsGraph.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Symptoms Not Recorded.", Toast.LENGTH_SHORT).show();
                        Log.w("Firestore", "Error adding document", e);
                    });
        } else {
            Toast.makeText(this, "Symptoms Not Recorded.", Toast.LENGTH_SHORT).show();
            Log.w("Firestore", "No user logged in");
        }
    }

    private String getCurrentUser() {
        return currentUser.getUid();
    }
}

