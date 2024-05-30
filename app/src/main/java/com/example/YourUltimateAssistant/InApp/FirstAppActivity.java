package com.example.YourUltimateAssistant.InApp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.ScheduledNotification.NotificationReceiver;
import com.example.YourUltimateAssistant.Settings.SettingsFragment;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.Utils.InternetConnection;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class FirstAppActivity extends AppCompatActivity {

    // Declaration of variables
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_app_activity);

        // Initializing views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set the initial fragment to HomeFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();


        // Setup bottom navigation menu
        menu();
    }

    // Method to setup bottom navigation menu
    private void menu() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                return true;
            }
            if (item.getItemId() == R.id.Profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, profileFragment).commit();
                return true;
            }
            if (item.getItemId() == R.id.Settings) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, settingsFragment).commit();
                return true;
            }
            return false;
        });
    }

    // Method to set reminder notification
    public static void reminderNotificationSet(Context context) {
        FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                if (userModel.isNotificationsAllowed()) {
                    setNotifyManager(context);
                }
            }
        });
    }

    // Method to set alarm manager for notification
    public static void setNotifyManager(Context context) {

        // Set the time for notification
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Set repeating alarm manager for notification
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), NotificationReceiver.class);

        // Specify FLAG_IMMUTABLE for the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }
}