package com.example.myemechanic;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

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
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
DrawerLayout drawerLayout ;
NavigationView navigationView;
String myuid;
ActionBarDrawerToggle actionBarDrawerToggle;
String refreshToken;
FirebaseAuth firebaseAuth;
    //location
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    Double currentLongitude,currentLatitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_PERMISSION_CODE=123;
    String driversLocation;
    RecyclerView recyclerView;
    AdapterTechnicians myRAdapter;
    ArrayList<Technician> techniciansArrayList;
    ProgressDialog progressDialog;




Toolbar toolbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)
        ) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
drawerLayout=findViewById(R.id.drawer_layout);
navigationView=findViewById(R.id.navigation_view);
actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.menu_open,R.string.closed_menu);
drawerLayout.addDrawerListener(actionBarDrawerToggle);
actionBarDrawerToggle.syncState();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
//MainLocationCode();
        firebaseAuth = FirebaseAuth.getInstance();

UpdateToken();

getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        replaceFragment(new MechanicHomeFragment());

navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                replaceFragment(new MechanicHomeFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_account:
                replaceFragment(new MechanicProfileFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_chats:
                replaceFragment(new ChatlistFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_history:
                replaceFragment(new MechReportFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_requests:
                replaceFragment(new MechViewRequestsFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_reviews:
                replaceFragment(new MechanicViewReviews());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;



            case R.id.nav_logout:

                drawerLayout.closeDrawer(GravityCompat.START);

                // check online status
                myuid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Technicians").child(myuid);
                HashMap<String, Object> hashMap = new HashMap<>();
                String statuss="";
                hashMap.put("onlineStatus", statuss);
                dbref.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent1 =new Intent(getApplicationContext(),Mechanic_login.class);
                        startActivity(intent1);
                    }
                });


                break;
        }
        return true;
    }
});





    }
    public void replaceFragment(Fragment fragment){

        androidx.fragment.app.FragmentManager fragmentManager=getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment,fragment);
        fragmentTransaction.addToBackStack(null);           //Optional
        fragmentTransaction.commit();



    }

    private void UpdateToken() {
        String UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            refreshToken = task.getResult();
                            if (UserId != null) {
                                FirebaseDatabase.getInstance().getReference("Tokens").child(UserId).child("token").setValue(refreshToken);
                            } else {
                                Log.e(TAG, "onComplete: User id is null");
                            }
                        }
                    }
                });
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


        locationTrack = new LocationTrack(MainActivity.this);


        if (locationTrack.canGetLocation()) {


            currentLongitude = locationTrack.getLongitude();
            currentLatitude = locationTrack.getLatitude();
            Log.d(TAG, "onComplete: current longitude"+currentLongitude);
            Log.d(TAG, "onComplete: current latitude"+currentLatitude);


            updateLocationToFirebase();
            updateLocationToFirestore();

            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }


    }
    public void updateLocationToFirestore(){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        String userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseFirestore.getInstance();
        Map<String,Object> user= new HashMap<>();
        user.put("mechanicCurrentLatitude",String.valueOf(currentLatitude));
        user.put("mechanicCurrentLongitude",String.valueOf(currentLongitude));
        //user.put("driversLocality",locality);
        database.collection("mechanics").document(userID).
                update("mechanicCurrentLatitude",String.valueOf(currentLatitude),"mechanicCurrentLongitude",String.valueOf(currentLongitude))
                .addOnCompleteListener
                        (new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FancyToast.makeText(MainActivity.this,"location Updated !",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true);

                                }
                                else{
                                    FancyToast.makeText(MainActivity.this," failed location Update!",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true);

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       FancyToast.makeText(MainActivity.this," database not found !",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true);

                    }
                });



    }

    private void updateLocationToFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Technicians");
        //  GeoFire geoFire = new GeoFire(ref);
        //  geoFire.setLocation(userId, new GeoLocation(40.2334983, -3.7185183));
        Map<String, Object> userToy = new HashMap<>();

        userToy.put("currentLatitude", String.valueOf(currentLatitude));
        userToy.put("currentLongitude", String.valueOf(currentLongitude));
        DatabaseReference myRef2=FirebaseDatabase.getInstance().getReference("Technicians").child(userId);
        myRef2.updateChildren(userToy).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Geofire");
                GeoFire geofire=new GeoFire(databaseReference);
                geofire.setLocation(userId,new GeoLocation(currentLatitude,currentLongitude));
                FancyToast.makeText(getApplicationContext(), "Location Updated automatically", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

            }
        });



        // startActivity(new Intent(getApplicationContext(), TechnicianHomeActivity.class));


//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference additionalUserInfoRef = rootRef.child("Location");
//        Query userQuery = additionalUserInfoRef.orderByChild("userid").equalTo(userId);
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    Map<String, Object> user3 = new HashMap<>();
//
//
//                    user3.put("1",currentLongitude);
//                    user3.put("0",currentLatitude);
//
//
//                    ds.getRef().child("location").updateChildren(user3).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//
//
//                            Toast.makeText(getApplicationContext(),"Location Updated Successfully",Toast.LENGTH_SHORT).show();
//
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            Toast.makeText(getApplicationContext(),"saving failed try again later",Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
//
//                Toast.makeText(getApplicationContext(),"Failed location Update check your connection",Toast.LENGTH_SHORT).show();
//
//            }
//        };
//        userQuery.addListenerForSingleValueEvent(valueEventListener);




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
                        currentLatitude=location.getLatitude();
                        currentLongitude=location.getLongitude();
                        if(currentLatitude!=0&& currentLongitude!=0){
                            updateLocationToFirebase();
                            updateLocationToFirestore();
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
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // Fine Location Permission is not granted so ask for permission
//            askForLocationPermission();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private boolean locationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkOnlineStatus("");
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
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void checkOnlineStatus(String status) {
        // check online status
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myuid = user.getUid();
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Technicians").child(myuid);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("onlineStatus", status);
            dbref.updateChildren(hashMap);
        }


    }
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myuid = user.getUid();
        }

    }

    private void checkTypingStatus(String typing) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myuid = user.getUid();
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Technicians").child(myuid);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("typingTo", typing);
            dbref.updateChildren(hashMap);
        }

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }
}