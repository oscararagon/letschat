
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Button SendButton;
    private EditText MessageEditText;
    private ImageView AddImage;
    private ImageView AddDocument;
    private CircleImageView ImgContact;
    private TextView username, mobileNumber;

    private Intent intent;
    private SharedPreferences sp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        intent = getIntent();

        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

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

    private void sendMessage(String sender, String receiver, String msg, final long nanoTime) {

        HashMap<String, Object> message = new HashMap<>();

        message.put("sender", sender);
        message.put("receiver", receiver);
        message.put("message", msg);


        db.collection("Chats").document(sender+""+receiver)
                .collection("Messages").document(String.valueOf(nanoTime)).set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatActivity.this, String.valueOf(nanoTime), Toast.LENGTH_SHORT).show();
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
     *
     * Viene chiamato all'onCreate
     * **/

    private void readMessages(){

    }
}
