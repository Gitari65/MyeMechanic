package com.example.myemechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.ktx.Firebase;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class Mechanic_signup extends AppCompatActivity {

    //Initialization section
    EditText email,pass,username,confpass;
    ProgressBar progressBar;
    Button button;
    ImageButton button1;
    FirebaseFirestore fStore;
    TextView textView;
    String userid,Email;
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //progerss
        final ProgressDialog pd = new ProgressDialog(Mechanic_signup.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Signing up....");
        pd.setIndeterminate(true);
        pd.setCancelable(false);


        email=(EditText)findViewById(R.id.mechanic_signup_email);
        pass=(EditText)findViewById(R.id.mechanic_signup_password);
        confpass=(EditText)findViewById(R.id.mechanic_signup_confpassword);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_mechsignup);
        button=findViewById(R.id.mechanic_signup_button);
        textView=findViewById(R.id.txtmechanicSignup);
        button1=findViewById(R.id.login_back_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Mechanic_signup.this,MainActivity2.class);
                startActivity(intent);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Mechanic_signup.this,Mechanic_login.class);
                startActivity(intent);
            }
        });




button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String confpassw=confpass.getText().toString().trim();

        if (email.getText().toString().equals("")) {
            email.setError("Please Enter Email");
            email.requestFocus();
            return;
        }
        if (pass.getText().toString().equals("")) {
            pass.setError("Please Enter password");
            pass.requestFocus();
            return;
        }
        if(pass.getText().toString().trim().length()<6)
        {
            pass.setError(" more than 5 characters required");
            pass.requestFocus();
            return;
        }

        if(!confpassw.equals(pass.getText().toString().trim()) )
        {
            confpass.setError("password do not match");
            confpass.requestFocus();
            return;
        }


        //firebase authentication
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
      ;
        pd.show();

        mAuth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                pd.hide();
                Toast.makeText(Mechanic_signup.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Mechanic_signup.this,Mechanic_registration.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Mechanic_signup.this,"Email already exists",Toast.LENGTH_SHORT).show();
            }
        });



    }});


            }
        }





