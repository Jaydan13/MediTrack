package com.example.meditrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private List<RecordItem> recordList;

    public RecordsAdapter(List<RecordItem> recordList) {
        this.recordList = recordList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dosage, date, time;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recordMedName);
            dosage = itemView.findViewById(R.id.recordDosage);
            date = itemView.findViewById(R.id.recordDate);
            time = itemView.findViewById(R.id.recordTime);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordsAdapter.ViewHolder holder, int position) {
        RecordItem item = recordList.get(position);

        holder.name.setText(item.getName());
        holder.dosage.setText("Dosage: " + item.getDosage());
        holder.date.setText("Date: " + item.getDate());
        holder.time.setText("Time: " + item.getTime());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
