package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewStock extends AppCompatActivity {

    ImageButton backBtn;
    Button editStockBtn, deleteStockBtn;
    TextView viewMedName, viewQuantity, viewExpDate;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_stock);

        ThemeHelper.applyTheme(this);

        backBtn = findViewById(R.id.backBtn);
        editStockBtn = findViewById(R.id.editStockBtn);
        deleteStockBtn = findViewById(R.id.deleteStockBtn);

        viewMedName = findViewById(R.id.viewMedName);
        viewQuantity = findViewById(R.id.viewQuantity);
        viewExpDate = findViewById(R.id.viewExpDate);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        int quantity = getIntent().getIntExtra("quantity", 0);
        String expDate = getIntent().getStringExtra("expDate");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewStock.this, Inventory.class);
                startActivity(intent);
            }
        });

        viewMedName.setText(name);
        viewQuantity.setText(quantity);
        viewExpDate.setText(expDate);

        deleteStockBtn.setOnClickListener(v -> {

            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).collection("inventory").document(id).delete().addOnSuccessListener(unused -> {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                finish();
            });
        });

        editStockBtn.setOnClickListener(v -> {

            Intent intent = new Intent(ViewStock.this, EditStock.class);

            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("quantity", quantity);
            intent.putExtra("expDate", expDate);

            startActivity(intent);
        });
    }
}