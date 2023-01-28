package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class DriverProblemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView textViewEn, textViewBr, textViewEl, textViewTy;
    Button button, button1, buttonChoose,buttonUpload;
    ImageView imageView;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private Uri uri;
    private ProgressDialog progressDialog;
    String carPart,carModel,carProblemDescription;

    private Bitmap bitmap = null;
    private int CAMERA_REQUEST = 12;
    EditText editText;
    String[] problem = {"Engine", "Tyres", "Electrical", "Brakes", "Don't know"};
    String[] carModelP = {"Sedan", "Suzuki", "Mazda", "Toyota", "Don't know"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar actionBar = getSupportActionBar();
        //  assert actionBar != null;
        //  actionBar.hide();

        setContentView(R.layout.activity_driver_problem);
        imageView=findViewById(R.id.imgAvatarProblem);
        buttonChoose = findViewById(R.id.btnChoosePic);
        button1 = findViewById(R.id.problem_backbutton);
        button = findViewById(R.id.btnfindMechs);
        editText = findViewById(R.id.edttextCarProblemDesc);
        progressDialog = new ProgressDialog(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, problem);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinner
        Spinner spin = (Spinner) findViewById(R.id.spinnerProblem);
        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        //Performing action onItemSelected and onNothing selected
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carPart = spin.getSelectedItem().toString();

                Log.d(TAG, "onCreate: carPart" + carPart);
                buttonChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                    }
                });


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().equals("")) {
                            editText.setError("write something");
                            editText.requestFocus();
                            return;
                        }
                        if (imageView == null) {
                            Toast.makeText(getApplicationContext(),"Kindly Attach a photo",Toast.LENGTH_SHORT).show();
                              buttonChoose.setError("upload a photo of the problem");
                         buttonChoose.requestFocus();
                            return;
                        }
                        //storing spinner
                    final   String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        carProblemDescription = editText.getText().toString();
                        FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                        Map<String, Object> userToy = new HashMap<>();
                        userToy.put("carPart", carPart);
                        userToy.put("carProblemDescription", carProblemDescription);


//                        FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Request").push().setValue(userToy).
//                                addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            FancyToast.makeText(getApplicationContext(), "Upload made successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
//
//                                            Intent intent1 = new Intent(getApplicationContext(), MapsActivity.class);
//                                            intent1.putExtra("carPart", carPart);
//                                            intent1.putExtra("driverId", user_id);
//                                             intent1.putExtra("carProblemDescription",carProblemDescription);
//                                            //intent.putExtra("carModel", "Suzuki");
//                                            startActivity(intent1);
//                                        }
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//
//                                    }
//                                });



                  }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, carModelP);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinner
        Spinner spin1 = (Spinner) findViewById(R.id.spinnerCarModel);
        spin1.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //Setting the ArrayAdapter data on the Spinner
        spin1.setAdapter(aa1);
        //Performing action onItemSelected and onNothing selected
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 carModel = spin1.getSelectedItem().toString();
                Log.d(TAG, "onCreate: carModel" + carModel);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().equals("")) {
                            editText.setError("write something");
                            editText.requestFocus();
                            return;
                        }
                        uploadImage();
                        //storing spinner
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
                         final  String  date = dateFormat.format(new Date());
                        final long  timestamp = System.currentTimeMillis();
                        carProblemDescription = editText.getText().toString();
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                        DatabaseReference myRef1=FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Request").push();
                        DatabaseReference myRef2=FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Driver").child(user_id).push();
                        Map<String, Object> userToy1 = new HashMap<>();
                        userToy1.put("carModel", carModel);
                        userToy1.put("driversId", user_id);
                        userToy1.put("mechanicId", "");
                        userToy1.put("carPart", carPart);
                        userToy1.put("carProblemDescription", carProblemDescription);


                        userToy1.put("finalStatus", "");
                        userToy1.put("status", "not sent");
                        userToy1.put("date", date);
                        userToy1.put("timestamp", timestamp);

                        myRef2.setValue(userToy1);

                        myRef1.setValue(userToy1)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            String childKey= myRef1.getKey();

                                            FancyToast.makeText(getApplicationContext(), "Upload made successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                                            Intent intent1 = new Intent(getApplicationContext(), MapsActivity.class);
                                            intent1.putExtra("carModel", carModel);
                                            intent1.putExtra("carPart", carPart);
                                            intent1.putExtra("driverId", user_id);
                                            intent1.putExtra("childKey", childKey);
                                            intent1.putExtra("date", date);
                                            intent1.putExtra("timestamp", timestamp);
                                            intent1.putExtra("carProblemDescription",carProblemDescription);


                                            startActivity(intent1);
                                        }
                                    }
                                });

                        {
                            //cancelling request
                            String Current_userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                            db3.collection("driverRequest").document(Current_userId).set(userToy1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: spinner stored");

                                           // Toast.makeText(getApplicationContext(), "request cancelled xx",Toast.LENGTH_SHORT).show();


                                        }
                                    });

                        }

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), DriversHomeActivity.class);
                startActivity(intent1);
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void chooseImage() {
        ;if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            FancyToast.makeText(getApplicationContext(), "Allow Camera Permissions", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
            ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent , CAMERA_REQUEST);
        }
    }


    private void uploadImage() {
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Uploading Image To Firebase");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String fileName = System.currentTimeMillis()+".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Images").child(fileName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            storageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> saveToFirebase(uri.toString()));
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                FancyToast.makeText(getApplicationContext(), e.toString(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                progressDialog.dismiss();
            });

            if(uri!= null) {
                StorageReference imageRef= FirebaseStorage.getInstance().getReference().child("ProblemImages").child(System.currentTimeMillis()+"."+getFileExtension(uri));
            }
        }


    }

    private void saveToFirebase(String url){

        HashMap<String,Object> map=new HashMap();

        map.put("imageUrl",url);
       FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("driverRequest").document(uid).set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onSuccess: successful problemPhoto Firestore storage");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: failed Firestore storage");
                    }
                });


    }


    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REQUEST && resultCode== Activity.RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(bitmap);
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}