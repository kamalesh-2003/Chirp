
package com.example.auctioneer;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class launchvideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchvideo);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.logovid4);

        final VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(videoUri);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Video playback completed, start MainActivity
                Intent intent = new Intent(launchvideo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        videoView.start();
    }
}
