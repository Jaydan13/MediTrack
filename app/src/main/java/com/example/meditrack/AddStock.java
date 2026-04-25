package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddStock extends AppCompatActivity {

    EditText stockName, stockQuantity;
    ImageButton backBtn, barcodeBtn;
    Button expiryDate, saveStockBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_stock);

        stockName = findViewById(R.id.stockName);
        stockQuantity = findViewById(R.id.stockQuantity);
        backBtn = findViewById(R.id.backBtn);
        barcodeBtn = findViewById(R.id.barcodeBtn);
        expiryDate = findViewById(R.id.expiryDate);
        saveStockBtn = findViewById(R.id.saveStockBtn);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddStock.this, Inventory.class);
                startActivity(intent);
            }
        });

    }
}