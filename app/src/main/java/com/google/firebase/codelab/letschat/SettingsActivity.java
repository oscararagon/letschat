package com.google.firebase.codelab.letschat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;

    private SharedPreferences sp;

    private Button saveButton, signoutButton;
    private ImageView imgEditProfilePic, imgFullScreen, imgRemoveProfilePic;
    private CircleImageView imgProfilePic;
    private EditText txtUsername;
    private TextView messagesLogout;

    private String imgPath;
    private boolean imgIsNull = true;


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
        messagesLogout = (TextView) findViewById(R.id.warningLogout);

        saveButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);
        imgEditProfilePic.setOnClickListener(this);
        imgRemoveProfilePic.setOnClickListener(this);
        imgProfilePic.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                //salvo modifiche sul db
                if (txtUsername.getText().toString().isEmpty()){
                    //username empty
                    messagesLogout.setText(R.string.invalid_username);
                    messagesLogout.setVisibility(View.VISIBLE);
                }



            case R.id.sign_out_button:
                //cosa fare al click del bottone di logout
                //user.put(LOGGED, false);
                break;

            case R.id.imgEditProfilePic:
                //edit profile picture
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                    }
                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                }

                break;

            case R.id.profileImage:
                //show profile picture
                if(imgPath != null) {
                    imgFullScreen.setVisibility(View.VISIBLE);
                    BitmapDrawable drawable = (BitmapDrawable) imgProfilePic.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    //rotateImage(imgPath, bitmap, null, imgFullScreen);
                    //salvo l'immagine profilo nelle SharedPreferences
                    sp.edit().putString("profilePic", imgPath).apply();
                }
                break;

            case R.id.imgRemoveProfilePic:
                //remove profile pic
                imgProfilePic.setImageResource(R.drawable.ic_baseline_account_circle_24);
                imgRemoveProfilePic.setVisibility(View.GONE);
                imgIsNull = true;
                break;
        }
    }
}
