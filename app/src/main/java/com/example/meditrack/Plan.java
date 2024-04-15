package com.example.meditrack;

import com.google.firebase.firestore.FirebaseFirestore;

public class Plan {
    private String name;
    private String unit;
    private int dosage;
    private String startDate;
    private String endDate;

    // Default constructor required for Firestore
    public Plan() {
    }

    // Constructor to initialize the fields
    public Plan(String name, String unit, int dosage, String startDate, String endDate) {
        this.name = name;
        this.unit = unit;
        this.dosage = dosage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters for the fields
    // You can generate these automatically in Android Studio

    // Method to add a new plan to Firestore
    public static void addPlanToFirestore(Plan plan) {
        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document with a generated ID to the "plan" collection
        db.collection("plan")
                .add(plan)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    String planId = documentReference.getId();
                    System.out.println("Plan added with ID: " + planId);
                })
                .addOnFailureListener(e -> {
                    // Error adding document
                    System.err.println("Error adding plan: " + e);
                });
    }
}
