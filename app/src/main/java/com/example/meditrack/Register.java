package com.example.meditrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    EditText birthDateEditText;
    EditText firstNameEditText, lastNameEditText, emailEditText, numberEditText;
    EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the title bar and set the activity to fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register); // Moved setContentView() here after requestFeature()

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize EditText fields
        birthDateEditText = findViewById(R.id.birthDateEditText);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        emailEditText = findViewById(R.id.email);
        numberEditText = findViewById(R.id.number);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);

        // Initialize Button
        createAccountButton = findViewById(R.id.createAccountButton);

        // Set OnClickListener for the Create Account Button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from EditText fields
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String number = numberEditText.getText().toString().trim();
                String birthDate = birthDateEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Check for empty fields
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || number.isEmpty() ||
                        birthDate.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if password and confirm password match
                if (!password.equals(confirmPassword)) {
                    // Passwords do not match, display a message
                    Toast.makeText(Register.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    return; // Exit the method
                }

                // Save data to Firebase
                saveUserData(firstName, lastName, email, number, birthDate, username, password, confirmPassword);
            }
        });

        // Find the TextView for "Already have an Account? Click here"
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        // Set OnClickListener to open the GetStarted activity
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the GetStarted activity
                Intent intent = new Intent(Register.this, GetStarted.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the birth date EditText
        birthDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        birthDateEditText.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void saveUserData(String firstName, String lastName, String email, String number, String birthDate,
                              String username, String password, String confirmPassword) {
        // Generate a unique key for the user
        String userId = databaseReference.push().getKey();

        // Create a User object
        User user = new User(userId, firstName, lastName, email, number, birthDate, username, password, confirmPassword);

        // Save the user to Firebase
        databaseReference.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Display a success message
                        Toast.makeText(Register.this, "You successfully created an account", Toast.LENGTH_SHORT).show();

                        // Optionally, you can navigate to another activity after successful account creation
                        Intent intent = new Intent(Register.this, GetStarted.class);
                        startActivity(intent);
                        finish(); // Finish the current activity to prevent going back to it
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Display a failure message
                        Toast.makeText(Register.this, "Failed creating account", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}