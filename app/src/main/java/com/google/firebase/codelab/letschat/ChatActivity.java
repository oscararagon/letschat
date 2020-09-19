
package com.google.firebase.codelab.letschat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Button SendButton;
    private EditText MessageEditText;
    private ImageView AddImage;
    private ImageView AddDocument;
    private CircleImageView ImgContact;
    private TextView username, mobileNumber;
    private RecyclerView recyclerView;

    private MessageAdapter messageAdapter;

    private List<FriendlyMessage> mChat;
    private boolean Chatfound;
    private String chatCollectionId;

    private Intent intent;
    private SharedPreferences sp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().hide();

        SendButton = (Button) findViewById(R.id.sendButton);
        MessageEditText = (EditText) findViewById(R.id.messageEditText);
        AddDocument = (ImageView) findViewById(R.id.addDocument);
        AddImage = (ImageView) findViewById(R.id.addImage);
        ImgContact = (CircleImageView) findViewById(R.id.imgContact);
        username = (TextView) findViewById(R.id.contactName);
        mobileNumber = (TextView) findViewById(R.id.item_mobileNumber);
        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();

        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        getChatCollection(sp.getString("mobile", ""), intent.getStringExtra("mobileReceiver"));

        if(intent != null){
            username.setText(intent.getStringExtra("usernameReceiver"));
            mobileNumber.setText(intent.getStringExtra("mobileReceiver"));
            if(intent.getStringExtra("profilePicReceiver").equals("")){
                ImgContact.setImageResource(R.drawable.ic_account_circle_black_36dp);
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
        AddImage.setOnClickListener(this);
        AddDocument.setOnClickListener(this);
        ImgContact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                //invia il messaggio
                sendMessage(sp.getString("mobile", ""), intent.getStringExtra("mobileReceiver"), MessageEditText.getText().toString(), System.nanoTime());
                break;

            case R.id.addImage:
                //Nuovo intent per scegliere immagine dalla galleria
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);*/
                break;

            case R.id.addDocument:

                break;

            case R.id.imgContact:

                break;
        }
    }

    /**
     * Metodo per l'invio dei messaggio
     * All'invio effettuato del messaggio si leggono poi i
     * messaggi della chat per poi visualizzarli correttamente a video
     * */

    private void sendMessage(String sender, String receiver, String msg, final long nanoTime) {

        HashMap<String, Object> message = new HashMap<>();

        message.put("sender", sender);
        message.put("receiver", receiver);
        message.put("message", msg);

        HashMap<String, Object> lastMessage = new HashMap<>();
        lastMessage.put("lastMessage", msg);

        if(!Chatfound)
            chatCollectionId = sender + "" + receiver;

        db.collection("Chats").document(chatCollectionId).set(lastMessage);
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
        mChat = new ArrayList<>();

        //leggo tutti i messaggi che ci sono nella chat e li aggiungo man mano alla List di FriendlyMessage
        db.collection("Chats").document(chatCollectionId).collection("Messages").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mChat.clear();
                        for(DocumentSnapshot msgDocument : task.getResult()){
                            FriendlyMessage msg = new FriendlyMessage(msgDocument.getString("sender"), msgDocument.getString("receiver"), msgDocument.getString("message").trim());
                            mChat.add(msg);
                        }
                        messageAdapter = new MessageAdapter(ChatActivity.this, mChat, sp.getString("mobile", ""));
                        recyclerView.setAdapter(messageAdapter);
                    }
                });
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
                        if(Chatfound)
                            readMessages();
                    }
                });
    }
}