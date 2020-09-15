
package com.google.firebase.codelab.letschat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Button SendButton;
    private EditText MessageEditText;
    private ImageView AddImage;
    private ImageView AddDocument;
    private CircleImageView ImgContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SendButton = (Button) findViewById(R.id.sendButton);
        MessageEditText = (EditText) findViewById(R.id.messageEditText);
        AddDocument = (ImageView) findViewById(R.id.addDocument);
        AddImage = (ImageView) findViewById(R.id.addImage);
        ImgContact = (CircleImageView) findViewById(R.id.imgContact);

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
}
