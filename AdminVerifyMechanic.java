package com.example.myemechanic;

import static android.content.ContentValues.TAG;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        String current_userId=getIntent().getStringExtra("currentuserid");

        Intent intent =new Intent(getApplicationContext(),AdminReports.class);
        intent.putExtra("currentuserid",current_userId);
        startActivity(intent);

    }
});


    }
    public void CreatePDF(){
        String current_userId= getIntent().getStringExtra("currentuserid");


// Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("DriverRequest").child("MechanicWork");

// Retrieve data from the database
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                // Create a new PDF document
                Document doc = new Document(new Rectangle(PageSize.A4.getWidth() + 200, PageSize.A4.getHeight()));
                try {

                    // Create a file in the internal storage directory
                    File file=new File(getExternalFilesDir("/"),"/output.pdf");

                    try {
                        PdfWriter.getInstance(doc, new FileOutputStream(file));
                        doc.open();

                        Toast.makeText(getApplicationContext(), " Downloading... ",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), " Downloaded",Toast.LENGTH_SHORT).show();



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // Create a new PdfWriter for the file
                    // Create a new PdfWriter for the file

                    Font nameFont3 = new Font();
                    nameFont3.setSize(19);
                    nameFont3.setStyle(Font.BOLD);
                    Phrase dataPhrase3 = new Phrase();
                    Paragraph dataParagraph3 = new Paragraph();
                    Chunk titleChunk3 = new Chunk("My-Mechanic Service Reports", nameFont3);
                    dataParagraph3.setSpacingAfter(10);
                    dataPhrase3.add(titleChunk3);
                    dataParagraph3.add(dataPhrase3);

// Add the paragraph to the PDF
                    doc.add(dataParagraph3);
                    // Create a new font for the name
                    Font nameFont = new Font();
                    nameFont.setSize(12);
                    nameFont.setStyle(Font.BOLD);

                    Chunk dateChunk1 = new Chunk("DATE", nameFont);
                    Chunk timeChunk1 = new Chunk("TIME", nameFont);

                    Chunk modelChunk1 = new Chunk("MODEL", nameFont);
                    Chunk partChunk1 = new Chunk("PART", nameFont);
                    Chunk problemChunk1 = new Chunk("PROBLEM", nameFont);
                    Chunk priceChunk1 = new Chunk("PRICE", nameFont);
                    Chunk phoneChunk1 = new Chunk("DRIVER", nameFont);
                    Chunk methodChunk1 = new Chunk("PAYMENT", nameFont);
                    Chunk statusChunk1 = new Chunk("STATUS", nameFont);

                    Phrase dataPhrase1 = new Phrase();
                    dataPhrase1.add(dateChunk1);
                    dataPhrase1.add("         ");  // Add some space between the name and address
                    dataPhrase1.add(timeChunk1);
                    dataPhrase1.add("        ");
                    dataPhrase1.add(modelChunk1);
                    dataPhrase1.add("         ");  // Add some space between the name and address
                    dataPhrase1.add(partChunk1);
                    dataPhrase1.add("          ");  // Add some space between the name and address
                    dataPhrase1.add(problemChunk1);
                    dataPhrase1.add("     ");  // Add some space between the name and address
                    dataPhrase1.add(priceChunk1);
                    dataPhrase1.add("       ");  // Add some space between the name and address
                    dataPhrase1.add(phoneChunk1);
                    dataPhrase1.add("        ");  // Add some space between the name and address
                    dataPhrase1.add(methodChunk1);
                    dataPhrase1.add("      ");  // Add some space between the name and address
                    dataPhrase1.add(statusChunk1);
                    // Create a new paragraph and add the phrase to it
                    Paragraph dataParagraph1 = new Paragraph();
                    dataParagraph1.add(dataPhrase1);
                    doc.add(dataParagraph1);
                    if (snapshot.hasChildren()) {

                    for (DataSnapshot child : snapshot.getChildren()) {
                        // Create a new phrase and add the chunks to it
                        Phrase dataPhrase = new Phrase();

                        String date = child.child("date").exists() ? child.child("date").getValue(String.class) : "      N/A       ";
                        String problem = child.child("workProblem").exists() ? child.child("workProblem").getValue(String.class) : "  N/A   ";
                        String price = child.child("workPrice").exists() ? child.child("workPrice").getValue(String.class) : "N/A";
                        String phone = child.child("driverPhoneNumber").exists() ? child.child("driverPhoneNumber").getValue(String.class) : "   N/A  ";
                        String paymentMethod = child.child("paymentMethod").exists() ? child.child("paymentMethod").getValue(String.class) : " N/A ";
                        String paymentStatus = child.child("paymentStatus").exists() ? child.child("paymentStatus").getValue(String.class) : " N/A ";
                        String address = child.child("carPart").exists() ? child.child("carPart").getValue(String.class) : "  N/A  ";

                        String name = child.child("carModel").exists() ? child.child("carModel").getValue(String.class) : "  N/A  ";
//                        String name = child.child("carModel").getValue(String.class);





    // Create a new chunk with the name
    Chunk nameChunk = new Chunk(name);
    Chunk dateChunk = new Chunk(date);

    Chunk problemChunk = new Chunk(problem);

    Chunk priceChunk = new Chunk(price);
    Chunk phoneChunk = new Chunk(phone);
    Chunk methodChunk = new Chunk(paymentMethod);
    Chunk statusChunk = new Chunk(paymentStatus);
// Create a new chunk with the address
    Chunk addressChunk = new Chunk(address);

// Create a new phrase and add the chunks to it
//    Phrase dataPhrase = new Phrase();

    dataPhrase.add(dateChunk);
    dataPhrase.add("      ");  // Add some space between the name and address
    dataPhrase.add(nameChunk);
    dataPhrase.add("        ");  // Add some space between the name and address
    dataPhrase.add(addressChunk);
    dataPhrase.add("        ");  // Add some space between the name and address
    dataPhrase.add(problemChunk);
    dataPhrase.add("           ");  // Add some space between the name and address
    dataPhrase.add(priceChunk);
    dataPhrase.add("           ");  // Add some space between the name and address
    dataPhrase.add(phoneChunk);
    dataPhrase.add("           ");  // Add some space between the name and address
    dataPhrase.add(methodChunk);
    dataPhrase.add("          ");  // Add some space between the name and address
    dataPhrase.add(statusChunk);

// Create a new paragraph and add the phrase to it
    Paragraph dataParagraph = new Paragraph();
    dataParagraph.add(dataPhrase);

// Add the paragraph to the PDF
    doc.add(dataParagraph);





                    }}else {
                        // If the snapshot has no children, add a message to the PDF
                        doc.add(new Paragraph("No data available"));
                    }
                    doc.close();
                    // Add data to the PDF




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