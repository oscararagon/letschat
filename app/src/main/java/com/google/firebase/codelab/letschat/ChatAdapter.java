package com.google.firebase.codelab.letschat;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayAdapter<Chat> {
    public ChatAdapter(@NonNull  Context mContext, int resource, @NonNull ArrayList<Chat> chatList) {
        super(mContext, resource, chatList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_item_layout, parent, false);
        }

        TextView txtUsername, txtLastMessage, txtChatTime;
        CircleImageView profilePic;
        ImageView tick;
        ImageView msgReceived;

        txtUsername = (TextView) convertView.findViewById(R.id.item_username);
        txtLastMessage = (TextView) convertView.findViewById(R.id.last_message);
        txtChatTime = (TextView) convertView.findViewById(R.id.time_last_message);
        profilePic = (CircleImageView) convertView.findViewById(R.id.item_profile_photo);
        tick = (ImageView) convertView.findViewById(R.id.tick_message);
        msgReceived = (ImageView) convertView.findViewById(R.id.received_message);

        final Chat chat = getItem(position);

        SharedPreferences sp = getContext().getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        profilePic.setImageResource(0);
        profilePic.setBackgroundResource(0);

        if(chat.getProfilePic().equals("")){
            profilePic.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }else{
            Glide.with(getContext()).load(chat.getProfilePic()).into(profilePic);
        }

        if(!chat.getSenderMessage().equals(sp.getString("mobile", ""))){
            tick.setVisibility(View.GONE);
            msgReceived.setVisibility(View.VISIBLE);
        }else {
            tick.setVisibility(View.VISIBLE);
            msgReceived.setVisibility(View.GONE);
        }
        txtUsername.setText(chat.getUser());
        txtLastMessage.setText(chat.getLastMessage());
        txtChatTime.setText(chat.getChatTime());

        return convertView;
    }
}
