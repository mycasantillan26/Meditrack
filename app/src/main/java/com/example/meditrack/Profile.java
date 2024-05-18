package com.example.meditrack;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Profile extends AppCompatActivity {
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix
    private Spinner alarmToneSpinner, languageSpinner;

    private MediaPlayer mediaPlayer;

    private Switch notificationSwitch;
    private Button logoutButton, backButton, saveButton, wakeUpTimeButton, bedTimeButton;
    private ImageView editProfileButton;
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
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
        notificationSwitch = findViewById(R.id.switch1);
        alarmToneSpinner = findViewById(R.id.dropdown_button2);
        wakeUpTimeButton = findViewById(R.id.wakeUpTimeButton);
        languageSpinner = findViewById(R.id.dropdown_button5);
        bedTimeButton = findViewById(R.id.bedTimeButton);




        // Get the AudioManager service
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

// Get the switch view
        Switch notificationSwitch = findViewById(R.id.switch1);

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The switch is enabled/checked, enable notifications
                    // Set the ringer mode to normal
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(
                                android.provider.Settings
                                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                        startActivity(intent);
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                } else {
                    // The switch is disabled/unchecked, disable notifications
                    // Set the ringer mode to silent
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(
                                android.provider.Settings
                                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                        startActivity(intent);
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            }
        });


        // Set up listeners for other spinners similarly
        alarmToneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean userSelected = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!userSelected) {
                    userSelected = true;
                    return;
                }

                String selectedAlarmTone = parent.getItemAtPosition(position).toString();

                // Stop the currently playing alarm tone (if any)
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                // Start the selected alarm tone
                int alarmToneResId = getAlarmToneResId(selectedAlarmTone);
                if (alarmToneResId != 0) {
                    mediaPlayer = MediaPlayer.create(Profile.this, alarmToneResId);
                    mediaPlayer.start();
                }
            }

            private int getAlarmToneResId(String alarmToneName) {
                switch (alarmToneName) {
                    case "Wake up!":
                        return R.raw.wake_up;
                    case "Alarm":
                        return R.raw.alarm;
                    case "Warning":
                        return R.raw.warning;
                    case "Banana":
                        return R.raw.banana_song;
                    case "Rooster":
                        return R.raw.rooster_alarm;
                    case "Bird Chirping":
                        return R.raw.bird_chirping;

                    default:
                        return 0;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (if necessary)
            }

        });
        Button wakeUpTimeButton = findViewById(R.id.wakeUpTimeButton);

        wakeUpTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(Profile.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // This gets called when the user sets the time in the dialog

                                // Format the time and set it as the text of the button
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                wakeUpTimeButton.setText(selectedTime);
                            }
                        }, hour, minute, false); // Use 24 hour format

                // Show the dialog
                timePickerDialog.show();
            }
        });

        Button bedTimeButton = findViewById(R.id.bedTimeButton);

        bedTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(Profile.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // This gets called when the user sets the time in the dialog

                                // Format the time and set it as the text of the button
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                bedTimeButton.setText(selectedTime);

                                // Save the selected time in SharedPreferences
                                SharedPreferences.Editor editor = getSharedPreferences("MySettings", MODE_PRIVATE).edit();
                                editor.putString("bedTime", selectedTime);
                                editor.apply();
                            }
                        }, hour, minute, false); // Use 24 hour format

                // Show the dialog
                timePickerDialog.show();
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
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
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
                // Close the current activity
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save settings

                Intent intent = new Intent(Profile.this, Landingpage.class);
                saveSettings();

                // Display a toast message
                Toast.makeText(Profile.this, "Changes saved", Toast.LENGTH_SHORT).show();


            }
        });
        SharedPreferences prefs = getSharedPreferences("MySettings", MODE_PRIVATE);
        String notificationSetting = prefs.getString("notification", "default");
        String alarmToneSetting = prefs.getString("alarmTone", "default");
        String wakeUpTimeSetting = prefs.getString("wakeUpTime", "default");
        String languageSetting = prefs.getString("language", "default");
        String bedTimeSetting = prefs.getString("bedTime", "default");

        // Set the selected items of your spinners and the checked state of your switch
        notificationSwitch.setChecked(Boolean.parseBoolean(notificationSetting));
        alarmToneSpinner.setSelection(getIndex(alarmToneSpinner, alarmToneSetting));
        wakeUpTimeButton.setText(wakeUpTimeSetting);
        languageSpinner.setSelection(getIndex(languageSpinner, languageSetting));
        bedTimeButton.setText(bedTimeSetting);

        // ... existing code ...egtfrefdsa
    }

    // Helper method to get the index of a specified item in a spinner
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private void saveSettings() {
        // Retrieve selected values from spinners
        String notificationSetting = String.valueOf(notificationSwitch.isChecked());
        String alarmToneSetting = alarmToneSpinner.getSelectedItem().toString();
        String wakeUpTimeSetting = wakeUpTimeButton.getText().toString();
        String languageSetting = languageSpinner.getSelectedItem().toString();
        String bedTimeSetting = bedTimeButton.getText().toString();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Create an Intent to the AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarmTone", alarmToneSetting); // Pass the alarm tone to the receiver

        // Create a PendingIntent from the intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags);

        String selectedTime = bedTimeButton.getText().toString(); // Get the selected time from the spinner

        // Create a SimpleDateFormat instance with the time format
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mma", Locale.ENGLISH);

        try {
            // Parse the selected time into a Date object
            Date date = sdf.parse(selectedTime);

            // Create a Calendar instance from the Date object
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Get the hour and minute from the Calendar instance
            int hour = calendar.get(Calendar.HOUR_OF_DAY);


            // Set the alarm
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Save settings to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("MySettings", MODE_PRIVATE).edit();
        editor.putString("notification", notificationSetting);
        editor.putString("alarmTone", alarmToneSetting);
        editor.putString("wakeUpTime", wakeUpTimeSetting);
        editor.putString("language", languageSetting);
        editor.putString("bedTime", bedTimeSetting);
        editor.apply();

//        private void showNotification(String title, String message) {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelId")
//                    .setSmallIcon(R.drawable.notification) // replace with your own icon
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//            // notificationId is a unique int for each notification that you must define
//            notificationManager.notify(notificationId, builder.build());
//        }
    }


}
