package com.example.YourUltimateAssistant.ScheduledNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.YourUltimateAssistant.InApp.FirstAppActivity;
import com.example.YourUltimateAssistant.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // When the broadcast is received, create and show the notification
        Notification notification = createNotification(context);
        showNotification(context, notification);
    }

    // Method to create the notification
    private Notification createNotification(Context context) {
        // Intent to open FirstAppActivity when notification is clicked
        Intent intent = new Intent(context, FirstAppActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        // RemoteViews for custom notification layout
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.small_notification_layout);
        RemoteViews notificationLayoutExpanded = new RemoteViews(context.getPackageName(), R.layout.notification_layout);

        // Build the notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.your_ultimate_assistant_logo) // Replace with your app's icon
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    // Method to show the notification
    private void showNotification(Context context, Notification notification) {
        // Get NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Your Ultimate Assistant Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(1, notification);
    }
}
