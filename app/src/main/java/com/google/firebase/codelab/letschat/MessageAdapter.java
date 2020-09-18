package com.google.firebase.codelab.letschat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private static int MSG_RIGHT = 0;
    private static int MSG_LEFT = 1;

    private boolean isRight;

    private Context mContext;
    private String sender, receiver, currentMobile;

    public MessageAdapter(Context mContext, String sender, String receiver, String currentMobile) {
        this.mContext = mContext;
        this.sender = sender;
        this.receiver = receiver;
        this.currentMobile = currentMobile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_sent, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_received, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot chatDocument : task.getResult()) {
                            if(chatDocument.getId().equals(sender+""+receiver) ||  chatDocument.getId().equals(receiver+""+sender)){
                                db.collection("Chats").document(chatDocument.getId()).collection("Messages")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot msgDocument : task.getResult()) {
                                                    if(msgDocument.getString("sender").equals(currentMobile)){
                                                        isRight = true;
                                                    } else
                                                        isRight = false;
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        if(isRight)
            return MSG_RIGHT;
        else
            return  MSG_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //private String

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
