package com.example.myemechanic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MechanicViewReviews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MechanicViewReviews extends Fragment implements RecyclerViewInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MechanicViewReviews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MechanicViewReviews.
     */
    // TODO: Rename and change types and number of parameters
    public static MechanicViewReviews newInstance(String param1, String param2) {
        MechanicViewReviews fragment = new MechanicViewReviews();
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
    private AdapterReviews myReviewAdapter;
    ArrayList<reviews_details> reviewsDetailsArrayList;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    DocumentSnapshot documentSnapshot;
    String driverFirstName;
    RatingBar ratingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_mechanic_view_reviews, container, false);
ratingBar=view.findViewById(R.id.mechViewtotal_star_rating);

        reviewsDetailsArrayList=new ArrayList<reviews_details>();
        myReviewAdapter = new AdapterReviews(getContext(),reviewsDetailsArrayList,  this);


        recyclerView=view.findViewById(R.id.RVmechViewreviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();

        MyEventChangeListener();
        String current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setTotalRatings();
        return view;

        // Inflate the layout for this fragment
    }



    private void MyEventChangeListener(){

        String current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "MyEventChangeListener: id= "+current_userId);

        DatabaseReference databaseReference   = FirebaseDatabase.getInstance().getReference("MechanicReviews").child("Ratings").child(current_userId);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reviews : snapshot.getChildren()){
                    //descriptions.add(request.child("serviceDescription").getValue().toString());
                    reviews_details reviewsDetails=reviews.getValue(reviews_details.class);
                    if(reviewsDetails!=null){
                        reviewsDetailsArrayList.add(reviewsDetails);
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: null reviews");
                    }


                }

                myReviewAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myReviewAdapter);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        });
    }
    private  void setTotalRatings(){

        String current_userId =FirebaseAuth.getInstance().getCurrentUser().getUid();


        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dbRef =  FirebaseDatabase.getInstance().getReference("MechanicReviews").child("Ratings").child(current_userId);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double total = 0.0;
                double count = 0.0;
                double average = 0.0;

                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.child("rating").getValue()!=null){
                        Float rating = Float.valueOf((ds.child("rating").getValue().toString()));
                        total = total + rating;
                        count = count + 1;
                        average = total / count;
                    }
                    else{
                        Log.d(TAG, "onDataChange: nullsnapshot");
                    }

                }


                final DatabaseReference newRef = db.child("MechanicReviews").child(current_userId).child("AverageRating");
                newRef.child("current").setValue(average);
                ratingBar.setRating((float) average);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    @Override
    public void onItemClick(int position) {

    }
}