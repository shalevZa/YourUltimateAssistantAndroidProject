package com.example.YourUltimateAssistant.InApp;

import android.annotation.*;
import android.app.Dialog;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.YourUltimateAssistant.Adapters.UsersChatAdapter;
import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.example.YourUltimateAssistant.Utils.InternetConnection;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class UsersChat extends AppCompatActivity {

    // UI elements
    ImageButton backToHomeBtn, refreshBtn;
    EditText messageLine;
    ImageView closeReplayLayout;
    ImageButton sendBtn;
    ListView chatListView;
    TextView lineReplayLinearText;
    LinearLayout lineReplayLinearlayout;
    TextView usersChatText;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_chat_activity);

        //Set check if there internet connection and make popup if no
        InternetConnection.checkConnectionToNetwork(this);

        // Initialize UI elements
        sendBtn = findViewById(R.id.sendBtn);
        chatListView = findViewById(R.id.chatListView);
        backToHomeBtn = findViewById(R.id.backToHomeBtn);
        messageLine = findViewById(R.id.messageLine);
        refreshBtn = findViewById(R.id.refreshBtn);
        lineReplayLinearText = findViewById(R.id.lineReplayLinearText);
        lineReplayLinearlayout = findViewById(R.id.lineReplayLinearlayout);
        closeReplayLayout = findViewById(R.id.closeReplayLayout);
        usersChatText = findViewById(R.id.usresChatUsersLink);

        // Set click listener for closing replay layout
        closeReplayLayout.setOnClickListener(v -> lineReplayLinearlayout.setVisibility(View.GONE));

        // Set click listener for users chat link
        usersChatText.setOnClickListener(v -> {startActivity(new Intent(UsersChat.this , UsersInChat.class));});

        chatListView.setDivider(null);

        // Set click listener for refresh button
        refreshBtn.setOnClickListener(v -> {
            UsersChatAdapter.addMessageToListView(getBaseContext(), FirebaseUtils.getCurrentUserId(), chatListView);
        });

        UsersChatAdapter.addMessageToListView(getBaseContext(), FirebaseUtils.getCurrentUserId(), chatListView);

        // Set click listener for back to home button
        backToHomeBtn.setOnClickListener(v -> startActivity(new Intent(UsersChat.this, FirstAppActivity.class)));

        // Set click listener for send button
        sendBtn.setOnClickListener(v -> {
            FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {
                FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task1 -> {
                    if (!messageLine.getText().toString().isEmpty()) {
                        String timeString = null;
                        LocalTime currentTime = null;
                        DateTimeFormatter formatter = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            currentTime = LocalTime.now();
                            formatter = DateTimeFormatter.ofPattern("HH:mm");
                            timeString = currentTime.format(formatter);
                        }
                        boolean isVisible = lineReplayLinearlayout.getVisibility() == View.VISIBLE;
                        UserModel userModel = task1.getResult().toObject(UserModel.class);
                        UsersChatAdapter.createNewMessage(getBaseContext(), messageLine.getText().toString(), userModel.getNickname(), FirebaseUtils.getCurrentUserId(), FieldValue.serverTimestamp(), timeString, chatListView, isVisible, lineReplayLinearText.getText().toString());
                        messageLine.setText("");
                        lineReplayLinearlayout.setVisibility(View.GONE);
                    }
                });
            });
        });

        // Set long click listener for chat list view
        chatListView.setOnItemLongClickListener((parent, view, position, id) -> {
            createPopUpWindow(position);
            return false;
        });
    }

    @SuppressLint("MissingInflatedId")
    public void createPopUpWindow(int position) {
        // Initialize variables to hold message TextView and sender name TextView
        TextView messageTextView, senderNameTextView;

        // Get the view corresponding to the item at the given position in the chat list view
        View itemView = chatListView.getChildAt(position);

        // Check if the itemView is not null
        if (itemView != null) {
            // Find the message TextView and sender name TextView based on their IDs
            if (itemView.findViewById(R.id.otherPeopleMessageTextView) != null) {
                // If the message is from another user
                messageTextView = itemView.findViewById(R.id.otherPeopleMessageTextView);
                senderNameTextView = itemView.findViewById(R.id.otherSenderNameTextView);
            } else {
                // If the message is from the current user
                messageTextView = itemView.findViewById(R.id.myMessageTextView);
                senderNameTextView = itemView.findViewById(R.id.myNameTextView);
            }
        } else {
            // If the itemView is null, set TextViews to null
            messageTextView = null;
            senderNameTextView = null;
        }

        // Check if both messageTextView and senderNameTextView are not null
        if (messageTextView != null && senderNameTextView != null) {
            // Retrieve the message and sender name from the TextViews
            String message = messageTextView.getText().toString();
            String senderName = senderNameTextView.getText().toString();

            // Create a dialog for the pop-up window
            Dialog dialog = new Dialog(UsersChat.this);
            dialog.setContentView(R.layout.chat_long_press_message);
            dialog.show();

            // Find buttons and image view in the dialog layout
            Button copyBtn = dialog.findViewById(R.id.copyBtn);
            Button replayBtn = dialog.findViewById(R.id.replayBtn);
            Button saveInHistoryBtn = dialog.findViewById(R.id.saveInHistory);
            ImageView deleteMessage = dialog.findViewById(R.id.deleteMessageBtn);

            // Set onClickListener for copyBtn to copy the message to clipboard
            copyBtn.setOnClickListener(v -> {
                ClipboardManager clipboardManager = (ClipboardManager) UsersChat.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", message);
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(UsersChat.this, "Copied", Toast.LENGTH_SHORT).show();
            });

            // Set onClickListener for replayBtn to display the message for replay
            replayBtn.setOnClickListener(v -> {
                lineReplayLinearlayout.setVisibility(View.VISIBLE);
                lineReplayLinearText.setText(senderName + " : " + message);
                dialog.dismiss();
            });

            // Set onClickListener for saveInHistoryBtn to save the message in history
            saveInHistoryBtn.setOnClickListener(v -> {
                Map<String, Object> savedMessage = new HashMap<>();
                savedMessage.put("message", message);
                FirebaseUtils.getUserFromFirestore().collection("Saved Messages").document().set(savedMessage)
                        .addOnCompleteListener(task -> Toast.makeText(UsersChat.this, "Saved", Toast.LENGTH_SHORT).show());
            });

            deleteMessage.setOnClickListener(v -> {

                if(!(itemView.findViewById(R.id.otherPeopleMessageTextView) != null)){
                    FirebaseFirestore.getInstance().collection("UsersChat").whereEqualTo("message", message)
                            .get().addOnCompleteListener(task -> {

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    if (message.equals(document.getString("message"))) {
                                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("UsersChat").document(document.getId());
                                        documentReference.delete().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                UsersChatAdapter.addMessageToListView(getBaseContext(), FirebaseUtils.getCurrentUserId(), chatListView);
                                                Toast.makeText(this, "Deleted!!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            });

                }
                else
                    Toast.makeText(this , "You can delete just your messages" , Toast.LENGTH_SHORT).show();

            });
        }
    }

}
