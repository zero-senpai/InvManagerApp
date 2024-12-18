package com.zybooks.jakebinvmanager.controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.data.database.DatabaseExecutor;
import com.zybooks.jakebinvmanager.data.model.Item;
import com.zybooks.jakebinvmanager.data.model.Role;
import com.zybooks.jakebinvmanager.data.model.User;
import com.zybooks.jakebinvmanager.ui.ItemAdapter;
import com.zybooks.jakebinvmanager.view.NewItemFragment;
import com.zybooks.jakebinvmanager.view.SettingsFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private Button logoutButton;
    private Button settingsButton;
    private Button addItemButton;  // Button to add an item
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DatabaseExecutor databaseExecutor;
    private FrameLayout fragmentContainer;  // Fragment container to hold the Add Item fragment
    private Role userRole;
    private User loggedInUser;  // To hold the logged-in user info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //branch: include to ask for the permissions on MainActivity load in
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
        // Initialize UI components
        logoutButton = findViewById(R.id.logoutButton);
        addItemButton = findViewById(R.id.addItemButton);
        settingsButton = findViewById(R.id.settingsButton);
        recyclerView = findViewById(R.id.recyclerView);

        fragmentContainer = findViewById(R.id.fragmentContainer); // Get reference to fragment container

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DatabaseExecutor
        databaseExecutor = DatabaseExecutor.getInstance(this);

        // Get logged-in user info from the Intent that started this activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            if (username != null && !username.isEmpty()) {
                // If username is passed, fetch the user info from the database
                fetchUserByUsername(username);
            }
        }

        // Set up logout button to return to login screen
        logoutButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

        // Set onClickListener for Add Item button (visible only for ADMIN and MANAGER)
        addItemButton.setOnClickListener(v -> showAddItemFragment());
        // Set click listener for Settings button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsFragment();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchItems(); // Ensure this method re-queries the database
    }

    private void fetchUserByUsername(String username) {
        // Using the DatabaseExecutor to query the user from the database by username
        databaseExecutor.getUserByUsername(username, new DatabaseExecutor.UserCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    loggedInUser = user;
                    userRole = loggedInUser.getRole();
                    fetchItems();

                    // Show the welcome message on the main thread
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Welcome, " + user.getUsername(), Toast.LENGTH_SHORT).show());

                    // Show or hide the "Add Item" button based on the user's role
                    updateAddItemButtonVisibility();
                } else {
                    // If user is not found, show the error message on the main thread
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onUserFetchedFailed() {
                // Show error if user fetching fails on the main thread
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch user", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateAddItemButtonVisibility() {
        // Show the "Add Item" button only for ADMIN and MANAGER roles
        if (loggedInUser != null && (loggedInUser.getRole() == Role.ADMIN || loggedInUser.getRole() == Role.MANAGER)) {
            addItemButton.setVisibility(View.VISIBLE);
        } else {
            addItemButton.setVisibility(View.GONE);
        }
    }

    private void openSettingsFragment() {
        // Hide RecyclerView when settings fragment is shown
        recyclerView.setVisibility(View.GONE);

        // Make the fragment container visible
        fragmentContainer.setVisibility(View.VISIBLE);

        // Create an instance of SettingsFragment
        SettingsFragment settingsFragment = new SettingsFragment();

        // Begin a FragmentTransaction to add the fragment dynamically
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the current fragment with SettingsFragment inside the fragment container
        transaction.replace(R.id.fragmentContainer, settingsFragment);

        // Add the transaction to the backstack to allow navigation back
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    public void closeSettingsFragment() {
        // Hide the fragment container
        fragmentContainer.setVisibility(View.GONE);

        // Show the RecyclerView again
        recyclerView.setVisibility(View.VISIBLE);

        // Optionally, remove the fragment
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer))
                .commit();

        // Refresh RecyclerView to show updated list of items
        refreshRecyclerView();
    }






    private void fetchItems() {
        databaseExecutor.getItems(new DatabaseExecutor.ItemCallback() {

            @Override
            public void onItemsFetched(List<Item> items) {
                Log.d("MainActivity", "Fetched items: " + items);

                // Initialize the adapter only once (if not already initialized)
                if (itemAdapter == null) {
                    itemAdapter = new ItemAdapter(MainActivity.this, items, userRole); // Pass the correct context and role
                    recyclerView.setAdapter(itemAdapter); // Set the adapter to the RecyclerView
                } else {
                    itemAdapter.updateItems(items); // If the adapter is already initialized, update it
                }

                runOnUiThread(() -> {
                    // Update the RecyclerView with the fetched items
                    if (items != null && !items.isEmpty()) {
                        itemAdapter.notifyDataSetChanged(); // This refreshes the RecyclerView
                    } else {
                        // Handle the case where items are null or empty
                        Toast.makeText(MainActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onItemsFetchedFailed() {
                Log.e("MainActivity", "Failed to fetch items from database");
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Failed to fetch items", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showAddItemFragment() {
        // Hide RecyclerView when fragment is shown
        recyclerView.setVisibility(View.GONE);

        // Show the fragment container and set its height to match the parent
        fragmentContainer.setVisibility(View.VISIBLE);

        // Create a new instance of NewItemFragment
        NewItemFragment newItemFragment = new NewItemFragment();

        // Begin a fragment transaction and replace the current fragment with NewItemFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, newItemFragment);  // Replace fragment container
        transaction.addToBackStack(null);  // Add to back stack to allow navigation
        transaction.commit();
    }

    public void hideAddItemFragment() {
        // Hide the fragment container again after the item is created or when close is clicked
        fragmentContainer.setVisibility(View.GONE);

        // Show the RecyclerView again
        recyclerView.setVisibility(View.VISIBLE);

        // Optionally, remove the fragment
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer))
                .commit();

        // Refresh RecyclerView to show updated list of items
        refreshRecyclerView();
    }

    public void refreshRecyclerView() {
        // Fetch the latest list of items and refresh the RecyclerView
        fetchItems();
    }

// MainActivity.java

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied. Notifications will not be sent.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
