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

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;
    private Context context;
    private Role userRole;

    // Constructor
    public ItemAdapter(Context context, List<Item> itemList, Role userRole) {
        this.context = context;
        this.items = itemList;
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
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textViewItemName, textViewItemCount;
        Button buttonPlus, buttonMinus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemCount = itemView.findViewById(R.id.textViewItemCount);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
        }
    }
}
