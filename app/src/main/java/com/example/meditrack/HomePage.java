package com.example.meditrack;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        ImageButton addMedication = findViewById(R.id.btnAddMedication);
        TextView hintText = findViewById(R.id.txtAddHint);

        addMedication.setOnHoverListener((v, event) -> {
            hintText.setVisibility(View.VISIBLE);
            return false;
        });

        addMedication.setOnFocusChangeListener((v, hasFocus) -> {
            hintText.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });

        ImageButton recordsBtn = findViewById(R.id.recordsButton);
        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Records.class);
                startActivity(intent);
            }
        });

        ImageButton inventoryBtn = findViewById(R.id.inventoryButton);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Inventory.class);
                startActivity(intent);
            }
        });

        ImageButton settingsBtn = findViewById(R.id.settingsButton);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Settings.class);
                startActivity(intent);
            }
        });

    }
}
