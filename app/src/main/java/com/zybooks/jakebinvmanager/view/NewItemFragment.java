package com.zybooks.jakebinvmanager.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.data.database.DatabaseExecutor;
import com.zybooks.jakebinvmanager.data.model.Item;

import java.util.List;

public class NewItemFragment extends Fragment {

    private EditText itemNameEditText, serialNumberEditText, priceEditText, quantityEditText, photoEditText;
    private Button saveButton;

    private DatabaseExecutor databaseExecutor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_item, container, false);

        // Initialize UI components
        itemNameEditText = view.findViewById(R.id.itemNameEditText);
        serialNumberEditText = view.findViewById(R.id.serialNumberEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);  // Add quantity field
        photoEditText = view.findViewById(R.id.photoEditText);
        saveButton = view.findViewById(R.id.saveButton);

        // Initialize DatabaseExecutor
        databaseExecutor = DatabaseExecutor.getInstance(getContext());

        // Set onClickListener for Save button
        saveButton.setOnClickListener(v -> createItem());

        return view;
    }

    private void createItem() {
        String itemName = itemNameEditText.getText().toString();
        String quantityStr = quantityEditText.getText().toString();
        String serialNumberStr = serialNumberEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String photo = photoEditText.getText().toString();  // Get the photo field

        if (itemName.isEmpty() || quantityStr.isEmpty() || serialNumberStr.isEmpty() || priceStr.isEmpty() || photo.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse values from the input fields
        int quantity = Integer.parseInt(quantityStr);
        double serialNumber = Double.parseDouble(serialNumberStr);
        double price = Double.parseDouble(priceStr);

        // Create the new item
        Item newItem = new Item(itemName, serialNumber, price, quantity, photo);

        // Insert the item into the database
        databaseExecutor.createItem(newItem, new DatabaseExecutor.ItemCreationCallback() {
            @Override
            public void onItemCreated() {
                // Show success message on UI thread
                Toast.makeText(getContext(), "Item created successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemCreationFailed() {
                Log.e("NewItemFragment", "Item creation failed!"); // Add logging
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Item creation failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }


        });
    }
}
