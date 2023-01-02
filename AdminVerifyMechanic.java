package com.example.myemechanic;

import static android.content.ContentValues.TAG;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminVerifyMechanic extends AppCompatActivity {
EditText editTextf,editTexts,editTexte,editTextv;
ImageView imageViewp,imageViewl;
Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verify_mechanic);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        String firstName=getIntent().getStringExtra("fName");
        String secondName=getIntent().getStringExtra("sName");
        String mechanicEmail=getIntent().getStringExtra("email");
        String profilePhotoUrl=getIntent().getStringExtra("profilephotourl");
        String current_userId=getIntent().getStringExtra("currentuserid");
        String licenseUrl=getIntent().getStringExtra("licenceUrl");


        String verificationStatus=getIntent().getStringExtra("registrationStatus");
editTexte=findViewById(R.id.verymechanic_email);
editTextf=findViewById(R.id.verymechanic_firstname);
editTexts=findViewById(R.id.verymechanic_secondname);
Button button=findViewById(R.id.button_adminUpdate);
button2=findViewById(R.id.button_adminReport);
editTextv=findViewById(R.id.verymechanic_verystatus);
imageViewl=findViewById(R.id.mech_licencePicv);
        imageViewp=findViewById(R.id.mech_profilePicv);
        //setting images to imageview
        Picasso.get().load(profilePhotoUrl).into(imageViewp);
        Picasso.get().load(licenseUrl).into(imageViewl);



editTextv.setText(verificationStatus);
editTexts.setText(secondName);
editTextf.setText(firstName);
editTexte.setText(mechanicEmail);

String verificationStatustext=editTextv.getText().toString().trim();


        //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //updating mechanics
        String verificationStatustext=editTextv.getText().toString().trim();
        FirebaseFirestore db3 = FirebaseFirestore.getInstance();
        db3.collection("mechanics").document(current_userId).update("registrationStatus", verificationStatustext)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: spinner stored");

                        Toast.makeText(getApplicationContext(),"updated details successfully",Toast.LENGTH_SHORT).show();

                    }
                });
    }
});
button2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
CreatePDF();
    }
});


    }
    public void CreatePDF(){
        String current_userId= getIntent().getStringExtra("currentuserid");


// Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Technicians").child(current_userId);

// Retrieve data from the database
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("emaill").getValue(String.class);
                String address = snapshot.child("firstNamee").getValue(String.class);

                // Create a new PDF document
                Document doc = new Document();
                try {

                    // Create a file in the internal storage directory
                    File file=new File(getExternalFilesDir("/"),"/output.pdf");
                    try {
                        PdfWriter.getInstance(doc, new FileOutputStream(file));
                        Toast.makeText(getApplicationContext(), " Downloading... ",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), " Downloaded",Toast.LENGTH_SHORT).show();



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // Create a new PdfWriter for the file
                    // Create a new PdfWriter for the file
                    PdfWriter.getInstance(doc, new FileOutputStream(file));
                    doc.open();
                    // Add data to the PDF
                    doc.add(new Paragraph(name));
                    doc.add(new Paragraph(address));

                    doc.close();


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onDataChange: error pdf ");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                Log.d(TAG, "onDatabaseError: error pdf ");

            }
        });

    }
}