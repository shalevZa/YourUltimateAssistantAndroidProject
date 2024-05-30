package com.example.YourUltimateAssistant.InApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.YourUltimateAssistant.Adapters.UsersAdapter;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.InternetConnection;

public class UsersInChat extends AppCompatActivity {

    ListView listView;
    ImageButton backToChatBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_in_chat_activity);

        //Set check if there internet connection and make popup if no
        InternetConnection.checkConnectionToNetwork(this);

        // Initialize UI components
        listView = findViewById(R.id.usersListView);
        backToChatBtn = findViewById(R.id.backToChatBtn);

        // Set click listener for back button to return to UsersChat activity
        backToChatBtn.setOnClickListener(v -> {
            startActivity(new Intent(UsersInChat.this, UsersChat.class));
        });

        // Populate ListView with user data using UsersAdapter
        UsersAdapter.setRatingListView(this, listView);
    }
}
