package com.example.myemechanic;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myemechanic.databinding.ActivityMapsBinding;
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

public class MapsActivity extends AppCompatActivity  implements OnMapReadyCallback, RecyclerViewInterface {
    //location
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_PERMISSION_CODE=123;
    LocationTrack locationTrack;
    Double currentLongitude,currentLatitude;
    TextView textView;
    String driversLocation;
    RecyclerView recyclerView;
    AdapterTechnicians myRAdapter;
    ArrayList<Technician> techniciansArrayList,techniciansArrayList1;
    ProgressDialog progressDialog;
    LinearLayout layout;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


                layout=findViewById(R.id.layoutMapsDetails);
textView=findViewById(R.id.textviewtoggleMaps);
        recyclerView=findViewById(R.id.myrecylerviewNearTechnicians);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // toggle details
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout.getVisibility() == View.VISIBLE){
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
        techniciansArrayList=new ArrayList<Technician>();
        techniciansArrayList1=new ArrayList<Technician>();




        progressDialog=new ProgressDialog(MapsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        //progressDialog.show();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

//        MainLocationCode();
        fetchLocation();

//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        // techniciansArrayList.add(technician);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // techniciansArrayList=new ArrayList<Technician>();
//getCurrentLocation();
        fetchLocation();

//        LatLng latLng1 = new LatLng(currentLatitude, currentLongitude);
//        mCurrLocationMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//        marker = mMap.addMarker(new MarkerOptions().position(latLng1).title("You")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference("Technicians");
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // techniciansArrayList1.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Technician  technician = s.getValue(Technician.class);

                    techniciansArrayList1.add(technician);

                    for (int i = 0; i < techniciansArrayList1.size(); i++) {
                        if (technician.currentLatitude!=null){
                            //Recycler view nearest mechanics
//                            techniciansArrayList.add(technician);
//                            myRAdapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(myRAdapter);
//                    if(progressDialog.isShowing()){
//                        progressDialog.dismiss();
//                    }

                            //near longitudes

                            LatLng latLng = new LatLng(Double.parseDouble(technician.currentLatitude), Double.parseDouble(technician.currentLongitude));
                            if (mMap != null) {
                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(technician.firstNamee));
                            }
                        }

                    }
 //                            techniciansArrayList.add(technician);
//                    myRAdapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(myRAdapter);
//                    if(progressDialog.isShowing()){
//                        progressDialog.dismiss();
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void fetchLocation() {
        checkLocationPermission();
        if(!locationEnabled()){
            new AlertDialog.Builder(this)
                    .setTitle("Location Needed")
                    .setMessage("Please Turn on Location Services on the application")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User declined for Background Location Permission.
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        currentLatitude =location.getLatitude();
                        currentLongitude=location.getLongitude();
                        if(currentLongitude!=0&& currentLatitude!=0){
                            getNearestMechanic();
                            // updateLocationToFirebase();
                            LatLng latLng1 = new LatLng(currentLatitude, currentLongitude);
//        mCurrLocationMarker = mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            marker = mMap.addMarker(new MarkerOptions().position(latLng1).title("You")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                           // getNearestMechanic();

                            updateLocationToFirebase();

                        }else{
                            Log.d(TAG, "onSuccess: Location object Null");
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Something Wrong Happened.Try Again Later",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // Fine Location Permission is not granted so ask for permission
//            askForLocationPermission();
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private boolean locationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    ///location
    private  void  getCurrentLocation(){
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        locationTrack = new LocationTrack(MapsActivity.this);


        if (locationTrack.canGetLocation()) {


            currentLongitude = locationTrack.getLongitude();
            currentLatitude = locationTrack.getLatitude();
            Log.d(TAG, "onComplete: current longitude"+currentLongitude);
            Log.d(TAG, "onComplete: current latitude"+currentLatitude);


            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }


    }

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


        locationTrack = new LocationTrack(MapsActivity.this);


        if (locationTrack.canGetLocation()) {


            currentLongitude = locationTrack.getLongitude();
            currentLatitude = locationTrack.getLatitude();
            Log.d(TAG, "onComplete: current longitude"+currentLongitude);
            Log.d(TAG, "onComplete: current latitude"+currentLatitude);
getNearestMechanic();

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

                    user3.put("driverLongitude",String.valueOf(currentLongitude));
                    user3.put("driverLatitude",String.valueOf(currentLatitude));


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
        new AlertDialog.Builder(MapsActivity.this)
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

        myRAdapter = new AdapterTechnicians(getApplicationContext(),techniciansArrayList, (RecyclerViewInterface) this);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                progressDialog.show();
                // System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                DatabaseReference locationChild = FirebaseDatabase.getInstance().getReference("Technicians");
                locationChild.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //descriptions.add(request.child("serviceDescription").getValue().toString());

                            Technician technician=dataSnapshot.getValue(Technician.class);
                            techniciansArrayList.add(technician);

                        //  String name = dataSnapshot.child("name").getValue(String.class);
                        // System.out.println(String.format("Electrician name: %s", name));


                        myRAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(myRAdapter);
//                        techniciansArrayList.clear();
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
        long timestamp=getIntent().getLongExtra("timestamp",0);
        intent.putExtra("timestamp", timestamp);
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