package com.example.auctioneer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registeringpage extends AppCompatActivity {

    private EditText usernameEditText,emailEditText,fullnamedittext,bioupdater;
    private TextView referaltext;
    private FirebaseFirestore firestore;
    private CollectionReference usercol;
    private Button updateProfileButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeringpage);
        androidx.appcompat.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.logo_img,null);
        actionbar.setCustomView(v);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userdet = user.getDisplayName();
        usernameEditText = findViewById(R.id.unameid);

        emailEditText = findViewById(R.id.emailEditText);
        updateProfileButton = findViewById(R.id.createProfileButton);
        fullnamedittext = findViewById(R.id.fullNameEditText);
        bioupdater = findViewById(R.id.bioEditText);
        referaltext = findViewById(R.id.textView10);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String fullname = fullnamedittext.getText().toString().trim();
                String bioo = bioupdater.getText().toString().trim();
                String reftext = referaltext.getText().toString().trim();

                if (!username.isEmpty()) {
                    firestore = FirebaseFirestore.getInstance();
                    usercol = firestore.collection("userdetails").document(username).collection("userdat");
                    updateProfile(username, email,fullname,bioo,reftext);
                } else {
                    Toast.makeText(registeringpage.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateProfile(String username, String email,String fullname,String bioo,String reftex) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // You can perform any other necessary actions here

                                // Update email if it is provided
                                if (!email.isEmpty()) {
                                    user.updateEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Email updated successfully
                                                        // You can perform any other necessary actions here
                                                    } else {
                                                        Toast.makeText(registeringpage.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                        // Handle the error
                                                    }
                                                }
                                            });
                                }

                                // Update contact if it is provided
                                if (!fullname.isEmpty()) {
                                    if (!bioo.isEmpty()) {
                                        String cdata2 = fullnamedittext.getText().toString().trim();
                                        String cdata3 = bioupdater.getText().toString().trim();
                                        String cdata4= usernameEditText.getText().toString().trim();
                                        Map<String, Object> commentData1 = new HashMap<>();
                                        commentData1.put("fullname", cdata2);
                                        commentData1.put("bio", cdata3);
                                        commentData1.put("username", cdata4);
                                        usercol.document(username).set(commentData1);
                                        usercol.add(commentData1)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Failed to send comment
                                                        Toast.makeText(registeringpage.this, "Failed to send comment", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }


                                if (!reftex.isEmpty()) {


                                }

                                Toast.makeText(registeringpage.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                startAuctionMapActivity();
                            } else {
                                Toast.makeText(registeringpage.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                // Handle the error
                            }
                        }
                    });
        } else {
            Toast.makeText(registeringpage.this, "User is null", Toast.LENGTH_SHORT).show();
            // Handle the case where the user is null
        }
    }
    private void startAuctionMapActivity() {
        Intent intent = new Intent(registeringpage.this,aucmap.class);
        startActivity(intent);
        finish();
    }
}

