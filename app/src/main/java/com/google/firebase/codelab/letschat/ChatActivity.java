
package com.google.firebase.codelab.letschat;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.ApiUtil;
import com.google.gson.internal.$Gson$Preconditions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Button SendButton;
    private EditText MessageEditText;
    private CircleImageView ImgContact;
    private TextView username, mobileNumber;
    private RecyclerView recyclerView;
    private ImageView imgFullScreen;
    private TextView msgBadge;

    private MessageAdapter messageAdapter;

    private List<FriendlyMessage> mChat;
    private boolean Chatfound;
    private String chatCollectionId;

    private Intent intent;
    private SharedPreferences sp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatCollection;

    //private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //notificationManager = NotificationManagerCompat.from(this);

        getSupportActionBar().hide();

        SendButton = (Button) findViewById(R.id.sendButton);
        MessageEditText = (EditText) findViewById(R.id.messageEditText);
        ImgContact = (CircleImageView) findViewById(R.id.imgContact);
        username = (TextView) findViewById(R.id.contactName);
        mobileNumber = (TextView) findViewById(R.id.item_mobileNumber);
        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        imgFullScreen = (ImageView) findViewById(R.id.imgFullScreen);
        msgBadge = (TextView) findViewById(R.id.msg_badge);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();

        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        getChatCollection(sp.getString("mobile", ""), intent.getStringExtra("mobileReceiver"));

        readMessages();

        if(intent != null){
            username.setText(intent.getStringExtra("usernameReceiver"));
            mobileNumber.setText(intent.getStringExtra("mobileReceiver"));
            if(intent.getStringExtra("profilePicReceiver").equals("")){
                ImgContact.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }else
                Glide.with(this).load(intent.getStringExtra("profilePicReceiver")).into(ImgContact);
        }

        MessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    SendButton.setEnabled(true);
                } else {
                    SendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        SendButton.setOnClickListener(this);
        ImgContact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                //invia il messaggio
                sendMessage(sp.getString("mobile", ""), intent.getStringExtra("mobileReceiver"),
                        sp.getString("remoteProfilePic", ""),
                        intent.getStringExtra("profilePicReceiver"),
                        sp.getString("username", ""),
                        intent.getStringExtra("usernameReceiver"),
                        MessageEditText.getText().toString(),
                        new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()),
                        new Timestamp(new Date()), System.nanoTime());

                // Creo l'intent per quando si clicca su una notifica
                /*Intent i = new Intent(this, ChatActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

                //creazione notifica nel dispositivo del destinatario
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Notification.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_textsms_24)
                        .setContentTitle(sp.getString("username", ""))
                        .setContentText(MessageEditText.getText().toString())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                // faccio apparire la notifica
                notificationManager.notify(1, builder.build()); // notificationId is a unique int for each notification that you must define
*/
                break;

            case R.id.imgContact:
                if(!intent.getStringExtra("profilePicReceiver").equals("")) {
                    imgFullScreen.setVisibility(View.VISIBLE);
                    Glide.with(ChatActivity.this).load(intent.getStringExtra("profilePicReceiver")).into(imgFullScreen);
                } else{
                    ImgContact.setImageResource(R.drawable.ic_baseline_account_circle_24);
                }
                break;
        }
    }

    /**
     * Metodo per l'invio dei messaggio
     * All'invio effettuato del messaggio si leggono poi i
     * messaggi della chat per poi visualizzarli correttamente a video
     * */

    private void sendMessage(String sender, String receiver, String profilePic1, String profilePic2, String user1, String user2, String msg, String chatTime, Timestamp timestamp, final long nanoTime) {

        HashMap<String, Object> message = new HashMap<>();

        message.put("sender", sender);
        message.put("receiver", receiver);
        message.put("message", msg);
        message.put("chatTime", chatTime);
        message.put("timestamp", timestamp);

        HashMap<String, Object> lastMessage = new HashMap<>();
        lastMessage.put("lastMessage", msg);
        lastMessage.put("chatTime", chatTime);
        lastMessage.put("sender", sender);
        lastMessage.put("timestamp", timestamp);

        if(!Chatfound) {
            HashMap<String, Object> dataChat = new HashMap<>();
            dataChat.put("profilePic1", profilePic1);
            dataChat.put("profilePic2", profilePic2);
            dataChat.put("username1", user1);
            dataChat.put("username2", user2);
            dataChat.put("mobile1", sp.getString("mobile", ""));
            dataChat.put("mobile2", intent.getStringExtra("mobileReceiver"));
            dataChat.put("lastMessage", msg);
            dataChat.put("chatTime", chatTime);
            dataChat.put("timestamp", timestamp);
            dataChat.put("sender", sender);

            chatCollectionId = sender + "" + receiver;
            db.collection("Chats").document(chatCollectionId).set(dataChat);
            Chatfound = true;
        }
        db.collection("Chats").document(chatCollectionId).update("lastMessage", msg);
        db.collection("Chats").document(chatCollectionId).update("chatTime", chatTime);
        db.collection("Chats").document(chatCollectionId).update("sender", sender);
        db.collection("Chats").document(chatCollectionId).update("timestamp", timestamp);
        db.collection("Chats").document(chatCollectionId).collection("Messages").document(String.valueOf(nanoTime)).set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MessageEditText.setText(null);
                        readMessages();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /**
     * metodo che viene chiamato all'apertura dell'activity
     * per prelevare tutti i messaggi della chat tra sender e receiver
     * **/

    private void readMessages(){
        if(Chatfound) {
            mChat = new ArrayList<>();
            //leggo tutti i messaggi che ci sono nella chat e li aggiungo man mano alla List di FriendlyMessage
            db.collection("Chats").document(chatCollectionId).collection("Messages")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            mChat.clear();
                            for (DocumentSnapshot msgDocument : value) {
                                FriendlyMessage msg = new FriendlyMessage(msgDocument.getString("sender"), msgDocument.getString("receiver"), msgDocument.getString("message").trim(), msgDocument.getString("chatTime"), msgDocument.getTimestamp("timestamp"));
                                mChat.add(msg);
                            }
                            //ordino i messaggi della chat secondo la data
                            Collections.sort(mChat, new Comparator<FriendlyMessage>() {
                                @Override
                                public int compare(FriendlyMessage msg1, FriendlyMessage msg2) {
                                    return msg1.getTimestamp().compareTo(msg2.getTimestamp());
                                }
                            });

                            messageAdapter = new MessageAdapter(ChatActivity.this, mChat, sp.getString("mobile", ""));
                            recyclerView.setAdapter(messageAdapter);
                        }
                    });
        }
    }

    /**
     * Metodo che serve per ottenere la CollectionReference della chat
     * tra i due utenti.
     * Viene chiamato all'onCreate e se viene trovata una chat esistente leggo i
     * messaggi per visualizzarli correttamente a schermo
     * */

    private void getChatCollection(final String sender, final String receiver){
        db.collection("Chats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Chatfound = false;
                        for (QueryDocumentSnapshot chatDocument : task.getResult()) {
                            if(chatDocument.getId().equals(sender+""+receiver) ||  chatDocument.getId().equals(receiver+""+sender)){
                                chatCollection = db.collection("Chats").document(chatDocument.getId()).collection("Messages");
                                Chatfound = true;
                                chatCollectionId = chatDocument.getId();
                            }
                        }
                        readMessages();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(imgFullScreen.getVisibility() == View.VISIBLE){
            imgFullScreen.setImageDrawable(null);
            imgFullScreen.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
            Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
