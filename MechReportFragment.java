package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MechReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MechReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MechReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MechReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MechReportFragment newInstance(String param1, String param2) {
        MechReportFragment fragment = new MechReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    LineChart linechart;
    int engineCount=0,tyresCount=0,electricalCount=0,brakesCount=0;
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
      View view= inflater.inflate(R.layout.fragment_mech_report, container, false);
        // Create a new line chart
         linechart = (LineChart) view.findViewById(R.id.lineChart);



      return  view;
    }
    public void GetCarPartCount() {
String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

// Create a new list of entries for the engine count
        List<Entry> engineEntries = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Work").child("drivers").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("carpart").getValue().equals("Tyres")) {
                        tyresCount++;
                    } else if (snapshot.child("carpart").getValue().equals("Engine")) {
                        engineCount++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        Log.d(TAG, "GetCarPartCount: "+ engineCount);
        engineEntries.add(new Entry(1, (float) engineCount));
        List<Entry> tyreEntries = new ArrayList<>();
        tyreEntries.add(new Entry(2, (float) tyresCount));
// Retrieve the count of "engine" children in the "Work/drivers" directory
//        DatabaseReference engineRef = FirebaseDatabase.getInstance().getReference("Work").child("drivers").child(userId);
//        engineRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long engineCount = dataSnapshot.getChildrenCount();
//                // Add the count as an entry to the list
//                engineEntries.add(new Entry(1, (float) engineCount));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle error
//            }
//        });
//
//// Create a new list of entries for the tyre count
//        List<Entry> tyreEntries = new ArrayList<>();
//
//// Retrieve the count of "tyre" children in the "Work/drivers" directory
//        DatabaseReference tyreRef = FirebaseDatabase.getInstance().getReference("Work").child("drivers").child("tyre");
//        tyreRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long tyreCount = dataSnapshot.getChildrenCount();
//                // Add the count as an entry to the list
//                tyreEntries.add(new Entry(2, (float) tyreCount));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle error
//            }
//        });

// Create a new data set for the engine count
        LineDataSet engineDataSet = new LineDataSet(engineEntries, "Engine Count");
        engineDataSet.setColor(Color.RED);
        engineDataSet.setValueTextColor(Color.RED);

// Create a new data set for the tyre count
        LineDataSet tyreDataSet = new LineDataSet(tyreEntries, "Tyre Count");
        tyreDataSet.setColor(Color.BLUE);
        tyreDataSet.setValueTextColor(Color.BLUE);

// Add the data sets to a list
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(engineDataSet);
        dataSets.add(tyreDataSet);

// Create a new data object from the data sets
        LineData data = new LineData(dataSets);

// Set the data on the chart
        linechart.setData(data);

// Refresh the chart
        linechart.invalidate();

    }
}