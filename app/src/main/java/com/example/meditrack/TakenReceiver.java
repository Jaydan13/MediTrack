package com.example.meditrack;

import com.example.meditrack.StockNotificationHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TakenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String userId = intent.getStringExtra("userId"); // ✅ PASS THIS IN INTENT
        String reminderId = intent.getStringExtra("reminderId");

        String name = intent.getStringExtra("name");
        int dosage = intent.getIntExtra("dosage", 0);
        String intervalStr = intent.getStringExtra("interval");
        String durationStr = intent.getStringExtra("duration");

        if (userId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // ✅ 1. CREATE RECORD
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        Map<String, Object> record = new HashMap<>();
        record.put("name", name);
        record.put("dosage", dosage);
        record.put("date", date);
        record.put("time", time);
        record.put("timestamp", System.currentTimeMillis()); // 🔥 REQUIRED

        db.collection("users")
                .document(userId)
                .collection("records")
                .add(record);

        db.collection("users")
                .document(userId)
                .collection("inventory")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot doc : snapshot) {

                        Long currentQty = doc.getLong("quantity");
                        if (currentQty == null) return;

                        long newQty = currentQty - dosage;

                        if (newQty < 0) newQty = 0;

                        doc.getReference().update("quantity", newQty);

                        if (newQty <= 5) {

                            String message = name + " is low in stock (" + newQty + ")";

                            StockNotificationHelper.sendLowStockNotification(context, message);
                        }
                    }
                });

        // ✅ 2. DELETE CURRENT REMINDER
        if (reminderId != null) {
            db.collection("users")
                    .document(userId)
                    .collection("reminder")
                    .document(reminderId)
                    .delete();
        }

        // ✅ 3. CREATE NEXT REMINDER
        if (intervalStr != null && durationStr != null) {

            int interval = 1;
            int remaining = 0;

            try {
                interval = Integer.parseInt(intervalStr);
                remaining = Integer.parseInt(durationStr) - 1;
            } catch (Exception e) {
                return; // stop if invalid
            }

            if (remaining > 0) {

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, interval);

                Map<String, Object> newReminder = new HashMap<>();
                newReminder.put("name", name);
                newReminder.put("dosage", dosage);
                newReminder.put("interval", intervalStr);
                newReminder.put("duration", String.valueOf(remaining));

                // ⚠️ TEMP (works but not ideal)
                newReminder.put("time", new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(calendar.getTime()));

                // 🔥 BETTER (future-proof)
                newReminder.put("timestamp", calendar.getTimeInMillis());

                db.collection("users")
                        .document(userId)
                        .collection("reminder")
                        .add(newReminder);
            }
        }

        Toast.makeText(context, "Medicine taken", Toast.LENGTH_SHORT).show();
    }
}