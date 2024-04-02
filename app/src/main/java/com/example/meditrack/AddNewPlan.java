package com.example.meditrack;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class AddNewPlan extends AppCompatActivity {

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

        // Set listener for start date spinner
        // Set listener for start date spinner
        // Set listener for start date spinner
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


        // Create adapter for end date spinner
        ArrayAdapter<CharSequence> endDateAdapter = ArrayAdapter.createFromResource(this,
                R.array.end_date_array, android.R.layout.simple_spinner_item);
        endDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endDateSpinner.setAdapter(endDateAdapter);
    }
}