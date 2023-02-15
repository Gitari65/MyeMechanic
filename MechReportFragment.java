package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MechReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MechReportFragment extends Fragment implements RecyclerViewInterface {

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
    private BarChart barChart;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private RecyclerView recyclerView;
    private ArrayList<request_details> requestsArrayList;
    private mechanicsAdapter mechanicsAdapter2;
    private AdapterReports myRAdapter,adapter;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;

TextView textviewpaid,textviewUnpaid,textviewCancelled,textviewModel,textviewPart,textviewComplete,textViewIncomplete;
    int engineCount,tyresCount,electricalCount,brakesCount;
    int paidCount;
    int unpaidCount,transactionCount;
    long cancelledCount;
    int modelCount;
    int partCount;
    Button button,buttonDetails;
    long startTimestamp,endTimestamp;
    int completeCount;
    TextView startingdatetextview,enddatetextview,textViewTransaction;
    DatePicker startDatePicker,endDatePicker;
    int incompleteCount;
    int suzukiCount,toyotaCount,sedanCount,mazdaCount;
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
      textviewpaid=view.findViewById(R.id.txtView_paidCount);
        textviewUnpaid=view.findViewById(R.id.txtView_unpaidCount);
        textviewCancelled=view.findViewById(R.id.txtView_cancelledCount);
        textViewIncomplete=view.findViewById(R.id.txtView_incompleteCount);
        textviewPart=view.findViewById(R.id.txtView_partCount);
        textviewModel=view.findViewById(R.id.txtView_modelCount);
        textviewComplete=view.findViewById(R.id.txtView_finishedCount);
        button=view.findViewById(R.id.btnFullReportButtonM);
        startDatePicker = view.findViewById(R.id.start_date_pickerM);
        endDatePicker = view.findViewById(R.id.end_date_pickerM);
        startingdatetextview=view.findViewById(R.id.start_date_inputM);
        enddatetextview=view.findViewById(R.id.end_date_inputM);
        textViewTransaction=view.findViewById(R.id.textViewTranscationCountM);
        buttonDetails=view.findViewById(R.id.btnViewReportButtonM);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    saveFragmentAsPDF();
                }

            }
        });

        DatePicker datePicker=startDatePicker;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    startingdatetextview.setText(sdf.format(calendar.getTime()));
                    startTimestamp = calendar.getTimeInMillis();
                }
            });
        }

        DatePicker datePicker1=endDatePicker;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            endDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker1, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    enddatetextview.setText(sdf.format(calendar.getTime()));
                    endTimestamp = calendar.getTimeInMillis();
                }
            });
        }
//        Calendar startCalendar = Calendar.getInstance();
//        startCalendar.set(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth());
//        startTimestamp = startCalendar.getTimeInMillis();

