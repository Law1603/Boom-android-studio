// Import required libraries and packages
package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Define the AcountActivity class which extends AppCompatActivity
public class AcountActivity extends AppCompatActivity {

    // Define variables for UI elements
    private TextView userEmail;
    private Button logoutBtn, changePasswordBtn, deleteAccountBtn;

    // Override the onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the action bar title
        getSupportActionBar().setTitle("My Account");

        // Connect this activity to its layout
        setContentView(R.layout.activity_acount);

        // Connect the variables to the UI elements
        userEmail = findViewById(R.id.userEmail);
        logoutBtn = findViewById(R.id.logoutBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);

        // Create an instance of FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Display the user's email on the screen
        userEmail.setText(mAuth.getCurrentUser().getEmail());

        // When the logout button is clicked
        logoutBtn.setOnClickListener(view -> {
            // Show an alert dialog to confirm if the user wants to log out
            new AlertDialog.Builder(AcountActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to Sign out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> logout())
                    .setNegativeButton("No", null)
                    .show();
        });

        // When the change password button is clicked
        changePasswordBtn.setOnClickListener(view -> {
            // Start the Changepassword activity
            Intent intent = new Intent(AcountActivity.this, Changepassword.class);
            startActivity(intent);
        });

        // When the delete account button is clicked
        deleteAccountBtn.setOnClickListener(view -> {
            // Show an alert dialog to confirm if the user wants to delete the account
            new AlertDialog.Builder(AcountActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> deleteAccount(mAuth))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    // Define a method to log out the user
    private void logout() {
        // Sign out from FirebaseAuth
        FirebaseAuth.getInstance().signOut();
        // Start the LoginActivity and clear the top activities
        Intent intent = new Intent(AcountActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Finish this activity
        finish();
    }

    // Define a method to delete the user's account
    private void deleteAccount(FirebaseAuth mAuth) {
        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Try to delete the user
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // If the task is successful, log out the user
                    logout();
                } else {
                    // If the task fails, display a message
                    Toast.makeText(AcountActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
