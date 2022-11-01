package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Mechanic_registration extends AppCompatActivity  implements
        View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText fname,sname,email,phoneno,edtGarageLocation,edtGarageName;
    TextView photo,licence;
    Button button;

    CheckBox checkBox,checkBox1,checkBox2,checkBox3,checkBoxEngine,checkBoxTyres,checkBoxElectric,checkBoxBrakes;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String garageLocation;
    int Image_Request_Code = 7;
    Bitmap photo1;
    ImageView imageViewL,imageViewP;
    private static final int CAMERA_REQ = 1;
    String[]constituencies = {"Tetu", "Kieni", "Mathira", "Othaya", "Mukurweni", "NyeriTown"};
    private static final int TAKE_PICTURE = 1;
    private static final String IMG_REQUEST_ID = null;
    private static final int PICK_IMAGE_REQUEST = 22;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_registration);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mAuth =FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//progerss
        final ProgressDialog pd = new ProgressDialog(Mechanic_registration.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("please wait....");
        pd.setIndeterminate(true);
        pd.setCancelable(false);





        //spinner
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,constituencies);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        //Performing action onItemSelected and onNothing selected
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String garageLocation=spin.getSelectedItem().toString();
               //storing spinner
                String current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Object> user3 = new HashMap<>();
                user3.put("garageLocation",garageLocation);
                user3.put("current_userId",current_userId);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                db3.collection("mechanics").document(current_userId).set(user3, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: spinner stored");
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        fname = findViewById(R.id.mechanic_firstname);
        sname = findViewById(R.id.mechanic_secondname);
        email= findViewById(R.id.mechanic_idnumber);
        phoneno = findViewById(R.id.mechanic_phonenumber);
      //licence = findViewById(R.id.mechanic_licence);
       // photo = findViewById(R.id.mechanic_passport);
        progressBar = findViewById(R.id.progressBarReg);
     //   imageViewP=findViewById(R.id.imgviewPhoto);
        button = findViewById(R.id.button_mechanic_details);
        edtGarageName=findViewById(R.id.mechanic_garageName);
       // edtGarageLocation=findViewById(R.id.mechanic_garageLocation);

checkBox=findViewById(R.id.checkBoxM);
        checkBox1=findViewById(R.id.checkBoxN);
        checkBox2=findViewById(R.id.checkBoxS);
        checkBox3=findViewById(R.id.checkBoxT);
        checkBoxElectric=findViewById(R.id.checkBoxElectric);
        checkBoxBrakes=findViewById(R.id.checkBoxBrakes);
        checkBoxEngine=findViewById(R.id.checkBoxEngine);
        checkBoxTyres=findViewById(R.id.checkBoxTyre);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference().child("mechanicsCarModels");

                DatabaseReference  mDatabase1 = FirebaseDatabase.getInstance().getReference().child("mechanicsCarParts");


                if(checkBoxElectric.isChecked()) {

                    String user_id = mAuth.getCurrentUser().getUid();
                    FirebaseFirestore dbE = FirebaseFirestore.getInstance();
                    Map<String, Object> userE = new HashMap<>();
                    userE.put("Electrical", "True");
                    userE.put("Don't know", "True");
                    dbE.collection("mechanics").document(user_id).set(userE, SetOptions.merge());



                }
                if(checkBoxBrakes.isChecked()) {

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore dbB = FirebaseFirestore.getInstance();
                    Map<String, Object> userB = new HashMap<>();
                    userB.put("Brakes", "True");
                    dbB.collection("mechanics").document(user_id).set(userB, SetOptions.merge());



                }
                if(checkBoxTyres.isChecked()) {

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore dbT = FirebaseFirestore.getInstance();
                    Map<String, Object> userT = new HashMap<>();
                    userT.put("Tyres", "True");
                    dbT.collection("mechanics").document(user_id).set(userT, SetOptions.merge());



                }
                if(checkBoxEngine.isChecked()) {

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                    Map<String, Object> userEn = new HashMap<>();
                    userEn.put("Engine", "True");
                    dbEn.collection("mechanics").document(user_id).set(userEn, SetOptions.merge());


                }
                if(!checkBoxElectric.isChecked() && !checkBoxEngine.isChecked()&&!checkBoxTyres.isChecked()&&!checkBoxBrakes.isChecked())
                {
                    checkBox3.setError("Please check at least one");

                    checkBoxEngine.requestFocus();
                    return;
                }


                if(checkBox.isChecked()) {

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                    Map<String, Object> userMaz = new HashMap<>();
                    userMaz.put("modelMazda", "True");
                    dbEn.collection("mechanics").document(user_id).collection("carModels").document(user_id).set(userMaz, SetOptions.merge());

                            }

                            if(checkBox1.isChecked()) {

                                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                                Map<String, Object> userNis = new HashMap<>();
                                userNis.put("modelNissan", "True");
                                dbEn.collection("mechanics").document(user_id).collection("carModels").document(user_id).set(userNis, SetOptions.merge());


                            }

                            if(checkBox3.isChecked()) {
                                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                                Map<String, Object> userToy = new HashMap<>();
                                userToy.put("modelToyota","True");
                                dbEn.collection("mechanics").document(user_id).collection("carModels").document(user_id).set(userToy, SetOptions.merge());


                            }

                            if(checkBox2.isChecked()) {

                                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                                Map<String, Object> userSuz = new HashMap<>();
                                userSuz.put("modelSuzuki", "True");
                                dbEn.collection("mechanics").document(user_id).collection("carModels").document(user_id).set(userSuz, SetOptions.merge());



                            }
                            if(!checkBox2.isChecked() && !checkBox1.isChecked()&&!checkBox.isChecked()&&!checkBox3.isChecked())
                            {
                                checkBox3.setError("Please check atleast one");

                               checkBox.requestFocus();
                                return;
                            }










                String garageName=edtGarageName.getText().toString().trim();
                String firstName = fname.getText().toString().trim();
                String secondName = sname.getText().toString().trim();
                String phoneNumber = phoneno.getText().toString().trim();
                String Email = email.getText().toString().trim();

                if (firstName.equals("")) {
                    fname.setError("Please Enter name");
                    fname.requestFocus();
                    return;
                }


                if (garageName.equals("")) {
                  edtGarageName .setError("field required");
                    edtGarageName .requestFocus();
                    return;
                }

                if (secondName.equals("")) {
                    sname.setError("Please Enter name");
                    sname.requestFocus();
                    return;
                }
                if (Email.equals("")) {
                    email.setError("Please Enter email");
                    email.requestFocus();
                    return;
                }
                if (phoneNumber.length() != 10) {
                    phoneno.setError("Please Enter valid phone number");
                    phoneno.requestFocus();
                    return;
                }


                ////storing email to realtime database
                Map<String, Object> map1 = new HashMap<>();

                map1.put("email",email);


                String id = mAuth.getCurrentUser().getUid();
                //email to database realtime

                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user_db = mDatabase.child(user_id);

                current_user_db.child("email").setValue(Email);
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



                                //chats setup
                                String firstNamee = fname.getText().toString().trim();
                                String secondNamee = sname.getText().toString().trim();
                                String mobilee = phoneno.getText().toString().trim();
                                String emaill = email.getText().toString().trim();
                                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Technicians");
                                mAuth =FirebaseAuth.getInstance();
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Technician technician = new Technician(firstNamee,secondNamee,emaill,mobilee,userid,"","","");

                                if (uid != null){
                                    databaseReference1.child(uid).setValue(technician).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // progressDialog.dismiss();
                                            FancyToast.makeText(getApplicationContext(), "Account Created Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                                            // startActivity(new Intent(getApplicationContext(), TechnicianHomeActivity.class));
                                            finish();
                                        }
                                    });


                                }



                //token storing
                // Get new FCM registration token
                String userToken =  Objects.requireNonNull(task.getResult());

                FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                Map<String, Object> user1 = new HashMap<>();;
                user1.put("mechanicToken", userToken);
                db3.collection("mechanics").document(uid).set(user1, SetOptions.merge())
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




        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("firstName", firstName);
                user.put("secondName", secondName);
                user.put("mechanicEmail", Email);
                user.put("phoneNumber", phoneNumber);

                user.put("garageName",garageName);
                user.put("mechanicAvailability","open");
                user.put("current_userId",uid);
                user.put("registrationStatus", "pending");

pd.show();

// Add a new document with a generated ID
                db.collection("mechanics").document(uid).set(user,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                if(task.isSuccessful()){

                                    Intent intent= new Intent(Mechanic_registration.this, UploadActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "details submission success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "submission failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                //email document

//saveInFirestore();
            }
        });
    }

private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent.createChooser(intent,"select picture",PICK_IMAGE_REQUEST ));

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQ && resultCode ==RESULT_OK && data!=null && data.getData()!=null){


            try {
                Bitmap bitmapimg = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                imageViewP.setImageBitmap(bitmapimg);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    public  void saveInFirestore(){
        ProgressDialog  progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("please wait.....");
        progressDialog.show();

        StorageReference reference=storageReference.child("picture/"+ UUID.randomUUID().toString());
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressDialog.dismiss();
                Toast.makeText(Mechanic_registration.this,"upload image success",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Mechanic_registration.this,"upload image failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}