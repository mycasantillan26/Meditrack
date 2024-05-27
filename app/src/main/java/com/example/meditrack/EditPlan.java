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

    updateButton.setOnClickListener(v -> updatePlanDetails());

    setupDatePicker(startDateSpinner);
    setupDatePicker(endDateSpinner);
  }

  private void setupDatePicker(EditText editText) {
    editText.setFocusable(false);
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
          hideAdditionalFields();

          findViewById(R.id.counterTextView).setVisibility(View.VISIBLE);
          findViewById(R.id.timeEditText).setVisibility(View.VISIBLE);
          findViewById(R.id.commentEditText).setVisibility(View.VISIBLE);
          findViewById(R.id.addTimeButton).setVisibility(View.VISIBLE);

          timeEditText.setFocusable(false);
          timeEditText.setClickable(true);

          TextView counterTextView = findViewById(R.id.counterTextView);
          counterTextView.setText("1");
          timeEditText.setText("Select Time");
          Button addTimeButton = findViewById(R.id.addTimeButton);
          addTimeButton.setText("+");

          timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showTimePickerDialog(timeEditText);
            }
          });
          timeEditText.setFocusableInTouchMode(false);

        } else if (selectedItem.equals("Morning") || selectedItem.equals("Before Sleep")) {
          hideAdditionalFields();
          findViewById(R.id.timeEditText).setVisibility(View.VISIBLE);
          findViewById(R.id.addTimeButton).setVisibility(View.GONE);
          findViewById(R.id.counterTextView).setVisibility(View.GONE);


          timeEditText.setText(selectedItem.equals("Morning") ? "8:00 AM" : "8:00 PM");
          timeEditText.setFocusableInTouchMode(false);
          timeEditText.setFocusable(false);
          timeEditText.setOnClickListener(null);

        } else if (selectedItem.equals("Every X hours")) {
          hideAdditionalFields();
          findViewById(R.id.hourNumber).setVisibility(View.VISIBLE);
          findViewById(R.id.addButton).setVisibility(View.VISIBLE);
          findViewById(R.id.subButton).setVisibility(View.VISIBLE);
          findViewById(R.id.timeEditText).setVisibility(View.GONE);
          findViewById(R.id.addTimeButton).setVisibility(View.GONE);
          findViewById(R.id.counterTextView).setVisibility(View.GONE);
        } else if (selectedItem.equals("Specific day of the week")) {
          hideAdditionalFields();
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

          timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showTimePickerDialog(timeEditText);
            }
          });
          timeEditText.setFocusableInTouchMode(false);

          // Fetch and show additional time fields
          fetchPlanDetailsForAdditionalFields();
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    });

    findViewById(R.id.addTimeButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        counter++;
        showCounterTextView(counter);
      }
    });

    for (int i = 2; i <= 5; i++) {
      final int index = i;
      findViewById(getResources().getIdentifier("addTimeButton" + i, "id", getPackageName())).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          hideCounterTextView(index);
        }
      });
    }

    timeEditText.setFocusable(false);
    timeEditText.setClickable(true);
    timeEditText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog(timeEditText);
      }
    });
    timeEditText.setFocusableInTouchMode(false);

    timeEditText2.setFocusable(false);
    timeEditText2.setClickable(true);
    timeEditText2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog(timeEditText2);
      }
    });
    timeEditText2.setFocusableInTouchMode(false);

    timeEditText3.setFocusable(false);
    timeEditText3.setClickable(true);
    timeEditText3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog(timeEditText3);
      }
    });
    timeEditText3.setFocusableInTouchMode(false);

    timeEditText4.setFocusable(false);
    timeEditText4.setClickable(true);
    timeEditText4.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog(timeEditText4);
      }
    });
    timeEditText4.setFocusableInTouchMode(false);

    timeEditText5.setFocusable(false);
    timeEditText5.setClickable(true);
    timeEditText5.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog(timeEditText5);
      }
    });
    timeEditText5.setFocusableInTouchMode(false);

    findViewById(R.id.hourNumber).setVisibility(View.VISIBLE);
    findViewById(R.id.addButton).setVisibility(View.VISIBLE);
    findViewById(R.id.subButton).setVisibility(View.VISIBLE);
    updateHourNumberDisplay();

    findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hourNumber++;
        updateHourNumberDisplay();
      }
    });

    findViewById(R.id.subButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (hourNumber > 1) {
          hourNumber--;
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
  }

  private void toggleButtonState(View v, String day) {
    Button button = (Button) v;

    Object tag = button.getTag();
    boolean isSelected = tag != null && (Boolean) tag;

    if (isSelected) {
      button.setBackgroundResource(R.drawable.circle_white_background);
      button.setTag(false);
      selectedDays.remove(day);
    } else {
      button.setBackgroundColor(getResources().getColor(R.color.clicked_button_color));
      button.setTag(true);
      selectedDays.add(day);
    }
  }

  private void hideAdditionalFields() {
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
  }

  private void showCounterTextView(int counter) {
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
        counter = 1;
        for (int i = 2; i <= 5; i++) {
          findViewById(getResources().getIdentifier("counterTextView" + i, "id", getPackageName())).setVisibility(View.VISIBLE);
          findViewById(getResources().getIdentifier("timeEditText" + i, "id", getPackageName())).setVisibility(View.VISIBLE);
          findViewById(getResources().getIdentifier("addTimeButton" + i, "id", getPackageName())).setVisibility(View.VISIBLE);
        }
        break;
    }
  }

  private void hideCounterTextView(int index) {
    findViewById(getResources().getIdentifier("counterTextView" + index, "id", getPackageName())).setVisibility(View.GONE);
    findViewById(getResources().getIdentifier("timeEditText" + index, "id", getPackageName())).setVisibility(View.GONE);
    findViewById(getResources().getIdentifier("addTimeButton" + index, "id", getPackageName())).setVisibility(View.GONE);
  }

  private void showTimePickerDialog(EditText editText) {
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    TimePickerDialog timePickerDialog = new TimePickerDialog(EditPlan.this, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
        String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay % 12, minute, timeFormat);
        editText.setText(time);
      }
    }, hour, minute, false);
    timePickerDialog.show();
    editText.setGravity(Gravity.CENTER);
  }

  private void fetchPlanDetails() {
    planRef.get().addOnSuccessListener(documentSnapshot -> {
      if (documentSnapshot.exists()) {
        Map<String, Object> data = documentSnapshot.getData();
        if (data != null) {
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
          nameEditText.setText((String) data.get("name"));

          Object dosageObject = data.get("dosage");
          if (dosageObject instanceof Number) {
            Number dosageNumber = (Number) dosageObject;
            if (dosageNumber.doubleValue() == dosageNumber.intValue()) {
              dosageEditText.setText(String.format(Locale.getDefault(), "%d", dosageNumber.intValue())); // Integer format
            } else {
              dosageEditText.setText(String.format(Locale.getDefault(), "%.2f", dosageNumber.doubleValue())); // Double format
            }
          }
          commentEditText.setText((String) data.get("comment"));

          updateTimeEditTextFromData(timeEditText, data, "time");
          updateTimeEditTextFromData(timeEditText2, data, "time2");
          updateTimeEditTextFromData(timeEditText3, data, "time3");
          updateTimeEditTextFromData(timeEditText4, data, "time4");
          updateTimeEditTextFromData(timeEditText5, data, "time5");

          Timestamp startDateTimestamp = (Timestamp) data.get("startDate");
          Timestamp endDateTimestamp = (Timestamp) data.get("endDate");
          if (startDateTimestamp != null && endDateTimestamp != null) {
            startDateSpinner.setText(dateFormat.format(startDateTimestamp.toDate()));
            endDateSpinner.setText(dateFormat.format(endDateTimestamp.toDate()));
          }

          String intakeMethod = (String) data.get("Intake_method");
          setSpinnerSelection(inTimeSpinner, intakeMethod);
          updateDayButtons((List<String>) data.get("selectedDays"));

          if (intakeMethod != null && (intakeMethod.equals("In time") || intakeMethod.equals("Specific day of the week"))) {
            showAdditionalTimeFields(data); // Ensure fields with data are shown for both "In time" and "Specific day of the week"
          }

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

  private void fetchPlanDetailsForAdditionalFields() {
    planRef.get().addOnSuccessListener(documentSnapshot -> {
      if (documentSnapshot.exists()) {
        Map<String, Object> data = documentSnapshot.getData();
        if (data != null) {
          showAdditionalTimeFields(data);
        }
      }
    }).addOnFailureListener(e -> {
      Toast.makeText(EditPlan.this, "Failed to fetch additional fields: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

  private void updateTimeEditTextFromData(EditText editText, Map<String, Object> data, String key) {
    if (data.containsKey(key)) {
      editText.setText((String) data.get(key));
      editText.setGravity(Gravity.CENTER);
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
    updatedData.put("unit", unitsDropdown.getSelectedItem().toString());
    String intakeMethod = inTimeSpinner.getSelectedItem().toString();
    updatedData.put("Intake_method", intakeMethod);

    updatedData.put("startDate", convertStringToTimestamp(startDateSpinner.getText().toString()));
    updatedData.put("endDate", convertStringToTimestamp(endDateSpinner.getText().toString()));

    if ("Specific day of the week".equals(intakeMethod)) {
      updatedData.put("selectedDays", selectedDays);
    }

    if (!"Every X hours".equals(intakeMethod)) {
      updatedData.put("time", timeEditText.getText().toString());
      putIfNotEmpty(updatedData, "time2", timeEditText2.getText().toString());
      putIfNotEmpty(updatedData, "time3", timeEditText3.getText().toString());
      putIfNotEmpty(updatedData, "time4", timeEditText4.getText().toString());
      putIfNotEmpty(updatedData, "time5", timeEditText5.getText().toString());
    }

    if ("Every X hours".equals(intakeMethod)) {
      updatedData.put("hourNumber", hourNumber);
      List<String> nextOccurrences = calculateNextOccurrences(hourNumber);
      updatedData.put("nextOccurrences", nextOccurrences);
    }

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

  private List<String> calculateNextOccurrences(int intervalHours) {
    List<String> nextOccurrences = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    Date currentTime = calendar.getTime();
    nextOccurrences.add(formatTime(calendar.getTime()));
    for (int i = 1; i < 24 / intervalHours; i++) {
      calendar.add(Calendar.HOUR_OF_DAY, intervalHours);
      nextOccurrences.add(formatTime(calendar.getTime()));
    }
    return nextOccurrences;
  }

  private String formatTime(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    return sdf.format(date);
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
}
