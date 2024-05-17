package com.example.meditrack;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix
    private Spinner notificationSpinner, alarmToneSpinner, wakeUpTimeSpinner, languageSpinner, bedTimeSpinner;
    private Button logoutButton, backButton, saveButton;
    private ImageView editProfileButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(currentUser == null) {
            startActivity(new Intent(Profile.this, GetStarted.class));
        }
            super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_settings);

        // Initialize views
        editProfileButton = findViewById(R.id.imageView4);
        logoutButton = findViewById(R.id.button4);
        backButton = findViewById(R.id.button3);
        saveButton = findViewById(R.id.button2);
        notificationSpinner = findViewById(R.id.dropdown_button1);
        alarmToneSpinner = findViewById(R.id.dropdown_button2);
        wakeUpTimeSpinner = findViewById(R.id.dropdown_button3);
        languageSpinner = findViewById(R.id.dropdown_button4);
        bedTimeSpinner = findViewById(R.id.dropdown_button5);


        notificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedNotification = parent.getItemAtPosition(position).toString();

                // Get the AudioManager and Vibrator services
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


                switch (selectedNotification) {
                    case "Vibrate only":
                        // Check if the device has a vibrator
                        if (vibrator.hasVibrator()) {
                            // Vibrate for 500 milliseconds
                            vibrator.vibrate(500);
                        }
                        // Turn off sounds
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        break;
                    case "Sounds only":
                        // Turn off vibration
                        if (vibrator.hasVibrator()) {
                            vibrator.cancel();
                        }
                        // Turn on sounds
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        break;
                    case "Vibrate and Sounds":
                        // Turn on vibration
                        if (vibrator.hasVibrator()) {
                            vibrator.vibrate(500);
                        }
                        // Turn on sounds
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        break;
                    case "None":
                        // Turn off vibration
                        if (vibrator.hasVibrator()) {
                            vibrator.cancel();
                        }
                        // Set to default mode
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        break;
                }

                Toast.makeText(Profile.this, "Selected notification: " + selectedNotification, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (if necessary)
                String defaultNotification = "No notification selected";
                Toast.makeText(Profile.this, defaultNotification, Toast.LENGTH_SHORT).show();
            }
        });


        // Set up listeners for other spinners similarly
        alarmToneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAlarmTone = parent.getItemAtPosition(position).toString();
                // Handle alarm tone spinner selection
                // For example, you can display a toast message with the selected alarm tone
                Toast.makeText(Profile.this, "Selected alarm tone: " + selectedAlarmTone, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (if necessary)
                // For example, you can display a default alarm tone option
                String defaultAlarmTone = "No alarm tone selected";
                Toast.makeText(Profile.this, defaultAlarmTone, Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listeners for buttons
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for editProfileButton
                // Start the EditProfile activity
                startActivity(new Intent(Profile.this, EditProfile.class));
            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog.Builder instance
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");

                // Set up the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the logout process here
                        // For example, if you're using Firebase Authentication, you can call FirebaseAuth.getInstance().signOut();
                        FirebaseAuth.getInstance().signOut();
                        currentUser=null;
                        // Then redirect the user to the login activity
                        Intent intent = new Intent(Profile.this, GetStarted.class);
                        startActivity(intent);
                        finish(); // Close the current activity
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // Dismiss the dialog
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save settings and close the activity
                saveSettings();
                finish();
            }
        });
    }

    private void saveSettings() {
        // Retrieve selected values from spinners
        String notificationSetting = notificationSpinner.getSelectedItem().toString();
        String alarmToneSetting = alarmToneSpinner.getSelectedItem().toString();
        String wakeUpTimeSetting = wakeUpTimeSpinner.getSelectedItem().toString();
        String languageSetting = languageSpinner.getSelectedItem().toString();
        String bedTimeSetting = bedTimeSpinner.getSelectedItem().toString();

        // Save settings to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("MySettings", MODE_PRIVATE).edit();
        editor.putString("notification", notificationSetting);
        editor.putString("alarmTone", alarmToneSetting);
        editor.putString("wakeUpTime", wakeUpTimeSetting);
        editor.putString("language", languageSetting);
        editor.putString("bedTime", bedTimeSetting);
        editor.apply();
    }


}
