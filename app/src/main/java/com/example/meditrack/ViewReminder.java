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

public class ViewReminder extends AppCompatActivity {

    ImageButton backBtn;
    TextView viewMedName, viewDosage, viewTime, viewInterval, viewDuration;
    Button editRemindBtn, deleteRemindBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_reminder);

        backBtn = findViewById(R.id.backBtn);
        editRemindBtn = findViewById(R.id.editRemindBtn);
        deleteRemindBtn = findViewById(R.id.deleteRemindBtn);

        viewMedName = findViewById(R.id.viewMedName);
        viewDosage = findViewById(R.id.viewDosage);
        viewTime = findViewById(R.id.viewTime);
        viewInterval = findViewById(R.id.viewInterval);
        viewDuration = findViewById(R.id.viewDuration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String dosage = getIntent().getStringExtra("dosage");
        String time = getIntent().getStringExtra("time");
        String interval = getIntent().getStringExtra("interval");
        String duration = getIntent().getStringExtra("duration");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewReminder.this, HomePage.class);
                startActivity(intent);
            }
        });

        viewMedName.setText(name);
        viewDosage.setText(dosage);
        viewTime.setText(time);
        viewInterval.setText(interval);
        viewDuration.setText(duration);

        editRemindBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ViewReminder.this, EditReminder.class);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("dosage", dosage);
            intent.putExtra("time", time);
            intent.putExtra("interval", interval);
            intent.putExtra("duration", duration);

            startActivity(intent);
        });

        deleteRemindBtn.setOnClickListener(v -> {

            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).collection("reminder").document(id).delete().addOnSuccessListener(unused -> {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                finish(); // go back to HomePage
            });
        });
    }
}