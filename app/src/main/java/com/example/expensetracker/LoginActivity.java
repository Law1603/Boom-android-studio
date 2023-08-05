// Defines the package where this class is located
package com.example.expensetracker;

// Importing required classes from Android, Firebase and Java libraries
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// LoginActivity class definition, extending AppCompatActivity
public class LoginActivity extends AppCompatActivity {

    // Declaring variables for input fields, buttons, FirebaseAuth, ProgressDialog, SharedPreferences
    private EditText email, password;
    private Button loginBtn;
    private TextView loginQn;
    private CheckBox remember;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // onCreate method gets called when activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the layout of this activity to activity_login
        setContentView(R.layout.activity_login);

        // Creating an AuthStateListener to respond to changes in the user's sign-in state
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // If user is signed in, start MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        // Assigning views from the layout to the variables
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        loginBtn = findViewById(R.id.loginBtn);
        loginQn = findViewById(R.id.loginQn);
        remember = findViewById(R.id.checkBox2);
        TextView mforget_password = findViewById(R.id.forgot_password);

        // Initializing Firebase Auth and ProgressDialog
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Setting onClickListener for the loginQn and mforget_password TextViews
        loginQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        mforget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, resetpasswordActivity.class);
                startActivity(intent);
            }
        });

        // Initializing SharedPreferences and its Editor
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // If "remember" checkbox was previously checked, autofill email and password fields
        boolean rememberMeChecked = sharedPreferences.getBoolean("remember", false);
        if (rememberMeChecked) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");

            if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPassword)) {
                email.setText(savedEmail);
                password.setText(savedPassword);
                remember.setChecked(true);
            }
        }

        // Set a listener for changes in checkbox state
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If checkbox is checked, save email and password to SharedPreferences
                    editor.putBoolean("remember", true);
                    editor.putString("email", email.getText().toString().trim());
                    editor.putString("password", password.getText().toString().trim());
                } else {
                    // If checkbox is not checked, clear SharedPreferences
                    editor.clear();
                }
                editor.apply();
            }
        });

        // Set an onClickListener for the login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString().trim();
                String passwordString = password.getText().toString().trim();

                // Perform input validation
                if (TextUtils.isEmpty(emailString)) {
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(passwordString)) {
                    password.setError("Password is required");
                    return;
                }

                // Show a ProgressDialog while signing in
                progressDialog.setMessage("Logging in...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                // Attempt to sign in with email and password
                mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If sign-in is successful, check if email is verified
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // If email is verified, start MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign-in fails, display a message to the user
                            Toast.makeText(LoginActivity.this, "Failed to log in. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
}
