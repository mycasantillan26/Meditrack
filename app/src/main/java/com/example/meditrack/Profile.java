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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private Spinner notificationSpinner, alarmToneSpinner, wakeUpTimeSpinner, languageSpinner, bedTimeSpinner;
    private Button logoutButton, backButton, saveButton;
    private ImageView editProfileButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        // Initialize views
        editProfileButton = findViewById(R.id.textView2);
        logoutButton = findViewById(R.id.button);
        backButton = findViewById(R.id.button3);
        saveButton = findViewById(R.id.button2);


        // Set up listeners for spinners
        notificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedNotification = parent.getItemAtPosition(position).toString();
                // Handle notification spinner selection
                // For example, you can display a toast message with the selected notification
                Toast.makeText(Profile.this, "Selected notification: " + selectedNotification, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (if necessary)
                // For example, you can display a default notification option
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
                finish(); // Close the current activity
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
