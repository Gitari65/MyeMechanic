package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class MechanicSelectDriver extends AppCompatActivity {
    DocumentSnapshot documentSnapshot;
    TextView textViewphoneno,textViewfName,textViewEmail,textViewsName;
    ImageView imageView;
    Double driverCurrentLatitude,driverCurrentLongitude;
    Button buttonCall,buttonLocate,buttonChat,buttonBack,buttonToggle;
    LinearLayout layout;
    //kenedychomba87@gmail.com
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_select_driver);
        buttonCall=findViewById(R.id.btncalldriver);
        buttonChat=findViewById(R.id.btnchatdriver);
        buttonLocate=findViewById(R.id.btnfindDriverLocation);
        buttonBack=findViewById(R.id.btnselectdriver_back);
        buttonToggle=findViewById(R.id.btnToggleDriverDetails);

        textViewEmail=findViewById(R.id.verydriver_emailD);
        textViewphoneno=findViewById(R.id.verydriver_phonenumberD);
        textViewfName=findViewById(R.id.verydriver_firstnameD);
        textViewsName=findViewById(R.id.verydriver_secondnameD);
        imageView=findViewById(R.id.driver_profilePicture);
        layout=findViewById(R.id.layoutDriverDetails);
        getDriverDetails();


        //toggle details
        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout.getVisibility() == View.VISIBLE){
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });


        //message
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_userId = getIntent().getStringExtra("driversId");
                Intent intent = new Intent(getApplicationContext(), FloatingChatActivity.class);
                intent.putExtra("uid", current_userId);
                startActivity(intent);
            }
        });
        //call
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneNumber();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    //methods
    public void getDriverDetails(){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String current_userId = getIntent().getStringExtra("driversId");



        db.collection("Drivers").document(current_userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String driverFirstName= documentSnapshot.getString("driverFirstName");
                                String driverSecondName= documentSnapshot.getString("driverSecondName");
                                String driverEmail= documentSnapshot.getString("driverEmail");
                                String driverPhoneNumber= documentSnapshot.getString("driverPhoneNumber");

                                String profileImageUrl= documentSnapshot.getString("profilePhoto");
                                Picasso.get().load(profileImageUrl).into(imageView);
                               driverCurrentLatitude= documentSnapshot.getDouble("driverCurrentLatitude");
                                driverCurrentLongitude= documentSnapshot.getDouble("driverCurrentLongitude");
                                buttonLocate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uriParsingGoogleMapsIntent();
                                    }
                                });


                                // String imageUrl=documentSnapshot.getString("imageUrl");

                                textViewEmail.setText(driverEmail);
                                textViewfName.setText(driverFirstName);
                                textViewphoneno.setText(driverPhoneNumber);
                                textViewsName.setText(driverSecondName);

                                // Log.d(TAG, "onComplete: mecha garage name "+garagename);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
            }
        }
    }

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MechanicSelectDriver.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                textViewphoneno=findViewById(R.id.verydriver_phonenumberD);
                String mechPhoneNumber=textViewphoneno.getText().toString();


                // Getting instance of Intent with action as ACTION_CALL
                Intent phone_intent = new Intent(Intent.ACTION_CALL);

                // Set data of Intent through Uri by parsing phone number
                phone_intent.setData(Uri.parse("tel:" + mechPhoneNumber));

                // start Intent
                startActivity(phone_intent);

            }
            else {
                textViewphoneno=findViewById(R.id.verydriver_phonenumberD);
                String mechPhoneNumber=textViewphoneno.getText().toString();
                // Getting instance of Intent with action as ACTION_CALL
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                // Set data of Intent through Uri by parsing phone number
                phone_intent.setData(Uri.parse("tel:" + mechPhoneNumber));
                // start Intent
                startActivity(phone_intent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    //maps intent
    public  void uriParsingGoogleMapsIntent(){
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&daddr="+destinationCityName));
//        String uri = "http://maps.google.com/maps?saddr="+driverCurrentLatitude+","+driverCurrentLongitude+"+&daddr="+mechanicCurrentLatitude+","+mechanicCurrentLongitude;
        //String uri = "http://maps.google.com/maps?saddr="+driverCurrentLatitude+","+driverCurrentLongitude+"+&daddr="+mechanicCurrentLatitude+","+mechanicCurrentLongitude;
//String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + yourLocationName + ")";
        String strUri = "http://maps.google.com/maps?q=loc:" + driverCurrentLatitude + "," + driverCurrentLongitude + " (" + "Label which you want" + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

}