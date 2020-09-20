package com.google.firebase.codelab.letschat;

import java.sql.Timestamp;
import java.util.Date;

public class FriendlyMessage {

    private String msg;
    private String sender;
    private String receiver;
    private String chatTime;
    private com.google.firebase.Timestamp timestamp;

    public FriendlyMessage(String sender, String receiver, String msg, String chatTime, com.google.firebase.Timestamp timestamp) {
        this.msg = msg;
        this.sender = sender;
        this.receiver = receiver;
        this.chatTime = chatTime;
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public com.google.firebase.Timestamp getTimestamp() { return timestamp; }

    public String getReceiver() {
        return receiver;
    }

    public String getchatTime() { return chatTime; }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) { this.timestamp = timestamp; }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setchatTime(String timestamp) { this.chatTime = chatTime; }
}
