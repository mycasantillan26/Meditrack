package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStarted extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView textViewError, createAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_getstarted);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewError = findViewById(R.id.textViewError);
        createAccount = findViewById(R.id.CreateAccount);

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Register activity
                startActivity(new Intent(GetStarted.this, Register.class));
            }
        });
    }

    private void loginUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            textViewError.setText("Empty username or password");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }

        if (username.equals("pharmacist@pharmacist.com") && password.equals("pharmacist")) {
            // Redirect to Pharmacist activity
            Intent intent = new Intent(GetStarted.this, Pharmacist.class);
            startActivity(intent);
            finish();
            return; // Exit the method
        }

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User successfully logged in
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Redirect to Today activity
                                Intent intent = new Intent(GetStarted.this, Today.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Login failed, display error message
                            String errorMessage = task.getException().getMessage();
                            Log.e("LoginActivity", "signInWithEmailAndPassword:failure", task.getException());
                            textViewError.setText(errorMessage);
                            textViewError.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}
