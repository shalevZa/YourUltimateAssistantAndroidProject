package com.example.YourUltimateAssistant.InApp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.YourUltimateAssistant.Login.LoginActivity;
import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.Utils.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.File;
import java.io.IOException;

public class HomeFragment extends Fragment {

    // Declaration of views
    LinearLayout writeWithVoice, textToSpeech, usersChat;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Set check if there internet connection and make popup if no
        InternetConnection.checkConnectionToNetwork(this.getActivity());

        // Initialize views
        writeWithVoice = view.findViewById(R.id.writeVoice);
        usersChat = view.findViewById(R.id.UsersChat);
        textToSpeech = view.findViewById(R.id.textToSpeech);

        // Set click listeners for each feature
        writeWithVoice.setOnClickListener(view1 -> {
            // Open WriteWithVoice activity
            Intent intent = new Intent(getActivity(), WriteWithVoice.class);
            startActivity(intent);
        });

        textToSpeech.setOnClickListener(view12 -> {
            // Open TextToSpeechActivity
            Intent intent = new Intent(getActivity(), TextToSpeechActivity.class);
            startActivity(intent);
        });

        usersChat.setOnClickListener(v -> {
            // Open UsersChat activity
            Intent intent = new Intent(getActivity(), UsersChat.class);
            startActivity(intent);
        });

        return view;
    }
}
