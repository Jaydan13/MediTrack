package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Inventory extends AppCompatActivity {

    ImageButton homeBtn, recordsBtn, profileBtn, locationBtn;
    Button addStockBtn;
    LinearLayout stockContainer;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        homeBtn = findViewById(R.id.homeBtn);
        recordsBtn = findViewById(R.id.recordsBtn);
        profileBtn = findViewById(R.id.profileBtn);
        locationBtn = findViewById(R.id.locationBtn);
        addStockBtn = findViewById(R.id.addStockBtn);
        stockContainer = findViewById(R.id.stockContainer);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadInventory();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, HomePage.class);
                startActivity(intent);
            }
        });

        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, Records.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, Profile.class);
                startActivity(intent);
            }
        });

        addStockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, AddStock.class);
                startActivity(intent);
            }
        });

    }
    private void loadInventory() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(Inventory.this, Login.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("inventory").get().addOnSuccessListener(queryDocumentSnapshots -> {
            stockContainer.removeAllViews();

            if (queryDocumentSnapshots.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("No stock yet");
                stockContainer.addView(empty);
                return;
            }

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String name = doc.getString("name");
                Long quantityLong = doc.getLong("quantity");

                int quantity = quantityLong != null ? quantityLong.intValue() : 0;

                View inventoryView = getLayoutInflater().inflate(R.layout.item_stock, null);

                TextView nameText = inventoryView.findViewById(R.id.stockMedName);
                TextView quantityText = inventoryView.findViewById(R.id.stockMedQuantity);

                nameText.setText(name);
                quantityText.setText("Quantity: " + quantity);

                stockContainer.addView(inventoryView);
            }
        });

    }
}