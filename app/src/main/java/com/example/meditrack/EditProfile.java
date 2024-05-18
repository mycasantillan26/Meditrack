package com.example.meditrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix
    private Button logoutButton, backButton, saveButton;
    private ImageView editProfileButton;

    private TextView firstNameEditText, lastNameEditText, emailEditText, numberEditText, birthDateEditText;

    private DatabaseReference databaseReference;
    private User user;
    private String userId;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(currentUser == null) {
            startActivity(new Intent(EditProfile.this, GetStarted.class));
        }
            super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_editprofile);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
             userId = currentUser.getUid();

             databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Assuming you have these EditText fields initialized
                         firstNameEditText = findViewById(R.id.editTextText4);
                         lastNameEditText = findViewById(R.id.editTextText3);
                         emailEditText = findViewById(R.id.editTextText2);
                         numberEditText = findViewById(R.id.editTextText1);
                         birthDateEditText = findViewById(R.id.editTextText7);

                        firstNameEditText.setText(user.getFirstName());
                        lastNameEditText.setText(user.getLastName());
                        emailEditText.setText(user.getEmail());
                        numberEditText.setText(user.getNumber());
                        birthDateEditText.setText(user.getBirthDate());
                        // Add other fields as necessary
                    }
                    Button saveButton = findViewById(R.id.button2);
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the updated values from EditText fields
                            String updatedFirstName = firstNameEditText.getText().toString().trim();
                            String updatedLastName = lastNameEditText.getText().toString().trim();
                            String updatedEmail = emailEditText.getText().toString().trim();
                            String updatedNumber = numberEditText.getText().toString().trim();
                            String updatedBirthDate = birthDateEditText.getText().toString().trim();

                            // Create a new User object with the updated values
                            User updatedUser = new User(userId, updatedFirstName, updatedLastName, updatedEmail, updatedNumber, updatedBirthDate, user.getUsername(), user.getPassword(), user.getConfirmPassword());

                            // Update the user details in the database
                            databaseReference.setValue(updatedUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(EditProfile.this, "Changes saved", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(EditProfile.this, Profile.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(EditProfile.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }

            });
        } else {
            // Handle the case where there is no current user
        }
        // Initialize the "Save" button


// Initialize the "Back" button
        Button backButton = findViewById(R.id.button3);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Profile activity
                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
            }
        });

    }
}
