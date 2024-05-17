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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    EditText birthDateEditText;
    EditText firstNameEditText, lastNameEditText, emailEditText, numberEditText;
    EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the title bar and set the activity to fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register); // Moved setContentView() here after requestFeature()

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

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

                // Create user account using Firebase Authentication
                registerUser(email, password);
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

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Registration success, get user ID
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // Save user details to Realtime Database
                        saveUserDetailsToDatabase(userId);

                        // Send OTP to email or phone (let's use email for this example)
                        sendEmailVerification(firebaseUser);

                        // Navigate to the Verify activity
                        navigateToGetStartedactivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Registration failed, display an error message
                        Toast.makeText(Register.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Registration failed, display an error message
                Toast.makeText(Register.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOnFailureListener(OnFailureListener onFailureListener) {
    }


    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnSuccessListener(aVoid -> Toast.makeText(Register.this, "Verification email sent.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Register.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void sendOtpToEmail(String email) {
        // This would be an API call to your backend
        // For demonstration, replace this with your actual API call logic
        String url = "https://yourbackend.example.com/send-otp?email=" + email;
        // Make the network request to your server to send the OTP
        // Use Volley, Retrofit, or any other HTTP library
    }

    private void verifyOtp(String otp, String email) {
        // After the user receives the OTP via email and inputs it in the app
        String url = "https://yourbackend.example.com/verify-otp?email=" + email + "&otp=" + otp;
        // Make the network request to verify the OTP
    }


    private void navigateToGetStartedactivity() {
        Intent intent = new Intent(Register.this, GetStarted.class);
        startActivity(intent);
    }


    private void saveUserDetailsToDatabase(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Create a new User object
        User user = new User(userId,
                firstNameEditText.getText().toString().trim(),
                lastNameEditText.getText().toString().trim(),
                emailEditText.getText().toString().trim(),
                numberEditText.getText().toString().trim(),
                birthDateEditText.getText().toString().trim(),
                usernameEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim(),
                confirmPasswordEditText.getText().toString().trim());

        // Store user details in database
        databaseReference.child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully
                    Toast.makeText(Register.this, "User details saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to save data
                    Toast.makeText(Register.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}