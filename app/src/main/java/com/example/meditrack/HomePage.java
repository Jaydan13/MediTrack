package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    ImageButton addMedication, recordsBtn, inventoryBtn, profileBtn;
    TextView hintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        addMedication = findViewById(R.id.btnAddMedication);
        hintText = findViewById(R.id.txtAddHint);

        addMedication.setOnHoverListener((v, event) -> {
            hintText.setVisibility(View.VISIBLE);
            return false;
        });

        addMedication.setOnFocusChangeListener((v, hasFocus) -> {
            hintText.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });

        recordsBtn = findViewById(R.id.recordsBtn);
        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Records.class);
                startActivity(intent);
            }
        });

        inventoryBtn = findViewById(R.id.inventoryBtn);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Inventory.class);
                startActivity(intent);
            }
        });

        profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Profile.class);
                startActivity(intent);
            }
        });

    }
}
