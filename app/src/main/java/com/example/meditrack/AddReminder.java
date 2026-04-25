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

    EditText medicationNameText, dosageText, intervalText, durationText;
    TextView selectedTime;
    Button timePicker, saveBtn;
    ImageButton backBtn;
    Spinner spinnerInterval, spinnerDuration;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String time = "";
    int selectedHour = 0;
    int selectedMinute = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reminder);

        medicationNameText = findViewById(R.id.medicationNameText);
        dosageText = findViewById(R.id.dosageText);
        intervalText = findViewById(R.id.intervalText);
        durationText = findViewById(R.id.durationText);

        selectedTime = findViewById(R.id.selectedTime);

        timePicker = findViewById(R.id.timePicker);
        saveBtn = findViewById(R.id.saveBtn);

        spinnerDuration = findViewById(R.id.spinnerDuration);
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddReminder.this, HomePage.class);
                startActivity(intent);
            }
        });

        timePicker.setOnClickListener(v -> {

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {

                        time = String.format("%02d:%02d", hourOfDay, minute);
                        selectedTime.setText(time);

                        // Save for alarm use
                        selectedHour = hourOfDay;
                        selectedMinute = minute;

                    },
                    12, 0, true
            );

            timePickerDialog.show();
        });

        saveBtn.setOnClickListener(view -> {
            setAlarm();
            saveMedicine();
        });

    }

    private void saveMedicine() {
        String name = medicationNameText.getText().toString().trim();
        String dosage = dosageText.getText().toString().trim();
        String intervalNo = intervalText.getText().toString().trim();
        String intervalType = spinnerInterval.getSelectedItem().toString();
        String durationNo = durationText.getText().toString().trim();
        String durationType = spinnerDuration.getSelectedItem().toString();

        if (name.isEmpty() || dosage.isEmpty() || intervalNo.isEmpty() || durationNo.isEmpty() || time.isEmpty()) {
            Toast.makeText(AddReminder.this, "Fill all Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String interval = intervalNo + " " + intervalType;
        String duration = durationNo + " " + durationType;

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

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

    private void setAlarm() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("name", medicationNameText.getText().toString());
        intent.putExtra("dosage", dosageText.getText().toString());
        intent.putExtra("intervalNo", intervalText.getText().toString());
        //intent.putExtra("intervalType", spinnerInterval.getTe);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                return;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }
}