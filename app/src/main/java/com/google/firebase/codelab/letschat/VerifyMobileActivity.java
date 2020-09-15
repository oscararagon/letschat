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
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VerifyMobileActivity extends AppCompatActivity {

    private TextView txtVerify, messagesVerify;
    private EditText codeVerification;
    private Button btnVerify;
    private LinearLayout layoutResend;
    private ProgressBar pBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private Map<String, Object> user;

    private SharedPreferences sp;

    private Intent intent;
    private String mobileNumber;
    private String message = "Your Let's Chat verification code is";
    private String remoteImgUri = "";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private static final String USERNAME = "username";
    private static final String MOBILE = "mobile";
    private static final String PROFILE_PIC = "profilePic";
    private static final String LOGGED = "logged";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        intent = getIntent();
        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        mobileNumber = intent.getStringExtra("mobileNumber");
        txtVerify = (TextView) findViewById(R.id.verifyMobile);
        messagesVerify = (TextView) findViewById(R.id.warningVerify);
        codeVerification = (EditText) findViewById(R.id.codeVerification);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        pBar = (ProgressBar) findViewById(R.id.progress_bar);
        layoutResend = (LinearLayout) findViewById(R.id.layoutResend);

        txtVerify.append(" "+mobileNumber);

        double min = 100000, max = 999999;
        SmsManager smsManager = SmsManager.getDefault();
        //sendSMSMessage. Prima creo il codice a 6 cifre a random
        final int randCode = (int) (Math.random() * (max - min +1) + min);
        message = message+" "+randCode;
        verifySMSPermission();

        //upload dell'immagine profile sullo Storage
        if(!intent.getStringExtra("localImgPath").equals("")){
            Uri file = Uri.fromFile(new File(intent.getStringExtra("localImgPath")));
            storageRef = storageRef.child("img"+intent.getStringExtra("mobileNumber")+".jpg");
            UploadTask uploadTask_stream = storageRef.putFile(file);
            Task<Uri> urlTask = uploadTask_stream.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        remoteImgUri = task.getResult().toString();
                    }
                }
            });
        }

        btnVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pBar.setVisibility(View.VISIBLE);
                //verifico che il contenuto di codeVerification coincida con il codeNumber mandato per SMS
                if(codeVerification.getText().toString().equals(String.valueOf(randCode))){
                    /**aggiungo l'utente al db Firestore. Inoltre salvo lo username in locale per i prossimi accessi*/

                    user = new HashMap<>();
                    user.put(USERNAME, intent.getStringExtra("username"));
                    sp.edit().putString("username", intent.getStringExtra("username")).apply();
                    sp.edit().putString("mobile", intent.getStringExtra("mobileNumber")).apply();

                    user.put(PROFILE_PIC, remoteImgUri);
                    user.put(MOBILE, intent.getStringExtra("mobileNumber"));
                    user.put(LOGGED, true);


                    //salvataggio utente su db
                    db.collection("Users").document(intent.getStringExtra("mobileNumber")).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(VerifyMobileActivity.this, "Welcome "+intent.getStringExtra("username"), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(VerifyMobileActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }else{
                    pBar.setVisibility(View.GONE);
                    messagesVerify.setText(R.string.invalid_verify_code);
                    messagesVerify.setVisibility(View.VISIBLE);
                }
            }
        });

        //invio nuovamente il messaggio con il codice di accesso
        layoutResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendSMSMessage(mobileNumber, message);
            }
        });

    }

    private void verifySMSPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }else{
            SendSMSMessage(mobileNumber, message);
        }
    }

    private void SendSMSMessage(String moNumber, String mex){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(moNumber, null, mex, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent",  Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SendSMSMessage(mobileNumber, message);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}
