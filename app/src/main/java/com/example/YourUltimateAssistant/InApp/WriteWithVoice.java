package com.example.YourUltimateAssistant.InApp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.Utils.InternetConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WriteWithVoice extends AppCompatActivity {

    // Declare UI elements
    ImageView speakImage, speechImage;
    ProgressBar progressBar;
    TextToSpeech textToSpeech;
    Button copyBtn, deleteBtn, saveToHistory;
    TextView backToMenu, textView;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_with_voice);

        //Set check if there internet connection and make popup if no
        InternetConnection.checkConnectionToNetwork(this);

        // Initialize UI components
        textView = findViewById(R.id.textView);
        speakImage = findViewById(R.id.speakImageBtn);
        speechImage = findViewById(R.id.speechImage);
        copyBtn = findViewById(R.id.copyBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        saveToHistory = findViewById(R.id.saveToHistoryBtn);
        backToMenu = findViewById(R.id.backToMenu);
        progressBar = findViewById(R.id.progressBar);

        // Initialize TextToSpeech engine
        textToSpeech = new TextToSpeech(this, i -> {
            if (i != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        // Button click listeners
        deleteBtn.setOnClickListener(view -> textView.setText(""));
        copyBtn.setOnClickListener(view -> {
            // Copy text to clipboard if not empty
            if (!textView.getText().toString().isEmpty()) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", textView.getText().toString());
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(WriteWithVoice.this, "Copied", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Text is empty", Toast.LENGTH_SHORT).show();
            }
        });
        backToMenu.setOnClickListener(view -> {
            // Navigate back to the main activity
            Intent intent = new Intent(WriteWithVoice.this, FirstAppActivity.class);
            startActivity(intent);
            finish();
        });
        speechImage.setOnClickListener(view -> {
            // Speak the text using TextToSpeech engine
            String text = textView.getText().toString();
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        });
        speakImage.setOnClickListener(v -> {
            // Initiate speech recognition
            speak();
        });
        saveToHistory.setOnClickListener(v -> {
            // Save text to Firestore collection if not empty
            if (!textView.getText().toString().isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUtils.getUserFromFirestore().get().addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> userHistory = new HashMap<>();
                    userHistory.put("text", textView.getText().toString());
                    FirebaseUtils.getUserFromFirestore().collection("writeWithYourVoice").add(userHistory).addOnSuccessListener(documentReference -> {
                        Toast.makeText(WriteWithVoice.this, "Added To History!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                });
            } else {
                Toast.makeText(WriteWithVoice.this, "Please enter text!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to initiate speech recognition
    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, speak something...");
        startActivityForResult(intent, 1000);
    }

    // Handle the result of speech recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            textView.setText("  " + (String) result.get(0));
        }
    }
}
