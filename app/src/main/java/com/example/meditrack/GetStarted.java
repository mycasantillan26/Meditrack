package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStarted extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);

        // Hide the title bar
        getSupportActionBar().hide();

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Find views
        EditText emailOrNumberEditText = findViewById(R.id.emailOrNumberEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        // Set OnClickListener for the "Create Account" button
        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Register activity when the button is clicked
                Intent intent = new Intent(GetStarted.this, Register.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the "Login" button
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered username and password
                String enteredUsername = emailOrNumberEditText.getText().toString().trim();
                String enteredPassword = passwordEditText.getText().toString().trim();

                // Check if the entered username and password are valid
                signInUser(enteredUsername, enteredPassword);
            }
        });

        // Set OnClickListener for the "Forgot Password" TextView
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password functionality here
                // For example, open a new activity to reset password
            }
        });
    }

    // Method to sign in the user
    private void signInUser(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, navigate to VerificationOTP activity
                            Intent intent = new Intent(GetStarted.this, VerificationOTP.class);
                            startActivity(intent);
                            finish(); // Finish the current activity to prevent going back to it
                        } else {
                            // Sign in failed, display an error message
                            Toast.makeText(GetStarted.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
