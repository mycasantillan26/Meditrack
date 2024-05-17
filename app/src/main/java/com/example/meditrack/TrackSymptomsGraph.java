package com.example.meditrack;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrackSymptomsGraph extends AppCompatActivity {

    private TextView weekgraph;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_symptoms_graph);
        weekgraph = findViewById(R.id.weekgraph);
        BarChart barChart = findViewById(R.id.barChart);
        fetchSymptomData(barChart);
        weekgraph.setText(getFormattedDateRange());
        testFetchSymptomData();

    }
    private String getFormattedDateRange() {
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);  // Start of the week (Sunday)
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_WEEK, 6);  // End of the week (Saturday)

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return "This week: " + dateFormat.format(start.getTime()) + " to " + dateFormat.format(end.getTime());
    }



    private void fetchSymptomData(BarChart barChart) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_WEEK, 6);

        db.collection("Symptoms")
                .whereEqualTo("userId", currentUser.getUid())
                .whereGreaterThanOrEqualTo("date", start.getTime())
                .whereLessThan("date", end.getTime())  // Make sure it's less than the end of the current week
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, Integer> symptomCounts = new HashMap<>();
                        task.getResult().forEach(document -> {
                            List<String> symptoms = (List<String>) document.get("SelectSymptoms");
                            for (String symptom : symptoms) {
                                symptomCounts.put(symptom, symptomCounts.getOrDefault(symptom, 0) + 1);
                            }
                        }); // Corrected the closure of the forEach lambda expression here

                        updateBarChart(symptomCounts, barChart);
                    } else {
                        // Handle failures
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }
    private void testFetchSymptomData() {
        db.collection("Symptoms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            System.out.println("User ID in firestore:" + document.getString("userId"));
                            System.out.println("User ID in app: "+ currentUser.getUid());
                        }
                    } else {
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }
    private void updateBarChart(Map<String, Integer> symptomCounts, BarChart barChart) {
        List<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        String[] symptoms = symptomCounts.keySet().toArray(new String[0]);
        Arrays.sort(symptoms, (a, b) -> symptomCounts.get(b) - symptomCounts.get(a));

        for (int i = 0; i < symptoms.length && i < 5; i++) {
            entries.add(new BarEntry(i, symptomCounts.get(symptoms[i])));
            labels.add(symptoms[i]);
            colors.add(getColorForSymptom(i));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size());
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        weekgraph.setText(getFormattedDateRange());
        weekgraph.setTextColor(Color.WHITE);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.invalidate(); // Refresh the chart
    }


    private int getColorForSymptom(int index) {
        // Define a fixed array of colors for symptoms
        int[] colors = new int[]{Color.rgb(244, 67, 54), Color.rgb(156, 39, 176), Color.rgb(3, 169, 244), Color.rgb(0, 150, 136), Color.rgb(255, 235, 59)};
        return colors[index % colors.length];
    }

}
