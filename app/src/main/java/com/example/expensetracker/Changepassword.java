// Defines the package where this class is located
package com.example.expensetracker;

// Importing required classes from Android and Firebase libraries
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

// Public class Changepassword which extends AppCompatActivity
public class Changepassword extends AppCompatActivity {

    // Declaring private variables for EditTexts and Button
    private EditText oldPasswordEditText, newPasswordEditText;
    private Button changePasswordButton;

    // Declaring FirebaseAuth and DatabaseReference for Firebase authentication and database interaction
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // onCreate method gets called when activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the layout of this activity to activity_changepassword
        setContentView(R.layout.activity_changepassword);

        // Associating the declared variables with actual elements in the layout
        oldPasswordEditText = findViewById(R.id.old_pass);
        newPasswordEditText = findViewById(R.id.new_pass);
        changePasswordButton = findViewById(R.id.btnChange);

        // Initializing FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initializing DatabaseReference instance
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setting onClickListener for the change password button
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fetching entered old and new passwords
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                // Validating new password, if empty show a toast message
                if (newPassword.isEmpty()) {
                    Toast.makeText(Changepassword.this, "Please enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Checking if the old and new passwords are the same
                if (oldPassword.equals(newPassword)) {
                    Toast.makeText(Changepassword.this, "New password must be different from the old password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Getting current user
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // If user is not null, trying to update password
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Checking if the password update is successful or not
                            if (task.isSuccessful()) {
                                // If successful, show success message
                                Toast.makeText(Changepassword.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // If not successful, show failure message
                                Toast.makeText(Changepassword.this, "Password change failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
