package com.example.meditrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddStock extends AppCompatActivity {

    //Variables
    EditText stockName, stockQuantity;
    ImageButton backBtn;
    Button expiryDate, saveStockBtn;
    String selectedExpiry = "";
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_stock);

        ThemeHelper.applyTheme(this);

        //Assigning Variables to the XML Id's
        stockName = findViewById(R.id.stockName);
        stockQuantity = findViewById(R.id.stockQuantity);

        backBtn = findViewById(R.id.backBtn);
        expiryDate = findViewById(R.id.expiryDate);
        saveStockBtn = findViewById(R.id.saveStockBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddStock.this, Inventory.class);
                startActivity(intent);
            }
        });

        //Expiry Date Selector
        expiryDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                month = month + 1;
                selectedExpiry = dayOfMonth + "/" + month + "/" + year;
                expiryDate.setText(selectedExpiry);
            },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        });

        //Save Button
        saveStockBtn.setOnClickListener(v -> saveStock());
    }
    private void saveStock() {
        //Get all inputs
        String name = stockName.getText().toString().trim();
        String strQuantity = stockQuantity.getText().toString().trim();

        //Check if Fields empty
        if (name.isEmpty() || strQuantity.isEmpty() || selectedExpiry.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(strQuantity);

        //Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        //Add to FireStore
        Map<String, Object> stock = new HashMap<>();
        stock.put("name", name);
        stock.put("quantity", quantity);
        stock.put("expDate", selectedExpiry);

        db.collection("users").document(userId).collection("inventory").add(stock).addOnSuccessListener(doc -> {
            Toast.makeText(this, "Stock Added", Toast.LENGTH_SHORT).show();
            finish(); // go back to Inventory
        }).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}