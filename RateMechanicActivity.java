package com.example.myemechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RateMechanicActivity extends AppCompatActivity {
Button submitButton;
RatingBar simpleRatingBar;
String driverFirstName;
String review;
ImageButton imageButton;
Float rating;
EditText editText;
DocumentSnapshot snapshot,documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_mechanic);
        // initiate rating bar and a button
        simpleRatingBar =findViewById(R.id.star_rating);
        submitButton = findViewById(R.id.submitRatingButton);
        imageButton=findViewById(R.id.ib_closeRate);
        rating=simpleRatingBar.getRating();
        String current_userId = getIntent().getStringExtra("mechanicId");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RateMechanicActivity.this,RatingMainActivity.class);
                intent.putExtra("mechanicId", current_userId);
                String date=getIntent().getStringExtra("date");
                String carPart=getIntent().getStringExtra("carPart");
                String carModel=getIntent().getStringExtra("carModel");
                long timestamp=getIntent().getLongExtra("timestamp",0);
                intent.putExtra("timestamp", timestamp);
                intent.putExtra("carPart", carPart);
                intent.putExtra("carModel", carModel);
                intent.putExtra("currentuserid", current_userId);
                intent.putExtra("date", date);

                startActivity(intent);
            }
        });
        editText=findViewById(R.id.Edt_driverReview);


        getDriverDetails();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating==null){
                    return;
                }
                else{
                    storeRatings();
                }


            }
        });
    }
    public void getDriverDetails(){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();



        db.collection("Drivers").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                driverFirstName= documentSnapshot.getString("driverFirstName");
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

    public  void storeRatings(){
        review=editText.getText().toString();
        rating =  simpleRatingBar.getRating();
        if (rating==null){
            Toast.makeText(getApplicationContext(),   "Select your ratings", Toast.LENGTH_LONG).show();

            return;
        }
        String current_userId = getIntent().getStringExtra("mechanicId");
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        getDriverDetails();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        String date = dateFormat.format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("review", review);
        hashMap.put("rating", rating);
        hashMap.put("name",driverFirstName);
        hashMap.put("date",date);


        DatabaseReference ratingRef  = FirebaseDatabase.getInstance().getReference("MechanicReviews").child("Ratings").child(current_userId);
        ratingRef.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),   "Thanks for the feedback 😊", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(RateMechanicActivity.this,RatingMainActivity.class);
                intent.putExtra("mechanicId", current_userId);

                startActivity(intent);

            }
        });
    }
}