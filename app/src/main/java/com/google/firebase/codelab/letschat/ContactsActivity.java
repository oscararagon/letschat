package com.google.firebase.codelab.letschat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.google.firebase.codelab.letschat.R.layout.contact_item_layout;

public class ContactsActivity extends AppCompatActivity {

    private ListView contactList;
    private TextView txtEmptyList;
    private EditText searchBar;

    private UserAdapter adapter;
    private ArrayList<Bundle> resultList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Bundle> users;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Contacts");
        }

        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        contactList = (ListView) findViewById(R.id.contactList);
        txtEmptyList = (TextView) findViewById(R.id.listEmpty);
        searchBar = (EditText) findViewById(R.id.searchBar);

        db.collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int totalUsers = 0;
                        users = new ArrayList<>();
                        for (QueryDocumentSnapshot userDocument : task.getResult()) {
                            if(!(userDocument.getString("mobile").equals(sp.getString("mobile", "")))){
                                Bundle user = new Bundle();
                                user.putString("profilePic", userDocument.getString("profilePic"));
                                user.putString("username", userDocument.getString("username"));
                                user.putString("mobile", userDocument.getString("mobile"));
                                users.add(user);
                                totalUsers++;
                            }
                        }
                        if(totalUsers < 1){
                            txtEmptyList.setVisibility(View.VISIBLE);
                            //contactList.setEmptyView(txtEmptyList);
                        }else {
                            //ordino gli username della lista contatti in ordine alfabetico
                            Collections.sort(users, new Comparator<Bundle>() {
                                @Override
                                public int compare(Bundle bundle, Bundle t1) {
                                    return bundle.getString("username").toUpperCase().compareTo(t1.getString("username").toUpperCase());
                                }
                            });
                            resultList = users;
                            adapter = new UserAdapter(ContactsActivity.this, contact_item_layout, users);
                            contactList.setAdapter(adapter);
                        }
                    }
                });

        //ogni volta che modifichiamo la stringa nella search bar chiamiamo la funzione filter
        // per filtrare la lista dei contatti
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resultList = filter(charSequence);
                adapter = new UserAdapter(ContactsActivity.this, contact_item_layout, resultList);
                contactList.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("profilePicReceiver", resultList.get(position).getString("profilePic"));
                intent.putExtra("usernameReceiver", resultList.get(position).getString("username"));
                intent.putExtra("mobileReceiver", resultList.get(position).getString("mobile"));
                startActivity(intent);
            }
        });
    }

    //filtriamo l'elenco dei contatti in base alla stringa inserita nella searchbar
    protected ArrayList<Bundle> filter(CharSequence constraint) {
        ArrayList<Bundle> filteredList = new ArrayList<Bundle>();
        if (constraint != null && constraint.length() > 0) {
            for (int i = 0; i < users.size(); i++) {
                if ((users.get(i).getString("username").toUpperCase())
                        .startsWith(constraint.toString().toUpperCase())) {
                    Bundle tmp = new Bundle(users.get(i));
                    filteredList.add(tmp);
                }
            }
        } else {
            filteredList.addAll(users);
        }
        return filteredList;
    }
}