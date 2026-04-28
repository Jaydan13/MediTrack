package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    ImageButton homeBtn, recordsBtn, inventoryBtn;
    Spinner colourSpinner, snoozeSpinner;
    Button saveBtn, logoutBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ThemeHelper.applyTheme(this);

        homeBtn = findViewById(R.id.homeBtn);
        recordsBtn = findViewById(R.id.recordsBtn);
        inventoryBtn = findViewById(R.id.inventoryBtn);

        colourSpinner = findViewById(R.id.colourSpinner);
        snoozeSpinner = findViewById(R.id.snoozeSpinner);

        saveBtn = findViewById(R.id.saveBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        mAuth = FirebaseAuth.getInstance();

        String[] colourOptions = {"Blue", "Green", "Orange", "Pink", "Purple", "Red", "Yellow"};
        String[] snoozeOptions = {"5 Mins", "10 Mins", "15 Mins", "20 Mins", "25 Mins", "30 Mins"};

        ArrayAdapter<String> colourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colourOptions);
        colourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> snoozeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, snoozeOptions);
        snoozeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        colourSpinner.setAdapter(colourAdapter);
        snoozeSpinner.setAdapter(snoozeAdapter);

        saveBtn.setOnClickListener(v -> {
            changeColour();
            changeSnooze();
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show();
            ThemeHelper.applyTheme(this);
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Settings.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, Records.class);
                startActivity(intent);
            }
        });

        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, Inventory.class);
                startActivity(intent);
            }
        });

    }
    private void changeColour() {
        String colour = colourSpinner.getSelectedItem().toString();

        int colourValue;

        switch (colour) {
            case "Green":
                colourValue = ContextCompat.getColor(this, R.color.green);
                break;
            case "Orange":
                colourValue = ContextCompat.getColor(this, R.color.orange);
                break;
            case "Pink":
                colourValue = ContextCompat.getColor(this, R.color.pink);
                break;
            case "Purple":
                colourValue = ContextCompat.getColor(this, R.color.purple);
                break;
            case "Red":
                colourValue = ContextCompat.getColor(this, R.color.red);
                break;
            case "Yellow":
                colourValue = ContextCompat.getColor(this, R.color.yellow);
                break;
            default:
                colourValue = ContextCompat.getColor(this, R.color.blue);
                break;
        }

        getSharedPreferences("SettingsPrefs", MODE_PRIVATE)
                .edit()
                .putInt("appColourValue", colourValue)
                .apply();
    }

    private void changeSnooze() {
        String snooze = snoozeSpinner.getSelectedItem().toString();

        Toast.makeText(this, "Snooze: " + snooze, Toast.LENGTH_SHORT).show();
    }
}