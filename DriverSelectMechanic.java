package com.example.myemechanic;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.provider.Settings;
import android.telephony.SmsManager;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myemechanic.Notifications.Data;
import com.example.myemechanic.Notifications.NotificationResponse;
import com.example.myemechanic.Notifications.NotificationSender;
import com.example.myemechanic.Services.RestClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverSelectMechanic extends AppCompatActivity  {
    EditText editTextf, editTexts, editTexte, editTextl,editTextp,editText2;
    ImageView imageViewp, imageViewl;
    LinearLayout layout,layoutPop,layoutDetails;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String UserToken;

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
    ImageButton imageButtonmpesa;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_PERMISSION_CODE=123;

    Button buttonCall, buttonReviews,buttonBack,buttonToggle,buttonMessage,buttonMessagePop,buttonFindOtherMechs,buttonNotifyPop,buttonLocationPop,buttonLocation,buttonrequest,btnrequestPop,btnCancelPop,buttoncancel;
    DocumentSnapshot documentSnapshot;
    TextView textView,txtInfoUpdate,textViewPayment;
    ImageView imageView;
    String childKey;
    String  mechanicPhoneNumber,phoneNo,message,  mechanicEmail,firstName, secondName,garageLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_select_mechanic);
       // ActionBar actionBar = getSupportActionBar();
      //  actionBar.hide();

        String current_userId = getIntent().getStringExtra("currentuserid");



 layout= (LinearLayout) findViewById(R.id.layoutDetails);
        layoutDetails= (LinearLayout) findViewById(R.id.layoutMehanicDetails);

        editTexte = findViewById(R.id.verymechanic_emailD);
        buttonBack = findViewById(R.id.btnselectmech_back);
        buttonrequest=findViewById(R.id.btnsendRequest);

