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

    //list that holds all stock items
    private List<StockItem> stockList;
    private static final int LOW_STOCK_THRESHOLD = 5;

    public InventoryAdapter(List<StockItem> stockList) {
        this.stockList = stockList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Create textview
        TextView name, quantity;

        //linking elements from item_stock.xml
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.stockMedName);
            quantity = itemView.findViewById(R.id.stockMedQuantity);
        }
    }

    @Override
    //Inflates the layout
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StockItem item = stockList.get(position);

        //Display date
        holder.name.setText(item.getName());
        if (item.getQuantity() <= LOW_STOCK_THRESHOLD) {
            holder.quantity.setText("Warning!! Low Stock: " + item.getQuantity());
        } else {
            holder.quantity.setText("Quantity: " + item.getQuantity());
        }

        if (item.getQuantity() <= LOW_STOCK_THRESHOLD) {
            holder.itemView.setBackgroundResource(R.drawable.low_stock_background);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.recycler_view_background);
        }

        //Layout becomes clickable
        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();

            //send data to View Stock
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