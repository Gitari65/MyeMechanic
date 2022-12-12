package com.example.myemechanic;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MechanicProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MechanicProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MechanicProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MechanicProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MechanicProfileFragment newInstance(String param1, String param2) {
        MechanicProfileFragment fragment = new MechanicProfileFragment();
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
    // request code
    private final int PICK_IMAGE_REQUEST = 22,PICK_IMAGE_REQUEST1 = 23;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Uri filePath,filePath1;

    FirebaseFirestore fstore;

    private ArrayList<mechanic_details> mechanicsArrayListArrayList;
    private String userId;
    EditText textfname,textsname,textidemail,textphonenumber;
    DocumentSnapshot documentSnapshot;
    ImageView imageView,imageView1;
    Button button;
    TextView textview;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_mechanic_profile, container, false);
        textfname=view.findViewById(R.id.txt_firstname);
        imageView=view.findViewById(R.id.imageView_profilepic);
        imageView1=view.findViewById(R.id.imgEditPhoto1);
        textsname=view.findViewById(R.id.txt_secondname);
        textidemail=view.findViewById(R.id.txt_mechanicemail);
        textphonenumber=view.findViewById(R.id.txt_phonenumber);
        button=view.findViewById(R.id.btnUpdateMechanic);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

getProfileFromFirestore();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        uploadProfilePhoto();
        UpdateProfileToFirestore();
    }
});
imageView1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        SelectProfilePhoto();
    }
});







        return view;
    }
    public void  getProfileFromFirestore(){
        {
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
                            Toast.makeText(getContext(),"details found",Toast.LENGTH_LONG).show();
                            //profile picture
                            String uri = documentSnapshot.getString("profilePhotoUrl");
                            Picasso.get().load(uri).fit().placeholder(R.drawable.account_icon1)
                                    .centerCrop()
                                    .into(imageView);

                            textfname.setText(documentSnapshot.getString("firstName"));
                            textsname.setText(documentSnapshot.getString("secondName"));
                            textidemail.setText(documentSnapshot.getString("mechanicEmail"));
                            textphonenumber.setText(documentSnapshot.getString("phoneNumber"));

                        }
                        else {
                            Toast.makeText(getContext(),"no such record found",Toast.LENGTH_SHORT).show();

                        }
                    }}

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"null snapshot",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void UpdateProfileToFirestore(){

            String firstname=textfname.getText().toString().trim();
            String secondname=textsname.getText().toString().trim();
            String email=textidemail.getText().toString().trim();
            String phone=textphonenumber.getText().toString().trim();
            String current_userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db3 = FirebaseFirestore.getInstance();
            db3.collection("mechanics").document(current_userId).
                    update("firstName", firstname,"secondName",secondname,"mechanicEmail",email,"phoneNumber",phone)
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
                                            Map<String, Object> userToy = new HashMap<>();

                                            userToy.put("profilePhotoUrl", profilePhoto);
                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            DatabaseReference myRef2= FirebaseDatabase.getInstance().getReference("Technicians").child(userId);
                                            myRef2.updateChildren(userToy).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    //  FancyToast.makeText(getApplicationContext(), "Location Updated automatically", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

                                                }
                                            });
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("profilePhotoUrl", profilePhoto);

                                            mAuth = FirebaseAuth.getInstance();
                                            FirebaseUser userID  = mAuth.getCurrentUser();
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            db.collection("mechanics").document(uid).set(user, SetOptions.merge())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d(TAG, "onSuccess: successful Photo Firestore storage");
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