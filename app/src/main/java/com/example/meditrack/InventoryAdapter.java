package com.example.meditrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<StockItem> stockList;

    public InventoryAdapter(List<StockItem> stockList) {
        this.stockList = stockList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.stockMedName);
            quantity = itemView.findViewById(R.id.stockMedQuantity);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StockItem item = stockList.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText("Quantity: " + item.getQuantity());

        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent = new Intent(context, ViewStock.class);

            intent.putExtra("id", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("quantity", item.getQuantity());
            intent.putExtra("expDate", item.getExpDate());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}