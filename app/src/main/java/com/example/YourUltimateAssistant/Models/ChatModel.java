package com.example.YourUltimateAssistant.Models;

import com.google.firebase.firestore.FieldValue;

public class ChatModel {

    // Define class fields
    private String message;
    private String senderName;
    private String userID;
    private FieldValue timestamp;
    private String timeString;
    private boolean haveAReplay;
    private String replayText;

    // Parameterized constructor to initialize all fields
    public ChatModel(String message, String senderName, String userID, FieldValue timestamp, String timeString, boolean haveAReplay, String replayText) {
        this.message = message;
        this.senderName = senderName;
        this.userID = userID;
        this.timestamp = timestamp;
        this.timeString = timeString;
        this.haveAReplay = haveAReplay;
        this.replayText = replayText;
    }

    // Default constructor
    public ChatModel(){}

    // Getter methods
    public String getMessage() {
        return message;
    }
    public String getSenderName() {
        return senderName;
    }
    public String getUserID() { return userID;}
    public FieldValue getTimestamp() {return timestamp;}
    public String getTimeString() {return timeString;}
    public boolean isHaveAReplay() {return haveAReplay;}
    public String getReplayText() {return replayText;}

    // Setter methods
    public void setMessage(String message) {this.message = message;}
    public void setSenderName(String senderName) {this.senderName = senderName;}
    public void setUserID(String userID) {this.userID = userID;}
    public void setTimestamp(FieldValue timestamp) {this.timestamp = timestamp;}
    public void setTimeString(String timeString) {this.timeString = timeString;}
    public void setHaveAReplay(boolean haveAReplay) {this.haveAReplay = haveAReplay;}
    public void setReplayText(String replayText) {this.replayText = replayText;}
}
