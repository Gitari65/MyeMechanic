package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MechanicSelectDriver extends AppCompatActivity {
    DocumentSnapshot documentSnapshot;
    TextView textViewphoneno,textViewfName,textViewEmail,textViewsName;
    ImageView imageView;
    Button buttonCall,buttonLocate,buttonChat,buttonBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_select_driver);
        buttonCall=findViewById(R.id.btncalldriver);
        buttonChat=findViewById(R.id.btnchatdriver);
        buttonLocate=findViewById(R.id.btnfindDriverLocation);
        buttonBack=findViewById(R.id.btnselectdriver_back);

        textViewEmail=findViewById(R.id.verydriver_emailD);
        textViewphoneno=findViewById(R.id.verydriver_phonenumberD);
        textViewfName=findViewById(R.id.verydriver_firstnameD);
        textViewsName=findViewById(R.id.verydriver_secondnameD);
        imageView=findViewById(R.id.driver_profilePicture);

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
}