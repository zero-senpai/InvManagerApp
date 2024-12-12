package com.zybooks.jakebinvmanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.data.model.Item;
import com.zybooks.jakebinvmanager.data.database.AppDatabase;
import com.zybooks.jakebinvmanager.data.model.Role;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;
    private Context context;
    private Role userRole;

    // Constructor
    public ItemAdapter(Context context, List<Item> itemList, Role userRole) {
        this.context = context;
        this.items = itemList != null ? itemList : new ArrayList<>(); // Initialize as an empty list if null
        this.userRole = userRole;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.textViewItemName.setText(item.getItemName());
        holder.textViewItemCount.setText(String.valueOf(item.getQuantity()));

        if (userRole.equals("Admin") || userRole.equals("Manager")) {
            holder.buttonPlus.setVisibility(View.VISIBLE);
            holder.buttonMinus.setVisibility(View.VISIBLE);

            holder.buttonPlus.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                holder.textViewItemCount.setText(String.valueOf(item.getQuantity()));
                AppDatabase.getInstance(context).itemDao().updateItem(item);
            });

            holder.buttonMinus.setOnClickListener(v -> {
                if (item.getQuantity() > 0) {
                    item.setQuantity(item.getQuantity() - 1);
                    holder.textViewItemCount.setText(String.valueOf(item.getQuantity()));
                    AppDatabase.getInstance(context).itemDao().updateItem(item);
                } else {
                    Toast.makeText(context, "Count cannot be negative", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.buttonPlus.setVisibility(View.GONE);
            holder.buttonMinus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0; // Return 0 if the list is null
    }


    // Method to update the list of items
    public void updateItems(List<Item> newItems) {
        if (newItems != null) {
            this.items.clear();
            this.items.addAll(newItems);
            notifyDataSetChanged(); // This will refresh the RecyclerView
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewItemName;
        private TextView textViewItemCount;
        private Button buttonPlus;
        private Button buttonMinus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.itemName);
            textViewItemCount = itemView.findViewById(R.id.textViewItemCount);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);

            // Handle button clicks for adjusting item quantity
            buttonPlus.setOnClickListener(v -> updateItemCount(1));  // Increment item count
            buttonMinus.setOnClickListener(v -> updateItemCount(-1)); // Decrement item count
        }

        // Method to update item count
        private void updateItemCount(int change) {
            int currentCount = Integer.parseInt(textViewItemCount.getText().toString());
            currentCount += change;
            // Make sure the item count doesn't go below 0
            if (currentCount < 0) {
                currentCount = 0;
            }
            textViewItemCount.setText(String.valueOf(currentCount));

            // Optionally update your data model here if necessary
        }
    }

}
