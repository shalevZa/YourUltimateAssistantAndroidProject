package com.example.YourUltimateAssistant.Settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;

import java.util.HashMap;
import java.util.Map;

public class ChangeNicknameFragment extends Fragment {

    ProgressBar progressBar;
    String nickname;
    Button setNicknameBtn;
    EditText newNickname;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_nickname, container, false);

        // Initialize views
        setNicknameBtn = view.findViewById(R.id.setNicknameBtn);
        newNickname = view.findViewById(R.id.newNickname);
        progressBar = view.findViewById(R.id.progressBar2);

        // Set click listener for setNicknameBtn
        setNicknameBtn.setOnClickListener(view1 -> {
            // Check if newNickname is not empty
            if (newNickname.length() > 1) {
                progressBar.setVisibility(View.VISIBLE);

                // Get the current user's data from Firestore
                FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Retrieve user data
                        UserModel userModel = task.getResult().toObject(UserModel.class);
                        nickname = userModel.getNickname();

                        // Update the nickname in Firestore
                        Map<String, Object> user = new HashMap<>();
                        user.put("nickname", newNickname.getText().toString());
                        FirebaseUtils.getUserFromFirestore().update(user).addOnSuccessListener(unused -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Nickname changed", Toast.LENGTH_SHORT).show();
                            // Clear the EditText
                            newNickname.setText("");
                        });
                    }
                });
            }
        });

        return view;
    }
}
