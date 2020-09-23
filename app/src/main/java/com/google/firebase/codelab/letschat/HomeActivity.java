package com.google.firebase.codelab.letschat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeActivity extends AppCompatActivity {

    private ListView chatList;
    private TextView txtEmptyList;
    private ImageView btnAddChat, btnOpenSettings;

    private SharedPreferences sp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Chat> chats;
    private ChatAdapter adapter;

    private int totalChat;

    private CollectionReference chatRef = db.collection("Chats");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //recupero lo SharedPreferences per poter poi salvare l'immagine profilo e lo username dell'utente
        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        //controllo che l'utente con questo cellulare non sia già loggato
        if(!sp.contains("username")){
            Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
            startActivity(intent);
        }

        getSupportActionBar().hide();

        chatList = (ListView) findViewById(R.id.chatList);
        txtEmptyList = (TextView) findViewById(R.id.listEmpty);
        btnAddChat = (ImageView) findViewById(R.id.btnAddChat);
        btnOpenSettings = (ImageView) findViewById(R.id.btnMenu);


        //ottengo le chat aperte dell'utente
        chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                chats = new ArrayList<>();
                chats.clear();
                for(QueryDocumentSnapshot chatDoc : value){
                    if(chatDoc.getId().contains(sp.getString("mobile", ""))){
                        //questa è una chat aperta
                        Chat chat;
                        if(sp.getString("remoteProfilePic", "").equals(chatDoc.getString("profilePic1"))){
                            chat = new Chat(chatDoc.getString("username2"), chatDoc.getString("mobile2"), chatDoc.getString("lastMessage"),
                                    chatDoc.getString("chatTime"), chatDoc.getString("profilePic2"),
                                    chatDoc.getString("sender"), chatDoc.getTimestamp("timestamp"));
                        }else{
                            chat = new Chat(chatDoc.getString("username1"), chatDoc.getString("mobile1"), chatDoc.getString("lastMessage"),
                                    chatDoc.getString("chatTime"), chatDoc.getString("profilePic1"),
                                    chatDoc.getString("sender"), chatDoc.getTimestamp("timestamp"));
                        }
                        chats.add(chat);
                        totalChat++;
                    }
                }
                if(totalChat < 1) chatList.setEmptyView(txtEmptyList);
                else{
                    //ordino gli item della lista in base al timestamp del lastMessage
                    Collections.sort(chats, new Comparator<Chat>() {
                        @Override
                        public int compare(Chat chat, Chat t1) {
                            return t1.getTimestamp().compareTo(chat.getTimestamp());
                        }
                    });
                    adapter = new ChatAdapter(HomeActivity.this, R.layout.chat_item_layout, chats);
                    chatList.setAdapter(adapter);

                }
            }
        });

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                intent.putExtra("profilePicReceiver", chats.get(position).getProfilePic());
                intent.putExtra("usernameReceiver", chats.get(position).getUser());
                intent.putExtra("mobileReceiver", chats.get(position).getMobile());
                startActivity(intent);
            }
        });

        chatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                new AlertDialog.Builder(HomeActivity.this)
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setTitle("Delete chat")
                        .setMessage("Do you want to delete this chat?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final String sender = sp.getString("mobile", "");
                                final String receiver = chats.get(position).getMobile();

                                //rimuovo chat dalla lista
                                chats.remove(position);
                                adapter = new ChatAdapter(HomeActivity.this, R.layout.chat_item_layout, chats);
                                chatList.setAdapter(adapter);
                                totalChat--;

                                if(totalChat < 1) chatList.setEmptyView(txtEmptyList);

                                //rimuovo chat dal db. La chat non viene completamente rimossa dal db ma solo disabilitata. Quando
                                //si manda un nuovo messaggio i vecchi messaggi vengono ripristinati
                                chatRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for(QueryDocumentSnapshot chatDoc : task.getResult()){
                                            if(chatDoc.getId().equals(sender+""+receiver)) {
                                                chatRef.document(sender+""+receiver).delete();
                                            }
                                            if (chatDoc.getId().equals(receiver+""+sender)) {
                                                chatRef.document(receiver+""+sender).delete();
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        btnAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(HomeActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });

        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    //quando premo indietro libero lo stack delle attività ed esco dall'app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}