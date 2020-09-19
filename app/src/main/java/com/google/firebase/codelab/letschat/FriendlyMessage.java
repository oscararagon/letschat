package com.google.firebase.codelab.letschat;

public class FriendlyMessage {

    private String msg;
    private String sender;
    private String receiver;
    private String chatTime;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String sender, String receiver, String msg, String chatTime) {
        this.msg = msg;
        this.sender = sender;
        this.receiver = receiver;
        this.chatTime = chatTime;
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTimestamp() { return chatTime; }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setTimestamp(String timestamp) { this.chatTime = timestamp; }
}
