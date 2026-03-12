package com.example.meditrack;

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

        ImageButton addButton = findViewById(R.id.btnAddMedication);
        TextView hintText = findViewById(R.id.txtAddHint);

        addButton.setOnHoverListener((v, event) -> {
            hintText.setVisibility(View.VISIBLE);
            return false;
        });

        addButton.setOnFocusChangeListener((v, hasFocus) -> {
            hintText.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });
    }
}
