package com.example.myemechanic;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class NearestMechanicActivity extends AppCompatActivity implements RecyclerViewInterface{
    //location
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
  Double currentLongitude,currentLatitude;
  String driversLocation;
  RecyclerView recyclerView;
  AdapterTechnicians myRAdapter;
  ArrayList<Technician> techniciansArrayList;
  ProgressDialog progressDialog;

  //imports




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_mechanic);

        recyclerView=findViewById(R.id.myrecylerviewTechnicians);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        techniciansArrayList=new ArrayList<Technician>();
        myRAdapter = new AdapterTechnicians(getApplicationContext(),techniciansArrayList, (RecyclerViewInterface) this);

        progressDialog=new ProgressDialog(NearestMechanicActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        //progressDialog.show();

        MainLocationCode();
        getNearestMechanic();
    }
    ///location
    private  void  MainLocationCode(){
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        locationTrack = new LocationTrack(NearestMechanicActivity.this);


        if (locationTrack.canGetLocation()) {


            currentLongitude = locationTrack.getLongitude();
            currentLatitude = locationTrack.getLatitude();
            Log.d(TAG, "onComplete: current longitude"+currentLongitude);
            Log.d(TAG, "onComplete: current latitude"+currentLatitude);


           updateLocationToFirebase();

            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }


    }

    private void updateLocationToFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Technicians");
      //  GeoFire geoFire = new GeoFire(ref);
      //  geoFire.setLocation(userId, new GeoLocation(40.2334983, -3.7185183));

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef = rootRef.child("Technicians");
        Query userQuery = additionalUserInfoRef.orderByChild("userid").equalTo(userId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    Map<String, Object> user3 = new HashMap<>();

                    user3.put("currentLongitude",String.valueOf(currentLongitude));
                    user3.put("currentLatitude",String.valueOf(currentLatitude));


                    ds.getRef().updateChildren(user3).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {


                            Toast.makeText(getApplicationContext(),"Location Updated Successfully",Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(),"saving failed try again later",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!

                Toast.makeText(getApplicationContext(),"Failed location Update check your connection",Toast.LENGTH_SHORT).show();

            }
        };
        userQuery.addListenerForSingleValueEvent(valueEventListener);




    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission((String) permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTrack !=null){
            locationTrack.stopListener();
        }

    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(NearestMechanicActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void getNearestMechanic()
    {
        DatabaseReference myRef =FirebaseDatabase.getInstance().getReference().child("Geofire");
        GeoFire geoFire= new GeoFire(myRef);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLatitude,currentLongitude), 8587.8 );


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                progressDialog.show();
               // System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                DatabaseReference locationChild = FirebaseDatabase.getInstance().getReference("Technicians");
                locationChild.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                      //  String name = dataSnapshot.child("name").getValue(String.class);
                       // System.out.println(String.format("Electrician name: %s", name));
                        Technician technician=dataSnapshot.getValue(Technician.class);
                       techniciansArrayList.add(technician);
                        myRAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(myRAdapter);
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        throw databaseError.toException();
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }


            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String carProblemDescription=getIntent().getStringExtra("carProblemDescription");
        String carModel=getIntent().getStringExtra("carModel");
        String carPart=getIntent().getStringExtra("carPart");
        String driverId=getIntent().getStringExtra("driverId");
        String childKey=getIntent().getStringExtra("childKey");
        String date=getIntent().getStringExtra("date");


        Intent intent = new Intent(getApplicationContext(), DriverSelectMechanic.class);
        intent.putExtra("fName", techniciansArrayList.get(position).getFirstNamee());
        intent.putExtra("sName", techniciansArrayList.get(position).getSecondNamee());
        intent.putExtra("email", techniciansArrayList.get(position).getEmaill());
        intent.putExtra("phonenumber", techniciansArrayList.get(position).getMobilee());

        intent.putExtra("profilephotourl", techniciansArrayList.get(position).getProfilePhotoUrl());
        intent.putExtra("currentuserid", techniciansArrayList.get(position).getUserid());
        intent.putExtra("carPart", carPart);
        intent.putExtra("childKey", childKey);
        intent.putExtra("date", date);
        intent.putExtra("carModel", carModel);
        intent.putExtra("carProblemDescription", carProblemDescription);
        intent.putExtra("driverId", driverId);
        startActivity(intent);

    }
}