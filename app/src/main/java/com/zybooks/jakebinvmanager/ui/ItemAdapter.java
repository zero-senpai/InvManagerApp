package com.zybooks.jakebinvmanager.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.controller.MainActivity;
import com.zybooks.jakebinvmanager.data.database.AppDatabase;
import com.zybooks.jakebinvmanager.data.database.DatabaseExecutor;
import com.zybooks.jakebinvmanager.data.model.Item;
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

    /**
     * Binds data to our UI elements in the layout resource for the item cards
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.textViewItemName.setText(item.getItemName());
        holder.textViewItemCount.setText(String.valueOf(item.getQuantity()));

        // Debugging user role to ensure it's correct
        Log.d("ItemAdapter", "User role: " + userRole);

        if (userRole == Role.ADMIN || userRole == Role.MANAGER) {
            holder.buttonPlus.setVisibility(View.VISIBLE);
            holder.buttonMinus.setVisibility(View.VISIBLE);

            // Handle plus button click (increase quantity)
            holder.buttonPlus.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                holder.textViewItemCount.setText(String.valueOf(item.getQuantity()));
                // Perform update operation in background thread
                DatabaseExecutor.updateItem(context, item, new DatabaseExecutor.UpdateCallback() {
                    @Override
                    public void onUpdateSuccess() {
                        // UI update is done on the main thread by default
                        Log.d("ItemAdapter", "Item updated successfully");
                    }

                    @Override
                    public void onUpdateFail() {
                        // Handle failure (e.g., show a toast)
                        Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            // Handle minus button click (decrease quantity)
            holder.buttonMinus.setOnClickListener(v -> {
                if (item.getQuantity() > 0) {
                    item.setQuantity(item.getQuantity() - 1);
                    holder.textViewItemCount.setText(String.valueOf(item.getQuantity()));
                    // Perform update operation in background thread
                    DatabaseExecutor.updateItem(context, item, new DatabaseExecutor.UpdateCallback() {
                        @Override
                        public void onUpdateSuccess() {
                            Log.d("ItemAdapter", "Item updated successfully");
                        }

                        @Override
                        public void onUpdateFail() {
                            Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(context, "Count cannot be negative", Toast.LENGTH_SHORT).show();
                }
            });

            // Handle delete button click
            holder.buttonDelete.setOnClickListener(v -> {
                DatabaseExecutor.deleteItem(context, item.getId(), new DatabaseExecutor.ItemDeleteCallback() {
                    @Override
                    public void onDeleteSuccess() {
                        // Remove the item from the list
                        items.remove(position);
                        // Notify the adapter about the item removal
                        notifyItemRemoved(position);
                        // Optionally, you can notify the range change if there are any other items after deletion
                        notifyItemRangeChanged(position, items.size());
                        // Show success message on the main thread
                        ((MainActivity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onDeleteFail() {
                        // Show the failure message on the main thread
                        ((MainActivity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show());
                    }
                });
            });
        } else {
            holder.buttonPlus.setVisibility(View.GONE);
            holder.buttonMinus.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);
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
        private ImageButton buttonDelete; // Added delete button reference

        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.itemName);
            textViewItemCount = itemView.findViewById(R.id.textViewItemCount);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonDelete = itemView.findViewById(R.id.buttonDelete); // Initialize delete button

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

