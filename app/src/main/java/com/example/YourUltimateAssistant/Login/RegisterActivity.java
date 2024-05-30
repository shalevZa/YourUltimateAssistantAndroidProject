package com.example.YourUltimateAssistant.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Random r = new Random();
    RelativeLayout layout;
    EditText phoneEditText, nicknameEditText, emailEditText, passwordEditText;
    Button registerBtn;
    TextView backToLogin;
    CheckBox showPasswordSwitch;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Initialize UI elements
        registerBtn = findViewById(R.id.button);
        backToLogin = findViewById(R.id.textView2);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        nicknameEditText = findViewById(R.id.editTextTextPostalAddress);
        phoneEditText = findViewById(R.id.editTextPhone);
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        showPasswordSwitch = findViewById(R.id.checkBox);
        layout = findViewById(R.id.relative);

        // Create a notification channel
        createNotificationChannel();

        // Handle back to login click
        backToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Toggle password visibility
        showPasswordSwitch.setOnClickListener(view -> passwordEditText.setTransformationMethod(showPasswordSwitch.isChecked() ? null : PasswordTransformationMethod.getInstance()));

        // Handle register button click
        registerBtn.setOnClickListener(view -> {
            // Validate input fields
            if (validateFields()) {
                // Register user and show welcome notification
                createUser(emailEditText.getText().toString(), nicknameEditText.getText().toString(), phoneEditText.getText().toString(), passwordEditText.getText().toString());
                showWelcomeNotification();
                // Navigate to login activity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to create a notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Welcome notification", "Welcome notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    // Method to validate input fields
    public boolean validateFields() {

        boolean isValid = true;

        if (passwordEditText.length() < 8 && !passwordEditText.getText().toString().isEmpty()) {
            passwordRequirements(passwordEditText);
            isValid = false;
        }
        if (emailEditText.getText().toString().isEmpty()) {
            emailRequirements(emailEditText);
            isValid = false;
        }
        if (phoneEditText.length() < 10 && !phoneEditText.getText().toString().isEmpty()) {
            phoneRequirements(phoneEditText);
            isValid = false;
        }

        return isValid;

    }

    // Method to create a new user
    private void createUser(String email, String nickname, String phoneNumber, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            UserModel userModel = new UserModel(email, nickname, password, phoneNumber, false);
            FirebaseUtils.getUserFromFirestore().set(userModel).addOnSuccessListener(unused -> {
                Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();
            });
        });
    }

    // Method to display password requirements toast
    public void passwordRequirements(EditText password) {
        Toast.makeText(RegisterActivity.this, "The password must be at least 8 characters long", Toast.LENGTH_LONG).show();
    }

    // Method to display email requirements toast
    public void emailRequirements(EditText email) {
        Toast.makeText(RegisterActivity.this, "The email is invalid", Toast.LENGTH_SHORT).show();
    }

    // Method to display phone requirements toast
    public void phoneRequirements(EditText phone) {
        Toast.makeText(RegisterActivity.this, "The phone number is invalid", Toast.LENGTH_SHORT).show();
    }

    // Method to show welcome notification
    public void showWelcomeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(RegisterActivity.this, "Welcome notification");
        builder.setContentTitle("Your Ultimate Assistant");
        builder.setContentText("Hi " + nicknameEditText.getText() + ", Welcome to Your Ultimate Assistant");
        builder.setSmallIcon(R.drawable.your_ultimate_assistant_logo);
        builder.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(RegisterActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {return;}
        managerCompat.notify(new Random().nextInt(), builder.build());
    }



}
