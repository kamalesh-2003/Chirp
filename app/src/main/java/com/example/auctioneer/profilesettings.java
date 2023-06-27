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
import android.widget.ImageButton;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class profilesettings extends AppCompatActivity {
    private Button updateProfileButton;
    private FirebaseFirestore firestore;
    private CollectionReference usercol;
   private Button updateprof;
   private EditText profname,profemail,proffullname,profbio;
   ImageButton button4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesettings);

        button4 =findViewById(R.id.imageButton2);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent inten3 =new Intent(profilesettings.this,aucmap.class);
                startActivity(inten3);
            }
        });
        androidx.appcompat.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.logo_img,null);
        actionbar.setCustomView(v);
        profname = findViewById(R.id.unameid);
        profemail=findViewById(R.id.emailEditText);
        proffullname=findViewById(R.id.fullNameEditText);
        profbio=findViewById(R.id.bioEditText);

        FirebaseUser userm = FirebaseAuth.getInstance().getCurrentUser();

        String uname = userm.getDisplayName();
        firestore = FirebaseFirestore.getInstance();
        usercol = firestore.collection("userdetails").document(uname).collection("userdat");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();
            String email = user.getEmail();
            profname.setText(username);
            profemail.setText(email);
            usercol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Fetching the specific fields from the document
                             String fullnamee = document.getString("fullname");
                            String bioo = document.getString("bio");
                            System.out.println(bioo);

                            // Update the UI or perform necessary actions with the fetched fields

                                    proffullname.setText(fullnamee);
                                        profbio.setText(bioo);



                        }
                    }
                }
            });



            updateprof = findViewById(R.id.createProfileButton);

            updateprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username =profname.getText().toString().trim();
                String email = profemail.getText().toString().trim();
                String fullname = proffullname.getText().toString().trim();
                String bioo = profbio.getText().toString().trim();


                if (!username.isEmpty()) {

                    updateProfile(username, email, fullname, bioo);
                } else {
                    Toast.makeText(profilesettings.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        }

    }

    private void updateProfile(String username, String email,String fullname,String bioo) {
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
                                                        Toast.makeText(profilesettings.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                        // Handle the error
                                                    }
                                                }
                                            });
                                }

                                if (!fullname.isEmpty()) {
                                    String cdata2= proffullname.getText().toString().trim();
                                    Map<String, Object> commentData1 = new HashMap<>();
                                    commentData1.put("fullname", cdata2);
                                    usercol.add(commentData1)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to send comment
                                                    Toast.makeText(profilesettings.this, "Failed to send comment", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                                if (!bioo.isEmpty()) {
                                    String cdata3= profbio.getText().toString().trim();
                                    Map<String, Object> commentData2 = new HashMap<>();
                                    commentData2.put("bio", cdata3);
                                    usercol.add(commentData2)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to send comment
                                                    Toast.makeText(profilesettings.this, "Failed to send comment", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }



                                Toast.makeText(profilesettings.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                startAuctionMapActivity();
                            } else {
                                Toast.makeText(profilesettings.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                // Handle the error
                            }
                        }
                    });
        } else {
            Toast.makeText(profilesettings.this, "User is null", Toast.LENGTH_SHORT).show();
            // Handle the case where the user is null
        }
    }
    private void startAuctionMapActivity() {
        Intent intent = new Intent(profilesettings.this,aucmap.class);
        startActivity(intent);
        finish();
    }
}
