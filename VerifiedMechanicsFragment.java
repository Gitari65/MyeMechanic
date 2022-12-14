package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifiedMechanicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifiedMechanicsFragment extends Fragment implements RecyclerViewInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ArrayList<mechanic_details> mechanicsArrayList;
    private mechanicsAdapter mechanicsAdapter2;
    private  MyAdapter myAdapter;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;
    ProgressBar loadingPB;

    public VerifiedMechanicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifiedMechanicsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifiedMechanicsFragment newInstance(String param1, String param2) {
        VerifiedMechanicsFragment fragment = new VerifiedMechanicsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_verified_mechanics, container, false);
        recyclerView=view.findViewById(R.id.myrecylerviewV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mechanicsArrayList=new ArrayList<mechanic_details>();
        myAdapter = new MyAdapter(getContext(),mechanicsArrayList,  this);

        progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();





        return view;
    }

    private void EventChangeListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("mechanics").whereEqualTo("registrationStatus", "verified").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Log.d(TAG, "onEvent: error firestore ");
                    return;
                }
                assert value != null;
                for(DocumentChange documentChange :value.getDocumentChanges())
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        mechanicsArrayList.add(documentChange.getDocument().toObject(mechanic_details.class));


                    }
                myAdapter.notifyDataSetChanged();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), AdminVerifyMechanic.class);
        intent.putExtra("fName", mechanicsArrayList.get(position).getFirstName());
        intent.putExtra("sName", mechanicsArrayList.get(position).getSecondName());
        intent.putExtra("email", mechanicsArrayList.get(position).getMechanicEmail());
        intent.putExtra("registrationStatus", mechanicsArrayList.get(position).getRegistrationStatus());
        intent.putExtra("licenceUrl", mechanicsArrayList.get(position).getLicenceUrl());
        intent.putExtra("profilephotourl", mechanicsArrayList.get(position).getProfilePhotoUrl());
        intent.putExtra("currentuserid", mechanicsArrayList.get(position).getCurrent_userId());
        startActivity(intent);

    }
}