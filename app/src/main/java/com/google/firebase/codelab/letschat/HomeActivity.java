package com.google.firebase.codelab.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        btnAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(HomeActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
    }
}