buttonReviews=findViewById(R.id.btn_viewratings);
        buttoncancel=findViewById(R.id.btncancelRequestmech);
        imageView=findViewById(R.id.imageViewHome);
        buttonMessage=findViewById(R.id.btnchatMech);
        buttonToggle=findViewById(R.id.btnToggleMechDeatils);
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
        textViewPayment=findViewById(R.id.btn_driverPaymech);

        //setting images to imageview
        //Picasso.get().load(profilePhotoUrl).into(imageViewp);

        //getting recyclerview inf




        //String verificationStatustext = editTextv.getText().toString().trim();\
        getMechanicDetails();
      //  RequestProcessingDetails();


        layout.setVisibility(View.GONE);
        RequestPopUpWindow();

        // toggle details
        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutDetails.getVisibility() == View.VISIBLE){
                    layoutDetails.setVisibility(View.GONE);
                } else {
                    layoutDetails.setVisibility(View.VISIBLE);
                }
            }
        });
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        //view reviews
        buttonReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=getIntent().getStringExtra("date");
                String mechanicPhoneNumber= getIntent().getStringExtra("phonenumber");
                long timestamp=getIntent().getLongExtra("timestamp",0);

                Intent intent = new Intent(getApplicationContext(), RatingMainActivity.class);
                intent.putExtra("mechanicId", current_userId);
                intent.putExtra("date", date);
                intent.putExtra("timestamp", timestamp);
                //String date=getIntent().getStringExtra("date");
                String carPart=getIntent().getStringExtra("carPart");
                String carModel=getIntent().getStringExtra("carModel");

                intent.putExtra("carPart", carPart);
                intent.putExtra("carModel", carModel);
                intent.putExtra("currentuserid", current_userId);
                intent.putExtra("date", date);

                intent.putExtra("phonenumber", mechanicPhoneNumber);

                startActivity(intent);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriversHomeActivity.class);

                startActivity(intent);
            }
        });
        textViewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mechanicPhoneNumber= getIntent().getStringExtra("phonenumber");
                String date=getIntent().getStringExtra("date");
                Intent intent = new Intent(getApplicationContext(), MpesaPaymentActivity.class);
                intent.putExtra("mechanicId", current_userId);
                intent.putExtra("phonenumber", mechanicPhoneNumber);
                intent.putExtra("date", date);
                long timestamp= getIntent().getLongExtra("timestamp",0);
                intent.putExtra("timestamp", timestamp);


                startActivity(intent);
            }
        });

        //payments


        //back intent
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriverViewMechanics.class);
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
        //request
        String carProblemDescription=getIntent().getStringExtra("carProblemDescription");
        String carModel=getIntent().getStringExtra("carModel");
        String carPart=getIntent().getStringExtra("carPart");
        String driverId=getIntent().getStringExtra("driverId");
        // REQUEST
        buttonrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i==0){
                    i++;
                    fetchLocation();
                    MatchedMechanicId();
                    RequestingMechanic();
                   // mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

                    //FIREBASE REALTIME





                }
                else{
                   // RequestPopUpWindow();
                    RequestProcessingDetails();
                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

                }



            }
        });
        //cancelling requesting mech
       buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelling request
                String current_userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                CancellingRequest();
                StoreCancelledRequests();
               // RequestProcessingDetails();




            }
        });
        //message
        buttonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_userId = getIntent().getStringExtra("currentuserid");
                Intent intent = new Intent(getApplicationContext(), FloatingChatActivity.class);
                intent.putExtra("uid", current_userId);
                long timestamp=getIntent().getLongExtra("timestamp",0);
                intent.putExtra("timestamp", timestamp);
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
        message = "Im trying to request you but you are offline .E-mechanic";
        phoneNo=editText2.getText().toString().trim();




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
    public void MatchedMechanicId(){
        //mech id update on request
        String Current_userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db3 = FirebaseFirestore.getInstance();
        String current_userId = getIntent().getStringExtra("currentuserid");

        db3.collection("driverRequest").document(Current_userId).
                update("mechanicId",current_userId,"status","sent")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: spinner stored");

                        // Toast.makeText(getApplicationContext(), "request cancelled xx",Toast.LENGTH_SHORT).show();


                    }
                });
    }
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
                                garageLocation= documentSnapshot.getString("garageLocation");
                                firstName=documentSnapshot.getString("firstName");
                                secondName=documentSnapshot.getString("secondName");
                                mechanicEmail=documentSnapshot.getString("mechanicEmail");
                                mechanicPhoneNumber=documentSnapshot.getString("phoneNumber");
                                if(documentSnapshot.getString("mechanicCurrentLongitude")!=null){
                                    mechanicCurrentLongitude=Double.parseDouble((documentSnapshot.getString("mechanicCurrentLongitude")));
                                    mechanicCurrentLatitude=Double.parseDouble((documentSnapshot.getString("mechanicCurrentLatitude")));
                                }



                                String profileImageUrl=documentSnapshot.getString("profilePhotoUrl");
                                Picasso.get().load(profileImageUrl).into(imageViewp);
                                editTextl.setText(garageLocation);
                                editTexts.setText(secondName);
                                editTextf.setText(firstName);
                                editTexte.setText(mechanicEmail);
                                editTextp.setText(mechanicPhoneNumber);

                                buttonLocation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uriParsingGoogleMapsIntents();
