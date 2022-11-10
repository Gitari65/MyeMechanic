package com.example.myemechanic;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static android.graphics.Color.blue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DriverSelectMechanic extends AppCompatActivity {
    EditText editTextf, editTexts, editTexte, editTextl,editTextp;
    ImageView imageViewp, imageViewl;
    LinearLayout layout,layoutPop;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    //GeoFire geoFire;
    private LocationRequest mlocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private long pressedTime;
    private FusedLocationProviderClient fusedLocationClient;
    TextView txtLat;
    Double driverCurrentLongitude;
    Double driverCurrentLatitude,mechanicCurrentLatitude,mechanicCurrentLongitude;
    Button button;
    String locality;
    int i=0;
    String current_userId;
    String provider;
    protected String latitude, longitude;
    private Context mContext;
    private Activity mActivity;

    private LinearLayout mRelativeLayout;
    private Button mButton;

    private PopupWindow mPopupWindow;


    FirebaseFirestore mAuth;
    NotificationManager mNotificationManager;
    private RecyclerView recyclerView;
    private ArrayList<mechanic_details> mechanicsArrayList;
    private mechanicsAdapter mechanicsAdapter2;
    private  MyAdapter myAdapter;
    private FirebaseFirestore db;
    String userToken,mechanicToken,driverToken;
    ProgressDialog progressDialog;
    String garagename;
    ProgressBar progressBar;
    //location
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;


    Button buttonCall, buttonBack,buttonMessage,buttonMessagePop,buttonLocationPop,buttonLocation,buttonrequest,btnrequestPop,btnCancelPop,buttoncancel;
    DocumentSnapshot documentSnapshot;
    TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_select_mechanic);
       // ActionBar actionBar = getSupportActionBar();
      //  actionBar.hide();
        String firstName = getIntent().getStringExtra("fName");
        String secondName = getIntent().getStringExtra("sName");
        String mechanicEmail = getIntent().getStringExtra("email");
        String mechanicToken = getIntent().getStringExtra("mechanicToken");
        String mechanicPhoneNumber= getIntent().getStringExtra("phonenumber");
        String profilePhotoUrl = getIntent().getStringExtra("profilephotourl");
        String current_userId = getIntent().getStringExtra("currentuserid");
        String licenseUrl = getIntent().getStringExtra("licenceUrl");


        String garageLocation = getIntent().getStringExtra("garageLocation");
        String garageName = getIntent().getStringExtra("garageName");

        Log.d(TAG, "onCreate: "+garageName);
        Log.d(TAG, "onCreate: "+current_userId);
        Log.d(TAG, "onCreate: "+licenseUrl);
        Log.d(TAG, "onCreate: "+garageLocation);

 layout= (LinearLayout) findViewById(R.id.layoutDetails);

        editTexte = findViewById(R.id.verymechanic_emailD);
        buttonBack = findViewById(R.id.btnselectmech_back);
        buttonrequest=findViewById(R.id.btnsendRequest);


        buttoncancel=findViewById(R.id.btncancelRequestmech);
        buttonMessage=findViewById(R.id.btnchatMech);
        editTextf = findViewById(R.id.verymechanic_firstnameD);
        editTexts = findViewById(R.id.verymechanic_secondnameD);
        buttonCall= findViewById(R.id.btncallMech);
        buttonLocation=findViewById(R.id.btnfindMechLocation);
        editTextp=findViewById(R.id.verymechanic_phonenumberD);
        editTextl=findViewById(R.id.verymechanic_garagelocationD);
        //buttonSelect.setEnabled(false);
        // editTextv=findViewById(R.id.verymechanic_verystatus);
        // imageViewl=findViewById(R.id.mech_licencePicv);
        imageViewp = findViewById(R.id.mech_profilePicD);
        //setting images to imageview
        Picasso.get().load(profilePhotoUrl).into(imageViewp);

        //getting recyclerview inf



        editTextl.setText(garageLocation);
        editTexts.setText(secondName);
        editTextf.setText(firstName);
        editTexte.setText(mechanicEmail);
        editTextp.setText(mechanicPhoneNumber);

        //String verificationStatustext = editTextv.getText().toString().trim();\

        layout.setVisibility(View.GONE);
        RequestPopUpWindow();
        //back intent
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriverProblemActivity.class);
                startActivity(intent);

            }
        });
        //request
        String carProblemDescription=getIntent().getStringExtra("carProblemDescription");
        String carModel=getIntent().getStringExtra("carModel");
        String carPart=getIntent().getStringExtra("carPart");
        String driverId=getIntent().getStringExtra("driverId");
        buttonrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i==0){
                    i++;
                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

                    //FIREBASE REALTIME
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
                    String date = dateFormat.format(new Date());
                    String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                    Map<String, Object> userToy = new HashMap<>();
                    userToy.put("date", date);
                    userToy.put("carPart", carPart);
                    userToy.put("carModel", carModel);
                    userToy.put("driversId",userId);
                    userToy.put("carProblemDescription", carProblemDescription);
                    FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Mechanics").child(current_userId).push().setValue(userToy).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FancyToast.makeText(getApplicationContext(), "request made wait", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();


                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
                else{
                   // RequestPopUpWindow();
                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
RequestProcessingDetails();
                }



            }
        });
        //cancelling requesting mech
       buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelling request
                String current_userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                db3.collection("driverRequest").document(current_userId).update("mechanicId", "")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: spinner stored");

                                Toast.makeText(getApplicationContext(), "request cancelled xx",Toast.LENGTH_SHORT).show();
