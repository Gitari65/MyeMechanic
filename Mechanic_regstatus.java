package com.example.myemechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Mechanic_regstatus extends AppCompatActivity {

    String userid,status_text;
    DocumentSnapshot documentSnapshot;
    TextView textview;
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();
Button button,button1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_regstatus);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getMechanicVerificationStatus();
        textview=findViewById(R.id.registration_status);
button=findViewById(R.id.button_regstatus);
button1=findViewById(R.id.btnlogout_regStatus);
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        getMechanicVerificationStatus();
    }
});
button1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent intent1 =new Intent(getApplicationContext(),Mechanic_login.class);
        startActivity(intent1);
    }
});



    }
   public void getMechanicVerificationStatus(){
       //firebase authentication
       mAuth= FirebaseAuth.getInstance();
       FirebaseUser userID  = mAuth.getCurrentUser();
       FirebaseDatabase database = FirebaseDatabase.getInstance();
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


       db.collection("mechanics").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {


               if (task.isSuccessful()) {
                   documentSnapshot = task.getResult();
                   if (documentSnapshot.exists()) {
                       Toast.makeText(Mechanic_regstatus.this,"verification may take 72hrs",Toast.LENGTH_LONG).show();

                       status_text= documentSnapshot.getString("registrationStatus");
                       textview.setText(status_text);
                       textview.setTextColor(Color.RED);
                       if(Objects.equals(status_text, "verified")){
                           textview.setText("Verified");
                           textview.setTextColor(Color.GREEN);
                           Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                           startActivity(intent);
                           Toast.makeText(Mechanic_regstatus.this,"your account is now verified",Toast.LENGTH_SHORT).show();
                       }
                   }
               else {
                       Toast.makeText(Mechanic_regstatus.this,"no such record found",Toast.LENGTH_SHORT).show();

                   }
               }}

       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(Mechanic_regstatus.this,"null snapshot",Toast.LENGTH_SHORT).show();
           }
       });
    }
}