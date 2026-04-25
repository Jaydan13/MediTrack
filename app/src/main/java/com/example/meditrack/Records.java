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
import com.google.firebase.firestore.Query;

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

        db.collection("users").document(userId).collection("records").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
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

                View recordView = getLayoutInflater().inflate(R.layout.record, null);

                TextView nameText = recordView.findViewById(R.id.recordMedName);
                TextView dosageText = recordView.findViewById(R.id.recordDosage);
                TextView dateText = recordView.findViewById(R.id.recordDate);
                TextView timeText = recordView.findViewById(R.id.recordTime);

                nameText.setText(name);
                dosageText.setText(dosage);
                dateText.setText(date);
                timeText.setText(time);

                recordsContainer.addView(recordView, 0);
            }
        });
    }
}