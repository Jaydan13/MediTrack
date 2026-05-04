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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditReminder extends AppCompatActivity {

    //Variables
    EditText editMedName, editDosage, editInterval, editDuration;
    Spinner editSpinnerInterval, editSpinnerDuration;
    Button timePicker, saveBtn;
    ImageButton backBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String id;
    String choose_time = "";
    long startTime;
    int selectedHour = 0;
    int selectedMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_reminder);

        ThemeHelper.applyTheme(this);

        //Assigning Variables to XML Id's
        editMedName = findViewById(R.id.editMedName);
        editDosage = findViewById(R.id.editDosage);
        editInterval = findViewById(R.id.editInterval);
        editDuration = findViewById(R.id.editDuration);

        editSpinnerInterval = findViewById(R.id.editSpinnerInterval);
        editSpinnerDuration = findViewById(R.id.editSpinnerDuration);

        timePicker = findViewById(R.id.timePicker);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Get data from View Reminder
        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        int dosage = getIntent().getIntExtra("dosage", 0);
        String time = getIntent().getStringExtra("time");
        String interval = getIntent().getStringExtra("interval");
        String duration = getIntent().getStringExtra("duration");
        startTime = getIntent().getLongExtra("startTime", 0);

        //Spinner Setup
        String[] intervalOptions = {"Hours", "Days", "Weeks"};
        String[] durationOptions = {"Days", "Weeks", "Months"};

        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervalOptions);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editSpinnerInterval.setAdapter(intervalAdapter);
        editSpinnerDuration.setAdapter(durationAdapter);

        //Display old data to screen
        editMedName.setText(name);
        editDosage.setText(String.valueOf(dosage));
        String[] intervalSplit = interval.split(" ");
        editInterval.setText(intervalSplit[0]);
        setSpinner(editSpinnerInterval, intervalSplit[1]);
        String[] durationSplit = duration.split(" ");
        editDuration.setText(durationSplit[0]);
        setSpinner(editSpinnerDuration, durationSplit[1]);
        choose_time = time;
        timePicker.setText(time);

        //Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditReminder.this, HomePage.class);
                startActivity(intent);
            }
        });

        //Select time for reminder
        timePicker.setOnClickListener(v -> {

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

                choose_time = String.format("%02d:%02d", hourOfDay, minute);
                timePicker.setText(choose_time);

                selectedHour = hourOfDay;
                selectedMinute = minute;

                }, 12, 0, true);

            timePickerDialog.show();
        });

        //Save Button
        saveBtn.setOnClickListener(view -> {
            updateReminder();
        });
    }

    String durationNo, durationType;
    // update reminder function
    private void updateReminder() {
        //Get input data
        String name = editMedName.getText().toString().trim();
        int dosage = Integer.parseInt(editDosage.getText().toString().trim());
        String intervalNo = editInterval.getText().toString().trim();
        String intervalType = editSpinnerInterval.getSelectedItem().toString().trim();
        durationNo = editDuration.getText().toString().trim();
        durationType = editSpinnerDuration.getSelectedItem().toString().trim();

        //Validations
        if (name.isEmpty() || intervalNo.isEmpty() || durationNo.isEmpty() || choose_time.isEmpty()) {
            Toast.makeText(EditReminder.this, "Fill all Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String interval = intervalNo + " " + intervalType;
        String duration = durationNo + " " + durationType;

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> updatedReminder = new HashMap<>();
        updatedReminder.put("name", name);
        updatedReminder.put("dosage", dosage);
        updatedReminder.put("time", choose_time);
        updatedReminder.put("interval", interval);
        updatedReminder.put("duration", duration);

        db.collection("users")
                .document(userId)
                .collection("reminder")
                .document(id)
                .update(updatedReminder)
                .addOnSuccessListener(unused -> {

                    //Cancel old alarm
                    AlarmHelper.cancelAlarm(this, id);

                    //Set new alarm
                    AlarmHelper.setAlarm(
                            this,
                            id,
                            selectedHour,
                            selectedMinute,
                            editMedName.getText().toString(),
                            Integer.parseInt( editDosage.getText().toString()),
                            editInterval.getText().toString(),
                            editSpinnerInterval.getSelectedItem().toString(),
                            editDuration.getText().toString(),
                            editSpinnerDuration.getSelectedItem().toString(),
                            startTime
                    );

                    Toast.makeText(this, "Reminder Updated", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditReminder.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    //to display old spinner input
    private void setSpinner(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }
}