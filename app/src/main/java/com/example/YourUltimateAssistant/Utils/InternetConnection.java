package com.example.YourUltimateAssistant.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.example.YourUltimateAssistant.R;

public class InternetConnection {

    // Method to check if the device is connected to the internet
    public static boolean isConnectedToInternet(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    // Method to check the internet connection and display a pop-up if there's no connection
    public static void checkConnectionToNetwork(Activity activity) {
        if(!isConnectedToInternet(activity)){
            createPopUpWindow(activity);
        }
    }

    // Method to create and display a pop-up dialog window indicating no internet connection
    @SuppressLint("MissingInflatedId")
    public static void createPopUpWindow(Activity activity) {
        // Create and display the dialog window
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.no_internet_connection_pop_up);
        dialog.show();
    }
}
