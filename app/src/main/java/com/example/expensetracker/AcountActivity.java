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

public class AcountActivity extends AppCompatActivity {

    private TextView userEmail;
    private Button logoutBtn, changePasswordBtn, deleteAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("My Account");
        setContentView(R.layout.activity_acount);

        userEmail = findViewById(R.id.userEmail);
        logoutBtn = findViewById(R.id.logoutBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userEmail.setText(mAuth.getCurrentUser().getEmail());

        logoutBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(AcountActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to Sign out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> logout())
                    .setNegativeButton("No", null)
                    .show();
        });

        changePasswordBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AcountActivity.this, Changepassword.class);
            startActivity(intent);
        });

        deleteAccountBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(AcountActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> deleteAccount(mAuth))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(AcountActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void deleteAccount(FirebaseAuth mAuth) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    logout();
                } else {
                    // Display a message to the user about the failure.
                    Toast.makeText(AcountActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
