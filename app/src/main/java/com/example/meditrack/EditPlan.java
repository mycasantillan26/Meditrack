package com.example.meditrack;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.google.firebase.Timestamp;
import android.util.Log;
import android.widget.AdapterView;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.view.Gravity;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.text.ParseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;







public class EditPlan extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<String> selectedDays = new ArrayList<>();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix


    private DocumentReference planRef;
    private EditText nameEditText, dosageEditText, commentEditText, timeEditText, timeEditText2, timeEditText3, timeEditText4, timeEditText5;
    private EditText startDateSpinner, endDateSpinner;
    private Spinner unitsDropdown, inTimeSpinner;
    private Button updateButton;
    private int counter = 0;
    private int hourNumber = 0;
    private TextView hourNumberDisplay;

    private void setupTimePickers() {
        setupTimePicker(timeEditText);
        setupTimePicker(timeEditText2);
        setupTimePicker(timeEditText3);
        setupTimePicker(timeEditText4);
        setupTimePicker(timeEditText5);
    }

    private void setupTimePicker(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(true);
        editText.setCursorVisible(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time to the EditText
                        String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                        String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                        editText.setText(time);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_editplan);

        initializeViews();
        setupSpinnerListeners();
        hourNumberDisplay = findViewById(R.id.hourNumber);



        String planId = getIntent().getStringExtra("PLAN_ID");
        if (planId == null) {
            Toast.makeText(this, "Plan ID is missing", Toast.LENGTH_LONG).show();
            finish();
        } else {
            planRef = db.collection("plans").document(planId);
            fetchPlanDetails();
            updateButton.setOnClickListener(v -> updatePlanDetails());
        }
    }

    private void updateHourNumberDisplay() {
        if (hourNumberDisplay != null) {
            hourNumberDisplay.setText(String.format(Locale.getDefault(), "%d", hourNumber));
        }
    }



    private void recordDay(String day) {
        // Here you can implement whatever you need to do when a day is recorded.
        // For example, you could update some data structure or view.
        Log.d("EditPlan", "Day recorded: " + day);
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        dosageEditText = findViewById(R.id.dosageEditText);
        commentEditText = findViewById(R.id.commentEditText);
        timeEditText = findViewById(R.id.timeEditText);
        timeEditText2 = findViewById(R.id.timeEditText2);
        timeEditText3 = findViewById(R.id.timeEditText3);
        timeEditText4 = findViewById(R.id.timeEditText4);
        timeEditText5 = findViewById(R.id.timeEditText5);
        startDateSpinner = findViewById(R.id.startDateSpinner);
        endDateSpinner = findViewById(R.id.endDateSpinner);
        unitsDropdown = findViewById(R.id.unitsDropdown);
        inTimeSpinner = findViewById(R.id.inTimeSpinner);
        updateButton = findViewById(R.id.updateButton);
        toggleDaySelection((Button) findViewById(R.id.mondayButton), "Monday");
        toggleDaySelection((Button) findViewById(R.id.tuesdayButton), "Tuesday");
        toggleDaySelection((Button) findViewById(R.id.wednesdayButton), "Wednesday");
        toggleDaySelection((Button) findViewById(R.id.thursdayButton), "Thursday");
        toggleDaySelection((Button) findViewById(R.id.fridayButton), "Friday");
        toggleDaySelection((Button) findViewById(R.id.saturdayButton), "Saturday");
        toggleDaySelection((Button) findViewById(R.id.sundayButton), "Sunday");

        updateButton.setOnClickListener(v -> updatePlanDetails());

        // Set up the date pickers
        setupDatePicker(startDateSpinner);
        setupDatePicker(endDateSpinner);
    }
    private void setupDatePicker(EditText editText) {
        editText.setFocusable(false);  // Disable keyboard input
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editText);
            }
        });
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditPlan.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Format the date and set it to the EditText
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        editText.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void setupSpinnerListeners() {
        inTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("In time")) {
                    // Hide additional layout elements
                    findViewById(R.id.counterTextView2).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText2).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton2).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView3).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText3).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton3).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView4).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText4).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton4).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView5).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText5).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton5).setVisibility(View.GONE);
                    findViewById(R.id.hourNumber).setVisibility(View.GONE);
                    findViewById(R.id.addButton).setVisibility(View.GONE);
                    findViewById(R.id.subButton).setVisibility(View.GONE);
                    findViewById(R.id.mondayButton).setVisibility(View.GONE);
                    findViewById(R.id.tuesdayButton).setVisibility(View.GONE);
                    findViewById(R.id.wednesdayButton).setVisibility(View.GONE);
                    findViewById(R.id.thursdayButton).setVisibility(View.GONE);
                    findViewById(R.id.fridayButton).setVisibility(View.GONE);
                    findViewById(R.id.saturdayButton).setVisibility(View.GONE);
                    findViewById(R.id.sundayButton).setVisibility(View.GONE);
                    findViewById(R.id.addButton).setVisibility(View.GONE);

                    findViewById(R.id.counterTextView).setVisibility(View.VISIBLE);
                    findViewById(R.id.timeEditText).setVisibility(View.VISIBLE);
                    findViewById(R.id.commentEditText).setVisibility(View.VISIBLE); // Assuming you have a commentEditText
                    findViewById(R.id.addTimeButton).setVisibility(View.VISIBLE);

                    // Make timeEditText non-editable and non-clickable
                    timeEditText.setFocusable(false);
                    timeEditText.setClickable(true);

                    TextView counterTextView = findViewById(R.id.counterTextView);
                    counterTextView.setText("1");
                    EditText timeEditText = findViewById(R.id.timeEditText);
                    timeEditText.setText("Select Time");
                    Button addTimeButton = findViewById(R.id.addTimeButton);
                    addTimeButton.setText("+");

                    timeEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create a TimePickerDialog to select the time
                            Calendar calendar = Calendar.getInstance();
                            int hour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour
                            int minute = calendar.get(Calendar.MINUTE); // Get the current minute
                            TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            // Set the selected time to the timeEditText2
                                            String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                                            String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                                            timeEditText.setText(time);
                                            // Set gravity to center
                                            timeEditText.setGravity(Gravity.CENTER);
                                        }
                                    }, hour, minute, false);
                            timePickerDialog.show();
                        }
                    });
                    timeEditText.setFocusableInTouchMode(false);

                } else if (selectedItem.equals("Morning") || selectedItem.equals("Before Sleep")) {
                    // Hide additional layout elements
                    findViewById(R.id.counterTextView).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView2).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText2).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton2).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView3).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText3).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton3).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView4).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText4).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton4).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView5).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText5).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton5).setVisibility(View.GONE);
                    findViewById(R.id.hourNumber).setVisibility(View.GONE);
                    findViewById(R.id.addButton).setVisibility(View.GONE);
                    findViewById(R.id.subButton).setVisibility(View.GONE);
                    findViewById(R.id.mondayButton).setVisibility(View.GONE);
                    findViewById(R.id.tuesdayButton).setVisibility(View.GONE);
                    findViewById(R.id.wednesdayButton).setVisibility(View.GONE);
                    findViewById(R.id.thursdayButton).setVisibility(View.GONE);
                    findViewById(R.id.fridayButton).setVisibility(View.GONE);
                    findViewById(R.id.saturdayButton).setVisibility(View.GONE);
                    findViewById(R.id.sundayButton).setVisibility(View.GONE);

                    findViewById(R.id.timeEditText).setVisibility(View.VISIBLE);


                    // Set the text of timeEditText to "8:00 AM" for Morning and "8:00 PM" for Before Sleep
                    EditText timeEditText = findViewById(R.id.timeEditText);
                    if (selectedItem.equals("Morning")) {
                        timeEditText.setText("8:00 AM");
                    } else {
                        timeEditText.setText("8:00 PM");
                    }

                    // Make timeEditText non-editable and non-clickable
                    timeEditText.setFocusableInTouchMode(false);
                    timeEditText.setFocusable(false);

                    timeEditText.setOnClickListener(null);
                } else if (selectedItem.equals("Every X hours")) {
                    findViewById(R.id.counterTextView).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView2).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText2).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton2).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView3).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText3).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton3).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView4).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText4).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton4).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView5).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText5).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton5).setVisibility(View.GONE);
                    findViewById(R.id.mondayButton).setVisibility(View.GONE);
                    findViewById(R.id.tuesdayButton).setVisibility(View.GONE);
                    findViewById(R.id.wednesdayButton).setVisibility(View.GONE);
                    findViewById(R.id.thursdayButton).setVisibility(View.GONE);
                    findViewById(R.id.fridayButton).setVisibility(View.GONE);
                    findViewById(R.id.saturdayButton).setVisibility(View.GONE);
                    findViewById(R.id.sundayButton).setVisibility(View.GONE);
                    findViewById(R.id.hourNumber).setEnabled(false);


                    // Show hourNumber, addButton, and subButton
                    findViewById(R.id.hourNumber).setVisibility(View.VISIBLE);
                    findViewById(R.id.addButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.subButton).setVisibility(View.VISIBLE);

                } else if (selectedItem.equals("Specific day of the week")) {
                    // Hide additional layout elements

                    findViewById(R.id.hourNumber).setVisibility(View.GONE);
                    findViewById(R.id.addButton).setVisibility(View.GONE);
                    findViewById(R.id.subButton).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView2).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText2).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton2).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView3).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText3).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton3).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView4).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText4).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton4).setVisibility(View.GONE);
                    findViewById(R.id.counterTextView5).setVisibility(View.GONE);
                    findViewById(R.id.timeEditText5).setVisibility(View.GONE);
                    findViewById(R.id.addTimeButton5).setVisibility(View.GONE);



                    // Make timeEditText non-editable
                    findViewById(R.id.timeEditText).setVisibility(View.VISIBLE);
                    findViewById(R.id.counterTextView).setVisibility(View.VISIBLE);
                    findViewById(R.id.addTimeButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.mondayButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.tuesdayButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.wednesdayButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.thursdayButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.fridayButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.saturdayButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.sundayButton).setVisibility(View.VISIBLE);

                    timeEditText.setFocusable(false);
                    timeEditText.setClickable(true);
                    showAdditionalTimeFieldsBasedOnData();

                    timeEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create a TimePickerDialog to select the time
                            Calendar calendar = Calendar.getInstance();
                            int hour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour
                            int minute = calendar.get(Calendar.MINUTE); // Get the current minute
                            TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            // Set the selected time to the timeEditText2
                                            String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                                            String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                                            timeEditText.setText(time);
                                            // Set gravity to center
                                            timeEditText.setGravity(Gravity.CENTER);
                                        }
                                    }, hour, minute, false);
                            timePickerDialog.show();
                        }
                    });
                    timeEditText.setFocusableInTouchMode(false);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.addTimeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;

                switch (counter) {
                    case 2:
                        findViewById(R.id.counterTextView2).setVisibility(View.VISIBLE);
                        findViewById(R.id.timeEditText2).setVisibility(View.VISIBLE);
                        findViewById(R.id.addTimeButton2).setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        findViewById(R.id.counterTextView3).setVisibility(View.VISIBLE);
                        findViewById(R.id.timeEditText3).setVisibility(View.VISIBLE);
                        findViewById(R.id.addTimeButton3).setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        findViewById(R.id.counterTextView4).setVisibility(View.VISIBLE);
                        findViewById(R.id.timeEditText4).setVisibility(View.VISIBLE);
                        findViewById(R.id.addTimeButton4).setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        findViewById(R.id.counterTextView5).setVisibility(View.VISIBLE);
                        findViewById(R.id.timeEditText5).setVisibility(View.VISIBLE);
                        findViewById(R.id.addTimeButton5).setVisibility(View.VISIBLE);
                        break;
                    default:
                        // Reset counter to 1 if it exceeds the expected limit
                        counter = 1;
                        // Re-show all additional layout elements
                        for (int i = 2; i <= 5; i++) {
                            findViewById(getResources().getIdentifier("counterTextView" + i, "id", getPackageName())).setVisibility(View.VISIBLE);
                            findViewById(getResources().getIdentifier("timeEditText" + i, "id", getPackageName())).setVisibility(View.VISIBLE);
                            findViewById(getResources().getIdentifier("addTimeButton" + i, "id", getPackageName())).setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });

        // Set OnClickListener for additional layout elements
        for (int i = 2; i <= 5; i++) {
            final int index = i;
            findViewById(getResources().getIdentifier("addTimeButton" + i, "id", getPackageName())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hide additional layout elements
                    findViewById(getResources().getIdentifier("counterTextView" + index, "id", getPackageName())).setVisibility(View.GONE);
                    findViewById(getResources().getIdentifier("timeEditText" + index, "id", getPackageName())).setVisibility(View.GONE);
                    findViewById(getResources().getIdentifier("addTimeButton" + index, "id", getPackageName())).setVisibility(View.GONE);
                }
            });
        }

        timeEditText.setFocusable(false);
        timeEditText.setClickable(true);
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time to the timeEditText2
                                String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                                String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                                timeEditText.setText(time);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
                timeEditText.setGravity(Gravity.CENTER);
            }
        });
        timeEditText.setFocusableInTouchMode(false);


        timeEditText2.setFocusable(false);
        timeEditText2.setClickable(true);
        timeEditText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a TimePickerDialog to select the time
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time to the timeEditText2
                                String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                                String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                                timeEditText2.setText(time);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
                timeEditText2.setGravity(Gravity.CENTER);
            }
        });
        timeEditText2.setFocusableInTouchMode(false);


        timeEditText3.setFocusable(false);
        timeEditText3.setClickable(true);
        timeEditText3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a TimePickerDialog to select the time
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time to the timeEditText2
                                String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                                String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                                timeEditText3.setText(time);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
                timeEditText3.setGravity(Gravity.CENTER);
            }
        });
        timeEditText3.setFocusableInTouchMode(false);


        timeEditText4.setFocusable(false);
        timeEditText4.setClickable(true);
        timeEditText4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a TimePickerDialog to select the time
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour
                int minute = calendar.get(Calendar.MINUTE); // Get the current minute
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time to the timeEditText2
                                String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                                String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                                timeEditText4.setText(time);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
                timeEditText4.setGravity(Gravity.CENTER);
            }
        });
        timeEditText4.setFocusableInTouchMode(false);


        timeEditText5.setFocusable(false);
        timeEditText5.setClickable(true);
        timeEditText5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a TimePickerDialog to select the time
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set the selected time to the timeEditText2
                                String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                                String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
                                timeEditText5.setText(time);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
                timeEditText5.setGravity(Gravity.CENTER);
            }
        });
        timeEditText5.setFocusableInTouchMode(false);


        findViewById(R.id.hourNumber).setVisibility(View.VISIBLE);
        findViewById(R.id.addButton).setVisibility(View.VISIBLE);
        findViewById(R.id.subButton).setVisibility(View.VISIBLE);
        updateHourNumberDisplay();

        // Add onClickListeners for the "Add" and "Subtract" buttons
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increment the hourNumber
                hourNumber++;
                // Update the UI
                updateHourNumberDisplay();
            }
        });

        findViewById(R.id.subButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure hourNumber does not go below 1
                if (hourNumber > 1) {
                    // Decrement the hourNumber
                    hourNumber--;
                    // Update the UI
                    updateHourNumberDisplay();
                }
            }
        });

        findViewById(R.id.mondayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDay("Monday");
                Button button = (Button) v;
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
            }
        });

        findViewById(R.id.tuesdayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDay("Tuesday");
                Button button = (Button) v;
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
            }
        });

        findViewById(R.id.wednesdayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDay("Wednesday");
                Button button = (Button) v;
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
            }
        });

        findViewById(R.id.thursdayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDay("Thursday");
                Button button = (Button) v;
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
            }
        });

        findViewById(R.id.fridayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDay("Friday");
                Button button = (Button) v;
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
            }
        });

        findViewById(R.id.saturdayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDay("Saturday");
                Button button = (Button) v;
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
            }
        });

        findViewById(R.id.sundayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDay("Sunday");
                Button button = (Button) v;
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
            }
        });
    }

    private void fetchPlanDetails() {
        planRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                if (data != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    nameEditText.setText((String) data.get("name"));
                    dosageEditText.setText(String.valueOf(data.get("dosage")));
                    commentEditText.setText((String) data.get("comment"));

                    updateTimeEditTextFromData(timeEditText, data, "time");
                    updateTimeEditTextFromData(timeEditText2, data, "time2");
                    updateTimeEditTextFromData(timeEditText3, data, "time3");
                    updateTimeEditTextFromData(timeEditText4, data, "time4");
                    updateTimeEditTextFromData(timeEditText5, data, "time5");

                    showAdditionalTimeFields(data);

                    Timestamp startDateTimestamp = (Timestamp) data.get("startDate");
                    Timestamp endDateTimestamp = (Timestamp) data.get("endDate");
                    if (startDateTimestamp != null && endDateTimestamp != null) {
                        startDateSpinner.setText(dateFormat.format(startDateTimestamp.toDate()));
                        endDateSpinner.setText(dateFormat.format(endDateTimestamp.toDate()));
                    }

                    setSpinnerSelection(inTimeSpinner, (String) data.get("Intake_method"));
                    updateDayButtons((List<String>) data.get("selectedDays"));

                    if (data.containsKey("nextOccurrences")) {
                        List<String> times = (List<String>) data.get("nextOccurrences");
                        if (times != null && !times.isEmpty()) {
                            calculateAndDisplayHourGap(times);
                        }
                    }
                } else {
                    Toast.makeText(EditPlan.this, "No data found for this plan.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditPlan.this, "Document does not exist.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditPlan.this, "Failed to fetch plan details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void calculateAndDisplayHourGap(List<String> times) {
        if (times.size() > 1) {
            hourNumber = calculateAverageInterval(times);
            updateHourNumberDisplay();
        }
    }

    private int calculateAverageInterval(List<String> times) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        long totalMinutes = 0;
        try {
            Date previousTime = sdf.parse(times.get(0));
            for (int i = 1; i < times.size(); i++) {
                Date currentTime = sdf.parse(times.get(i));
                if (currentTime.before(previousTime)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentTime);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    currentTime = cal.getTime();
                }
                totalMinutes += TimeUnit.MILLISECONDS.toMinutes(currentTime.getTime() - previousTime.getTime());
                previousTime = currentTime;
            }
            return (int) TimeUnit.MINUTES.toHours(totalMinutes / (times.size() - 1));
        } catch (ParseException e) {
            Log.e("EditPlan", "Error calculating average interval", e);
            return 0;
        }
    }


    private void showAdditionalTimeFields(Map<String, Object> data) {
        if (data.containsKey("time2") && !((String) data.get("time2")).isEmpty()) {
            findViewById(R.id.counterTextView2).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText2).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton2).setVisibility(View.VISIBLE);
        }
        if (data.containsKey("time3") && !((String) data.get("time3")).isEmpty()) {
            findViewById(R.id.counterTextView3).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText3).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton3).setVisibility(View.VISIBLE);
        }
        if (data.containsKey("time4") && !((String) data.get("time4")).isEmpty()) {
            findViewById(R.id.counterTextView4).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText4).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton4).setVisibility(View.VISIBLE);
        }
        if (data.containsKey("time5") && !((String) data.get("time5")).isEmpty()) {
            findViewById(R.id.counterTextView5).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText5).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton5).setVisibility(View.VISIBLE);
        }
    }

    private void updateDayButtons(List<String> selectedDays) {
        if (selectedDays != null) {
            if (selectedDays.contains("Monday")) setDayButtonSelected((Button) findViewById(R.id.mondayButton));
            if (selectedDays.contains("Tuesday")) setDayButtonSelected((Button) findViewById(R.id.tuesdayButton));
            if (selectedDays.contains("Wednesday")) setDayButtonSelected((Button) findViewById(R.id.wednesdayButton));
            if (selectedDays.contains("Thursday")) setDayButtonSelected((Button) findViewById(R.id.thursdayButton));
            if (selectedDays.contains("Friday")) setDayButtonSelected((Button) findViewById(R.id.fridayButton));
            if (selectedDays.contains("Saturday")) setDayButtonSelected((Button) findViewById(R.id.saturdayButton));
            if (selectedDays.contains("Sunday")) setDayButtonSelected((Button) findViewById(R.id.sundayButton));
        }
    }

    private void setDayButtonSelected(Button button) {
        button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
    }


    private void showAdditionalTimeFieldsBasedOnData() {
        if (!timeEditText2.getText().toString().isEmpty()) {
            findViewById(R.id.counterTextView2).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText2).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton2).setVisibility(View.VISIBLE);
        }
        if (!timeEditText3.getText().toString().isEmpty()) {
            findViewById(R.id.counterTextView3).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText3).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton3).setVisibility(View.VISIBLE);
        }
        if (!timeEditText4.getText().toString().isEmpty()) {
            findViewById(R.id.counterTextView4).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText4).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton4).setVisibility(View.VISIBLE);
        }
        if (!timeEditText5.getText().toString().isEmpty()) {
            findViewById(R.id.counterTextView5).setVisibility(View.VISIBLE);
            findViewById(R.id.timeEditText5).setVisibility(View.VISIBLE);
            findViewById(R.id.addTimeButton5).setVisibility(View.VISIBLE);
        }
    }


    private void hideAllTimeFields() {
        // Hide all time fields and reset visibility
        findViewById(R.id.counterTextView2).setVisibility(View.GONE);
        findViewById(R.id.timeEditText2).setVisibility(View.GONE);
        findViewById(R.id.addTimeButton2).setVisibility(View.GONE);
        // Repeat for timeEditText3, timeEditText4, timeEditText5
    }


    private void updateTimeEditTextFromData(EditText editText, Map<String, Object> data, String key) {
        if (data.containsKey(key)) {
            editText.setText((String) data.get(key));
            editText.setGravity(Gravity.CENTER); // Set gravity to center for better alignment
        }
    }
    private int calculateHourInterval(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault()); // 24-hour format
        try {
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            if (end.before(start)) {
                // Adjust for the next day if the end time is earlier in the day than start time
                Calendar cal = Calendar.getInstance();
                cal.setTime(end);
                cal.add(Calendar.DATE, 1);
                end = cal.getTime();
            }
            long difference = end.getTime() - start.getTime();
            return (int) (difference / (1000 * 60 * 60)); // Convert milliseconds to hours
        } catch (ParseException e) {
            Log.e("EditPlan", "Error parsing time", e);
            return 0;
        }
    }



    private void setTimeEditText(EditText editText, Map<String, Object> data, String key) {
        if (data.containsKey(key)) {
            editText.setText((String) data.get(key));
        }
    }




    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position != -1) {
            spinner.setSelection(position);
        } else {
            Toast.makeText(this, "Value not found in spinner options", Toast.LENGTH_SHORT).show();
        }
    }


    private void updatePlanDetails() {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", nameEditText.getText().toString());
        updatedData.put("dosage", parseDoubleOrZero(dosageEditText.getText().toString()));
        updatedData.put("comment", commentEditText.getText().toString());
        updatedData.put("time", timeEditText.getText().toString());
        updatedData.put("unit", unitsDropdown.getSelectedItem().toString());
        updatedData.put("Intake_method", inTimeSpinner.getSelectedItem().toString());
        updatedData.put("selectedDays", selectedDays);


        // Convert startDate and endDate from String to Timestamp
        updatedData.put("startDate", convertStringToTimestamp(startDateSpinner.getText().toString()));
        updatedData.put("endDate", convertStringToTimestamp(endDateSpinner.getText().toString()));

        putIfNotEmpty(updatedData, "time2", timeEditText2.getText().toString());
        putIfNotEmpty(updatedData, "time3", timeEditText3.getText().toString());
        putIfNotEmpty(updatedData, "time4", timeEditText4.getText().toString());
        putIfNotEmpty(updatedData, "time5", timeEditText5.getText().toString());

        planRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditPlan.this, "Plan updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditPlan.this, "Error updating plan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("EditPlan", "Update failed: " + e.getMessage());
                });
    }


    private Timestamp convertStringToTimestamp(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = dateFormat.parse(dateString);
            return new Timestamp(parsedDate);
        } catch (ParseException e) {
            Log.e("EditPlan", "Failed to parse date: " + dateString, e);
            return null;
        }
    }



    private void putIfNotEmpty(Map<String, Object> map, String key, String value) {
        if (!value.trim().isEmpty()) {
            map.put(key, value);
        }
    }



    private double parseDoubleOrZero(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    private void toggleDaySelection(Button button, String day) {
        List<String> selectedDays = new ArrayList<>();  // This should be an instance variable if you need to access it in updatePlanDetails
        button.setOnClickListener(v -> {
            if (!selectedDays.contains(day)) {
                selectedDays.add(day);
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color)); // Selected state
            } else {
                selectedDays.remove(day);
                button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color)); // Default state
            }
        });
    }

}