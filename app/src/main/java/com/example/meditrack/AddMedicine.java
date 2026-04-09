package com.example.meditrack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMedicine extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);

        EditText medicineNameText = findViewById(R.id.medicationNameText);
        EditText dosageText = findViewById(R.id.dosageText);
        EditText intervalText = findViewById(R.id.intervalText);
        TextView selectedTime = findViewById(R.id.selectedTime);
        Button timePicker = findViewById(R.id.timePicker);
        Button saveBtn = findViewById(R.id.saveBtn);
        Spinner spinnerInterval = findViewById(R.id.spinnerInterval);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("medications");

        String[] options = {"Hours", "Days"};

    }
}