//                                        String garageLocation = getIntent().getStringExtra("garageLocation");
//                                        Uri gmmIntentUri = Uri.parse("geo:0,0?q= "+garagename+","+garageLocation);
//                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                                        mapIntent.setPackage("com.google.android.apps.maps");
//                                        startActivity(mapIntent);
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
    public  void uriParsingGoogleMapsIntents(){
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&daddr="+destinationCityName));
//        String uri = "http://maps.google.com/maps?saddr="+driverCurrentLatitude+","+driverCurrentLongitude+"+&daddr="+mechanicCurrentLatitude+","+mechanicCurrentLongitude;
        //String uri = "http://maps.google.com/maps?saddr="+driverCurrentLatitude+","+driverCurrentLongitude+"+&daddr="+mechanicCurrentLatitude+","+mechanicCurrentLongitude;
//String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + yourLocationName + ")";
        String strUri = "http://maps.google.com/maps?q=loc:" + mechanicCurrentLatitude + "," + mechanicCurrentLongitude + " (" + "Label which you want" + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }



    @Override
    protected void onStart() {
        super.onStart();
        RequestProcessingDetails();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        RequestProcessingDetails();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


     public void StoreCancelledRequests(){
            String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long timestamp=getIntent().getLongExtra("timestamp",0);
         String date=getIntent().getStringExtra("date");
         String carPart=getIntent().getStringExtra("carPart");
         String carModel=getIntent().getStringExtra("carModel");
            Map<String, Object> user3 = new HashMap<>();
         user3.put("carPart",carPart);
         user3.put("mechId",current_userId);
         user3.put("carModel",carModel);
         user3.put("timestamp",timestamp);

            DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("Cancelled").child(driverId);
            dbRef.push().setValue(user3);
        }










    public void CancellingRequest(){
        String date=getIntent().getStringExtra("date");
        String childKey=getIntent().getStringExtra("childKey");
        long timestamp=getIntent().getLongExtra("timestamp",0);
        DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference();
        String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference additionalUserInfoRef2 = rootRef2.child("DriverRequest").child("Driver").child(user_id);
        Query userQuery2 = additionalUserInfoRef2.orderByChild("timestamp").equalTo(timestamp);
        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", "cancelled");
                    map.put("mechanicId", "");
                    ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {


                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery2.addListenerForSingleValueEvent(valueEventListener2);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("Request");
        Query userQuery = additionalUserInfoRef.orderByChild("timestamp").equalTo(timestamp);
        current_userId = getIntent().getStringExtra("currentuserid");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", "cancelled");
                    map.put("mechanicId", "");
                    ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //String status="rejected";


                            Toast.makeText(getApplicationContext(), " cancelling... ",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), " cancelled ",Toast.LENGTH_SHORT).show();
                            RequestProcessingDetails();
                            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

                            if(buttonrequest.getVisibility() == View.GONE){
                                buttonrequest.setVisibility(View.VISIBLE);
                            }
                            if(buttoncancel.getVisibility() == View.VISIBLE){
                                buttoncancel.setVisibility(View.GONE);
                            }





                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery.addListenerForSingleValueEvent(valueEventListener);



    }




    public void RequestingMechanic(){
        String childKey=getIntent().getStringExtra("childKey");
        String date=getIntent().getStringExtra("date");
      long timestamp=getIntent().getLongExtra("timestamp",0);
        DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference();
        String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference myRef2=FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Driver").child(user_id).push();
        DatabaseReference additionalUserInfoRef2 = rootRef2.child("DriverRequest").child("Driver").child(user_id);
        Query userQuery2 = additionalUserInfoRef2.orderByChild("timestamp").equalTo(timestamp);
        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", "sent");
                    map.put("mechanicId", current_userId);
                    ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {


                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery2.addListenerForSingleValueEvent(valueEventListener2);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef3 = rootRef.child("DriverRequest").child("Request");
        Query userQuery3 = additionalUserInfoRef3.orderByChild("timestamp").equalTo(timestamp);
        current_userId = getIntent().getStringExtra("currentuserid");
        ValueEventListener valueEventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", "sent");
                    map.put("mechanicId", current_userId);
                    ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //String status="rejected";
                            //send Notification
                            FirebaseDatabase.getInstance().getReference("Tokens").child(current_userId).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                     UserToken=snapshot.getValue(String.class);
                                     CheckingOnlineStatus();

//                                    SendNotification(UserToken,"New Request","You Have a new Driver  Request","Request");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d(TAG, "onCancelled: "+error.getMessage());
                                }
                            });

                            Toast.makeText(getApplicationContext(), " requesting... ",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), " waiting for mechanic... ",Toast.LENGTH_SHORT).show();

                            RequestProcessingDetails();

                            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
                            textView.setText("waiting..");
                            txtInfoUpdate.setText("waiting to get  mechanics response");




                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery3.addListenerForSingleValueEvent(valueEventListener3);



    }

    private void SendNotification(String userToken, String title, String message, String request) {
        Data data=new Data(title,message,request);
        NotificationSender sender=new NotificationSender(data,userToken);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        RestClient client = retrofit.create(RestClient.class);
        Call<NotificationResponse> call=client.sendNotification(sender);

        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if(response.body().success!=1){
                    Toast.makeText(getApplicationContext(), "Mechanic Not notified", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

    public void RequestPopUpWindow(){

        // Get the application context
        mContext = getApplicationContext();

        // Get the activity
        mActivity = DriverSelectMechanic.this;

        // Get the widgets reference from XML layout
        mRelativeLayout =  findViewById(R.id.rl);
        mButton = (Button) findViewById(R.id.btnsendRequest);
        editText2= (EditText) findViewById(R.id.verymechanic_phonenumberD);


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
            //mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

        }

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
        textView=customView.findViewById(R.id.textViewRequestStatus);
        layoutPop=customView.findViewById(R.id.layoutDetailsPop);
        buttonLocationPop=customView.findViewById(R.id.btnfindMechLocationPop);

                buttonNotifyPop=customView.findViewById(R.id.buttonNotifyrequestPop);

        txtInfoUpdate=customView.findViewById(R.id.textViewUpdatePop);
        buttonMessagePop=customView.findViewById(R.id.btnchatMechPop);
        buttonFindOtherMechs=customView.findViewById(R.id.buttonFindOtherMechPop);
        btnCancelPop=customView.findViewById(R.id.buttonCancelrequestPop);
        progressBar=customView.findViewById(R.id.requestProgressBarPop);
        //message
        buttonMessagePop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();

                String current_userId = getIntent().getStringExtra("currentuserid");
                Intent intent = new Intent(getApplicationContext(), FloatingChatActivity.class);
                intent.putExtra("uid", current_userId);
                long timestamp=getIntent().getLongExtra("timestamp",0);
                intent.putExtra("timestamp", timestamp);
                startActivity(intent);
            }
        });
        buttonNotifyPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNo=editText2.getText().toString().trim();
