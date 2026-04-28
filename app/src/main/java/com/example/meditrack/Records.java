package com.example.meditrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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

    ImageButton homeBtn, inventoryBtn, settingsBtn, pdfBtn;
    String userId;
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

        ThemeHelper.applyTheme(this);

        homeBtn = findViewById(R.id.homeBtn);
        inventoryBtn = findViewById(R.id.inventoryBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        pdfBtn = findViewById(R.id.pdfBtn);

        recyclerView = findViewById(R.id.recyclerViewRecords);
        recordList = new ArrayList<>();
        adapter = new RecordsAdapter(recordList);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadRecords();

        pdfBtn.setOnClickListener(v -> {
            exportPDF();
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Records.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Records.this, Inventory.class);
                startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Records.this, Settings.class);
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

        userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("records").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {

            if (error != null || value == null) return;

            recordList.clear();

            for (DocumentSnapshot doc : value.getDocuments()) {

                String name = doc.getString("name");
                Long dosageLong = doc.getLong("dosage");
                int dosage = dosageLong != null ? dosageLong.intValue() : 0;
                String date = doc.getString("date");
                String time = doc.getString("time");
                long timestamp = doc.getLong("timestamp");

                recordList.add(new RecordItem(name, dosage, date, time, timestamp));
            }

            adapter.notifyDataSetChanged();
        });
    }
    private void exportPDF() {
        Toast.makeText(this, "Export started", Toast.LENGTH_SHORT).show();

        userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("records").get().addOnSuccessListener(queryDocumentSnapshots -> {

            List<RecordPDF> records = new ArrayList<>();

            for (DocumentSnapshot doc : queryDocumentSnapshots) {

                String name = doc.getString("name");
                String date = doc.getString("date");
                String time = doc.getString("time");

                Long dosageLong = doc.getLong("dosage");
                int dosage = dosageLong != null ? dosageLong.intValue() : 0;

                records.add(new RecordPDF(name, dosage, date, time));
            }

            String filePath = PDFHelper.generatePDF(this, records);

            if (filePath != null) {
                Toast.makeText(this, "PDF saved!", Toast.LENGTH_SHORT).show();

                Uri uri = Uri.parse(filePath);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, "Share PDF"));

            } else {
                Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
            }

        });
    }
}