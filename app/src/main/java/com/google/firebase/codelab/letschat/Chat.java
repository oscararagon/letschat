package com.google.firebase.codelab.letschat;

public class Chat {

    private String user;
    private String lastMessage;
    private String chatTime;
    private String profilePic;
    private String senderMessage;

    public Chat(String user, String lastMessage, String chatTime, String profilePic, String senderMessage) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.chatTime = chatTime;
        this.profilePic = profilePic;
        this.senderMessage = senderMessage;
    }

    public String getUser() {
        return user;
    }

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

}
