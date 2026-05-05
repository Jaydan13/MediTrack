package com.example.meditrack;

import com.example.meditrack.StockNotificationHelper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<RemindMed> remindList;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public ReminderAdapter(List<RemindMed> remindList) {
        this.remindList = remindList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dosage, time;
        Button takenBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.remindMedName);
            dosage = itemView.findViewById(R.id.remindDosage);
            time = itemView.findViewById(R.id.remindTime);
            takenBtn = itemView.findViewById(R.id.takenBtn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remind_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderAdapter.ViewHolder holder, int position) {
        RemindMed item = remindList.get(position);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        holder.name.setText(item.getName());
        holder.dosage.setText("Dosage: " + item.getDosage());
        holder.time.setText("Time: " + item.getTime());

        holder.takenBtn.setOnClickListener(v -> {

            holder.takenBtn.setEnabled(false);

            saveRecord(v.getContext(), item);

            String userId = mAuth.getCurrentUser().getUid();

            String nameLower = item.getName().toLowerCase();
            db.collection("users").document(userId).collection("inventory").whereEqualTo("nameLower", nameLower).get().addOnSuccessListener(querySnapshot -> {

                for (DocumentSnapshot doc : querySnapshot) {

                    Long currentQtyLong = doc.getLong("quantity");
                    int currentQty = currentQtyLong != null ? currentQtyLong.intValue() : 0;

                    int newQty = currentQty - item.getDosage();

                    if (newQty < 0) newQty = 0;

                    doc.getReference().update("quantity", newQty);

                    if (newQty <= 5) {

                        Context context = v.getContext();

                        String message = item.getName() + " is low in stock (" + newQty + ")";

                        StockNotificationHelper.sendLowStockNotification(context, message);
                    }
                }
            });

            AlarmHelper.cancelAlarm(v.getContext(), item.getId());

            long intervalMillis;

            int intervalNo = Integer.parseInt(item.getIntervalNo());

            if (item.getIntervalType().equals("Hours")) {
                intervalMillis = intervalNo * 60 * 60 * 1000L;
            } else if (item.getIntervalType().equals("Days")) {
                intervalMillis = intervalNo * 24 * 60 * 60 * 1000L;
            } else {
                intervalMillis = intervalNo * 7 * 24 * 60 * 60 * 1000L;
            }

            long newTimeMillis = item.getStartTime() + intervalMillis;

            db.collection("users").document(userId).collection("reminder").document(item.getId()).update("startTime", newTimeMillis);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = sdf.format(newTimeMillis);

            db.collection("users").document(userId).collection("reminder").document(item.getId()).update("time", timeString);

            holder.time.setText("Time: " + timeString);

            AlarmHelper.setAlarm(v.getContext(), item.getId(), item.getHour(), item.getMinute(),
                    item.getName(), item.getDosage(), item.getIntervalNo(), item.getIntervalType(),
                    item.getDurationNo(), item.getDurationType(), item.getStartTime()
            );

            holder.takenBtn.postDelayed(() -> {
                holder.takenBtn.setEnabled(true);
            }, 3000);
        });

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();

            Intent intent = new Intent(context, ViewReminder.class);

            intent.putExtra("id", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("dosage", item.getDosage());
            intent.putExtra("time", item.getTime());
            intent.putExtra("interval", item.getInterval());
            intent.putExtra("duration", item.getDuration());
            intent.putExtra("startTime", item.getStartTime());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return remindList.size();
    }

    private void saveRecord(Context context, RemindMed item) {

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> record = new HashMap<>();
        record.put("name", item.getName());
        record.put("dosage", item.getDosage());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        record.put("date", date);
        record.put("time", time);
        record.put("timestamp", System.currentTimeMillis());

        db.collection("users").document(userId).collection("records").add(record).addOnSuccessListener(documentReference -> {
            Toast.makeText(context, "Marked as taken", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error saving record", Toast.LENGTH_SHORT).show();
        });
    }
}
