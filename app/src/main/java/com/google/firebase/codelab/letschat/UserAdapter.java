package com.google.firebase.codelab.letschat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<Bundle> {

    public UserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Bundle> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_item_layout, parent, false);
        }

        ImageView profilePic;
        TextView username, mobile;

        final Bundle user = getItem(position);


        profilePic = (ImageView) convertView.findViewById(R.id.item_profile_photo);
        username = (TextView) convertView.findViewById(R.id.item_username);
        mobile = (TextView) convertView.findViewById(R.id.item_mobileNumber);

        profilePic.setImageResource(0);
        profilePic.setBackgroundResource(0);
        profilePic.setImageBitmap(BitmapFactory.decodeFile(user.getString("profilePic")));

        username.setText(user.getString("username"));
        mobile.setText(user.getString("mobile"));

        return convertView;
    }
}
