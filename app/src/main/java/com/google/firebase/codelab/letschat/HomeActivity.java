package com.google.firebase.codelab.letschat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private ListView chatList;
    private TextView txtEmptyList;
    private ImageView btnAddChat, btnOpenSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();

        chatList = (ListView) findViewById(R.id.chatList);
        txtEmptyList = (TextView) findViewById(R.id.listEmpty);
        btnAddChat = (ImageView) findViewById(R.id.btnAddChat);
        btnOpenSettings = (ImageView) findViewById(R.id.btnMenu);

        chatList.setEmptyView(txtEmptyList);

    }
}
