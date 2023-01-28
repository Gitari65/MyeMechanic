package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class DriverViewMechanics extends AppCompatActivity implements  RecyclerViewInterface {
    private RecyclerView recyclerView;
    private ArrayList<mechanic_details> mechanicsArrayList;
    private mechanicsAdapter mechanicsAdapter2;
    private  MyAdapter myAdapter;
    TextView textView;
    String carPart,childKey,carModel;

    private FirebaseFirestore db;
    ProgressDialog progressDialog;
    ProgressBar loadingPB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_mechanics);
        //ActionBar actionBar = getSupportActionBar();
       // actionBar.hide();
        textView=findViewById(R.id.txtview_mechanicType);



        recyclerView=findViewById(R.id.myrecylerviewDV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mechanicsArrayList=new ArrayList<mechanic_details>();
        myAdapter = new MyAdapter(getApplicationContext(),mechanicsArrayList,  this);

        progressDialog=new ProgressDialog(DriverViewMechanics.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
       progressDialog.show();
    String carProblemDescription=getIntent().getStringExtra("carProblemDescription");
        String carModel=getIntent().getStringExtra("carModel");
       // String carPart=getIntent().getStringExtra("carPart");

        recyclerView.setAdapter(myAdapter);
        getDriverProblemDetails();
        Log.d(TAG, "onCreate: activityPart "+carPart);
        Log.d(TAG, "onCreate: activityModel "+carModel);
        Log.d(TAG, "onComplete: key"+childKey);




    }
    //kenedychomba87@gmail.com

    public void getDriverProblemDetails(){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
       String userID  = mAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //String current_userId = getIntent().getStringExtra("driversId");

        String childKey=getIntent().getStringExtra("childKey");
        Log.d(TAG, "getDriverProblemDetails: key"+childKey);

        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
         String carPart=getIntent().getStringExtra("carPart");
        String carModel=getIntent().getStringExtra("carModel");

        // CollectionReference mydbRef = db.collection("mechanics");


        db1.collection("mechanics").whereEqualTo(carPart, "True").whereEqualTo("registrationStatus","verified").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Log.d(TAG, "onEvent: error firestore ");
                    return;
                }
                assert value != null;
                for(DocumentChange documentChange :value.getDocumentChanges())
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        mechanicsArrayList.add(documentChange.getDocument().toObject(mechanic_details.class));


                    }
                myAdapter.notifyDataSetChanged();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });



        if (Objects.equals(carPart, "Don't know")){

            textView.setText(" Mechanics");
        }
        else{

            textView.setText(carModel+" "+ carPart+" Mechanics");

        }


        // Log.d(TAG, "onComplete: mecha garage name "+garagename);




    }

    @Override
    public void onItemClick(int position) {
        String carProblemDescription=getIntent().getStringExtra("carProblemDescription");
        String carModel=getIntent().getStringExtra("carModel");
        String carPart=getIntent().getStringExtra("carPart");
        String driverId=getIntent().getStringExtra("driverId");
        String childKey=getIntent().getStringExtra("childKey");
        String date=getIntent().getStringExtra("date");
        long timestamp=getIntent().getLongExtra("timestamp",0);

        Intent intent = new Intent(getApplicationContext(), DriverSelectMechanic.class);
        intent.putExtra("fName", mechanicsArrayList.get(position).getFirstName());
        intent.putExtra("sName", mechanicsArrayList.get(position).getSecondName());
        intent.putExtra("email", mechanicsArrayList.get(position).getMechanicEmail());
        intent.putExtra("phonenumber", mechanicsArrayList.get(position).getPhoneNumber());
        intent.putExtra("registrationStatus", mechanicsArrayList.get(position).getRegistrationStatus());
        intent.putExtra("garageLocation", mechanicsArrayList.get(position).getGarageLocation());
        intent.putExtra("profilephotourl", mechanicsArrayList.get(position).getProfilePhotoUrl());
        intent.putExtra("currentuserid", mechanicsArrayList.get(position).getCurrent_userId());
        intent.putExtra("mechanicToken", mechanicsArrayList.get(position).getMechanicToken());
        intent.putExtra("carPart", carPart);
        intent.putExtra("childKey", childKey);
        intent.putExtra("date", date);
        intent.putExtra("timestamp",timestamp);
        intent.putExtra("carModel", carModel);
        intent.putExtra("carProblemDescription", carProblemDescription);
        intent.putExtra("driverId", driverId);
        startActivity(intent);

    }
}