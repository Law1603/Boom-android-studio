// Defines the package where this class is located
package com.example.expensetracker;

// Importing required classes from Android, Firebase and Java libraries
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

// RegistrationActivity class definition, extending AppCompatActivity
public class RegistrationActivity extends AppCompatActivity {

    // Declaring variables for input fields, buttons, FirebaseAuth, ProgressDialog
    private EditText email, password;
    private Button registerBtn;
    private TextView registerQn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    // Regular expression to check if password contains at least one capital letter and a symbol
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.*(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$");

    // onCreate method gets called when activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the layout of this activity to activity_registration
        setContentView(R.layout.activity_registration);

        // Assigning views from the layout to the variables
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        registerBtn = findViewById(R.id.registerBtn);
        registerQn = findViewById(R.id.registerQn);

        // Initializing Firebase Auth and ProgressDialog
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Setting onClickListener for the registerQn TextView
        registerQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set an onClickListener for the register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

                // Perform input validation
                if (TextUtils.isEmpty(emailString)) {
                    email.setError("Email is Required.....");
                } else if (TextUtils.isEmpty(passwordString)) {
                    password.setError("Password is Required.....");
                } else if (!isValidPassword(passwordString)) {
                    password.setError("Password must be at least 8 characters, include a capital letter and a symbol");
                } else {
                    // Show a ProgressDialog while registering
                    progressDialog.setMessage("REGISTRATION is IN PROGRESS");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    // Create a new user with email and password
                    mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // If registration is successful, send a verification email
                                sendEmailVerification();
                            } else {
                                // If registration fails, display a message to the user
                                Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    // Method to send a verification email to the new user
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // If email is sent successfully, sign out and start LoginActivity
                        Toast.makeText(RegistrationActivity.this, "Registration Successful. Verification mail sent successfully..", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    } else {
                        // If email fails to send, display a message to the user
                        Toast.makeText(RegistrationActivity.this, "Error occurred sending verification mail..", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // Method to check if the password is valid
    private boolean isValidPassword(String password) {
        return password.length() >= 8 && PASSWORD_PATTERN.matcher(password).matches();
    }
}
