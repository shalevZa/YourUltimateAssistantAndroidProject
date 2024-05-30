package com.example.YourUltimateAssistant.History;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.YourUltimateAssistant.InApp.FirstAppActivity;
import com.example.YourUltimateAssistant.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryActivity extends AppCompatActivity {

    // Fragments for different history views
    WriteWithVoiceHistoryFragment writeWithVoiceHistoryFragment = new WriteWithVoiceHistoryFragment();
    TextToSpeechHistoryFragment textToSpeechHistoryFragment = new TextToSpeechHistoryFragment();
    UsersChatHistoryFragment usersChatHistoryFragment = new UsersChatHistoryFragment();

    BottomNavigationView bottomNavigationView; // Bottom navigation view
    RelativeLayout frame; // Frame layout for fragment container
    ImageButton backToHome; // Button to navigate back to the home activity

    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frame = findViewById(R.id.frame);
        backToHome = findViewById(R.id.backToHome);

        // Set the opening page to the WriteWithVoiceHistoryFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, writeWithVoiceHistoryFragment).commit();

        // Set listener for bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Switch fragments based on selected item
            if (item.getItemId() == R.id.writeWithVoice) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, writeWithVoiceHistoryFragment).commit();
                return true;
            }
            if (item.getItemId() == R.id.textToSpeech) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, textToSpeechHistoryFragment).commit();
                return true;
            }
            if (item.getItemId() == R.id.UsersChat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, usersChatHistoryFragment).commit();
                return true;
            }
            return false;
        });

        // Set click listener for backToHome button
        backToHome.setOnClickListener(v -> {
            // Start FirstAppActivity and finish this activity
            startActivity(new Intent(HistoryActivity.this, FirstAppActivity.class));
            finish();
        });
    }
}