buttoncancel.setVisibility(View.GONE);
layout.setVisibility(View.GONE);
buttonrequest.setVisibility(View.VISIBLE);
                                buttonrequest.setText("request");

                            }
                        });

            }
        });
        //message
        buttonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_userId = getIntent().getStringExtra("currentuserid");
                Intent intent = new Intent(getApplicationContext(), MainChatActivity.class);
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
   getMechanicDetails();
        Log.d(TAG, "onCreate: garage name"+garagename);





    }

    class CustomerChatsListAdapter extends ArrayAdapter<String> {

        private Activity context;
        private ArrayList<String> technicians, techniciansIds;
        private ArrayList<String> mobiles;
        private String user;

        public CustomerChatsListAdapter(Activity context, ArrayList<String> technicians, ArrayList<String> techniciansIds, ArrayList<String> mobiles, String user) {
            super(context, R.layout.mylist, technicians);
            // TODO Auto-generated constructor stub

            this.context = context;
            this.technicians = technicians;
            this.techniciansIds = techniciansIds;
            this.mobiles = mobiles;

            this.user = user;
        }
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.mylist, null, true);
            Button buttonMessage = findViewById(R.id.btnchatMech);
            String technician = techniciansIds.get(position);

            buttonMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("customer", user);
                    intent.putExtra("technician", technician);
                    intent.putExtra("isCustomer", true);
                    context.startActivity(intent);
                }
            });
            return rowView;
        }

    }
    //methods
    public void getMechanicDetails(){
       FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String current_userId = getIntent().getStringExtra("currentuserid");



        db.collection("mechanics").document(current_userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                             garagename= documentSnapshot.getString("garageName");
                                buttonLocation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String garageLocation = getIntent().getStringExtra("garageLocation");
                                        Uri gmmIntentUri = Uri.parse("geo:0,0?q= "+garagename+","+garageLocation);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        startActivity(mapIntent);
                                    }
                                });

                                Log.d(TAG, "onComplete: mecha garage name "+garagename);
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
    protected void onStart() {
        super.onStart();
        RequestProcessingDetails();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000>System.currentTimeMillis()){
            super.onBackPressed();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "press back again to view more mechanics",Toast.LENGTH_SHORT).show();


        }



    }

    public void RequestPopUpWindow(){

        // Get the application context
        mContext = getApplicationContext();

        // Get the activity
        mActivity = DriverSelectMechanic.this;

        // Get the widgets reference from XML layout
        mRelativeLayout =  findViewById(R.id.rl);
        mButton = (Button) findViewById(R.id.btnsendRequest);


        // Set a click listener for the text view

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.custom_layout,null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
        textView=customView.findViewById(R.id.textViewRequestStatus);
        layoutPop=customView.findViewById(R.id.layoutDetailsPop);
        buttonLocationPop=customView.findViewById(R.id.btnfindMechLocationPop);
        buttonMessagePop=customView.findViewById(R.id.btnchatMechPop);
        btnCancelPop=customView.findViewById(R.id.buttonCancelrequestPop);
        progressBar=customView.findViewById(R.id.requestProgressBarPop);


        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
       // RequestProcessingDetails();

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
        // Finally, show the popup window at the center location of root relative layout
//        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

    }
    public void RequestProcessingDetails(){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        db.collection("driverRequest").document(current_userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                               String status= documentSnapshot.getString("status");
                            if (Objects.equals(status, "accepted")){
                                Log.d(TAG, "onComplete: driverRequest Pop**");
                                buttonrequest.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);

                                textView.setText("Accepted");
                                textView.setTextColor(Color.GREEN);

                                progressBar.setVisibility(View.GONE);
                                layoutPop.setVisibility(View.VISIBLE);
                                btnCancelPop.setVisibility(View.GONE);



                            }
                                if (Objects.equals(status, "rejected")){
                                    textView.setText("Denied XX");
                                    Toast.makeText(getApplicationContext(), "request REJECTED XX mechanic unavailable",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "go back and try other available mechs",Toast.LENGTH_LONG).show();
                                btnCancelPop.setVisibility(View.VISIBLE);

//kahuko.alex19@students.dkut.ac.ke

                                }
                            else{
                                buttonrequest.setText("waiting....");
                                buttoncancel.setVisibility(View.VISIBLE);
                            }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    public  void mainLocationUpdate(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(DriverSelectMechanic.this);
        //button=findViewById(R.id.buttonGoogle);

        Log.d(TAG, "mainLocationUpdate:  method started");

         if (ActivityCompat.checkSelfPermission(DriverSelectMechanic.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(DriverSelectMechanic.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            getLocation();
            Log.d(TAG, "mainLocationUpdate: permission granted");

            buttonLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {
                  //    uriParsingGoogleMapsIntent();
                    }
                });


            } else {
                // do request the permission
                ActivityCompat.requestPermissions(DriverSelectMechanic.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 8);
            }
        }else {
             // do request the permission
             ActivityCompat.requestPermissions(DriverSelectMechanic.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 8);
         }
    }
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

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(DriverSelectMechanic.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            // list adress
                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(),
                                        1

                                );
                                //get address and location
                                driverCurrentLatitude = addresses.get(0).getLatitude();
                                driverCurrentLongitude = addresses.get(0).getLongitude();
                                locality = addresses.get(0).getLocality();
                                updateLocationToFirestore();

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d(TAG, "onComplete: location null");
                            }
                        }
                        else {
                            Log.d(TAG, "onComplete: location null");

                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to get Lat and Long");
                    }
                });
    }



    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DriverSelectMechanic.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                editTextp=findViewById(R.id.verymechanic_phonenumberD);
                String mechPhoneNumber=editTextp.getText().toString();


                // Getting instance of Intent with action as ACTION_CALL
                Intent phone_intent = new Intent(Intent.ACTION_CALL);

                // Set data of Intent through Uri by parsing phone number
                phone_intent.setData(Uri.parse("tel:" + mechPhoneNumber));

                // start Intent
                startActivity(phone_intent);

            }
            else {
                editTextp=findViewById(R.id.verymechanic_phonenumberD);
                String mechPhoneNumber=editTextp.getText().toString();
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


                locationTrack = new LocationTrack(DriverSelectMechanic.this);


                if (locationTrack.canGetLocation()) {


                    double driverCurrentLongitude = locationTrack.getLongitude();
                    double driverCurrentLatitude = locationTrack.getLatitude();
                    updateLocationToFirestore();

                    //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                } else {

                    locationTrack.showSettingsAlert();
                }


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


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
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
        new AlertDialog.Builder(DriverSelectMechanic.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

    public void updateLocationToFirestore(){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        String userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseFirestore.getInstance();
        Map<String,Object> user= new HashMap<>();
        user.put("driverCurrentLatitude",driverCurrentLatitude);
        user.put("driverCurrentLongitude",driverCurrentLongitude);
        //user.put("driversLocality",locality);
        database.collection("Drivers").document(userID).
               update("driverCurrentLatitude",driverCurrentLatitude,"driverCurrentLongitude",driverCurrentLongitude)
                .addOnCompleteListener
                        (new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FancyToast.makeText(DriverSelectMechanic.this,"location Updated !",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true);

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       FancyToast.makeText(DriverSelectMechanic.this," database not found !",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true);

                    }
                });



    }


    public void checkMechanicAvailability() {
        //firebase authentication

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        String firstName = getIntent().getStringExtra("fName");
        String secondName = getIntent().getStringExtra("sName");
        String mechanicEmail = getIntent().getStringExtra("email");
        String profilePhotoUrl = getIntent().getStringExtra("profilephotourl");
        String current_userId = getIntent().getStringExtra("currentuserid");
        //String licenseUrl=getIntent().getStringExtra("licenceUrl");


        db.collection("mechanics").document(current_userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String status_text = documentSnapshot.getString("mechanicAvailability");


                        if (Objects.equals(status_text, "open")) {
                            //buttonSelect.setEnabled(true);
                           // buttonSelect.setBackgroundColor(BLUE);

                           // Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                          //  startActivity(intent);
                           // Toast.makeText(getApplicationContext(), "your account is now verified", Toast.LENGTH_SHORT).show();
                        } else {
                            //buttonSelect.setEnabled(false);
                           // buttonSelect.setBackgroundColor(RED);
                           // buttonSelect.setText("Booked*");

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "no such record found", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "null snapshot", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void updateToken() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //String fcmToken= FirebaseInstanceIdInternal.getInstance().getToken();
       // FirebaseFirestore db3 = FirebaseFirestore.getInstance();
        //getuserToken
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: token not generated", task.getException());
                            return;
                        }
                        Log.d(TAG, "onComplete: token  generated", task.getException());


                        // Get new FCM registration token
                        String userToken =  Objects.requireNonNull(task.getResult());
                        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                        Map<String, Object> user1 = new HashMap<>();;
                        user1.put("driverToken", userToken);
                        db3.collection("Drivers").document(uid).update("driverToken", userToken)

                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if ((task.isSuccessful())){
                                            Log.d(TAG, "onComplete: token updated", task.getException());
                                        }
                                    }
                                });
                    }
                });




    }



}