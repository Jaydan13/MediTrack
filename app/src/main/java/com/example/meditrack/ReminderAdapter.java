package com.example.meditrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<RemindMed> remindList;

    public ReminderAdapter(List<RemindMed> remindList) {
        this.remindList = remindList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dosage, time;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.remindMedName);
            dosage = itemView.findViewById(R.id.remindDosage);
            time = itemView.findViewById(R.id.remindTime);
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

        holder.name.setText(item.getName());
        holder.dosage.setText("Dosage: " + item.getDosage());
        holder.time.setText("Time: " + item.getTime());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();

            Intent intent = new Intent(context, ViewReminder.class);

            intent.putExtra("id", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("dosage", item.getDosage());
            intent.putExtra("time", item.getTime());
            intent.putExtra("interval", item.getInterval());
            intent.putExtra("duration", item.getDuration());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return remindList.size();
    }
}
