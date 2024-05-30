package com.example.YourUltimateAssistant.Utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FirebaseUtils {

    // Returns the document reference for the current user's profile picture
    public static DocumentReference getCurrentProfilePicture() {
        return getUserFromFirestore().collection("Profile Picture").document(getCurrentUserId());
    }

    // Returns the current Firebase user
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Returns the ID of the current Firebase user
    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // Returns the document reference for the current user in Firestore
    public static DocumentReference getUserFromFirestore() {
        return FirebaseFirestore.getInstance().collection("Users").document(getCurrentUserId());
    }

    // Checks if the current user's email is verified
    public static boolean isEmailVerify() {
        return FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
    }

    // Returns a document reference for a chat document
    public static DocumentReference getChatDocument() {
        return FirebaseFirestore.getInstance().collection("UsersChat").document();
    }
}
