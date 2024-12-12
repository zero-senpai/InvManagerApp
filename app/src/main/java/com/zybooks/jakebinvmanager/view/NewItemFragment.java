package com.zybooks.jakebinvmanager.view;
import androidx.recyclerview.widget.RecyclerView;
import com.zybooks.jakebinvmanager.controller.MainActivity;
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

public class NewItemFragment extends Fragment {

    private EditText itemNameEditText, serialNumberEditText, priceEditText, quantityEditText, photoEditText;
    private Button saveButton, closeButton;  // Added closeButton

    private DatabaseExecutor databaseExecutor;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_item, container, false);

        // Initialize UI components
        itemNameEditText = view.findViewById(R.id.itemNameEditText);
        serialNumberEditText = view.findViewById(R.id.serialNumberEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        photoEditText = view.findViewById(R.id.photoEditText);
        saveButton = view.findViewById(R.id.saveButton);
        closeButton = view.findViewById(R.id.closeButton);  // Initialize closeButton

        // Initialize DatabaseExecutor
        databaseExecutor = DatabaseExecutor.getInstance(getContext());

        recyclerView = getActivity().findViewById(R.id.recyclerView);
        // Set onClickListener for Save button
        saveButton.setOnClickListener(v -> createItem());

        // Set onClickListener for Close button
        closeButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    private void createItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();
        String serialNumberStr = serialNumberEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String photo = photoEditText.getText().toString().trim();

        // Check for empty fields
        if (itemName.isEmpty() || quantityStr.isEmpty() || serialNumberStr.isEmpty() || priceStr.isEmpty() || photo.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse numeric values safely
            int quantity = Integer.parseInt(quantityStr);
            long serialNumber = Long.parseLong(serialNumberStr);
            double price = Double.parseDouble(priceStr);

            // Create a new item with parsed data
            Item newItem = new Item(itemName, serialNumber, price, quantity, photo);

            // Log the new item for debugging
            Log.d("NewItemFragment", "Creating item: " + newItem.toString());

            // Insert the item into the database
            databaseExecutor.createItem(newItem, new DatabaseExecutor.ItemCreationCallback() {
                @Override
                public void onItemCreated() {
                    Log.d("NewItemFragment", "Item successfully inserted into database");
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Item created successfully", Toast.LENGTH_SHORT).show();

                            // Refresh RecyclerView and close fragment
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).refreshRecyclerView();  // Refresh the list
                            }
                            closeFragment();
                        });
                    }
                }

                @Override
                public void onItemCreationFailed() {
                    Log.e("NewItemFragment", "Item creation failed!");
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Item creation failed!", Toast.LENGTH_SHORT).show();
                        });
                    }
                    // Ensure to call closeFragment() even on failure
                    closeFragment();
                }
            });
        } catch (NumberFormatException e) {
            // Log and show error for invalid numeric input
            Log.e("NewItemFragment", "Invalid numeric input", e);
            Toast.makeText(getContext(), "Please enter valid numeric values for quantity, serial number, and price", Toast.LENGTH_SHORT).show();
        }
    }


    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().onBackPressed();  // Close the fragment

            // Make sure RecyclerView is visible again
            recyclerView.setVisibility(View.VISIBLE);  // Ensure RecyclerView is visible
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshRecyclerView();  // Refresh RecyclerView
            }
        }
    }

}