//        Calendar endCalendar = Calendar.getInstance();
//        endCalendar.set(endDatePicker.getYear(), endDatePicker.getMonth(), endDatePicker.getDayOfMonth());
//        endTimestamp = endCalendar.getTimeInMillis();

        startingdatetextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDatePicker.getVisibility() == View.VISIBLE){
                    startDatePicker.setVisibility(View.GONE);
                } else {
                    startDatePicker.setVisibility(View.VISIBLE);
                }
                if(endDatePicker.getVisibility() == View.VISIBLE){
                    endDatePicker.setVisibility(View.GONE);
                } else {
                    endDatePicker.setVisibility(View.VISIBLE);
                }
            }
        });
        enddatetextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(endDatePicker.getVisibility() == View.VISIBLE){
                    endDatePicker.setVisibility(View.GONE);
                } else {
                    endDatePicker.setVisibility(View.VISIBLE);
                }
                if(startDatePicker.getVisibility() == View.VISIBLE){
                    startDatePicker.setVisibility(View.GONE);
                } else {
                    startDatePicker.setVisibility(View.VISIBLE);
                }
            }
        });
        // Create a new line chart
        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (endTimestamp == 0  || startTimestamp == 0){

                    Toast.makeText(getContext(), "Enter the start date ", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (startTimestamp>endTimestamp ){
                    Toast.makeText(getContext(), "start date cannot be past end date ", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(startTimestamp!=0 && endTimestamp!=0){
                    button.setVisibility(View.VISIBLE);
                    MyEventChangeListener();
                    getCancelledRequests();
                    GetCarPartCount();

                }



            }
        });

//Initialize the bar chart view
        barChart = (BarChart) view.findViewById(R.id.bar_chart);
GetCarPartCount();
getCancelledRequests();
        recyclerView=view.findViewById(R.id.myrecylerviewTransactions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestsArrayList=new ArrayList<request_details>();
        myRAdapter = new AdapterReports(getContext(),requestsArrayList,  this);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();
        MyEventChangeListener();

      return  view;
    }
    public void GetCarPartCount() {
String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Work").child("mechanics").child(userId);
        reference.orderByChild("timestamp").startAt(startTimestamp).endAt(endTimestamp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 engineCount=0;tyresCount=0;electricalCount=0;brakesCount=0;
                 suzukiCount=0;toyotaCount=0;sedanCount=0;mazdaCount=0;
                 paidCount=0;unpaidCount=0;cancelledCount=0;modelCount=0;completeCount=0;incompleteCount=0;
                 transactionCount=0;
                transactionCount= (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.child("paymentStatus").getValue() != null) {
                        if (snapshot.child("paymentStatus").getValue().equals("not paid")) {
                            unpaidCount++;
                        }  if (snapshot.child("paymentStatus").getValue().equals("paid")) {
                            paidCount++;
                            Log.d(TAG, "GetCarPartCountloop:engine "+ paidCount);
                        }

                    }

                    if (snapshot.child("carPart").getValue() != null) {
                        if (snapshot.child("carPart").getValue().equals("Tyres")) {
                            tyresCount++;
                        }  if (snapshot.child("carPart").getValue().equals("Engine")) {
                            engineCount++;
                            Log.d(TAG, "GetcarPartCountloop:engine "+ engineCount);
                        }
                        if (snapshot.child("carPart").getValue().equals("Brakes")) {
                            brakesCount++;
                        }
                        if (snapshot.child("carPart").getValue().equals("Electrical")) {
                            electricalCount++;
                        }
                    }

                    if (snapshot.child("carModel").getValue() != null) {
                        if (snapshot.child("carModel").getValue().equals("Sedan")) {
                            sedanCount++;
                        }
                        if (snapshot.child("carModel").getValue().equals("Suzuki")) {
                            suzukiCount++;
                        }
                        if (snapshot.child("carModel").getValue().equals("Mazda")) {
                            mazdaCount++;
                        }
                        if (snapshot.child("carModel").getValue().equals("Toyota")) {
                            toyotaCount++;
                        }
                    }

                }

                Log.d(TAG, "GetCarPartCount:outside for loop engine "+ engineCount);
                Log.d(TAG, "GetCarModelCount: outside for loop sedan"+ sedanCount);
                // Create a new list of entries for the engine count
                textViewTransaction.setText(String.valueOf(transactionCount));
                if(transactionCount == 0){
                    Toast.makeText(getContext(), "No records", Toast.LENGTH_SHORT).show();
                }
textviewpaid.setText(String.valueOf(paidCount));
textviewUnpaid.setText(String.valueOf(unpaidCount));
textviewComplete.setText(String.valueOf(paidCount));
                int largest = Math.max(Math.max(mazdaCount, toyotaCount), Math.max(sedanCount, suzukiCount));

                if (largest == suzukiCount) {
                   textviewModel.setText("suzuki");
                } else if (largest == mazdaCount) {
                    textviewModel.setText("Mazda");
                } else if (largest == toyotaCount) {
                    textviewModel.setText("Toyota");
                } else if (largest == sedanCount) {
                    textviewModel.setText("Sedan");
                }
                int largestPart = Math.max(Math.max(engineCount, brakesCount), Math.max(electricalCount, tyresCount));

                if (largestPart == tyresCount) {
                    textviewPart.setText("tyres");
                } else if (largestPart == engineCount) {
                    textviewPart.setText("Engine");
                } else if (largestPart == brakesCount) {
                    textviewPart.setText("Brakes");
                } else if (largestPart == electricalCount) {
                    textviewPart.setText("Electrical");
                }




                textViewIncomplete.setText(String.valueOf(unpaidCount-paidCount));
//Create a list of BarEntry objects to hold the data for the chart
                List<BarEntry> toyotaEntries = new ArrayList<>();
                toyotaEntries.add(new BarEntry(0f, toyotaCount));

                List<BarEntry> sedanEntries = new ArrayList<>();
                sedanEntries.add(new BarEntry(1f, sedanCount));

                List<BarEntry> suzukiEntries = new ArrayList<>();
                suzukiEntries.add(new BarEntry(2f, suzukiCount));
                List<BarEntry> mazdaEntries = new ArrayList<>();
                mazdaEntries.add(new BarEntry(3f, mazdaCount));
//Create a BarDataSet for each set of data
                BarDataSet sedanDataSet = new BarDataSet(sedanEntries, "Sedan");
                sedanDataSet.setColor(Color.YELLOW);
                BarDataSet toyotaDataSet = new BarDataSet(toyotaEntries, "Toyota");
                toyotaDataSet.setColor(Color.BLACK);
                BarDataSet suzukiDataSet = new BarDataSet(suzukiEntries, "Suzuki");
                suzukiDataSet.setColor(Color.RED);
                BarDataSet mazdaDataSet = new BarDataSet(mazdaEntries, "Mazda");
                mazdaDataSet.setColor(Color.BLUE);
//Add the data sets to a list
                List<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(sedanDataSet);
                dataSets.add(toyotaDataSet);
                dataSets.add(suzukiDataSet);
                dataSets.add(mazdaDataSet);


//Create a BarData object to hold the data sets
                BarData data = new BarData(dataSets);

//Set the data for the chart
                barChart.setData(data);

//Set the x-axis labels
                final String[] parts = new String[] {"Sedan",  "Toyota","Suzuki","Mazda"};
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(parts));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);

