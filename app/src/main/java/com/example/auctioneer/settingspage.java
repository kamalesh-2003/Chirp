package com.example.auctioneer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settingspage extends AppCompatActivity {
    private TextView userget;
private Button button1,button2,button3;
private ImageButton button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingspage);
button4 =findViewById(R.id.imageButton);
button4.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        finish();
        Intent inten3 =new Intent(settingspage.this,aucmap.class);
        startActivity(inten3);
    }
});
button1 =findViewById(R.id.shareTextView);
button1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent3 = new Intent(settingspage.this,adpages.class);
        startActivity(intent3);
    }
});
        button2=findViewById(R.id.guideTextView);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(settingspage.this,appuserguide.class);
                startActivity(intent1);
            }
        });
        button3=findViewById(R.id.guideTextView2);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(settingspage.this,aboutapppage.class);
                startActivity(intent2);
            }
        });


        userget = findViewById(R.id.shareTextView2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();
            userget.setText(username);
        }


    }
}