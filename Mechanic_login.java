package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class Mechanic_login extends AppCompatActivity {
    FirebaseAuth mAuth;
FirebaseFirestore fStore;
    DocumentSnapshot documentSnapshot,documentSnapshot1,documentSnapshot2,documentSnapshot5;
    EditText mechanic_login_email,mechanic_login_pass;
    ProgressBar progressBar;
   ProgressDialog pd;
    Button mechanic_login_button,button;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_login);
        //ActionBar actionBar = getSupportActionBar();
       // assert actionBar != null;
       // actionBar.hide();
        //progerss
        pd = new ProgressDialog(Mechanic_login.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Signing in....");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        mechanic_login_button=findViewById(R.id.mechanic_login_button);
        mechanic_login_email=findViewById(R.id.mechanic_login_email);
        mechanic_login_pass=findViewById(R.id.mechanic_login_password);
        textView=findViewById(R.id.register_text);
        progressBar=findViewById(R.id.progressBarM);
        button=findViewById(R.id.login_backbutton);
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
                Intent intent= new Intent(Mechanic_login.this, Mechanic_signup.class);
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

        mAuth=FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("Users");


        mAuth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               pd.show();
                if (!task.isSuccessful()) {
                    pd.dismiss();
                    // there was an error
                    Toast.makeText(Mechanic_login.this, "Authentication failed." , Toast.LENGTH_LONG).show();

                }
                else {
                    getMechanicVerificationStatus();








                }
            }
        });

    }
});




    }

    public void mechanicAuthentication(){
        //mechanic authentication
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("mechanics").document(String.valueOf(userID)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Toast.makeText(getApplicationContext(),"mechanic document exist",Toast.LENGTH_LONG).show();

                     String email= documentSnapshot.getString("mechanicEmail");


                    }
                    else {
                        Toast.makeText(getApplicationContext(),"no such record found",Toast.LENGTH_SHORT).show();

                    }
                }}

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"null snapshot",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //mechanic verification
    public void getMechanicVerificationStatus(){
        //firebase authentication
        mAuth= FirebaseAuth.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("mechanics").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task5) {
DocumentSnapshot documentSnapshot5;

                if (task5.isSuccessful()) {
                    pd.dismiss();
                    documentSnapshot5 = task5.getResult();
                    if (documentSnapshot5.exists()) {

                        String status_text= documentSnapshot5.getString("registrationStatus");

                        if(Objects.equals(status_text, "verified")){
                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        if(Objects.equals(status_text, "pending")){
                            Toast.makeText(getApplicationContext(),"verification may take 72hrs",Toast.LENGTH_LONG).show();

                            Intent intent= new Intent(getApplicationContext(), Mechanic_regstatus.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        Log.d(TAG, "onComplete: details not found");

                        adminUserTypeVerification();

                    }
                }}

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Log.d(TAG, "onFailure: null snapshot");            }
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



    public void GetUserType(){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");


// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }
}
