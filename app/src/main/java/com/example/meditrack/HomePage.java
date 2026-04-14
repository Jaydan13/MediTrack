package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

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

        hintText = findViewById(R.id.txtAddHint);

        recordsBtn = findViewById(R.id.recordsBtn);
        inventoryBtn = findViewById(R.id.inventoryBtn);
        profileBtn = findViewById(R.id.profileBtn);
        addMedication = findViewById(R.id.btnAddMedication);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        medicinesContainer = findViewById(R.id.medicinesContainer);

        loadMedicines();

        addMedication.setOnHoverListener((v, event) -> {
            hintText.setVisibility(View.VISIBLE);
            return false;
        });

        addMedication.setOnFocusChangeListener((v, hasFocus) -> {
            hintText.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });

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

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("medicines").get().addOnSuccessListener(queryDocumentSnapshots -> {
            medicinesContainer.removeAllViews();

            for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                String name = doc.getString("name");
                String time = doc.getString("time");

                addMedicineCard(name, time);
            }
        });
    }

    private void addMedicineCard(String name, String time) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(30, 30, 30, 30);

        TextView medName = new TextView(this);
        medName.setText(name);
        medName.setTextSize(18);

        TextView medTime = new TextView(this);
        medTime.setText("Time: "+ time);

        card.addView(medName);
        card.addView(medTime);

        medicinesContainer.addView(card);
    }
}
