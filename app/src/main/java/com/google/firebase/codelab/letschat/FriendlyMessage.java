package com.google.firebase.codelab.letschat;

public class FriendlyMessage {

    private String msg;
    private String sender;
    private String receiver;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String sender, String receiver, String msg) {
        this.msg = msg;
        this.sender = sender;
        this.receiver = receiver;
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

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
