package com.example.YourUltimateAssistant.InApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.Utils.InternetConnection;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextToSpeechActivity extends AppCompatActivity {

    // Declare UI elements
    ImageView speakImage;
    TextToSpeech textToSpeech;
    EditText writtenText;
    TextView backToMenu;
    Button deleteBtn, saveToHistoryBtn;
    ProgressBar progressBar;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_to_speech_activity);

        //Set check if there internet connection and make popup if no
        InternetConnection.checkConnectionToNetwork(this);

        // Initialize UI elements
        speakImage = findViewById(R.id.speakImage);
        writtenText = findViewById(R.id.writtenText);
        backToMenu = findViewById(R.id.backToMenu);
        saveToHistoryBtn = findViewById(R.id.saveToHistoryBtn);
        deleteBtn = findViewById(R.id.deleteTextBtn);
        progressBar = findViewById(R.id.progressBar);

        // Initialize TextToSpeech engine
        textToSpeech = new TextToSpeech(this, i -> {
            if (i != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        // Set click listener for speak image button
        speakImage.setOnClickListener(view -> {
            String text = writtenText.getText().toString();
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        });

        // Set click listener for back to menu button
        backToMenu.setOnClickListener(view -> {
            Intent intent = new Intent(TextToSpeechActivity.this, FirstAppActivity.class);
            startActivity(intent);
        });

        // Set click listener for delete text button
        deleteBtn.setOnClickListener(view -> writtenText.setText(""));

        // Set click listener for save to history button
        saveToHistoryBtn.setOnClickListener(v -> {
            if (!writtenText.getText().toString().isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUtils.getUserFromFirestore().get().addOnSuccessListener(documentSnapshot -> {
                    // Create a map to store user's text-to-speech history
                    Map<String , Object> userHistory = new HashMap<>();
                    userHistory.put("text" ,  writtenText.getText().toString());
                    // Add text to user's history in Firestore
                    FirebaseUtils.getUserFromFirestore().collection("textToSpeech").add(userHistory).addOnSuccessListener(documentReference -> {
                        Toast.makeText(TextToSpeechActivity.this, "Added To History!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                });
            } else {
                Toast.makeText(TextToSpeechActivity.this, "Please put a text!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
