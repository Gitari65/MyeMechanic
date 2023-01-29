package com.example.myemechanic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RatingMainActivity extends AppCompatActivity implements RecyclerViewInterface {
RatingBar simpleRatingBar,ratingBar;
    Float rating;
    DatabaseReference ratingRef;
    String driverFirstName;
    DocumentSnapshot snapshot,documentSnapshot;
    String review;
    Button submitButton;
    private AdapterReviews myReviewAdapter;
    ArrayList<reviews_details> reviewsDetailsArrayList;
ProgressDialog progressDialog;

    private Context mContext;
    private Activity mActivity;
    EditText editText;

    private LinearLayout mRelativeLayout;
    private Button mButton;
    RecyclerView recyclerView;
    TextView textView;
    ImageButton imageButtonBack;


    private PopupWindow mPopupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_main);
ratingBar=findViewById(R.id.total_star_rating);
imageButtonBack=findViewById(R.id.btn_backratings);


        getDriverDetails();
        reviewsDetailsArrayList=new ArrayList<reviews_details>();
        myReviewAdapter = new AdapterReviews(getApplicationContext(),reviewsDetailsArrayList,  this);


        recyclerView=findViewById(R.id.myrecylerviewReviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RatingMainActivity.this));

        progressDialog=new ProgressDialog(RatingMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();

        MyEventChangeListener();
        String current_userId = getIntent().getStringExtra("mechanicId");

        setTotalRatings();
        String carPart=getIntent().getStringExtra("carPart");
        textView=findViewById(R.id.tv_Addreview);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=getIntent().getStringExtra("date");


                Intent intent = new Intent(getApplicationContext(), RateMechanicActivity.class);
                intent.putExtra("mechanicId", current_userId);
                intent.putExtra("date", date);
                //String date=getIntent().getStringExtra("date");
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
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=getIntent().getStringExtra("date");

                Intent intent = new Intent(getApplicationContext(), DriverSelectMechanic.class);
                intent.putExtra("currentuserid", current_userId);
                intent.putExtra("date", date);
               // String date=getIntent().getStringExtra("date");
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

        ratingRef  = FirebaseDatabase.getInstance().getReference("MechanicReviews").child("Ratings").child(current_userId).push();
      // DatabaseReference ratingRef1  = FirebaseDatabase.getInstance().getReference("MechanicReviews").child(current_userId).child("rating");
//ratingRef1.setValue(rating);
        ratingRef.updateChildren(hashMap);
    }
    private void MyEventChangeListener(){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String current_userId = getIntent().getStringExtra("mechanicId");
        Log.d(TAG, "MyEventChangeListener: id= "+current_userId);

        DatabaseReference databaseReference   = FirebaseDatabase.getInstance().getReference("MechanicReviews").child("Ratings").child(current_userId);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reviews : snapshot.getChildren()){
                    //descriptions.add(request.child("serviceDescription").getValue().toString());
                    reviews_details reviewsDetails=reviews.getValue(reviews_details.class);
                    if(reviewsDetails!=null){
                        reviewsDetailsArrayList.add(reviewsDetails);
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: null reviews");
                    }
                  

                }

                myReviewAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myReviewAdapter);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        });
    }
    private  void setTotalRatings(){

        String current_userId = getIntent().getStringExtra("mechanicId");
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

                final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference dbRef =  FirebaseDatabase.getInstance().getReference("MechanicReviews").child("Ratings").child(current_userId);

                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double total = 0.0;
                        double count = 0.0;
                        double average = 0.0;

                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            if (ds.child("rating").getValue()!=null){

                               Float rating = Float.valueOf((ds.child("rating").getValue().toString()));
                                total = total + rating;
                                count = count + 1;
                                average = total / count;

                            }
                            else{
                                Log.d(TAG, "onDataChange: nullsnapshot");

                            }

                        }

                        final DatabaseReference newRef1 = db.child("Technicians").child(current_userId).child("mechanicRating");
                        newRef1.setValue(String.valueOf(average));
                        final DatabaseReference newRef = db.child("MechanicReviews").child(current_userId).child("AverageRating");
                        newRef.child("current").setValue(average);

                        ratingBar.setRating((float) average);
                        Map<String, Object> user3 = new HashMap<>();
                        user3.put("mechanicRating",average);
                        FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                        db3.collection("mechanics").document(current_userId).set(user3, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Log.d(ContentValues.TAG, "onSuccess: spinner stored");
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

    }

    @Override
    public void onItemClick(int position) {

    }
}