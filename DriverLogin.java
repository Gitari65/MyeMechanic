package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DriverLogin extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    DocumentSnapshot documentSnapshot,documentSnapshot1,documentSnapshot2;
    EditText mechanic_login_email,mechanic_login_pass;
    Button mechanic_login_button,button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mechanic_login_button=findViewById(R.id.mechanic_login_buttonD);
        mechanic_login_email=findViewById(R.id.mechanic_login_emailD);
        mechanic_login_pass=findViewById(R.id.mechanic_login_passwordD);
        textView=findViewById(R.id.register_textD);
        button=findViewById(R.id.login_backbuttonD);
        progressBar=findViewById(R.id.progressBarD);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(intent);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), DriverRegistration.class);
                startActivity(intent);
            }
        });

        mechanic_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mechanic_login_email.getText().toString().equals("")) {
                    mechanic_login_email.setError("Please Enter Email");
                    mechanic_login_email.requestFocus();
                    return;
                }
                if (mechanic_login_pass.getText().toString().equals("")) {
                    mechanic_login_pass.setError("Please Enter password");
                    mechanic_login_pass.requestFocus();
                    return;
                }
                String Email=mechanic_login_email.getText().toString().trim();
                String pass=mechanic_login_pass.getText().toString().trim();

                mAuth= FirebaseAuth.getInstance();

                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference myRef = database.getReference("Users");


                mAuth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Authentication failed." , Toast.LENGTH_LONG).show();

                        }
                        else {



                            driverUserTypeVerification();




                        }
                    }
                });

            }
        });




    }
    public void  driverUserTypeVerification(){
        //firebase authentication
        mAuth= FirebaseAuth.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Drivers").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task5) {
                DocumentSnapshot documentSnapshot5;

                if (task5.isSuccessful()) {
                    documentSnapshot5 = task5.getResult();
                    if (documentSnapshot5.exists()) {
                        Intent intent= new Intent(getApplicationContext(), DriversHomeActivity.class);
                        startActivity(intent);}
                    else {
                        adminUserTypeVerification();
                    }
                }}

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: null snapshot");
                Toast.makeText(getApplicationContext(),"failed!!check email or password",Toast.LENGTH_LONG).show();

            }
        });
    }
    public void adminUserTypeVerification(){
        //firebase authentication
        mAuth= FirebaseAuth.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Admin").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task5) {
                DocumentSnapshot documentSnapshot5;

                if (task5.isSuccessful()) {
                    documentSnapshot5 = task5.getResult();
                    if (documentSnapshot5.exists()) {
                        Intent intent= new Intent(getApplicationContext(), AdminHomeActivity.class);
                        startActivity(intent);}
                    else {
                        Log.d(TAG, "onComplete: details not found");
                        Toast.makeText(getApplicationContext(),"failed!!check email or password",Toast.LENGTH_LONG).show();

                    }
                }}

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: null snapshot");            }
        });
    }
}