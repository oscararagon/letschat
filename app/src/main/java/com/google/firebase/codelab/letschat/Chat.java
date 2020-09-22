package com.google.firebase.codelab.letschat;

import com.google.firebase.Timestamp;

public class Chat {

    private String user;
    private String profilePic;
    private String mobile;
    private String lastMessage;
    private String chatTime;
    private String senderMessage;
    private Timestamp timestamp;

    public Chat(String user, String mobile, String lastMessage, String chatTime, String profilePic, String senderMessage, Timestamp timestamp) {
        this.user = user;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.chatTime = chatTime;
        this.profilePic = profilePic;
        this.senderMessage = senderMessage;
        this.timestamp = timestamp;
    }

    public String getUser() { return user; }

    public String getMobile() { return mobile; }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getChatTime() {
        return chatTime;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public Timestamp getTimestamp() { return timestamp; }

}
