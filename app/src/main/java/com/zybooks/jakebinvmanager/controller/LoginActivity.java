package com.zybooks.jakebinvmanager.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.data.model.Role;
import com.zybooks.jakebinvmanager.data.model.User;
import com.zybooks.jakebinvmanager.data.database.DatabaseExecutor;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, signUpButton, createAdminButton;
    private DatabaseExecutor databaseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        createAdminButton = findViewById(R.id.createAdminButton);

        // Initialize DatabaseExecutor
        databaseExecutor = DatabaseExecutor.getInstance(LoginActivity.this);

        // Set onClickListener for Login button
        loginButton.setOnClickListener(v -> loginUser());

        // Set onClickListener for SignUp button
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Set onClickListener for Create Admin button
        createAdminButton.setOnClickListener(v -> createTestAdminUser());
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_LONG).show();
            return;
        }

        // Use DatabaseExecutor to check the credentials in the background
        databaseExecutor.loginUser(username, password, new DatabaseExecutor.LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                // If login is successful, pass the username to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", user.getUsername());
                startActivity(intent);
                finish();  // Close LoginActivity to prevent going back to it
            }

            @Override
            public void onLoginFailed() {
                // Post the Toast message to the main thread
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed. Invalid username or password.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Create a test Admin user
    private void createTestAdminUser() {
        String username = "admin";
        String email = "admin@admin.com";
        String password = "admin123";
        Role role = Role.ADMIN;  // Set role to Admin

        User adminUser = new User(username, email, password, role);  // Create the User object

        // Insert the admin user into the database (ensure that this is done asynchronously)
        databaseExecutor.createUser(adminUser, new DatabaseExecutor.TestUserCallback() {
            @Override
            public void onUserCreated() {
                // Show success message on UI thread
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Admin user created successfully!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onUserCreationFailed() {
                // Show error message on UI thread
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Failed to create admin user", Toast.LENGTH_SHORT).show());
            }

        });
    }

}
