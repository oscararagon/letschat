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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.accounttransfer.zzu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<Bundle> {

    private String imgUrl;

    private ImageView profilePic;


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseStorage imgRef = FirebaseStorage.getInstance();

        final Bundle user = getItem(position);


        profilePic = (ImageView) convertView.findViewById(R.id.item_profile_photo);
        username = (TextView) convertView.findViewById(R.id.item_username);
        mobile = (TextView) convertView.findViewById(R.id.item_mobileNumber);

        profilePic.setImageResource(0);
        profilePic.setBackgroundResource(0);



        db.collection("Users").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot userDocument : task.getResult()) {
                       StorageReference img = imgRef.getReferenceFromUrl(userDocument.getString("profilePic"));
                       Task<Uri> t = img.getDownloadUrl();
                       t.addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               imgUrl = uri.toString();
                           }
                       });
                       t.addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                                e.toString();
                           }
                       });
                    }
                }
            });

        Glide.with(getContext()).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.ALL.NONE).into(profilePic);


        username.setText(user.getString("username"));
        mobile.setText(user.getString("mobile"));

        return convertView;
    }
}
