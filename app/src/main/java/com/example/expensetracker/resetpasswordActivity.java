// Defines the package where this class is located
package com.example.expensetracker;

// Importing required classes from Android, Firebase libraries
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// resetpasswordActivity class definition, extending AppCompatActivity
public class resetpasswordActivity extends AppCompatActivity {

    // Declaring variables for input field, button and FirebaseAuth
    private EditText passwordEmail;
    private Button resetpassword;
    private FirebaseAuth firebaseAuth;

    // Default constructor
    public resetpasswordActivity() {
    }

    // onCreate method gets called when activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the layout of this activity to activity_resetpassword
        setContentView(R.layout.activity_resetpassword);

        // Assigning views from the layout to the variables
        passwordEmail=(EditText)findViewById(R.id.pass_email);
        resetpassword=(Button)findViewById(R.id.reset_pass);

        // Initializing Firebase Auth
        firebaseAuth=FirebaseAuth.getInstance();

        // Set an onClickListener for the resetpassword button
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=passwordEmail.getText().toString().trim();

                // Perform input validation
                if(useremail.equals(""))
                {
                    passwordEmail.setError("Email Required...",null);
                    return;
                }
                else
                {
                    // Send password reset email
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                // If email is sent successfully, start LoginActivity
                                Toast.makeText(resetpasswordActivity.this,"Email sent successfully..",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(resetpasswordActivity.this,LoginActivity.class));
                            }
                            else
                            {
                                // If email fails to send, display a message to the user
                                Toast.makeText(resetpasswordActivity.this,"Error sending email..",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
