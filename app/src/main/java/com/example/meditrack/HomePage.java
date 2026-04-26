package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    ImageButton addReminder, recordsBtn, inventoryBtn, profileBtn;
    RecyclerView recyclerView;
    ReminderAdapter adapter;
    List<RemindMed> remindList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        recordsBtn = findViewById(R.id.recordsBtn);
        inventoryBtn = findViewById(R.id.inventoryBtn);
        profileBtn = findViewById(R.id.profileBtn);
        addReminder = findViewById(R.id.btnAddReminder);

        recyclerView = findViewById(R.id.recyclerViewReminder);
        remindList = new ArrayList<>();
        adapter = new ReminderAdapter(remindList);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadMedicines();

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, AddReminder.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines(); // refresh list
    }

    private void loadMedicines() {

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(HomePage.this, Login.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("reminder").get().addOnSuccessListener(queryDocumentSnapshots -> {

            remindList.clear();

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                String name = doc.getString("name");
                Long dosageLong = doc.getLong("dosage");
                int dosage = dosageLong != null ? dosageLong.intValue() : 0;
                String time = doc.getString("time");
                String interval = doc.getString("interval");
                String duration = doc.getString("duration");
                String intervalNo = doc.getString("intervalNo") != null ? doc.getString("intervalNo") : "0";
                String intervalType = doc.getString("intervalType") != null ? doc.getString("intervalType") : "";
                String durationNo = doc.getString("durationNo") != null ? doc.getString("durationNo") : "0";
                String durationType = doc.getString("durationType") != null ? doc.getString("durationType") : "";
                long startTime = doc.getLong("startTime") != null ? doc.getLong("startTime") : 0;

                remindList.add(new RemindMed(id, name, dosage, time, interval, duration, intervalNo, intervalType, durationNo, durationType,startTime, 0,0));
            }
            adapter.notifyDataSetChanged();
        });
    }
}