sendSMS();
buttonNotifyPop.setText("notifying sms sent");
buttonNotifyPop.setEnabled(false);
//                sendSMSMessage();
            }
        });
        buttonFindOtherMechs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);

                current_userId = getIntent().getStringExtra("currentuserid");
                String carProblemDescription=getIntent().getStringExtra("carProblemDescription");
                String carModel=getIntent().getStringExtra("carModel");
                String carPart=getIntent().getStringExtra("carPart");
                String date=getIntent().getStringExtra("date");
                long timestamp= getIntent().getLongExtra("timestamp",0);
                intent.putExtra("timestamp", timestamp);
                intent.putExtra("carPart", carPart);
                intent.putExtra("carModel", carModel);
                intent.putExtra("date", date);
                intent.putExtra("currentuserid", current_userId);
                startActivity(intent);
            }
        });


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
        String date=getIntent().getStringExtra("date");
        long timestamp=getIntent().getLongExtra("timestamp",0);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String current_userIdm = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String childKey=getIntent().getStringExtra("childKey");
        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Request");

        Log.d(TAG, "RequestProcessingDetails: timestamp: "+timestamp);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("Request");
        Query userQuery = additionalUserInfoRef.orderByChild("timestamp").equalTo(timestamp);
        current_userId = getIntent().getStringExtra("currentuserid");
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String status= (String) snapshot.child("status").getValue();
                            if (Objects.equals(status, "accepted")){
                                Log.d(TAG, "onComplete: driverRequest Pop**");
                                buttonrequest.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);

                                textView.setText("Accepted");
                                textView.setTextColor(Color.GREEN);
                                txtInfoUpdate.setText("Mechanic is to contact you check your chats");

                                progressBar.setVisibility(View.GONE);
                                layoutPop.setVisibility(View.VISIBLE);
                                btnCancelPop.setVisibility(View.GONE);



                            }
                            if (Objects.equals(status, "rejected")){
                                textView.setText("Denied XX");
                                txtInfoUpdate.setText("You can go back and try to get other mechanics");
                                Toast.makeText(getApplicationContext(), "request REJECTED XX mechanic might be unavailable",Toast.LENGTH_SHORT).show();
                                btnCancelPop.setVisibility(View.GONE);
                                buttonFindOtherMechs.setVisibility(View.VISIBLE);


//kahuko.alex19@students.dkut.ac.ke

                            }
                            if (Objects.equals(status, "cancelled")){
                                textView.setText("cancelled XX");
                                txtInfoUpdate.setText("You can go back and try to get other mechanics");
                              //  Toast.makeText(getApplicationContext(), "request REJECTED XX mechanic might be unavailable",Toast.LENGTH_SHORT).show();
                                btnCancelPop.setVisibility(View.GONE);
                                buttonFindOtherMechs.setVisibility(View.VISIBLE);
                                buttonrequest.setText("Request");


//kahuko.alex19@students.dkut.ac.ke

                            }
                            if (Objects.equals(status, "sent")){
                                Log.d(TAG, "onComplete: driverRequest Pop**");
                                buttonrequest.setVisibility(View.GONE);
                                layout.setVisibility(View.GONE);

                                buttonrequest.setText("waiting....");
                                buttoncancel.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                                layoutPop.setVisibility(View.GONE);
                                btnCancelPop.setVisibility(View.GONE);



                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery.addValueEventListener(valueEventListener1);





    }
    public void CheckingOnlineStatus(){
        String current_userId = getIntent().getStringExtra("currentuserid");


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef = rootRef.child("Technicians");
        Query userQuery1 = additionalUserInfoRef.orderByChild("userid").equalTo(current_userId);

        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String status= (String) snapshot.child("onlineStatus").getValue();
                            phoneNo=(String) snapshot.child("mobilee").getValue();
                            message = "Im trying to request you but you are offline .E-mechanic";
                            Log.d(TAG, "onDataChange: mobile"+phoneNo);
                            if (Objects.equals(status, "online")){
                                SendNotification(UserToken,"New Request","You Have a new Driver  Request","Request");


                            }
                            else{
                                if(phoneNo!=null){
                                    Log.d(TAG, "onDataChange: else mobile"+phoneNo);

                                    buttonNotifyPop.setVisibility(View.VISIBLE);
                                    buttonNotifyPop.setText("mechanic  offline sms");
                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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
    protected void sendSMSMessage() {

        message = "Im trying to request you but you are offline .E-mechanic";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DriverSelectMechanic.this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(DriverSelectMechanic.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
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
                        driverCurrentLatitude =location.getLatitude();
                        driverCurrentLongitude=location.getLongitude();
                        if(driverCurrentLatitude!=0&& driverCurrentLongitude!=0){
                           // updateLocationToFirebase();
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
        if (ContextCompat.checkSelfPermission(DriverSelectMechanic.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // Fine Location Permission is not granted so ask for permission
//            askForLocationPermission();
            ActivityCompat.requestPermissions(DriverSelectMechanic.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private boolean locationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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


                     driverCurrentLongitude = locationTrack.getLongitude();
                    driverCurrentLatitude = locationTrack.getLatitude();
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
    private void sendSMS() {
        message = "Hi mechanic,I'm trying to request you but you seem to be offline .My-mechanic";
        phoneNo=editText2.getText().toString().trim();


        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DriverSelectMechanic.this, new String[]{Manifest.permission.SEND_SMS}, 101);
                    return;
                }


                SmsManager sms = SmsManager.getDefault();
                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phoneNo, null, message, sentIntent, deliveredIntent);


            }
            else {
                SmsManager sms = SmsManager.getDefault();
                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phoneNo, null, message, sentIntent, deliveredIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
                sendSMS();

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
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    CheckingOnlineStatus();

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                    buttonNotifyPop.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                  return;
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
        if (locationTrack !=null){
            locationTrack.stopListener();
        }

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
                                else{
                                    FancyToast.makeText(DriverSelectMechanic.this," failed location Update!",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,true);

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