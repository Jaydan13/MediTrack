package com.example.meditrack;

import android.app.DatePickerDialog;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditStock extends AppCompatActivity {

    //Variables
    ImageButton backBtn;
    TextView editStockName, editStockQuantity;
    Button editExpiryDate, saveStockBtn;
    String id;
    String selectedExpiry = "";
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_stock);

        ThemeHelper.applyTheme(this);

        //Assigning variables to XML Id's
        backBtn = findViewById(R.id.backBtn);
        editExpiryDate = findViewById(R.id.editExpiryDate);
        saveStockBtn = findViewById(R.id.saveStockBtn);

        editStockName = findViewById(R.id.editStockName);
        editStockQuantity = findViewById(R.id.editStockQuantity);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Get data from View Stock
        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        int quantity = getIntent().getIntExtra("quantity", 0);
        String expDate = getIntent().getStringExtra("expDate");

        //Display old data
        editStockName.setText(name);
        editStockQuantity.setText(String.valueOf(quantity));
        editExpiryDate.setText(expDate);
        selectedExpiry = expDate;

        //Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditStock.this, Inventory.class);
                startActivity(intent);
            }
        });

        //Set expiry date
        editExpiryDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                month = month + 1;
                selectedExpiry = dayOfMonth + "/" + month + "/" + year;
                editExpiryDate.setText(selectedExpiry);
            },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        });

        //Save Button
        saveStockBtn.setOnClickListener(v -> updateStock());
    }
    private void updateStock() {
        //Get data input
        String name = editStockName.getText().toString().trim();
        String strQuantity = editStockQuantity.getText().toString().trim();
        String nameLower = name.toLowerCase();

        //Validations
        if (name.isEmpty() || strQuantity.isEmpty() || selectedExpiry.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = Integer.parseInt(strQuantity);

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> updatedStock = new HashMap<>();
        updatedStock.put("name", name);
        updatedStock.put("nameLower", nameLower);
        updatedStock.put("quantity", quantity);
        updatedStock.put("expDate", selectedExpiry);

        db.collection("users").document(userId).collection("inventory").document(id).update(updatedStock).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Reminder Updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditStock.this, Inventory.class);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}