package com.example.YourUltimateAssistant.InApp;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.example.YourUltimateAssistant.Login.LoginActivity;
import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.Utils.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    // Firebase authentication instance
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Request code for image pick
    int IMAGE_PIC_CODE = 1000;

    // Declaration of views
    ImageView profilePicture, deleteAccount;
    Button logout;
    TextView nicknameProfile, nicknameProfilePic, profilePassword, profileEmail, profilePhone , deletePic;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Set check if there internet connection and make popup if no
        InternetConnection.checkConnectionToNetwork(this.getActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profilePicture = view.findViewById(R.id.profilePicture);
        nicknameProfilePic = view.findViewById(R.id.nicknameProfilePic);
        nicknameProfile = view.findViewById(R.id.profileNickname);
        profilePassword = view.findViewById(R.id.profilePassword);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePhone = view.findViewById(R.id.profilePhone);
        logout = view.findViewById(R.id.logout);
        deleteAccount = view.findViewById(R.id.imageView9);
        deletePic = view.findViewById(R.id.deletePicture);

        // Load user data and profile picture
        loadData();
        setProfilePicture();

        // Set swipe refresh layout listener
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.container);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData();
            setProfilePicture();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Set click listeners
        deleteAccount.setOnClickListener(v -> createPopUpWindow());
        profilePicture.setOnClickListener(view1 -> pickImageFromGallery());
        logout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        deletePic.setOnClickListener(v -> deleteProfilePicture());

        return view;
    }

    // Method to load user data
    private void loadData() {
        FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                nicknameProfilePic.setText(userModel.getNickname());
                nicknameProfile.setText(userModel.getNickname());
                profilePassword.setText(userModel.getPassword());
                profileEmail.setText(userModel.getEmail());
                profilePhone.setText(userModel.getPhoneNumber());
            }
        });
    }

    // Method to pick image from gallery
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PIC_CODE);
    }

    // Handling result of image pick
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PIC_CODE && data != null && data.getData() != null) {
            String imageUri = String.valueOf(data.getData());
            updateProfilePicture(imageUri);
        }
    }

    // Method to update profile picture
    private void updateProfilePicture(String imageUri) {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUri);
        FirebaseUtils.getCurrentProfilePicture().set(data).addOnSuccessListener(documentReference -> {
            Toast.makeText(getActivity(), "Profile Picture Changed!", Toast.LENGTH_SHORT).show();
            setProfilePicture();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to change profile picture!", Toast.LENGTH_SHORT).show();
        });
    }

    // Method to set profile picture from Firebase
    private void setProfilePicture() {
        FirebaseUtils.getCurrentProfilePicture().get().addOnSuccessListener(documentSnapshot -> {
            String imageUrl = documentSnapshot.getString("imageUrl");
            if (imageUrl != null) {
                loadProfileImage(imageUrl);
            } else {
                // Handle when no profile picture is available
                // For now, setting a default image or leaving it blank
            }
        });
    }

    // Method to load and display profile image using Glide library
    public void loadProfileImage(String imageUrl) {
        if (getActivity() != null)
            Glide.with(getActivity()).load(imageUrl).into(profilePicture);
    }

    // Method to delete profile picture
    private void deleteProfilePicture() {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", null);
        FirebaseUtils.getCurrentProfilePicture().set(data).addOnSuccessListener(documentReference -> {
            Toast.makeText(getActivity(), "Profile Picture Deleted!", Toast.LENGTH_SHORT).show();
            loadProfileImage(null);
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to delete profile picture!", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("MissingInflatedId")
    public void createPopUpWindow() {

        // Create and display the dialog window
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.shure_for_delete_acount);
        dialog.show();

        // Initialize buttons from dialog layout
        Button yesBtn = dialog.findViewById(R.id.yesBtn);
        Button noBtn = dialog.findViewById(R.id.noBtn);

        // Set click listener for "Yes" button
        yesBtn.setOnClickListener(v -> {
            // Delete user data from Firestore
            FirebaseUtils.getUserFromFirestore().delete().addOnCompleteListener(task -> {});
            // Delete user from Firebase Authentication
            FirebaseUtils.getCurrentUser().delete();
            // Sign out the user
            firebaseAuth.signOut();
            // Redirect to login activity
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });
        
        // Set click listener for "No" button
        noBtn.setOnClickListener(v -> dialog.dismiss());
    }
}
