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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddMedicine extends AppCompatActivity {

    EditText medicationNameText, dosageText, intervalText, durationText;
    TextView selectedTime;
    Button timePicker, saveBtn;
    Spinner spinnerInterval, spinnerDuration;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String time = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);

        medicationNameText = findViewById(R.id.medicationNameText);
        dosageText = findViewById(R.id.dosageText);
        intervalText = findViewById(R.id.intervalText);
        selectedTime = findViewById(R.id.selectedTime);
        durationText = findViewById(R.id.durationText);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        timePicker = findViewById(R.id.timePicker);
        saveBtn = findViewById(R.id.saveBtn);
        spinnerInterval = findViewById(R.id.spinnerInterval);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String[] intervalOptions = {"Hours", "Days", "Weeks"};
        String[] durationOptions = {"Days", "Weeks", "Months"};

        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervalOptions);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInterval.setAdapter(intervalAdapter);
        spinnerDuration.setAdapter(durationAdapter);

        timePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog picker = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                time = hourOfDay + ":" + minute1;
                selectedTime.setText("Time: " + time);
            }, hour, minute, true);

            picker.show();
        });

        saveBtn.setOnClickListener(view -> saveMedicine());

    }

    private void saveMedicine() {
        String name = medicationNameText.getText().toString().trim();
        String dosage = dosageText.getText().toString().trim();
        String intervalNo = intervalText.getText().toString().trim();
        String intervalType = spinnerInterval.getSelectedItem().toString();
        String durationNo = durationText.getText().toString().trim();
        String durationType = spinnerDuration.getSelectedItem().toString();

        if (name.isEmpty() || dosage.isEmpty() || intervalNo.isEmpty() || durationNo.isEmpty() || time.isEmpty()) {
            Toast.makeText(AddMedicine.this, "Fill all Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String interval = intervalNo + " " + intervalType;
        String duration = durationNo + " " + durationType;

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> medicine = new HashMap<>();
        medicine.put("name", name);
        medicine.put("dosage", dosage);
        medicine.put("interval", interval);
        medicine.put("duration", duration);
        medicine.put("time", time);

        db.collection("users").document(userId).collection("medicines").add(medicine).addOnSuccessListener(doc -> {
            Toast.makeText(this, "Medicine Saved!!!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " +e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}