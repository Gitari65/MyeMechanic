package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.example.myemechanic.Model.StkPushResponse;
import com.example.myemechanic.Services.STKPushService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MpesaPaymentActivity extends AppCompatActivity {
//    String CONSUMER_KEY = "JoWqG6vOIa5IJn6Ccoj3qROWicOSvtXA";
//    String CONSUMER_SECRET = "1AKQWXAzAJUpOeIw";
//    Daraja daraja;
    Button  btnpayment,btnConfirm,btnDecline;
    String mobile;
    String AMOUNT;
    DocumentSnapshot documentSnapshot;
    String driverSecondName,driverFirstName,driverEmail,driverPhoneNumber;
    String Problem,date,carModel,Payment,Amount;
    TextView textViewProblem,textViewPayment,textViewTime,textViewCarModel;

private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_payment);
        progressDialog = new ProgressDialog(MpesaPaymentActivity.this);

        textViewPayment=findViewById(R.id.textViewConfirmPayment);
        textViewCarModel=findViewById(R.id.textViewConfirmCarModel);
        textViewProblem=findViewById(R.id.textViewConfirmProblem);
        textViewTime=findViewById(R.id.textViewConfirmTime);
        btnpayment=findViewById(R.id.buttonPay2);
        btnConfirm=findViewById(R.id.buttonConfirm2);
        getDriverDetails();
        getReportDetails();




//
//        //for Mpesa
//        daraja = Daraja.with(CONSUMER_KEY, CONSUMER_SECRET, new DarajaListener<AccessToken>() {
//            @Override
//            public void onResult(@NonNull AccessToken accessToken) {
//                Log.i(MpesaPaymentActivity.this.getClass().getSimpleName(), accessToken.getAccess_token());
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.e(MpesaPaymentActivity.this.getClass().getSimpleName(), error);
//            }
//        });

        btnpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMpesaPayment(driverPhoneNumber, Amount);
            }
        });


    }
    private void makeMpesaPayment(String mobile, String  amount){
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Processing MPESA Request");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String timestamp=String.valueOf(System.currentTimeMillis());

        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://17bd-105-160-63-85.in.ngrok.io/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();
        STKPushService restClient=retrofit.create(STKPushService.class);
        Call<StkPushResponse> call=restClient.pushSTK(
                1,
                Utils.sanitizePhoneNumber(mobile)
//                timestamp
//                visitID
        );
        call.enqueue(new Callback<StkPushResponse>() {
            @Override
            public void onResponse(@NonNull Call<StkPushResponse> call, @NonNull Response<StkPushResponse> response) {
                assert response.body() != null;
                if(response.body().getResponseCode().equals("0")){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Payment Prompted",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Worked: ");
//                    mRef.child(visitID).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                FirebaseDatabase.getInstance().getReference("Tokens").child(vetid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        String VetToken=snapshot.getValue(String.class);
//                                        SendNotification(VetToken,"New Request","You Have a new Visit Request","Request");
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                                paymentDailog.dismiss();
//                                if(serviceType.equals("Disease Treatment")){
//                                    Intent intent=new Intent(RequestVisitation.this,Diagnose.class);
//                                    Bundle bundle=new Bundle();
//                                    bundle.putString("VISITID",visitID);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);
//                                    finish();
//                                }else{
//                                    finish();
//                                }
//
//                            }
//                        }
//                    });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Failed: ");
//                    paymentDailog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<StkPushResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                Timber.tag(TAG).d("onFailure: Something wrong Happened");
//                paymentDailog.dismiss();
            }
        });

//        LNMExpress lnmExpress = new LNMExpress(
//                "174379",
//                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
//                TransactionType.CustomerPayBillOnline,
//                amount,
//                mobile,
//                "174379",
//                mobile,
//                "http://mpesa-requestbin.herokuapp.com/1od2was1",
//                "Care It App",
//                "Care It App"
//        );

//        //For both Sandbox and Production Mode
//        daraja.requestMPESAExpress(lnmExpress,
//                new DarajaListener<LNMResult>() {
//                    @Override
//                    public void onResult(@NonNull LNMResult lnmResult) {
//                        Log.i(MpesaPaymentActivity.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
//                        FancyToast.makeText(getApplicationContext(), lnmResult.ResponseDescription, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
//                        progressDialog.dismiss();
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        Log.i(MpesaPaymentActivity.this.getClass().getSimpleName(), error);
//                        FancyToast.makeText(getApplicationContext(), error, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
//                        progressDialog.dismiss();
//                    }
//                }
//        );
    }
    public  void getDriverDetails()
    {
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
                                driverSecondName= documentSnapshot.getString("driverSecondName");
                                driverEmail= documentSnapshot.getString("driverEmail");
                                driverPhoneNumber= documentSnapshot.getString("driverPhoneNumber");

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public  void  getReportDetails(){
        String Date1=getIntent().getStringExtra("date");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("MechanicWork");
        Query userQuery = additionalUserInfoRef.orderByChild("date").equalTo(Date1);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Problem= (String) snapshot.child("workProblem").getValue();
                            Amount= (String) snapshot.child("workPrice").getValue();
                            carModel= (String) snapshot.child("carModel").getValue();
                           // user= (String) snapshot.child("driverFirstName").getValue();
                            //phone = (String) snapshot.child("driverPhoneNumber").getValue();
                           String paymentStatus=(String)snapshot.child("paymentStatus").getValue();

                            date= (String) snapshot.child("recordDate").getValue();
                            Payment= (String) snapshot.child("paymentMethod").getValue();


                            textViewPayment.setText(Amount);
                            textViewTime.setText(date);
                            textViewCarModel.setText(carModel);
                            textViewProblem.setText(Problem);

                            if(Amount==null){
                                textViewPayment.setText("Waiting for Pricing");
                                btnConfirm.setVisibility(View.GONE);
                                btnpayment.setVisibility(View.GONE);

                            }
                            else
                            {
                                if(Objects.equals(paymentStatus, "paid")){
                                    btnConfirm.setText("Already Paid");
                                    btnConfirm.setVisibility(View.VISIBLE);
                                    btnConfirm.setEnabled(false);


                                }
                                else{

                                    if(Objects.equals(Payment, "Mpesa")){

                                        btnpayment.setVisibility(View.VISIBLE);

                                    }
                                    if(Objects.equals(Payment, "Cash")){

                                        btnConfirm.setVisibility(View.VISIBLE);

                                    }
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
        userQuery.addValueEventListener(valueEventListener);



    }


}