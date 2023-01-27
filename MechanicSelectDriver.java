package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MechanicSelectDriver extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    DocumentSnapshot documentSnapshot,documentSnapshot1;
    TextView textViewphoneno,textViewfName,textViewEmail,textViewsName,textViewAddWork;
    ImageView imageView;
    Double driverCurrentLatitude,driverCurrentLongitude;
    Button buttonCall,buttonLocate,buttonChat,buttonBack,buttonToggle,buttonSaveWork;
    EditText editTextProblem,editTextCost,editTexttimeTaken,editTextPrice,editTextCarPart,editTextCarModel;
    LinearLayout layout,layoutWorkDetails;
    String paymentMethod,cost,price,problem, time,part,model;
    String []payment={"Mpesa","Cash","Bank"};
    ProgressDialog pd;
    String mechanicfirstName,mechanicEmail,mechanicPhoneNumber,mechanicsecondName;
    String driverFirstName,driverSecondName,driverEmail,driverPhoneNumber,profileImageUrl;

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
        buttonSaveWork=findViewById(R.id.btnSaveWorkDetails);
        //storing spinner

        pd = new ProgressDialog(MechanicSelectDriver.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Saving....");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        textViewEmail=findViewById(R.id.verydriver_emailD);
        textViewphoneno=findViewById(R.id.verydriver_phonenumberD);
        textViewfName=findViewById(R.id.verydriver_firstnameD);
        textViewsName=findViewById(R.id.verydriver_secondnameD);
        imageView=findViewById(R.id.driver_profilePicture);
        layout=findViewById(R.id.layoutDriverDetails);
        textViewAddWork=findViewById(R.id.txtRecordWork);
        layoutWorkDetails=findViewById(R.id.layoutWorkDetails);
        editTextPrice=findViewById(R.id.edtWorkPrice);
        editTextCost=findViewById(R.id.edtProblemCost);
        editTextProblem=findViewById(R.id.edtProblemFixed);
        editTextCarModel=findViewById(R.id.edtProblemcarModel);
        editTextCarPart=findViewById(R.id.edtProblemCarPart);
        editTexttimeTaken=findViewById(R.id.edtProblemTimetaken);
        getDriverDetails();
        //get string
        problem=editTextProblem.getText().toString();
        price=editTextPrice.getText().toString();
        cost=editTextCost.getText().toString();
        time=editTexttimeTaken.getText().toString();
String problemcarpart=getIntent().getStringExtra("CarPart");
String problemcarmodel=getIntent().getStringExtra("CarModel");
        editTextCarPart.setText(problemcarpart);
        editTextCarModel.setText(problemcarmodel);

        //spinner
        Spinner spin = (Spinner) findViewById(R.id.spinnerPaymentMethod);
        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(MechanicSelectDriver.this,android.R.layout.simple_spinner_item,payment);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        //Performing action onItemSelected and onNothing selected
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              paymentMethod=spin.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonSaveWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeWorkDetails();
                storeReport();
            }
        });


        //toggle details
        textViewAddWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout.getVisibility() == View.VISIBLE){
                    layout.setVisibility(View.GONE);
                }
                if(layoutWorkDetails.getVisibility() == View.VISIBLE){
                    layoutWorkDetails.setVisibility(View.GONE);
                } else {
                    layoutWorkDetails.setVisibility(View.VISIBLE);
                }
            }
        });

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
                Intent intent=new Intent( getApplicationContext(),MainActivity.class);
                startActivity(intent);

            }
        });

    }
    //methods
    public void storeWorkDetails(){
        //get string
        problem=editTextProblem.getText().toString();
        price=editTextPrice.getText().toString();
        cost=editTextCost.getText().toString();
        time=editTexttimeTaken.getText().toString();
        part=editTextCarPart.getText().toString();
        model=editTextCarModel.getText().toString();
        //utils
        if (price.equals("")) {
            editTextPrice.setError(" Enter the price paid");
            editTextPrice.requestFocus();
            return;
        }
        if (part.equals("")) {
            editTextCarPart.setError(" Enter part repaired");
            editTextCarPart.requestFocus();
            return;
        }
        if (model.equals("")) {
            editTextCarModel.setError(" Enter the carModel");
            editTextCarModel.requestFocus();
            return;
        }
        if (cost.equals("")) {
            editTextCost.setError("Please Enter cost");
            editTextCost.requestFocus();
            return;
        }
        if (problem.equals("")) {
            editTextProblem.setError(" describe problem that was fixed");
            editTextProblem.requestFocus();
            return;
        }
        if (time.equals("")) {
            editTexttimeTaken.setError(" describe problem that was fixed");
            editTexttimeTaken.requestFocus();
            return;
        }


        pd.show();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        String recordDate = dateFormat.format(new Date());
        String workChildKey = getIntent().getStringExtra("workChildKey");
        String current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String date = getIntent().getStringExtra("date");
        DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("MechanicWork");
        Log.d(TAG, "storeWorkDetails: date: "+date);
        Query userQuery = additionalUserInfoRef.orderByChild("date").equalTo(date);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    Map<String, Object> user3 = new HashMap<>();
                    user3.put("workExpense",cost);
                    user3.put("workPrice",price);
                    user3.put("paymentMethod",paymentMethod);
                    user3.put("workProblem",problem);
                    user3.put("recordDate",recordDate);
                    user3.put("timeTaken",time);
                    user3.put("paymentStatus","not paid");
                    ds.getRef().updateChildren(user3).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Work Saved Successfully",Toast.LENGTH_SHORT).show();
                            editTextCost.setText("");
                            editTextPrice.setText("");
                            editTexttimeTaken.setText("");
                            editTextProblem.setText("");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"saving failed try again later",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Work not saved check you connection",Toast.LENGTH_SHORT).show();

            }
        };
        userQuery.addListenerForSingleValueEvent(valueEventListener);



    }
    public void storeReport(){
        //get string
        problem=editTextProblem.getText().toString();
        price=editTextPrice.getText().toString();
        cost=editTextCost.getText().toString();
        time=editTexttimeTaken.getText().toString();
        part=editTextCarPart.getText().toString();
        model=editTextCarModel.getText().toString();
        //utils
        if (price.equals("")) {
            editTextPrice.setError(" Enter the price paid");
            editTextPrice.requestFocus();
            return;
        }
        if (part.equals("")) {
            editTextCarPart.setError(" Enter part repaired");
            editTextCarPart.requestFocus();
            return;
        }
        if (model.equals("")) {
            editTextCarModel.setError(" Enter the carModel");
            editTextCarModel.requestFocus();
            return;
        }
        if (cost.equals("")) {
            editTextCost.setError("Please Enter cost");
            editTextCost.requestFocus();
            return;
        }
        if (problem.equals("")) {
            editTextProblem.setError(" describe problem that was fixed");
            editTextProblem.requestFocus();
            return;
        }
        if (time.equals("")) {
            editTexttimeTaken.setError(" describe problem that was fixed");
            editTexttimeTaken.requestFocus();
            return;
        }
//      final Long timestamp = System.currentTimeMillis();
        String current_userId = getIntent().getStringExtra("driversId");
        String timestampIntent = getIntent().getStringExtra("timestamp");

        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();
getDriverDetails();
getMechanicDetails();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        if (timestampIntent!=null){
            DatabaseReference additionalUserInfoRef = rootRef.child("Work").child("mechanics").child(mechId);
            Query userQuery = additionalUserInfoRef.orderByChild("timestamp").equalTo(timestampIntent);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> user3 = new HashMap<>();
                        user3.put("workExpense",cost);
                        user3.put("workPrice",price);
                        user3.put("paymentMethod",paymentMethod);
                        user3.put("workProblem",problem);
                        user3.put("timeTaken",time);

                        user3.put("driversId",current_userId);
                        user3.put("driverFirstName",driverFirstName);
                        user3.put("driverPhoneNumber",driverPhoneNumber);
                        user3.put("paymentStatus","not paid");

                        ds.getRef().updateChildren(user3).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {


//                                Toast.makeText( getApplicationContext()," saved ",Toast.LENGTH_SHORT).show();




                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userQuery.addValueEventListener(valueEventListener);

                DatabaseReference additionalUserInfoRef1 = rootRef.child("Work").child("drivers").child(mechId);
                Query userQuery1 = additionalUserInfoRef1.orderByChild("timestamp").equalTo(timestampIntent);
                ValueEventListener valueEventListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {

                            Map<String, Object> user4 = new HashMap<>();
                            user4.put("workExpense",cost);
                            user4.put("workPrice",price);
                            user4.put("paymentMethod",paymentMethod);
                            user4.put("workProblem",problem);
                            user4.put("timeTaken",time);
                            user4.put("mechanicId",mechId);

                            user4.put("mechanicFirstName",mechanicfirstName);
                            user4.put("mechanicPhoneNumber",mechanicPhoneNumber);
                            user4.put("paymentStatus","not paid");


                            ds.getRef().updateChildren(user4).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                    Toast.makeText( getApplicationContext()," saved ",Toast.LENGTH_SHORT).show();




                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    }
                };
                userQuery1.addValueEventListener(valueEventListener1);
            }
        }



