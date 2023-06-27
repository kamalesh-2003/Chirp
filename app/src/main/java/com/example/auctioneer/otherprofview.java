package com.example.auctioneer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class otherprofview extends AppCompatActivity {
    private Button btnshare,btnrep;
    private TextView textusk,textfn,textbio;
    private FirebaseFirestore firestore;
    private CollectionReference usercol;
    private Intent collectdat;
    private String uname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherprofview);
        collectdat =getIntent();
        uname = collectdat.getStringExtra("usenam");
        firestore = FirebaseFirestore.getInstance();
        textusk = findViewById(R.id.usertst);
        textfn = findViewById(R.id.textviewfname);
        textbio = findViewById(R.id.textViewbio);
        DocumentReference docRef = firestore.collection("userdetails").document(uname).collection("userdat").document(uname);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fullnamee = document.getString("fullname");
                        String bioo = document.getString("bio");
                        String usname1 = document.getString("username");
                        textbio.setText(bioo);
                        textfn.setText(fullnamee);
                        textusk.setText(usname1);
                    }
                }

            }
        });



    }
}