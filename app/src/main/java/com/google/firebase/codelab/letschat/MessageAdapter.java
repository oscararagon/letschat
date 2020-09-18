package com.google.firebase.codelab.letschat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private static int MSG_RIGHT = 0;
    private static int MSG_LEFT = 1;

    private boolean isRight;

    private Context mContext;
    private String currentMobile;
    private List<FriendlyMessage> mChat;

    public MessageAdapter(Context mContext, List<FriendlyMessage> mChat, String currentMobile) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.currentMobile = currentMobile;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_sent, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_received, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.msg.setText(mChat.get(position).getMsg());
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mChat.get(position).getSender().equals(currentMobile)) isRight = true;
        else isRight = false;
        if(isRight)
            return MSG_RIGHT;
        else
            return  MSG_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            msg = itemView.findViewById(R.id.show_message);

        }
    }
}
