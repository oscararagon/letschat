package com.google.firebase.codelab.friendlychat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyMobileActivity extends AppCompatActivity {


    private TextView txtVerify;
    private EditText codeVerification;
    private Button btnVerify;
    private LinearLayout layoutResend;

    private Intent intent;
    private String mobileNumber;
    private String message = "Your Let's Chat verification code is";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        intent = getIntent();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mobileNumber = intent.getStringExtra("mobileNumber");

        txtVerify = (TextView) findViewById(R.id.verifyMobile);
        codeVerification = (EditText) findViewById(R.id.codeVerification);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        layoutResend = (LinearLayout) findViewById(R.id.layoutResend);


        txtVerify.append(" "+mobileNumber);

        double min = 100000, max = 999999;
        SmsManager smsManager = SmsManager.getDefault();
        //sendSMSMessage. Prima creo il codice a 6 cifre a random
        final int randCode = (int) (Math.random() * (max - min +1) + min);
        message = message+" "+randCode;
        verifySMSPermission();

        btnVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //verifico che il contenuto di codeVerification coincida con il codeNumber mandato per SMS
                if(codeVerification.getText().toString().equals(String.valueOf(randCode))){
                    /**aggiungere l'utente al db Firestore*/


                    Toast.makeText(view.getContext(), "Welcome "+intent.getStringExtra("username"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerifyMobileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

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
