package com.example.YourUltimateAssistant.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.YourUltimateAssistant.Models.ChatModel;
import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UsersChatAdapter extends ArrayAdapter<LinearLayout> {

    public static List<LinearLayout> list = new ArrayList<>();
    public static UsersChatAdapter adapter;

    //constructive action
    public UsersChatAdapter(Context context, List<LinearLayout> items) {
        super(context, 0, items);
    }


    @Override
    //The action creates and returns a view of a specific item at a given location in the adapter's dataset.
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_chat_message_layout, parent, false);
        }
        LinearLayout containerLayout = convertView.findViewById(R.id.myMessageLayout);
        ViewGroup parentView = (ViewGroup) item.getParent();
        if (parentView != null) {
            parentView.removeView(item);
        }
        containerLayout.removeAllViews();
        containerLayout.addView(item);

        return containerLayout;
    }

    //The action is called from an external class
    //The action calls all the relevant actions in order to create a chat message
    public static void createNewMessage(Context context , String messageText , String userName , String userID , FieldValue timestamp, String timeString , ListView listView , boolean haveAReplay , String replayText){

        addMessageToFirestore(messageText , userName , userID , timestamp , timeString, haveAReplay , replayText);
        addMessageToListView(context ,userID , listView);
    }


   // The action adds each message from the firebase server into the listview by checking if it is a message from the same user or not
    @SuppressLint("SimpleDateFormat")
    public static void addMessageToListView(Context context, String userID, ListView listView) {


        FirebaseFirestore.getInstance().collection("UsersChat").orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


                list.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {

                    LinearLayout messageLayout, replayMessageLayout = null, otherReplayMessageLayout = null;

                    TextView messageTextView, senderNameTextView, timeTextView, replayMessageTextView = null, otherReplayMessageTextView = null;

                    //check if the message is from the same profile UID
                    if (userID.equals(document.getString("userID"))) {

                        messageLayout = LayoutInflater.from(context).inflate(R.layout.users_chat_message_layout, null, false)
                                .findViewById(R.id.myMessageLayout);

                        messageTextView = messageLayout.findViewById(R.id.myMessageTextView);
                        senderNameTextView = messageLayout.findViewById(R.id.myNameTextView);
                        timeTextView = messageLayout.findViewById(R.id.myTimeTextView);
                        replayMessageLayout = messageLayout.findViewById(R.id.replayLinearlayout);
                        replayMessageTextView = messageLayout.findViewById(R.id.replayLinearText);

                    } else {
                        messageLayout = LayoutInflater.from(context).inflate(R.layout.users_chat_message_layout, null, false)
                                .findViewById(R.id.otherPeopleMessageLayout);
                        messageTextView = messageLayout.findViewById(R.id.otherPeopleMessageTextView);
                        senderNameTextView = messageLayout.findViewById(R.id.otherSenderNameTextView);
                        timeTextView = messageLayout.findViewById(R.id.otherTimeTextView);
                        otherReplayMessageLayout = messageLayout.findViewById(R.id.otherReplayLinearlayout);
                        otherReplayMessageTextView = messageLayout.findViewById(R.id.otherReplayLinearText);

                    }

                    messageTextView.setText(document.getString("message"));


                    //set a replay view for message
                    if (document != null)
                        if (document.getBoolean("haveAReplay")) {

                            if (userID.equals(document.getString("userID")) && replayMessageLayout != null && replayMessageTextView != null) {
                                replayMessageLayout.setVisibility(View.VISIBLE);
                                replayMessageTextView.setText(document.getString("replayText"));
                            } else {
                                if (otherReplayMessageLayout != null && otherReplayMessageTextView != null) {
                                    otherReplayMessageLayout.setVisibility(View.VISIBLE);
                                    otherReplayMessageTextView.setText(document.getString("replayText"));
                                }
                            }
                        } else {
                            if (otherReplayMessageLayout != null && replayMessageLayout != null) {
                                replayMessageLayout.setVisibility(View.GONE);
                                otherReplayMessageLayout.setVisibility(View.GONE);
                            }

                        }

                    senderNameTextView.setText(document.getString("senderName"));

                    //set time for message
                    String time = document.getString("timeString");
                    if (time != null) {
                        timeTextView.setText(time);

                        list.add(messageLayout);
                    }
                    if (context != null) {

                        adapter = new UsersChatAdapter(context, list);
                        listView.setAdapter(adapter);

                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                listView.smoothScrollToPosition(adapter.getCount() - 1);
                            }
                        });


                    }


                }
            }
        });



    }

    //the action adds messages to Firestore with ChatModel
    private static void addMessageToFirestore(String text , String userName , String userID, FieldValue timestamp ,String timeString, boolean haveAReplay , String replayText){

        if(!(text.isEmpty() && userName.isEmpty())) {

            ChatModel chatModel = new ChatModel(text , userName , userID, timestamp ,timeString, haveAReplay , replayText);

            FirebaseUtils.getChatDocument().set(chatModel).addOnCompleteListener(task -> {

            });
        }
    }
}
