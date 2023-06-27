package com.example.auctioneer;
        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;

        import com.google.android.gms.ads.AdRequest;
        import com.google.android.gms.ads.LoadAdError;
        import com.google.android.gms.ads.interstitial.InterstitialAd;
        import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
        import com.google.android.gms.ads.FullScreenContentCallback;

public class adpages extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    Intent collectdhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adpages);

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-7878214466027765/6353271615", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The interstitialAd object will be non-null when the ad is loaded successfully.
                mInterstitialAd = interstitialAd;
                showInterstitialAd(); // Call the method to show the ad here
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
                Log.d("TAG", "Interstitial ad failed to load: " + loadAdError.getMessage());
            }
        });
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    collectdhat =getIntent();

                    finish();
                    Intent seninte =new Intent(adpages.this,rewardspage.class);
                    startActivity(seninte);
                }
            });
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

}
