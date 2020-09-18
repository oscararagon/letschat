package com.google.firebase.codelab.letschat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
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

        TextView username, mobile;
        ImageView profilePic;

        final Bundle user = getItem(position);

        profilePic = (ImageView) convertView.findViewById(R.id.item_profile_photo);
        username = (TextView) convertView.findViewById(R.id.item_username);
        mobile = (TextView) convertView.findViewById(R.id.item_mobileNumber);

        profilePic.setImageResource(0);
        profilePic.setBackgroundResource(0);


        //se l'utente non ha scelto l'immagine profilo, allora carico l'omino
        if(user.getString("profilePic").equals("")){
            profilePic.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }else{
            Glide.with(getContext()).load(user.getString("profilePic")).into(profilePic);
        }
        username.setText(user.getString("username"));
        mobile.setText(user.getString("mobile"));

        return convertView;
    }
}