//        Map<String, Object> user3 = new HashMap<>();
//        user3.put("workExpense",cost);
//        user3.put("workPrice",price);
//        user3.put("paymentMethod",paymentMethod);
//        user3.put("workProblem",problem);
//        user3.put("timeTaken",time);
//        user3.put("timestamp",timestamp);
//        user3.put("driversId",current_userId);
//        user3.put("driverFirstName",driverFirstName);
//        user3.put("driverPhoneNumber",driverPhoneNumber);
//        user3.put("paymentStatus","not paid");
//





    public void getMechanicDetails(){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        db.collection("mechanics").document(mechId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot1 = task.getResult();
                            if (documentSnapshot1.exists()) {


                                mechanicfirstName=documentSnapshot1.getString("firstName");
                                mechanicEmail=documentSnapshot1.getString("mechanicEmail");
                                mechanicsecondName=documentSnapshot1.getString("secondName");

                                mechanicPhoneNumber=documentSnapshot1.getString("phoneNumber");



;




                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
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
                                driverFirstName= documentSnapshot.getString("driverFirstName");
                                driverSecondName= documentSnapshot.getString("driverSecondName");
                                 driverEmail= documentSnapshot.getString("driverEmail");
                                 driverPhoneNumber= documentSnapshot.getString("driverPhoneNumber");

                                 profileImageUrl= documentSnapshot.getString("profilePhoto");
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}