package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.example.myemechanic.Technician;

public class DriverRegistration extends AppCompatActivity {
EditText firstName,secondName,phoneNumber,Email_reg,password,confPassword;
Button button_register;
FirebaseFirestore fstore;

ProgressBar progressBar;
FirebaseAuth mAuth;
TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);
      //  ActionBar actionBar = getSupportActionBar();
      //  actionBar.hide();
        //progerss
         ProgressDialog pd = new ProgressDialog(DriverRegistration.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("please wait....");
        pd.setIndeterminate(true);
        pd.setCancelable(false);


        firstName=findViewById(R.id.driver_register_firstname);
        secondName =findViewById(R.id.driver_register_secondname);
        Email_reg=findViewById(R.id.driver_register_email);
        phoneNumber=findViewById(R.id.driver_register_phonenumber);
        password=findViewById(R.id.driver_register_password);
        confPassword=findViewById(R.id.driver_register_confpassword);
        button_register=findViewById(R.id.driver_register_button);
        progressBar=findViewById(R.id.progressBar_driverreg);
        textView =findViewById(R.id.driverreg_text);
        String Email=Email_reg.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String confirmpass=confPassword.getText().toString().trim();


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DriverRegistration.this, DriverLogin.class);
                startActivity(intent);
            }
        });



        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email=Email_reg.getText().toString().trim();
                String pass=password.getText().toString().trim();
                String confirmpass=confPassword.getText().toString().trim();
                mAuth =FirebaseAuth.getInstance();

                if (firstName.getText().toString().trim().equals("")) {
                    firstName.setError("Please Enter name");
                    firstName.requestFocus();
                    return;
                }
                if (secondName.getText().toString().trim().equals("")) {
                    secondName.setError("Please Enter name");
                    secondName.requestFocus();
                    return;
                }
                if (Email_reg.getText().toString().trim().equals("")) {
                    Email_reg.setError("Please Enter your email");
                    Email_reg.requestFocus();
                    return;
                }
                if (phoneNumber.getText().toString().trim().length() != 10) {
                    phoneNumber.setError("Please Enter valid phone number");
                    phoneNumber.requestFocus();
                    return;
                }

                if (pass.equals("")) {
                    password.setError("Please Enter password");
                    password.requestFocus();
                    return;
                }
                if (confirmpass.equals("")) {
                    confPassword.setError("Please Enter password");
                    confPassword.requestFocus();
                    return;
                }
                if (!pass.equals(confirmpass)) {
                    confPassword.setError("Password dont match");
                    confPassword.requestFocus();
                    return;
                }


                //email document
                mAuth =FirebaseAuth.getInstance();
                FirebaseUser userID  = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();



               // RegisterWithFirestore();
                AuthenticateWithFirebase();
                pd.dismiss();
            }
        }
        );


    }
     public void RegisterWithFirestore(){


//token
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
                         db3.collection("Drivers").document(uid).set(user1, SetOptions.merge())
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



         String Email=Email_reg.getText().toString().trim();
         String pass=password.getText().toString().trim();
         String PhoneNumber=phoneNumber.getText().toString().trim();
         String FirstName=firstName.getText().toString().trim();
         String SecondName=secondName.getText().toString().trim();

         String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         ///chat database settings
         String mobilee=phoneNumber.getText().toString().trim();
String emaill=Email_reg.getText().toString().trim();
         String secondNamee=secondName.getText().toString().trim();
         String firstNamee=firstName.getText().toString().trim();
         String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Technicians");
         mAuth =FirebaseAuth.getInstance();

         Technician technician = new Technician(firstNamee,secondNamee,emaill,mobilee,userid,"","","");
         if (uid != null){
             databaseReference.child(uid).setValue(technician).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                    // progressDialog.dismiss();
                     FancyToast.makeText(getApplicationContext(), "Account Created Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    // startActivity(new Intent(getApplicationContext(), TechnicianHomeActivity.class));
                     finish();
                 }
             });


     }




         //getuserToken


         //FirebaseUser userID  = mAuth.getCurrentUser();
         FirebaseFirestore db = FirebaseFirestore.getInstance();
       //  DocumentReference df=fstore.collection("Drivers").document(String.valueOf(userID));
        // AuthenticateWithFirebase();

         FirebaseFirestore df = FirebaseFirestore.getInstance();
         FirebaseUser userID  = mAuth.getCurrentUser();

         // df=fstore.collection("mechanics").document(String.valueOf(userID));;
         Map<String, Object> user = new HashMap<>();;
         user.put("driverPhoneNumber", PhoneNumber);
         user.put("driverSecondName", SecondName);
         user.put("driverFirstName", FirstName);
         user.put("driverEmail", Email);
         user.put("currentD_userId", uid);
         user.put("driverCurrentLatitude","");
         user.put("driverCurrentLongitude","");
         user.put("driverTimestamp", ServerValue.TIMESTAMP);
//         user.put("driverToken", userToken);

         df.collection("Drivers").document(uid)
                 .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 Toast.makeText(DriverRegistration.this,"email submitted",Toast.LENGTH_SHORT).show();
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(DriverRegistration.this,"email error reg",Toast.LENGTH_SHORT).show();
             }
         });

    }
    public  void  AuthenticateWithFirebase(){
        String Email=Email_reg.getText().toString().trim();
        String pass=password.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    RegisterWithFirestore();
                    Toast.makeText(DriverRegistration.this,"details registered",Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(DriverRegistration.this, DriversHomeActivity.class);
                    startActivity(intent);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DriverRegistration.this,"email already exist",Toast.LENGTH_SHORT).show();
            }
        });


    }
}