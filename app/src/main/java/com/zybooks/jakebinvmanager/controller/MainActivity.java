package com.zybooks.jakebinvmanager.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.data.database.DatabaseExecutor;
import com.zybooks.jakebinvmanager.data.model.Item;
import com.zybooks.jakebinvmanager.data.model.User;
import com.zybooks.jakebinvmanager.ui.ItemAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button logoutButton;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DatabaseExecutor databaseExecutor;

    private User loggedInUser;  // To hold the logged-in user info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        logoutButton = findViewById(R.id.logoutButton);
        recyclerView = findViewById(R.id.recyclerView);

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
    }

    private void fetchUserByUsername(String username) {
        // Using the DatabaseExecutor to query the user from the database by username
        databaseExecutor.getUserByUsername(username, new DatabaseExecutor.UserCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    loggedInUser = user;
                    // Fetch items based on logged-in user's role
                    fetchItems();
                    // Show the welcome message on the main thread
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Welcome, " + user.getUsername(), Toast.LENGTH_SHORT).show());
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


    private void fetchItems() {
        // Fetch items from the database via DatabaseExecutor
        databaseExecutor.getItems(new DatabaseExecutor.ItemCallback() {
            @Override
            public void onItemsFetched(List<Item> items) {
                // Pass the role of the logged-in user to the adapter
                itemAdapter = new ItemAdapter(MainActivity.this, items, loggedInUser.getRole());
                recyclerView.setAdapter(itemAdapter);
            }

            @Override
            public void onItemsFetchedFailed() {
                // Show a message if the items fetch fails
                Toast.makeText(MainActivity.this, "Failed to fetch items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
