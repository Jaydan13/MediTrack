package com.example.meditrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddReminder extends AppCompatActivity {

    //Variables
    EditText medicationNameText, dosageNo, intervalText, durationText;
    Button timePicker, saveBtn;
    ImageButton backBtn;
    Spinner spinnerInterval, spinnerDuration;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String choose_time = "";
    String userId;
    int selectedHour = 0;
    int selectedMinute = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reminder);

        ThemeHelper.applyTheme(this);

        //Assigning Variables to the XML Id's
        medicationNameText = findViewById(R.id.medicationNameText);
        dosageNo = findViewById(R.id.dosageNo);
        intervalText = findViewById(R.id.intervalText);
        durationText = findViewById(R.id.durationText);

        timePicker = findViewById(R.id.timePicker);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);

        spinnerDuration = findViewById(R.id.spinnerDuration);
        spinnerInterval = findViewById(R.id.spinnerInterval);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Spinner Setup
        String[] intervalOptions = {"Hours", "Days", "Weeks"};
        String[] durationOptions = {"Days", "Weeks", "Months"};

        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervalOptions);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInterval.setAdapter(intervalAdapter);
        spinnerDuration.setAdapter(durationAdapter);

        //Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddReminder.this, HomePage.class);
                startActivity(intent);
            }
        });

        //Select Time
        timePicker.setOnClickListener(v -> {

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

                choose_time = String.format("%02d:%02d", hourOfDay, minute);
                timePicker.setText(choose_time);

                // Save for alarm use
                selectedHour = hourOfDay;
                selectedMinute = minute;

            }, 12, 0, true);

            timePickerDialog.show();
        });

        //Save Button
        saveBtn.setOnClickListener(view -> {
            saveReminder();
        });

    }
    private void saveReminder() {
        //Get all inputs
        String name = medicationNameText.getText().toString().trim();
        String dosageStr = dosageNo.getText().toString().trim();
        if (dosageStr.isEmpty()) {
            Toast.makeText(this, "Enter dosage", Toast.LENGTH_SHORT).show();
            return;
        }
        int dosage;
        try {
            dosage = Integer.parseInt(dosageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Dosage must be a number (e.g. 2)", Toast.LENGTH_SHORT).show();
            return;
        }
        String intervalNo = intervalText.getText().toString().trim();
        String intervalType = spinnerInterval.getSelectedItem().toString();
        String durationNo = durationText.getText().toString().trim();
        String durationType = spinnerDuration.getSelectedItem().toString();

        //Check if Fields Empty
        if (name.isEmpty() || intervalNo.isEmpty() || durationNo.isEmpty() || choose_time.isEmpty()) {
            Toast.makeText(this, "Fill all Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //To make sure user is logged in
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();

        String interval = intervalNo + " " + intervalType;
        String duration = durationNo + " " + durationType;

        //Create collection in FireStore
        String reminderId = db.collection("users").document(userId).collection("reminder").document().getId();

        //Add to FireStore
        Map<String, Object> reminder = new HashMap<>();
        reminder.put("id", reminderId);
        reminder.put("name", name);
        reminder.put("dosage", dosage);
        reminder.put("interval", interval);
        reminder.put("duration", duration);
        reminder.put("time", choose_time);
        reminder.put("startTime", System.currentTimeMillis());
        reminder.put("durationNo", durationNo);
        reminder.put("durationType", durationType);

        //Get reminder details from FireStore
        db.collection("users").document(userId).collection("reminder").document(reminderId).set(reminder).addOnSuccessListener(doc -> {

                    //Set Alarm
                    AlarmHelper.setAlarm(this,
                            reminderId,
                            selectedHour,
                            selectedMinute,
                            name,
                            dosage,
                            intervalNo,
                            intervalType,
                            durationNo,
                            durationType,
                            System.currentTimeMillis()
                    );

                    Toast.makeText(this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}