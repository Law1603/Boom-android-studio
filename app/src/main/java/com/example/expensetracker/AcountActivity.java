package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        changePasswordBtn = findViewById(R.id.changePasswordBtn); // Assume you have this button in your XML
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn); // Assume you have this button in your XML

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userEmail.setText(mAuth.getCurrentUser().getEmail());

        logoutBtn.setOnClickListener(view -> new AlertDialog.Builder(AcountActivity.this).setTitle("BOOM")
                .setMessage("Are you sure you want to Sign out!!!").setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(AcountActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null).show());

        changePasswordBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AcountActivity.this, Changepassword.class);
            startActivity(intent);
        });

        deleteAccountBtn.setOnClickListener(view -> new AlertDialog.Builder(AcountActivity.this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(AcountActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle failure
                            }
                        });
                    }
                })
                .setNegativeButton("No", null).show());
    }
}
