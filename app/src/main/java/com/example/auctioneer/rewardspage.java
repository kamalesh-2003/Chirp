package com.example.auctioneer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class rewardspage extends AppCompatActivity {
    private TextView reftest;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewardspage);
        reftest = findViewById(R.id.userreward);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();
            reftest.setText(username);
        }
        reftest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reftestword = reftest.getText().toString().trim();

                // Get the ClipboardManager instance
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData object with the text to copy
                ClipData clipData = ClipData.newPlainText("Text", reftestword);

                // Set the ClipData object as the current clipboard contents
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(rewardspage.this, "Copied referral code", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
