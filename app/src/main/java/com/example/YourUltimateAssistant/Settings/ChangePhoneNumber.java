package com.example.YourUltimateAssistant.Settings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;

import java.util.HashMap;
import java.util.Map;

public class ChangePhoneNumber extends Fragment {

    ProgressBar progressBar;
    EditText newPhoneNumber;
    Button setNewPhone;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_phone_number, container, false);

        // Initialize views
        newPhoneNumber = view.findViewById(R.id.newPhone);
        setNewPhone = view.findViewById(R.id.setPhoneBtn);
        progressBar = view.findViewById(R.id.progressBar);

        // Set click listener for setNewPhone button
        setNewPhone.setOnClickListener(v -> {
            // Check if new phone number length is 10 digits
            if (newPhoneNumber.length() == 10) {
                progressBar.setVisibility(View.VISIBLE);

                // Get the current user's data from Firestore
                FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update the phone number in Firestore
                        Map<String, Object> user = new HashMap<>();
                        user.put("phoneNumber", newPhoneNumber.getText().toString());
                        FirebaseUtils.getUserFromFirestore().update(user).addOnSuccessListener(unused -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Phone number changed", Toast.LENGTH_SHORT).show();
                            // Clear the EditText
                            newPhoneNumber.setText("");
                        });
                    }
                });
            } else {
                // Show a toast message if the phone number length is not 10 digits
                Toast.makeText(getActivity(), "Phone number must be 10 digits long", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
