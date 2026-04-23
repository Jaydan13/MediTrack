package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Records extends AppCompatActivity {

    ImageButton homeBtn, inventoryBtn, profileBtn, pdfBtn;
    LinearLayout recordsContainer;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_records);

        homeBtn = findViewById(R.id.homeBtn);
        inventoryBtn = findViewById(R.id.inventoryBtn);
        profileBtn = findViewById(R.id.profileBtn);
        pdfBtn = findViewById(R.id.pdfBtn);

        recordsContainer = findViewById(R.id.recordsContainer);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadRecords();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Records.this, HomePage.class);
                startActivity(intent);
            }
        });

        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Records.this, Inventory.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Records.this, Profile.class);
                startActivity(intent);
            }
        });


    }
    private void loadRecords() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(Records.this, Login.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("records").get().addOnSuccessListener(queryDocumentSnapshots -> {
            recordsContainer.removeAllViews();

            if (queryDocumentSnapshots.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("No records yet");
                recordsContainer.addView(empty);
                return;
            }

            for (DocumentSnapshot doc: queryDocumentSnapshots) {

                String name = doc.getString("name");
                String dosage = doc.getString("dosage");
                String date = doc.getString("date");
                String time = doc.getString("time");

                addRecordsView(name, dosage, date, time);
            }
        });
    }
    private void addRecordsView(String name, String dosage, String date, String time) {

        LinearLayout recordLayout = new LinearLayout(this);
        recordLayout.setOrientation(LinearLayout.VERTICAL);
        recordLayout.setPadding(30, 30, 30, 30);
        recordLayout.setBackgroundColor(0xFFEFEFEF);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 30);
        recordLayout.setLayoutParams(params);

        TextView nameText = new TextView(this);
        nameText.setText("Medicine: " + (name != null ? name : "Unknown"));
        nameText.setTextSize(16);

        TextView dosageText = new TextView(this);
        dosageText.setText("Dosage: " + (dosage != null ? dosage : "N/A"));

        TextView dateText = new TextView(this);
        dateText.setText("Date: " + (date != null ? date : "N/A"));

        TextView timeText = new TextView(this);
        timeText.setText("Time: " + (time != null ? time : "N/A"));

        recordLayout.addView(nameText);
        recordLayout.addView(dosageText);
        recordLayout.addView(dateText);
        recordLayout.addView(timeText);

        // Add to TOP (newest first)
        recordsContainer.addView(recordLayout, 0);
    }
}