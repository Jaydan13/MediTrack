package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends AppCompatActivity {

    ImageButton homeBtn, recordsBtn, settingsBtn, locationBtn;
    Button addStockBtn;
    RecyclerView recyclerView;
    InventoryAdapter adapter;
    List<StockItem> stockList;
    private static final int LOW_STOCK_THRESHOLD = 5;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        ThemeHelper.applyTheme(this);

        homeBtn = findViewById(R.id.homeBtn);
        recordsBtn = findViewById(R.id.recordsBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        locationBtn = findViewById(R.id.locationBtn);
        addStockBtn = findViewById(R.id.addStockBtn);

        recyclerView = findViewById(R.id.recyclerViewInventory);
        stockList = new ArrayList<>();
        adapter = new InventoryAdapter(stockList);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadInventory();
        createNotificationChannel();

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, PharmacyMap.class);
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

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, Records.class);
                startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, Settings.class);
                startActivity(intent);
            }
        });

    }

    // refresh list
    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }

    private void loadInventory() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(Inventory.this, Login.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("inventory").get().addOnSuccessListener(queryDocumentSnapshots -> {

            stockList.clear();

            boolean lowStockFound = false;
            StringBuilder lowStockItems = new StringBuilder();

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                String name = doc.getString("name");
                String nameLower = doc.getString("nameLower");
                Long quantityLong = doc.getLong("quantity");
                String expDate = doc.getString("expDate");

                int quantity = quantityLong != null ? quantityLong.intValue() : 0;

                stockList.add(new StockItem(id, name, nameLower, quantity, expDate));

                if (quantity <= LOW_STOCK_THRESHOLD) {
                    lowStockFound = true;
                    lowStockItems.append(name).append(" (").append(quantity).append(")\n");
                }
            }

            adapter.notifyDataSetChanged();

        });
    }
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "low_stock_channel", "Low Stock Alerts",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Alerts when medication stock is low");

            android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}