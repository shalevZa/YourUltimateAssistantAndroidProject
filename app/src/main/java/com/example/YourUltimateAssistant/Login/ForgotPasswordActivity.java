package com.example.YourUltimateAssistant.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.YourUltimateAssistant.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Firebase authentication instance
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // UI elements
    Button changeBtn;
    TextView backToLogin;
    EditText emailEditText;
    CheckBox showPasswordSwitch;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        // Initialize UI elements
        changeBtn = findViewById(R.id.button);
        backToLogin = findViewById(R.id.textView2);
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        showPasswordSwitch = findViewById(R.id.checkBox);

        // Navigate back to the login activity
        backToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Change password button click listener
        changeBtn.setOnClickListener(view -> {
            // Validate email field
            if (emailEditText.getText().toString().isEmpty()) {
                emailEditText.setError("Required field");
            } else {
                // Send password reset email
                firebaseAuth.sendPasswordResetEmail(emailEditText.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Password reset email sent successfully
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                // Show alert dialog
                                showAlertDialog();
                                // Sign out the user
                                firebaseAuth.signOut();
                            } else {
                                // Failed to send reset email
                                Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email. Check your email address", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    // Method to display a toast message for email requirements
    public void emailRequirements(EditText email) {
        Toast.makeText(ForgotPasswordActivity.this, "The email is invalid", Toast.LENGTH_LONG).show();
    }

    // Method to display an alert dialog after sending reset password email
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setTitle("We sent you a reset password message");
        builder.setMessage("For change your E-mail click on \"Continue\"");
        builder.setPositiveButton("Continue", (dialog, which) -> {
            // Navigate to the login activity
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();

            // Open email app to change email
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
