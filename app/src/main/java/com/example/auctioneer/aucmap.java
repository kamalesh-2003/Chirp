package com.example.auctioneer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class aucmap extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private TextView cityTextView;
    private EditText messageEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton sendButton;
    private AdView mAdView,mAdView1,mAdview2;
    ScrollView scrollty;
    private ImageButton picsendbutton;
    private LinearLayout messageContainer;
    private static final int PERMISSION_ID = 44;
    private int SELECT_PICTURE = 200;
    // Firebase Firestore
    private FirebaseFirestore firestore;
    private CollectionReference comments;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    Uri selectedImageUri;
    String downloadUrl;
String usern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aucmap);
        androidx.appcompat.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.logo_img,null);
        actionbar.setCustomView(v);
        mAdview2 =findViewById(R.id.adView);
        MobileAds.initialize(this);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdview2.loadAd(adRequest2);

        scrollty = findViewById(R.id.scroll1);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });

        scrollty.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollty.getScrollY() == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform the refresh action here
                refreshPage();
            }
        });


        cityTextView = findViewById(R.id.cityTextView);
        messageEditText = findViewById(R.id.commentEditText);
        sendButton = findViewById(R.id.postCommentButton);
        messageContainer = findViewById(R.id.messageContainer);
        picsendbutton = findViewById(R.id.picsend);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        getLastLocation();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    String cityName = cityTextView.getText().toString().substring(19); // Extract city name from the TextView
                    sendComment(message); // Call the sendComment method
                }
            }
        });
        picsendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString().trim();
                picupload();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dropdown, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_profile:
                // Handle profile button click
                openProfile();
                return true;
            case R.id.action_settings:
                // Handle settings button click
                openSettings();
                return true;
            case R.id.logout_settings:
                // Handle settings button click
                openlogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openProfile() {
        Intent send1 = new Intent(aucmap.this, profilesettings.class);
        startActivity(send1);
    }

    private void openSettings() {
        Intent send2 = new Intent(aucmap.this, settingspage.class);
        startActivity(send2);
    }

   private void openlogout(){
       FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
       firebaseAuth.signOut();
       finish();
       Intent send3 = new Intent(aucmap.this, MainActivity.class);
       startActivity(send3);
   }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        try {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                // Reverse geocoding to get the city name
                                Geocoder geocoder = new Geocoder(aucmap.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses.size() > 0) {
                                        String cityName = addresses.get(0).getLocality();
                                        updateCityTextView(cityTextView, cityName);
                                        comments = firestore.collection("chatrooms").document(cityName).collection("crmessages");
                                        fetchComments();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please enable location", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    public static void updateCityTextView(TextView cityTextView, String cityName) {
        cityTextView.setText("Trending posts in " + cityName);
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Please allow location tracking to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Geocoder geocoder;

// Initialize fusedLocationClient, locationCallback, and geocoder in your class constructor or onCreate method

    private void requestNewLocationData() {
        // Create the location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Update interval in milliseconds

        // Create the location callback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                // Get the latest location
                Location location = locationResult.getLastLocation();

                // Use Geocoder to get the city name from the location
                Geocoder geocoder = new Geocoder(aucmap.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        String cityName = addresses.get(0).getLocality();
                        updateCityTextView(cityTextView, cityName);
                        comments = firestore.collection("chatrooms").document(cityName).collection("crmessages");
                        fetchComments();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Request location updates
        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            requestPermissions();
        }
    }



    private void sendComment(String message) {
        // Create a new comment document in Firestore
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("message", message);

        // Get the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        String date = dateFormat.format(currentDate);
        String time = timeFormat.format(currentDate);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

           usern = user.getDisplayName();


        // Add date and time to the comment data
        commentData.put("date", date);
        commentData.put("time", time);
        commentData.put("username",usern);

        comments.add(commentData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Comment sent successfully
                        // Retrieve the comment message
                        String message = messageEditText.getText().toString().trim();
                         System.out.println(usern);
                        // Add the comment dynamically to the ScrollView
                        addcomment(message, date, time,usern);
                        messageEditText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to send comment
                        Toast.makeText(aucmap.this, "Failed to send comment", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void fetchComments() {
        comments.orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                        int count = 0; // Initialize count outside the loop

                        for (int i = 0; i < documentSnapshots.size(); i++) {
                            DocumentSnapshot documentSnapshot = documentSnapshots.get(i);
                            String message = documentSnapshot.getString("message");
                            String date = documentSnapshot.getString("date");
                            String time = documentSnapshot.getString("time");
                            String boxtext = documentSnapshot.getString("boxtext");
                            String usernamefetch = documentSnapshot.getString("username");

                            // Create the regular expression pattern
                            String regex = "\\bhttps\\b";

                            // Create a Pattern object
                            Pattern pattern = Pattern.compile(regex);

                            // Create a Matcher object
                            Matcher matcher = pattern.matcher(message);

                            if (matcher.find()) {
                                Uri pichan = Uri.parse(message);
                                picadd(pichan, date, time, boxtext, usernamefetch);

                            } else {
                                addcomment(message, date, time, usernamefetch);

                            }

                        }
                    }
                } else {
                    // Error occurred while fetching comments
                    Toast.makeText(aucmap.this, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addcomment(String message, String date, String time,String uname2) {
        View commentView = getLayoutInflater().inflate(R.layout.post_item, null);
        TextView messageTextView = commentView.findViewById(R.id.messageTextView);
        TextView dateTextView = commentView.findViewById(R.id.dateTextView);
        TextView timeTextView = commentView.findViewById(R.id.timeTextView);
        TextView userdatan1= commentView.findViewById(R.id.usertextn);


        // Set the comment message, date, and time
        messageTextView.setText(message);
        dateTextView.setText(date);
        timeTextView.setText(time);
        userdatan1.setText(uname2);
        // Add the click listener to the comment view
        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taker1= userdatan1.getText().toString().trim();
                Intent intent = new Intent(aucmap.this, post_holder_acti.class);
                intent.putExtra("message", message);
                intent.putExtra("usern",taker1);
                intent.putExtra("date",date);
                intent.putExtra("time",time);
                startActivity(intent);
            }
        });

        SpannableString spannableString = new SpannableString(userdatan1.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent6 = new Intent(aucmap.this, otherprofview.class);
                intent6.putExtra("usenam",uname2);
                startActivity(intent6);
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userdatan1.setText(spannableString);
        userdatan1.setMovementMethod(LinkMovementMethod.getInstance());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(0, 0, 0, 10);
        commentView.setLayoutParams(layoutParams);

        // Add the comment view to the messageContainer LinearLayout
        messageContainer.addView(commentView, 0);
    }

    private void picupload() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_OPEN_DOCUMENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    String imageName = UUID.randomUUID().toString();
                    final StorageReference imageRef = storage.getReference().child("images").child(imageName);

                    // Upload the image to Firebase Storage
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Retrieve the download URL of the image
                                    imageRef.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    downloadUrl = uri.toString();
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                    String usern = user.getDisplayName();
                                                    Toast.makeText(aucmap.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    Map<String, Object> commentData = new HashMap<>();
                                                    commentData.put("message", downloadUrl);
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                                                    String boxtext = messageEditText.getText().toString();
                                                    Date currentDate = new Date();
                                                    String date = dateFormat.format(currentDate);
                                                    String time = timeFormat.format(currentDate);
                                                    commentData.put("date", date);
                                                    commentData.put("time", time);
                                                    commentData.put("boxtext",boxtext);
                                                    commentData.put("usern",usern);
                                                    comments.add(commentData)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    picadd(selectedImageUri,date,time,boxtext,usern);
                                                                }
                                                            });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to retrieve download URL
                                                    Toast.makeText(aucmap.this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to upload image
                                    Toast.makeText(aucmap.this, "Failed to upload image", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            }
        }
    }
    private void picadd(Uri selectedImageUri, String date, String time,String boxtest,String usern1) {
        View commentView = getLayoutInflater().inflate(R.layout.pic_post_item, null);
        TextView messagetextView = commentView.findViewById(R.id.messageTextView);
        TextView dateTextView = commentView.findViewById(R.id.dateTextView);
        TextView timeTextView = commentView.findViewById(R.id.timeTextView);
        ImageView imgpicpost = commentView.findViewById(R.id.pic1);
        TextView userdatan1= commentView.findViewById(R.id.usertextn);
        String message = messageEditText.getText().toString().trim();
        if(!message.isEmpty()){
            messagetextView.setText(boxtest);
        }

        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aucmap.this, pic_post_holder_acti.class);
                String uriString = selectedImageUri.toString();
                intent.putExtra("message", uriString);
                intent.putExtra("date",date);
                intent.putExtra("time",time);
                startActivity(intent);
            }
        });
        dateTextView.setText(date);
        timeTextView.setText(time);
        userdatan1.setText(usern1);
        Picasso.get().load(selectedImageUri).into(imgpicpost);
        messageEditText.setText("");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        layoutParams.setMargins(0, 0, 0, 10);
        commentView.setLayoutParams(layoutParams);

        messageContainer.addView(commentView, 0);
    }

    private void refreshPage() {
        // Simulate a delay before stopping the refresh animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update your data or perform any necessary actions here
                // For example, you can fetch new data from an API

                // Stop the refresh animation
                swipeRefreshLayout.setRefreshing(false);

                // Recreate the activity
                recreate();
            }
        }, 2000); // Simulate a 2-second delay, replace with your actual logic
    }


}
