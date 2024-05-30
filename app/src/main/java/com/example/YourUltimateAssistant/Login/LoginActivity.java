package com.example.YourUltimateAssistant.Login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.InApp.FirstAppActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // UI elements
    EditText emailEditText, passwordEditText;
    Button login;
    TextView forgotPassword, registerText;
    CheckBox showPasswordSwitch;
    ProgressBar progressBar;

    // Firebase authentication instance
    FirebaseAuth userAuth = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize UI elements
        login = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPasswordText);
        registerText = findViewById(R.id.registerText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        showPasswordSwitch = findViewById(R.id.showPasswordSwitch);
        progressBar = findViewById(R.id.progressBar);

        // Toggle password visibility
        showPasswordSwitch.setOnClickListener(view -> {
            passwordEditText.setTransformationMethod(showPasswordSwitch.isChecked() ? null : PasswordTransformationMethod.getInstance());
        });

        // Forgot password click listener
        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Register click listener
        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Login button click listener
        login.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (email.isEmpty())
                emailEditText.setError("Required field");
            else if (password.isEmpty())
                passwordEditText.setError("Required field");
            else
                loginIntoApp(email, password);
        });
    }

    // Method to handle login into the app
    private void loginIntoApp(String email, String password) {
        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUtils.getUserFromFirestore().get().addOnSuccessListener(documentSnapshot -> {
                    if (FirebaseUtils.isEmailVerify()) {
                        Toast.makeText(LoginActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, FirstAppActivity.class));
                        finish();
                        progressBar.setVisibility(View.INVISIBLE);
                        // Update user's password in Firestore
                        Map<String, Object> map = new HashMap<>();
                        map.put("password", password);
                        FirebaseUtils.getUserFromFirestore().update(map).addOnSuccessListener(unused -> {});
                    } else {
                        // Email verification pending
                        firebaseUser.sendEmailVerification();
                        userAuth.signOut();
                        showEmailVerificationDialog();
                    }
                });
            } else {
                // Failed login attempt
                Toast.makeText(LoginActivity.this, "Login failed. User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to show dialog for email verification
    private void showEmailVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email. You can't login without email verification.");
        builder.setPositiveButton("Continue", (dialog, which) -> {
            // Open email app
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Method to check if notifications are enabled
    public static boolean areNotificationsEnabled(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    // Method to navigate to app settings for permission
    public void goToSettingsForPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if the user is already logged in
        if (userAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, FirstAppActivity.class));
            finish();
        }
    }
}
