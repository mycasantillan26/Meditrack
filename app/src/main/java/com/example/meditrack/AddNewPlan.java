package com.example.meditrack;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.view.Gravity;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;
import android.util.Log;
import android.content.SharedPreferences;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.UUID; // This is usually needed for generating unique file names.
import java.text.ParseException; // For ParseException
import java.util.TimeZone;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.Button;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.text.TextUtils;
import java.util.List;
import java.util.ArrayList;
import android.widget.ImageButton;



public class AddNewPlan extends AppCompatActivity implements ImageChoiceDialogFragment.ImageChoiceListener {

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();
    //for one time login fix

    private ArrayAdapter<CharSequence> endDateAdapter;
    private TextView counterTextView;

    private Button addTimeButton;
    private Spinner unitSpinner, startDateSpinner, endDateSpinner;
    private EditText dosageEditText;
    private int counter = 1;
    private RelativeLayout parentLayout;
    private final boolean isAdditionalLayoutVisible = false;
    private int hourNumber = 1;
    private String imageUrl = null; // Class level variable to hold the image URL
    private Uri imageUri = null;
    private EditText nameEditText;
    private FirebaseFirestore db;
    private ImageView pillsIconImageView;


    private List<String> calculateNextOccurrences(int intervalHours) {
        List<String> nextOccurrences = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Get the current time
        Date currentTime = calendar.getTime();

        // Add the initial time
        nextOccurrences.add(formatTime(calendar.getTime()));

        // Calculate the next occurrences within a 24-hour period
        for (int i = 1; i < 24 / intervalHours; i++) {
            calendar.add(Calendar.HOUR_OF_DAY, intervalHours);
            nextOccurrences.add(formatTime(calendar.getTime()));
        }

        return nextOccurrences;
    }

    // Define formatTime method
    private String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    // Declare selectedDays as a class-level variable

    private final List<String> selectedDays = new ArrayList<>();

