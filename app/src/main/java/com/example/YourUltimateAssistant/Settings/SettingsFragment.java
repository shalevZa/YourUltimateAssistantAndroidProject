package com.example.YourUltimateAssistant.Settings;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationManagerCompat;

import com.example.YourUltimateAssistant.History.HistoryActivity;
import com.example.YourUltimateAssistant.InApp.FirstAppActivity;
import com.example.YourUltimateAssistant.Login.ForgotPasswordActivity;
import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.ScheduledNotification.NotificationReceiver;

import java.util.Calendar;

public class SettingsFragment extends Fragment {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch getANotification;
    ChangeNicknameFragment changeNicknameFragment = new ChangeNicknameFragment();
    ChangePhoneNumber changePhoneNumberFragment = new ChangePhoneNumber();
    Button changePassword, changeNickname, changePhoneNumber, historyBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views
        changePassword = view.findViewById(R.id.changePassword);
        changeNickname = view.findViewById(R.id.changeNickname);
        changePhoneNumber = view.findViewById(R.id.changePhoneNumber);
        historyBtn = view.findViewById(R.id.historyBtn);
        getANotification = view.findViewById(R.id.getANotify);

        // Check if notifications are enabled and update the switch accordingly
        FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && areNotificationsEnabled(getContext())) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                getANotification.setChecked(userModel.isNotificationsAllowed());
            }
        });

        // Set click listeners for various buttons
        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });

        changePassword.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        changeNickname.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, changeNicknameFragment).commit());

        changePhoneNumber.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, changePhoneNumberFragment).commit());

        getANotification.setOnClickListener(v -> {
            // Toggle notification preference in Firestore
            FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {
                if (getANotification.isChecked() && !areNotificationsEnabled(getContext())) {
                    goToPhoneSettingsForPermission();
                }
                if (getANotification.isChecked()) FirstAppActivity.reminderNotificationSet(getContext());
                if (task.isSuccessful()) {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    userModel.setNotificationsAllowed(!userModel.isNotificationsAllowed());
                    FirebaseUtils.getUserFromFirestore().set(userModel).addOnSuccessListener(unused -> {});
                    Toast.makeText(getContext(), "Changed!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    // Check if notifications are enabled on the device
    public static boolean areNotificationsEnabled(Context context) {
        if (context != null) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            return notificationManagerCompat.areNotificationsEnabled();
        }
        return false;
    }

    // Open phone settings for the app to allow notifications
    public void goToPhoneSettingsForPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


}
