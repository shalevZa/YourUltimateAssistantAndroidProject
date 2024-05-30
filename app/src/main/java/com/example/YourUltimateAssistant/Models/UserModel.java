package com.example.YourUltimateAssistant.Models;

public class UserModel {

    // Define class fields
    private String email;
    private String nickname;
    private String password;
    private String phoneNumber;
    private boolean isNotificationsAllowed;

    // Default constructor
    public UserModel() {}

    // Parameterized constructor to initialize all fields
    public UserModel(String email, String nickname, String password, String phoneNumber, boolean isNotificationsAllowed ) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isNotificationsAllowed = isNotificationsAllowed;
    }

    // Getter and setter methods for all fields
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isNotificationsAllowed() {
        return isNotificationsAllowed;
    }
    public void setNotificationsAllowed(boolean notificationsAllowed) {
        isNotificationsAllowed = notificationsAllowed;
    }
}
