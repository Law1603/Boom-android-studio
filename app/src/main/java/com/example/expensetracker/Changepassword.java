package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Changepassword extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText;
    private Button changePasswordButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        oldPasswordEditText = findViewById(R.id.old_pass);
        newPasswordEditText = findViewById(R.id.new_pass);
        changePasswordButton = findViewById(R.id.btnChange);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                if (newPassword.isEmpty()) {
                    Toast.makeText(Changepassword.this, "Please enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (oldPassword.equals(newPassword)) {
                    Toast.makeText(Changepassword.this, "New password must be different from the old password", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Changepassword.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Changepassword.this, "Password change failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
