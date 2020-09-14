package com.google.firebase.codelab.letschat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.codelab.letschat.R.layout.contact_item_layout;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;

    private static final String USERNAME = "username";
    private static final String PROFILE_PIC = "profilePic";
    private static final String LOGGED = "logged";


    private SharedPreferences sp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button saveButton, signoutButton;
    private ImageView imgEditProfilePic, imgFullScreen, imgRemoveProfilePic;
    private CircleImageView imgProfilePic;
    private EditText txtUsername;
    private TextView messages;
    private Map<String, Object> user = new HashMap<>();


    private String imgPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveButton = (Button) findViewById(R.id.save_button);
        signoutButton = (Button) findViewById(R.id.sign_out_button);
        imgProfilePic = (CircleImageView) findViewById(R.id.profileImage);
        imgFullScreen = (ImageView) findViewById(R.id.imgFullScreen);
        imgEditProfilePic = (ImageView) findViewById(R.id.imgEditProfilePic);
        imgRemoveProfilePic = (ImageView) findViewById(R.id.imgRemoveProfilePic);
        txtUsername = (EditText) findViewById(R.id.edit_username);
        messages = (TextView) findViewById(R.id.warningSave);

        saveButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);
        imgEditProfilePic.setOnClickListener(this);
        imgRemoveProfilePic.setOnClickListener(this);
        imgProfilePic.setOnClickListener(this);

        //prelevo il numero di telefono dallo SharedPreferences
        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

      /*  db.collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot userDocument : task.getResult()) {
                            if((userDocument.getString("mobile").equals(sp.getString("mobile", "")))){
                                imgPath = userDocument.getString("profilePic");
                            }
                        }
                    }
                });
        if(!imgPath.equals("")){
            Glide.with(this).load(imgPath).into(imgProfilePic);
            imgRemoveProfilePic.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                //salvo modifiche sul db
                if (txtUsername.getText().toString().isEmpty()){
                    //username empty
                    messages.setText(R.string.invalid_username);
                    messages.setVisibility(View.VISIBLE);
                } else {
                    user.put(USERNAME, txtUsername.getText().toString());
                    user.put(PROFILE_PIC, imgPath);
                    //save user on db
                    db.collection("Users").document(sp.getString("mobile", "")).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SettingsActivity.this, "Your data have been saved correctly!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }



            case R.id.sign_out_button:
                //cosa fare al click del bottone di logout
                user.put(LOGGED, false);
                db.collection("Users").document(sp.getString("mobile", "")).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SettingsActivity.this, "Logout successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                break;

            case R.id.imgEditProfilePic:
                //edit profile picture
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

                break;

            case R.id.profileImage:
                //show profile picture
                if(imgPath != null) {
                    imgFullScreen.setVisibility(View.VISIBLE);
                    BitmapDrawable drawable = (BitmapDrawable) imgProfilePic.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    SignInActivity.rotateImage(imgPath, bitmap, null, imgFullScreen);
                }
                break;

            case R.id.imgRemoveProfilePic:
                //remove profile pic
                imgProfilePic.setImageResource(R.drawable.ic_baseline_account_circle_24);
                imgRemoveProfilePic.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Bitmap bitmap = null;
                    Uri selectedImage = null;
                    selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgPath = SignInActivity.getPath(this, selectedImage);

                    SignInActivity.rotateImage(imgPath, bitmap, imgProfilePic, null);
                    imgRemoveProfilePic.setVisibility(View.VISIBLE);
                }
        }
    }
}
