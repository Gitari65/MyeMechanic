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
import android.widget.Adapter;
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
public class MechViewRequestsFragment extends Fragment implements RecyclerViewInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ArrayList<request_details> requestsArrayList;
    private mechanicsAdapter mechanicsAdapter2;
    private AdapterRequests myRAdapter;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;
    ProgressBar loadingPB;

    public MechViewRequestsFragment() {
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

        View view=inflater.inflate(R.layout.fragment_mech_view_requests, container, false);
        recyclerView=view.findViewById(R.id.myrecylerviewMVR);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsArrayList=new ArrayList<request_details>();
        myRAdapter = new AdapterRequests(getContext(),requestsArrayList,  this);

        progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();
        recyclerView.setAdapter(myRAdapter);

        EventChangeListener();





        return view;
    }

    private void EventChangeListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
      String  userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("driverRequest").whereEqualTo("mechanicId",userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        requestsArrayList.add(documentChange.getDocument().toObject(request_details.class));


                    }
                myRAdapter.notifyDataSetChanged();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
       // Intent intent = new Intent(getContext(), MechanicSelectDriver.class);
      //  intent.putExtra("driversId",requestsArrayList.get(position).getDriversId() );
       // Log.d(TAG, "onItemClick: driversId= "+requestsArrayList.get(position).getDriversId());
       // intent.putExtra("carModel", requestsArrayList.get(position).getCarModel());
      //  intent.putExtra("carProblemDescription", requestsArrayList.get(position).getCarProblemDescription());

      // startActivity(intent);

    }
}