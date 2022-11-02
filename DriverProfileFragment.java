package com.example.myemechanic;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DriverProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverProfileFragment newInstance(String param1, String param2) {
        DriverProfileFragment fragment = new DriverProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    EditText textFName,textSName,textEmail,textPhone;
    Button btnUpdate;
    ImageView imageView,imageView1;
    // request code
    private final int PICK_IMAGE_REQUEST = 22,PICK_IMAGE_REQUEST1 = 23;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Uri filePath,filePath1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_driver_profile, container, false);

        textEmail=view.findViewById(R.id.edtDriversEmail);
        textFName=view.findViewById(R.id.edtDriversFName);
        textPhone=view.findViewById(R.id.edtDriversPhone);
        imageView=view.findViewById(R.id.imageView_updateProfilePic);
        imageView1=view.findViewById(R.id.imgEditPhoto);
        textSName=view.findViewById(R.id.edtDriversSName);
        btnUpdate=view.findViewById(R.id.btnUpdateDriver);
        loadDriverDetails();

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SelectProfilePhoto();
            }
        });
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePhoto();
                updateDriverDetails();
            }
        });

        return view;
    }
    public void loadDriverDetails(){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        String userID  = mAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //String current_userId = getIntent().getStringExtra("driversId");



        db.collection("Drivers").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                               String firstName= documentSnapshot.getString("driverFirstName");
                                String secondName= documentSnapshot.getString("driverSecondName");
                                String phoneNumber= documentSnapshot.getString("driverPhoneNumber");
                                String Email= documentSnapshot.getString("driverEmail");
                                String uri = documentSnapshot.getString("profilePhoto");
                               // Picasso.get().load(uri).into(imageView);
                                Picasso.get().load(uri).fit().placeholder(R.drawable.account_icon1)
                                        .centerCrop()
                                        .into(imageView);
                                textEmail.setText(Email);
                                textFName.setText(firstName);
                                textPhone.setText(phoneNumber);
                                textSName.setText(secondName);


                                // Log.d(TAG, "onComplete: mecha garage name "+garagename);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void updateDriverDetails(){
        String firstname=textFName.getText().toString().trim();
        String secondname=textSName.getText().toString().trim();
        String email=textEmail.getText().toString().trim();
        String phone=textPhone.getText().toString().trim();
        String current_userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db3 = FirebaseFirestore.getInstance();
        db3.collection("Drivers").document(current_userId).
                update("driverFirstName", firstname,"driverSecondName",secondname,"driverEmail",email,"driverPhoneNumber",phone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: spinner  stored");

                        Toast.makeText(getContext(),"updated details successfully",Toast.LENGTH_SHORT).show();

                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getActivity().getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST1
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath= data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getActivity().getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
    private void SelectProfilePhoto()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST1);
    }
    private void uploadProfilePhoto()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "driverLicencePhoto/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {



                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getActivity(),
                                                    "photo details Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    //storing image to firestore

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadPhotoUrl) {
                                            //Now play with downloadPhotoUrl
                                            //Store data into Firebase Realtime Database
                                            String profilePhoto= downloadPhotoUrl.toString();
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("profilePhoto", profilePhoto);

                                            mAuth = FirebaseAuth.getInstance();
                                            FirebaseUser userID  = mAuth.getCurrentUser();
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            db.collection("Drivers").document(uid).set(user, SetOptions.merge())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d(TAG, "onSuccess: successful licencePhoto Firestore storage");
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onSuccess: failed Firestore storage");
                                                        }
                                                    });
                                        }
                                    });




                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getActivity(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
        else{
            Log.d(TAG, "uploadProfilePhoto: empty");
        }
    }

}