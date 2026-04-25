package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomePage extends AppCompatActivity {

    ImageButton addMedication, recordsBtn, inventoryBtn, profileBtn;
    TextView hintText;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    LinearLayout medicinesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        recordsBtn = findViewById(R.id.recordsBtn);
        inventoryBtn = findViewById(R.id.inventoryBtn);
        profileBtn = findViewById(R.id.profileBtn);
        addMedication = findViewById(R.id.btnAddMedication);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        medicinesContainer = findViewById(R.id.medicinesContainer);

        loadMedicines();

        addMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, AddMedicine.class);
                startActivity(intent);
            }
        });

        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Records.class);
                startActivity(intent);
            }
        });

        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Inventory.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Profile.class);
                startActivity(intent);
            }
        });

    }

    private void loadMedicines() {

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(HomePage.this, Login.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("medicines").get().addOnSuccessListener(queryDocumentSnapshots -> {

            medicinesContainer.removeAllViews();

            if (queryDocumentSnapshots.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("No reminders added yet");
                medicinesContainer.addView(empty);
                return;
            }

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String name = doc.getString("name");
                String dosage = doc.getString("dosage");
                String time = doc.getString("time");

                View reminderView = getLayoutInflater().inflate(R.layout.remind_medicine, null);

                TextView nameText = reminderView.findViewById(R.id.remindMedName);
                TextView dosageText = reminderView.findViewById(R.id.remindDosage);
                TextView timeText = reminderView.findViewById(R.id.remindTime);

                nameText.setText(name);
                dosageText.setText("Dosage: " + dosage);
                timeText.setText("Time: " + time);

                medicinesContainer.addView(reminderView);
            }
        });
    }
}
