package com.google.firebase.codelab.letschat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.google.firebase.codelab.letschat.R.layout.contact_item_layout;

public class ContactsActivity extends AppCompatActivity {

    private ListView contactList;
    private TextView txtEmptyList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Bundle> users;
    private SharedPreferences sp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        sp = this.getSharedPreferences("com.google.firebase.codelab.letschat", Context.MODE_PRIVATE);

        contactList = (ListView) findViewById(R.id.contactList);
        txtEmptyList = (TextView) findViewById(R.id.listEmpty);

         db.collection("Users").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int totalUsers = 0;
                                users = new ArrayList<>();
                                for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                    if(!(userDocument.getString("mobile").equals(sp.getString("mobileNumber", "")))){
                                        Bundle user = new Bundle();
                                        user.putString("profilePic", userDocument.getString("profilePic"));
                                        user.putString("username", userDocument.getString("username"));
                                        user.putString("mobile", userDocument.getString("mobile"));
                                        users.add(user);
                                        totalUsers++;
                                    }
                                }
                                if(totalUsers < 1){
                                    contactList.setEmptyView(txtEmptyList);
                                }else {
                                    UserAdapter adapter = new UserAdapter(ContactsActivity.this, contact_item_layout, users);
                                    contactList.setAdapter(adapter);
                                }
                            }
           });
    }
}
