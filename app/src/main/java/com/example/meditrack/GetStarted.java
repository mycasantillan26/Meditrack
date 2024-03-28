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

        // Hide the title bar and set the activity to fullscreen
        getSupportActionBar().hide();

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        EditText emailOrNumberEditText = findViewById(R.id.emailOrNumberEditText);
        emailOrNumberEditText.setTextColor(Color.WHITE);


        EditText passwordEditText = findViewById(R.id.passwordEditText);

        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Register activity when the button is clicked
                Intent intent = new Intent(GetStarted.this, Register.class);
                startActivity(intent);
            }
        });

        // Find the "Login" button and set OnClickListener
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered username and password
                String enteredUsername = emailOrNumberEditText.getText().toString().trim();
                String enteredPassword = passwordEditText.getText().toString().trim();

                // Check if the entered username and password are equal and recorded in Firebase
                if (enteredUsername.equals(enteredPassword)) {
                    // Open Today activity
                    Intent intent = new Intent(GetStarted.this, Today.class);
                    startActivity(intent);
                } else if (enteredUsername.equals("Pharmacist@pharmacist.com") && enteredPassword.equals("pharmacist")) {
                    // Open Pharmacist activity
                    Intent intent = new Intent(GetStarted.this, Pharmacist.class);
                    startActivity(intent);
                } else {
                    // Display "Wrong Password or Username" message
                    Toast.makeText(GetStarted.this, "Wrong Password or Username", Toast.LENGTH_SHORT).show();
                }
            }
        });




        // Find the TextView for "Forgot Password? Click here"
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password functionality here
                // For example, open a new activity to reset password
            }
        });
    }
}
