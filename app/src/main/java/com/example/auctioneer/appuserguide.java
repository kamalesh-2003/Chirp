package com.example.auctioneer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class appuserguide extends AppCompatActivity {
    Button closebut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appuserguide);
        closebut= findViewById(R.id.closeButton);
        closebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(appuserguide.this,settingspage.class);
                startActivity(intent);
            }
        });
    }
}