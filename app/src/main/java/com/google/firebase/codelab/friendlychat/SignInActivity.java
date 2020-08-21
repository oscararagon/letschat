/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;


    private Button mSignInButton;
    private ImageView imgProfilePic, imgEditProfilePic;
    private EditText txtMobile, txtUsername;
    private TextView messagesLogin;

    // Firebase instance variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Collega gli oggetti grafici al codice
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        imgProfilePic = (ImageView) findViewById(R.id.profileImage);
        imgEditProfilePic =  (ImageView) findViewById(R.id.imgEditProfilePic);
        txtMobile = (EditText) findViewById(R.id.mobile);
        txtUsername = (EditText) findViewById(R.id.username);
        messagesLogin = (TextView) findViewById(R.id.warningLogin);


        // Set click listeners
        mSignInButton.setOnClickListener(this);
        imgEditProfilePic.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                //cosa fare al click del bottone di login
                checkLogin(txtMobile.getText().toString(), txtUsername.getText().toString());
                break;
            case R.id.imgEditProfilePic:
                //edit profile picture
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        v.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                }

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;

            case R.id.profileImage:
                //show profile picture

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permesso accettato", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATE_ADDED};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumn[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    imgProfilePic.setImageResource(0);
                    imgProfilePic.setBackgroundResource(0);
                    imgProfilePic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                }
        }
    }

    public void checkLogin(String mobile, String user){
        String regex = "^\\d{10}$"; //regex per il numero di cellulare: controlla che abbia 10 cifre
        if(mobile.matches(regex)){
            if(!user.isEmpty()){
                //valid mobile phone and username
                Intent intent = new Intent(SignInActivity.this, VerifyMobileActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Welcome " + user, Toast.LENGTH_SHORT).show();
            }else{
                //username empty
                messagesLogin.setText(R.string.invalid_username);
                messagesLogin.setVisibility(View.VISIBLE);
            }
        }else if(user.isEmpty()){
            //invalid both mobile number and username
            messagesLogin.setText(R.string.login_empty_fields);
            messagesLogin.setVisibility(View.VISIBLE);
        }else{
            //invalid mobile number
            messagesLogin.setText(R.string.invalid_mobile);
            messagesLogin.setVisibility(View.VISIBLE);
        }
    }
}