    // Update the recordDay method
    private void recordDay(String day) {
        // Check if the day is already selected, if not, add it to the list
        if (!selectedDays.contains(day)) {
            selectedDays.add(day);
        } else {
            // If already selected, remove it (optional, depending on your requirements)
            selectedDays.remove(day);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_addnewplan);


        Spinner unitSpinner = findViewById(R.id.unitsDropdown);
        Spinner startDateSpinner = findViewById(R.id.startDateSpinner);
        Spinner endDateSpinner = findViewById(R.id.endDateSpinner);
        EditText dosageEditText = findViewById(R.id.dosageEditText);
        EditText commentEditText = findViewById(R.id.commentEditText);
        Button addReminderButton = findViewById(R.id.addReminderButton);
        Spinner inTimeSpinner = findViewById(R.id.inTimeSpinner);
        addTimeButton = findViewById(R.id.addTimeButton);
        counterTextView = findViewById(R.id.counterTextView);
        EditText timeEditText = findViewById(R.id.timeEditText);
        parentLayout = findViewById(R.id.rootLayout);
        nameEditText = findViewById(R.id.nameEditText);
        pillsIconImageView = findViewById(R.id.pillsIcon);


        counterTextView.setEnabled(false);
        timeEditText.setEnabled(false);
        addTimeButton.setEnabled(false);
        commentEditText.setEnabled(false);
        addReminderButton.setEnabled(false);
        unitSpinner.setEnabled(false);
        startDateSpinner.setEnabled(false);
        endDateSpinner.setEnabled(false);
        dosageEditText.setEnabled(false);
        inTimeSpinner.setEnabled(false);

        ImageButton uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(v -> {
            ImageChoiceDialogFragment dialogFragment = new ImageChoiceDialogFragment();
            dialogFragment.setImageChoiceListener(this);  // Set the listener
            dialogFragment.show(getSupportFragmentManager(), "imagePicker");
        });


        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if nameEditText is empty
                if (s.toString().isEmpty()) {
                    // Disable views
                    counterTextView.setEnabled(false);
                    timeEditText.setEnabled(false);
                    addTimeButton.setEnabled(false);
                    commentEditText.setEnabled(false);
                    addReminderButton.setEnabled(false);
                    unitSpinner.setEnabled(false);
                    startDateSpinner.setEnabled(false);
                    endDateSpinner.setEnabled(false);
                    dosageEditText.setEnabled(false);
                    inTimeSpinner.setEnabled(false);
                } else {
                    // Enable views
                    counterTextView.setEnabled(true);
                    timeEditText.setEnabled(true);
                    addTimeButton.setEnabled(true);
                    commentEditText.setEnabled(true);
                    addReminderButton.setEnabled(true);
                    unitSpinner.setEnabled(true);
                    startDateSpinner.setEnabled(true);
                    endDateSpinner.setEnabled(true);
                    dosageEditText.setEnabled(true);
                    inTimeSpinner.setEnabled(true);
                }
            }
        });


        addTimeButton = findViewById(R.id.addTimeButton);
        counterTextView = findViewById(R.id.counterTextView);


        EditText timeEditText2 = findViewById(R.id.timeEditText2);
        timeEditText2.setFocusableInTouchMode(true);
        timeEditText2.setFocusable(true);

        EditText timeEditText3 = findViewById(R.id.timeEditText3);
        timeEditText3.setFocusableInTouchMode(true);
        timeEditText3.setFocusable(true);

        EditText timeEditText4 = findViewById(R.id.timeEditText4);
        timeEditText4.setFocusableInTouchMode(true);
        timeEditText4.setFocusable(true);

        EditText timeEditText5 = findViewById(R.id.timeEditText5);
        timeEditText5.setFocusableInTouchMode(true);
        timeEditText5.setFocusable(true);


        Button addTimeButton = findViewById(R.id.addTimeButton);
        TextView counterTextView = findViewById(R.id.counterTextView);
        findViewById(R.id.addTimeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increment the counter
                counter++;

                // Update the TextView to display the new counter value
                counterTextView.setText(String.valueOf(counter));

                // Show additional layout elements
                findViewById(R.id.counterTextView2).setVisibility(View.VISIBLE);
                findViewById(R.id.timeEditText2).setVisibility(View.VISIBLE);
                findViewById(R.id.addTimeButton2).setVisibility(View.VISIBLE);
                // Make timeEditText editable
                EditText timeEditText2 = findViewById(R.id.timeEditText2);
                timeEditText2.setFocusableInTouchMode(true);
                timeEditText2.setFocusable(true);

                // Create new instances of EditText and Button
                EditText newTimeEditText = new EditText(AddNewPlan.this);
                Button newAddTimeButton = new Button(AddNewPlan.this);

                // Set attributes for the new timeEditText
                newTimeEditText.setId(View.generateViewId());
                newTimeEditText.setLayoutParams(timeEditText.getLayoutParams());
                newTimeEditText.setHint("Select Time " + counter);
                newTimeEditText.setTextColor(Color.BLACK);

                // Set attributes for the new addTimeButton
                newAddTimeButton.setLayoutParams(addTimeButton.getLayoutParams());
                newAddTimeButton.setText("+");
                newAddTimeButton.setTextColor(Color.WHITE);
                newAddTimeButton.setBackgroundResource(R.drawable.circle_white_background);

                // Add the new EditText and Button to the parent layout
                ((ViewGroup) parentLayout).addView(newTimeEditText);
                ((ViewGroup) parentLayout).addView(newAddTimeButton);
            }
        });


        // Update the TextView to display the new counter value
        counterTextView.setText(String.valueOf(counter));

        // Create new instances of EditText and Button
        EditText newTimeEditText = new EditText(AddNewPlan.this);
        Button newAddTimeButton = new Button(AddNewPlan.this);

        // Set attributes for the new timeEditText
        newTimeEditText.setId(View.generateViewId());
        newTimeEditText.setLayoutParams(timeEditText.getLayoutParams());
        newTimeEditText.setHint("Select Time " + counter);
        newTimeEditText.setTextColor(Color.BLACK);

        // Set attributes for the new addTimeButton
        newAddTimeButton.setLayoutParams(addTimeButton.getLayoutParams());
        newAddTimeButton.setText("+");
        newAddTimeButton.setTextColor(Color.WHITE);
        newAddTimeButton.setBackgroundResource(R.drawable.circle_white_background);


        // Create adapter for unit spinner
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this,
                R.array.unit_array, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        // Create adapter for start date spinner
        ArrayAdapter<CharSequence> startDateAdapter = ArrayAdapter.createFromResource(this,
                R.array.start_date_array, android.R.layout.simple_spinner_item);
        startDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startDateSpinner.setAdapter(startDateAdapter);

        // Set "Start Date" as the default selection for the start date spinner
        startDateSpinner.setSelection(startDateAdapter.getPosition("Start Date"));

        startDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if (selectedItem.equals("Start Date")) {
                    // Set the spinner text to "Start Date"
                    ((TextView) view).setText("Start Date");
                } else if (selectedItem.equals("Today")) {
                    // Display the date for today in Philippines
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
                    String currentDate = sdf.format(calendar.getTime());
                    ((TextView) view).setText(currentDate); // Update spinner text
                    Toast.makeText(AddNewPlan.this, "Today is: " + currentDate, Toast.LENGTH_SHORT).show();
                } else if (selectedItem.equals("Tomorrow")) {
                    // Display the date for tomorrow in Philippines
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
                    calendar.add(Calendar.DATE, 1);
                    String tomorrowDate = sdf.format(calendar.getTime());
                    ((TextView) view).setText(tomorrowDate); // Update spinner text
                    Toast.makeText(AddNewPlan.this, "Tomorrow is: " + tomorrowDate, Toast.LENGTH_SHORT).show();
                } else if (selectedItem.equals("Select Date")) {
                    // Open a date picker dialog to select a specific date
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewPlan.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            // Display the selected date
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            Calendar selectedDateCalendar = Calendar.getInstance();
                            selectedDateCalendar.set(year, month, dayOfMonth);
                            String selectedDate = sdf.format(selectedDateCalendar.getTime());
                            // Update spinner text with the selected date
                            startDateSpinner.setSelection(startDateAdapter.getPosition("Select Date"));
                            ((TextView) startDateSpinner.getSelectedView()).setText(selectedDate);
                            Toast.makeText(AddNewPlan.this, "Selected date is: " + selectedDate, Toast.LENGTH_SHORT).show();
                        }
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        endDateAdapter = ArrayAdapter.createFromResource(this,
                R.array.end_date_array, android.R.layout.simple_spinner_item);
        endDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endDateSpinner.setAdapter(endDateAdapter);


        endDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String startDate = ((TextView) startDateSpinner.getSelectedView()).getText().toString();

                if (selectedItem.equals("In One Week")) {
                    // Calculate end date based on start date + 7 days
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    try {
                        Date start = sdf.parse(startDate);
                        calendar.setTime(start);
                        calendar.add(Calendar.DATE, 7);
                        String endDate = sdf.format(calendar.getTime());
                        ((TextView) view).setText(endDate); // Update spinner text
                        Toast.makeText(AddNewPlan.this, "End date is one week from the start date", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (selectedItem.equals("In One Month")) {
                    // Calculate end date based on start date + 1 month
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    try {
                        Date start = sdf.parse(startDate);
                        calendar.setTime(start);
                        calendar.add(Calendar.MONTH, 1);
                        String endDate = sdf.format(calendar.getTime());
                        ((TextView) view).setText(endDate); // Update spinner text
                        Toast.makeText(AddNewPlan.this, "End date is one month from the start date", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (selectedItem.equals("Constantly")) {
                    // Calculate end date based on start date + 1 year
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    try {
                        Date start = sdf.parse(startDate);
                        calendar.setTime(start);
                        calendar.add(Calendar.YEAR, 1);
                        String endDate = sdf.format(calendar.getTime());
                        ((TextView) view).setText(endDate); // Update spinner text
                        Toast.makeText(AddNewPlan.this, "End date is one year from the start date", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (selectedItem.equals("Select Date")) {
                    // Open a date picker dialog to select a specific date for the end date
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewPlan.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            // Display the selected date
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            Calendar selectedDateCalendar = Calendar.getInstance();
                            selectedDateCalendar.set(year, month, dayOfMonth);
                            String selectedDate = sdf.format(selectedDateCalendar.getTime());
                            // Update spinner text with the selected date
                            endDateSpinner.setSelection(endDateAdapter.getPosition("Select Date"));
                            ((TextView) endDateSpinner.getSelectedView()).setText(selectedDate);
                            Toast.makeText(AddNewPlan.this, "Selected end date is: " + selectedDate, Toast.LENGTH_SHORT).show();
                        }
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


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
                            TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewPlan.this,
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


                    // Show hourNumber, addButton, and subButton
                    findViewById(R.id.hourNumber).setVisibility(View.VISIBLE);
                    findViewById(R.id.addButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.subButton).setVisibility(View.VISIBLE);

                } else if (selectedItem.equals("Specific day of the week")) {
                    // Hide additional layout elements

                    findViewById(R.id.hourNumber).setVisibility(View.GONE);
                    findViewById(R.id.addButton).setVisibility(View.GONE);
                    findViewById(R.id.subButton).setVisibility(View.GONE);


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

                    EditText timeEditText = findViewById(R.id.timeEditText);
                    timeEditText.setText("Select Time");

                    timeEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create a TimePickerDialog to select the time
                            Calendar calendar = Calendar.getInstance();
                            int hour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour
                            int minute = calendar.get(Calendar.MINUTE); // Get the current minute
                            TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewPlan.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewPlan.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewPlan.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewPlan.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewPlan.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewPlan.this,
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
          toggleButtonState(v, "Monday");
        }
      });

      findViewById(R.id.tuesdayButton).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toggleButtonState(v, "Tuesday");
        }
      });

      findViewById(R.id.wednesdayButton).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toggleButtonState(v, "Wednesday");
        }
      });
      findViewById(R.id.thursdayButton).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toggleButtonState(v, "Thursday");
        }
      });
      findViewById(R.id.fridayButton).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toggleButtonState(v, "Friday");
        }
      });

      findViewById(R.id.saturdayButton).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toggleButtonState(v, "Saturday");
        }
      });
      findViewById(R.id.sundayButton).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toggleButtonState(v, "Sunday");
        }
      });



        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

      addReminderButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String name = nameEditText.getText().toString().trim();
          String unit = unitSpinner.getSelectedItem().toString();
          String dosageStr = dosageEditText.getText().toString().trim();
          String startDate = ((TextView) startDateSpinner.getSelectedView()).getText().toString();
          String endDate = ((TextView) endDateSpinner.getSelectedView()).getText().toString();
          String comment = commentEditText.getText().toString().trim();
          String intakeMethod = inTimeSpinner.getSelectedItem().toString();

          // Validate inputs
          List<String> errors = new ArrayList<>();
          if (name.isEmpty()) errors.add("Plan Name");
          if (unit.equals("Select Unit")) errors.add("Unit");
          if (dosageStr.isEmpty()) errors.add("Dosage");
          if (startDate.equals("Start Date")) errors.add("Start Date");
          if (endDate.equals("End Date")) errors.add("End Date");
          if (intakeMethod.equals("Select Intake Method")) errors.add("Intake Method");
          if (selectedDays.isEmpty() && intakeMethod.equals("Specific day of the week")) errors.add("At least one day of the week");
          if (!errors.isEmpty()) {
            String errorText = "Please fill up the following: " + TextUtils.join(", ", errors);
            Toast.makeText(AddNewPlan.this, errorText, Toast.LENGTH_LONG).show();
            return;
          }

          int dosage = Integer.parseInt(dosageStr);
          SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
          Date startDateValue = null, endDateValue = null;

          try {
            startDateValue = sdf.parse(startDate);
            endDateValue = sdf.parse(endDate);
          } catch (ParseException e) {
            Toast.makeText(AddNewPlan.this, "Invalid date format.", Toast.LENGTH_SHORT).show();
            return;
          }

          Map<String, Object> plan = new HashMap<>();
          plan.put("name", name);
          plan.put("unit", unit);
          plan.put("dosage", dosage);
          plan.put("startDate", startDateValue);
          plan.put("endDate", endDateValue);
          plan.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
          plan.put("imageURL", imageUrl);
          plan.put("comment", comment);
          plan.put("Intake_method", intakeMethod);

          addIntakeSpecificDetails(plan, intakeMethod);

          db.collection("plans").add(plan)
            .addOnSuccessListener(documentReference -> {
              Toast.makeText(AddNewPlan.this, "Plan added successfully.", Toast.LENGTH_SHORT).show();
              startActivity(new Intent(AddNewPlan.this, Plans.class));
              finish();
            })
            .addOnFailureListener(e -> {
              Log.e("AddPlanError", "Error saving plan", e);
              Toast.makeText(AddNewPlan.this, "Error adding plan.", Toast.LENGTH_SHORT).show();
            });
        }

        private void addIntakeSpecificDetails(Map<String, Object> plan, String method) {
          switch (method) {
            case "Every X hours":
              List<String> nextOccurrences = calculateNextOccurrences(hourNumber);
              plan.put("nextOccurrences", nextOccurrences);
              break;
            case "Morning":
            case "Before Sleep":
              String presetTime = method.equals("Morning") ? "8:00 AM" : "8:00 PM";
              plan.put("time", presetTime);
              break;
            case "In time":
            case "Specific day of the week":
              // For both "In time" and "Specific day of the week", capture all time inputs.
              EditText timeEditText = findViewById(R.id.timeEditText);
              String timeValue = timeEditText.getText().toString().trim();
              if (!timeValue.isEmpty()) {
                plan.put("time", timeValue);
              }

              // Add more time values from additional time fields
              for (int i = 2; i <= 5; i++) {
                EditText additionalTimeEditText = findViewById(getResources().getIdentifier("timeEditText" + i, "id", getPackageName()));
                String additionalTimeValue = additionalTimeEditText.getText().toString().trim();
                if (!additionalTimeValue.isEmpty()) {
                  plan.put("time" + i, additionalTimeValue);
                }
              }

              if (method.equals("Specific day of the week")) {
                plan.put("selectedDays", selectedDays);
              }
              break;
            default:
              break;
          }
        }
      });


      pillsIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pillsIconImageView.getDrawable() != null) { // Check if an image is set
                    ImageOptionsDialogFragment optionsDialog = new ImageOptionsDialogFragment();
                    optionsDialog.setImageOptionsListener(new ImageOptionsDialogFragment.ImageOptionsListener() {
                        @Override
                        public void onRemoveSelected() {
                            pillsIconImageView.setImageDrawable(null); // Remove the image
                            uploadImageButton.setVisibility(View.VISIBLE); // Show the upload button again
                        }

                        @Override
                        public void onCancelSelected() {
                            // Optionally handle the cancel action
                        }
                    });
                    optionsDialog.show(getSupportFragmentManager(), "imageOptions");
                }
            }
        });

    }

  private void toggleButtonState(View v, String day) {
    Button button = (Button) v;

    // Use tags to track button state
    Object tag = button.getTag();
    boolean isSelected = tag != null && (Boolean) tag;

    if (isSelected) {
      button.setBackgroundResource(R.drawable.circle_white_background);
      button.setTag(false);
      selectedDays.remove(day); // Remove day if deselected
    } else {
      button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
      button.setTag(true);
      selectedDays.add(day); // Add day if selected
    }
  }


  private void updateHourNumberDisplay() {
        TextView hourNumberTextView = findViewById(R.id.hourNumber);
        hourNumberTextView.setText(String.valueOf(hourNumber));


        hourNumberTextView.setGravity(Gravity.CENTER);
    }

    @Override
    public void onImageChosen(Uri uri) {
        if (pillsIconImageView != null && uri != null) {
            imageUri = uri; // Set the class-level variable
            pillsIconImageView.setImageURI(uri);
            findViewById(R.id.uploadImageButton).setVisibility(View.GONE);
        }

        if (imageUri != null) { // Make sure imageUri is not null before proceeding
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String imageName = "uploads/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child(imageName);

            // Upload the image file to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                imageUrl = downloadUri.toString(); // Store the image URL in the class variable
                                Log.d("Upload", "Image URL: " + imageUrl);
                            })
                            .addOnFailureListener(e -> Log.e("Upload", "Failed to obtain download URL.", e)))
                    .addOnFailureListener(e -> Log.e("Upload", "Failed to upload image.", e));
        }
    }
}
