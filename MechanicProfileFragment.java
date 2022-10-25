package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    FirebaseFirestore fstore;
    FirebaseAuth mAuth;
    private ArrayList<mechanic_details> mechanicsArrayListArrayList;
    private String userId;
    TextView textfname,textsname,textidemail,textphonenumber;
    DocumentSnapshot documentSnapshot;
    ImageView imageView;
    TextView textview;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_mechanic_profile, container, false);
        textfname=(TextView)view.findViewById(R.id.txt_firstname);
        imageView=view.findViewById(R.id.imageView_profilepic);
        textsname=(TextView)view.findViewById(R.id.txt_secondname);
        textidemail=(TextView)view.findViewById(R.id.txt_mechanicemail);
        textphonenumber=(TextView)view.findViewById(R.id.txt_phonenumber);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

getProfileFromFirestore();






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
                            Picasso.get().load(uri).into(imageView);

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
}