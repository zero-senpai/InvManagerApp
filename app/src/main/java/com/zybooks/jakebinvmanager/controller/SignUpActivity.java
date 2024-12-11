package com.zybooks.jakebinvmanager.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.jakebinvmanager.R;
import com.zybooks.jakebinvmanager.data.database.DatabaseExecutor;
import com.zybooks.jakebinvmanager.data.model.Role;
import com.zybooks.jakebinvmanager.data.model.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private DatabaseExecutor databaseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);

        // Initialize DatabaseExecutor
        databaseExecutor = new DatabaseExecutor(this);

        // Set up onClickListener for Sign Up button
        signUpButton.setOnClickListener(v -> signUpUser());
    }

    private void signUpUser() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Auto-assign role as "User"
        Role role = Role.USER;

        // Create new user and sign up using the DatabaseExecutor
        User newUser = new User(username, email, password, role); // Assuming you have a constructor that accepts these fields

        // Use the executor to handle database operations asynchronously
        databaseExecutor.signUpUser(newUser, new DatabaseExecutor.SignUpCallback() {
            @Override
            public void onSignUpSuccess() {
                // Show success message and proceed to login screen
                Toast.makeText(SignUpActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                finish(); // Close SignUpActivity
            }

            @Override
            public void onSignUpFailed() {
                // Show error message
                Toast.makeText(SignUpActivity.this, "Sign Up Failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