//Set the y-axis labels
                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getAxisRight().setDrawGridLines(false);

//Enable the legend and set its position
                barChart.getLegend().setEnabled(true);
                barChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

//Refresh the chart
                barChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        Log.d(TAG, "GetCarPartCount:engine "+ engineCount);
        Log.d(TAG, "GetCarModelCount: sedan"+ sedanCount);

    }
    public void getCancelledRequests(){

        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("Rejected").child(mechId);
        dbRef.orderByChild("timestamp").startAt(startTimestamp).endAt(endTimestamp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//              ;modelCount=0;
                cancelledCount=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                 cancelledCount=dataSnapshot.getChildrenCount();
textviewCancelled.setText(String.valueOf(cancelledCount) );





                }

                Log.d(TAG, "GetCarPartCount:outside for loop engine "+ engineCount);
                Log.d(TAG, "GetCarModelCount: outside for loop sedan"+ sedanCount);
                // Create a new list of entries for the engine count

//Create a list of BarEntry objects to hold the data for the chart


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    private void saveFragmentAsPDF() {
        // Create a new document
        PdfDocument document = new PdfDocument();

        // Represents a page in the document,
        // in this case the entire fragment layout
        View content = getView();

        // Create a page info with the current page size
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), 2).create();

        // Start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // Draw the view on the page
        content.draw(page.getCanvas());

        // Finish the page
        document.finishPage(page);

        // Create a file to save the PDF
        File pdfFile = new File(Environment.getExternalStorageDirectory(), "MechanicReport.pdf");

        try {
            // Save the document
            document.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), " Download finished ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving  PDF", Toast.LENGTH_SHORT).show();
        }

        // Close the document
        document.close();
    }
    private void MyEventChangeListener(){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference;
        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance()
                .getReference().child("Work").child("mechanics").child(userId);

        databaseReference.orderByChild("timestamp").startAt(startTimestamp).endAt(endTimestamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot request : snapshot.getChildren()){
                    //descriptions.add(request.child("serviceDescription").getValue().toString());
                    request_details requests=request.getValue(request_details.class);
                    requestsArrayList.add(requests);

                }

                myRAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myRAdapter);
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

    @Override
    public void onItemClick(int position) {

    }
    @Override
    public void onResume() {
        super.onResume();
        myRAdapter.notifyDataSetChanged();
    }

}