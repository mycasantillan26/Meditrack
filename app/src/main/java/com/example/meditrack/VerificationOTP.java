package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationOTP extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verification_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Method to handle clicking the "Verify" button
    public void verifyAndNavigateToGetStarted(View view) {
        // Implement your OTP verification logic here
        // For example, you can start phone number verification here
        String phoneNumber = "+91XXXXXXXXXX"; // Replace with the user's phone number
        startPhoneNumberVerification(phoneNumber);
    }

    // Method to start phone number verification
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60L, // Timeout duration
                TimeUnit.SECONDS,
                this,
                callbacks);
    }

    // Implement OnVerificationStateChangedCallbacks
    private void setupVerificationCallbacks() {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases, the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices, Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                // Here, you can handle these scenarios by calling signInWithPhoneAuthCredential()
                // with the provided credential, if verification is successful.

                // For simplicity, we'll navigate to the GetStarted activity directly
                Intent intent = new Intent(VerificationOTP.this, GetStarted.class);
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance, if the phone number format is not valid.

                // Handle verification failure, e.g., display an error message
            }

            // Other callback methods...
        };
    }

    // Method to handle clicking the "Back" button
    public void backToGetStarted(View view) {
        // Navigate back to the GetStarted activity
        Intent intent = new Intent(VerificationOTP.this, GetStarted.class);
        startActivity(intent);
    }

    // Override onStart to setup verification callbacks
    @Override
    protected void onStart() {
        super.onStart();
        setupVerificationCallbacks();
    }
}
