package com.example.meditrack;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddMedicine extends AppCompatActivity {

    EditText medicineNameText, dosageText, intervalText, durationText;
    TextView selectedTime;
    Button timePicker, saveBtn;
    Spinner spinnerInterval, spinnerDuration;
    int hour, minute;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);

        medicineNameText = findViewById(R.id.medicationNameText);
        dosageText = findViewById(R.id.dosageText);
        intervalText = findViewById(R.id.intervalText);
        selectedTime = findViewById(R.id.selectedTime);
        durationText = findViewById(R.id.durationText);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        timePicker = findViewById(R.id.timePicker);
        saveBtn = findViewById(R.id.saveBtn);
        spinnerInterval = findViewById(R.id.spinnerInterval);

        databaseReference = FirebaseDatabase.getInstance().getReference("medications");

        String[] options = {"Hours", "Days"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddMedicine.this, android.R.layout.simple_spinner_dropdown_item, options);
        spinnerInterval.setAdapter(adapter);

        timePicker.setOnClickListener(view -> showTimePicker());

        saveBtn.setOnClickListener(view -> saveMedication());

    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(AddMedicine.this, (view, hourOfDay, minute) -> {
            this.hour = hourOfDay;
            this.minute = minute;
            selectedTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void saveMedication() {
        String medname = medicineNameText.getText().toString().trim();
        String dosage = dosageText.getText().toString().trim();
        String interval = intervalText.getText().toString().trim();
        String intervalType = spinnerInterval.getSelectedItem().toString();
        String duration = durationText.getText().toString().trim();
        String durationType = spinnerDuration.getSelectedItem().toString();

        if (medname.isEmpty() || dosage.isEmpty() || interval.isEmpty() || duration.isEmpty()) {
            Toast.makeText(AddMedicine.this, "Fill all Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();

        Medication med = new Medication(
                medname, dosage, hour, minute, Integer.parseInt(interval), intervalType, Integer.parseInt(duration), durationType
        );

        databaseReference.child(id).setValue(med).addOnSuccessListener(a -> {
            Toast.makeText(AddMedicine.this, "Saved", Toast.LENGTH_SHORT).show();

            AlarmHelper.setAlarm(this, med, id);
        });
    }
}