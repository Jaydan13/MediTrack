package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Records extends AppCompatActivity {

    ImageButton homeBtn, inventoryBtn, profileBtn, pdfBtn;
    RecyclerView recyclerView;
    RecordsAdapter adapter;
    List<RecordItem> recordList;
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

        recyclerView = findViewById(R.id.recyclerViewRecords);
        recordList = new ArrayList<>();
        adapter = new RecordsAdapter(recordList);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

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

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords(); // refresh list
    }

    private void loadRecords() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(Records.this, Login.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("records").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {

            recordList.clear();

            for (DocumentSnapshot doc: queryDocumentSnapshots) {

                String name = doc.getString("name");
                String dosage = doc.getString("dosage");
                String date = doc.getString("date");
                String time = doc.getString("time");

                recordList.add(new RecordItem(name, dosage, date, time));
            }
            adapter.notifyDataSetChanged();
        });
    }
}