package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverReportFragment extends Fragment implements RecyclerViewInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private Button button,button1;
    private ArrayList<request_details> requestsArrayList;
    private mechanicsAdapter mechanicsAdapter2;
    private AdapterReports myRAdapter,adapter;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;

    public DriverReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverReportFragment newInstance(String param1, String param2) {
        DriverReportFragment fragment = new DriverReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    int engineCount,tyresCount,electricalCount,brakesCount;
    int paidCount;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    int unpaidCount;
   int cancelledCount;
    int modelCount;
    int partCount;
    TextView textviewpaid,textviewUnpaid,textviewCancelled,textviewModel,textviewPart,textviewComplete,textViewIncomplete;
    int completeCount;
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
        View view=inflater.inflate(R.layout.fragment_driver_report, container, false);

        textviewpaid=view.findViewById(R.id.txtView_paidCountD);
        textviewUnpaid=view.findViewById(R.id.txtView_unpaidCountD);
        textviewCancelled=view.findViewById(R.id.txtView_cancelledCountD);
        textViewIncomplete=view.findViewById(R.id.txtView_incompleteCountD);
        textviewPart=view.findViewById(R.id.txtView_partCountD);
        textviewModel=view.findViewById(R.id.txtView_modelCountD);
        textviewComplete=view.findViewById(R.id.txtView_finishedCountD);
        button=view.findViewById(R.id.btnFullReportButtonD);
        // Create a new line chart
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
        recyclerView=view.findViewById(R.id.myrecylerviewReportsDriver);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsArrayList=new ArrayList<request_details>();
        myRAdapter = new AdapterReports(getContext(),requestsArrayList,  this);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();
        MyEventChangeListener();
getCancelledRequests();
GetCarPartCount();

        return view;
    }


    private void MyEventChangeListener(){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference;
        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance()
                .getReference().child("Work").child("drivers").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
//    public void CreateReport(){
//
//        String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//// Get a reference to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference().child("DriverRequest").child("MechanicWork");
//
//// Retrieve data from the database
//        ref.orderByChild("driversId").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//                // Create a new PDF document
//                Document doc = new Document(new Rectangle(PageSize.A4.getWidth() + 200, PageSize.A4.getHeight()));
//                try {
//
//                    // Create a file in the internal storage directory
//                    File file=new File(requireActivity().getExternalFilesDir("/"),"/output.pdf");
//
//                    try {
//                        PdfWriter.getInstance(doc, new FileOutputStream(file));
//                        doc.open();
//
//                        Toast.makeText(getContext(), " Downloading... ",Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getContext(), " Downloaded",Toast.LENGTH_SHORT).show();
//
//
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    // Create a new PdfWriter for the file
//                    // Create a new PdfWriter for the file
//
//                    Font nameFont3 = new Font();
//                    nameFont3.setSize(19);
//                    nameFont3.setStyle(Font.BOLD);
//                    Phrase dataPhrase3 = new Phrase();
//                    Paragraph dataParagraph3 = new Paragraph();
//                    Chunk titleChunk3 = new Chunk("My-Mechanic Service Reports", nameFont3);
//                    dataParagraph3.setSpacingAfter(10);
//                    dataPhrase3.add(titleChunk3);
//                    dataParagraph3.add(dataPhrase3);
//
//// Add the paragraph to the PDF
//                    doc.add(dataParagraph3);
//                    // Create a new font for the name
//                    Font nameFont = new Font();
//                    nameFont.setSize(12);
//                    nameFont.setStyle(Font.BOLD);
//
//                    Chunk dateChunk1 = new Chunk("DATE", nameFont);
//                    Chunk timeChunk1 = new Chunk("TIME", nameFont);
//
//                    Chunk modelChunk1 = new Chunk("MODEL", nameFont);
//                    Chunk partChunk1 = new Chunk("PART", nameFont);
//                    Chunk problemChunk1 = new Chunk("PROBLEM", nameFont);
//                    Chunk priceChunk1 = new Chunk("PRICE", nameFont);
//                    Chunk phoneChunk1 = new Chunk("DRIVER", nameFont);
//                    Chunk methodChunk1 = new Chunk("PAYMENT", nameFont);
//                    Chunk statusChunk1 = new Chunk("STATUS", nameFont);
//
//                    Phrase dataPhrase1 = new Phrase();
//                    dataPhrase1.add(dateChunk1);
//                    dataPhrase1.add("         ");  // Add some space between the name and address
//                    dataPhrase1.add(timeChunk1);
//                    dataPhrase1.add("        ");
//                    dataPhrase1.add(modelChunk1);
//                    dataPhrase1.add("         ");  // Add some space between the name and address
//                    dataPhrase1.add(partChunk1);
//                    dataPhrase1.add("          ");  // Add some space between the name and address
//                    dataPhrase1.add(problemChunk1);
//                    dataPhrase1.add("     ");  // Add some space between the name and address
//                    dataPhrase1.add(priceChunk1);
//                    dataPhrase1.add("       ");  // Add some space between the name and address
//                    dataPhrase1.add(phoneChunk1);
//                    dataPhrase1.add("        ");  // Add some space between the name and address
//                    dataPhrase1.add(methodChunk1);
//                    dataPhrase1.add("      ");  // Add some space between the name and address
//                    dataPhrase1.add(statusChunk1);
//                    // Create a new paragraph and add the phrase to it
//                    Paragraph dataParagraph1 = new Paragraph();
//                    dataParagraph1.add(dataPhrase1);
//                    doc.add(dataParagraph1);
//                    if (snapshot.hasChildren()) {
//
//                        for (DataSnapshot child : snapshot.getChildren()) {
//                            // Create a new phrase and add the chunks to it
//                            Phrase dataPhrase = new Phrase();
//
//                            String date = child.child("date").exists() ? child.child("date").getValue(String.class) : "      N/A       ";
//                            String problem = child.child("workProblem").exists() ? child.child("workProblem").getValue(String.class) : "  N/A   ";
//                            String price = child.child("workPrice").exists() ? child.child("workPrice").getValue(String.class) : "N/A";
//                            String phone = child.child("driverPhoneNumber").exists() ? child.child("driverPhoneNumber").getValue(String.class) : "   N/A  ";
//                            String paymentMethod = child.child("paymentMethod").exists() ? child.child("paymentMethod").getValue(String.class) : " N/A ";
//                            String paymentStatus = child.child("paymentStatus").exists() ? child.child("paymentStatus").getValue(String.class) : " N/A ";
//                            String address = child.child("carPart").exists() ? child.child("carPart").getValue(String.class) : "  N/A  ";
//
//                            String name = child.child("carModel").exists() ? child.child("carModel").getValue(String.class) : "  N/A  ";
////                        String name = child.child("carModel").getValue(String.class);
//
//
//
//
//
//                            // Create a new chunk with the name
//                            Chunk nameChunk = new Chunk(name);
//                            Chunk dateChunk = new Chunk(date);
//
//                            Chunk problemChunk = new Chunk(problem);
//
//                            Chunk priceChunk = new Chunk(price);
//                            Chunk phoneChunk = new Chunk(phone);
//                            Chunk methodChunk = new Chunk(paymentMethod);
//                            Chunk statusChunk = new Chunk(paymentStatus);
//// Create a new chunk with the address
//                            Chunk addressChunk = new Chunk(address);
//
//// Create a new phrase and add the chunks to it
////    Phrase dataPhrase = new Phrase();
//
//                            dataPhrase.add(dateChunk);
//                            dataPhrase.add("      ");  // Add some space between the name and address
//                            dataPhrase.add(nameChunk);
//                            dataPhrase.add("        ");  // Add some space between the name and address
//                            dataPhrase.add(addressChunk);
//                            dataPhrase.add("        ");  // Add some space between the name and address
//                            dataPhrase.add(problemChunk);
//                            dataPhrase.add("           ");  // Add some space between the name and address
//                            dataPhrase.add(priceChunk);
//                            dataPhrase.add("           ");  // Add some space between the name and address
//                            dataPhrase.add(phoneChunk);
//                            dataPhrase.add("           ");  // Add some space between the name and address
//                            dataPhrase.add(methodChunk);
//                            dataPhrase.add("          ");  // Add some space between the name and address
//                            dataPhrase.add(statusChunk);
//
//// Create a new paragraph and add the phrase to it
//                            Paragraph dataParagraph = new Paragraph();
//                            dataParagraph.add(dataPhrase);
//
//// Add the paragraph to the PDF
//                            doc.add(dataParagraph);
//
//
//
//
//
//                        }}else {
//                        // If the snapshot has no children, add a message to the PDF
//                        doc.add(new Paragraph("No data available"));
//                    }
//                    doc.close();
//                    // Add data to the PDF
//
//
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.d(TAG, "onDataChange: error pdf ");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Handle error
//                Log.d(TAG, "onDatabaseError: error pdf ");
//
//            }
//        });
//
//    }
    private void saveFragmentAsPDF() {
        // Create a new document
        PdfDocument document = new PdfDocument();

        // Represents a page in the document,
        // in this case the entire fragment layout
        View rootView = getView();

        // Create a page info with the current page size
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(rootView.getWidth(), rootView.getHeight(), 2).create();

        // Start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // Draw the view on the page
        rootView.draw(page.getCanvas());

        // Finish the page
        document.finishPage(page);

        // Create a file to save the PDF
        File pdfFile = new File(Environment.getExternalStorageDirectory(), "eMechanicDriverReport.pdf");

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
    public void GetCarPartCount() {
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Work").child("drivers").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                engineCount=0;tyresCount=0;electricalCount=0;brakesCount=0;
                suzukiCount=0;toyotaCount=0;sedanCount=0;mazdaCount=0;
                paidCount=0;unpaidCount=0;cancelledCount=0;modelCount=0;completeCount=0;incompleteCount=0;

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
                textviewpaid.setText(String.valueOf(paidCount));
                textviewUnpaid.setText(String.valueOf(unpaidCount));
                textviewComplete.setText(String.valueOf(paidCount));
                textViewIncomplete.setText(String.valueOf(unpaidCount-paidCount));
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







            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        Log.d(TAG, "GetCarPartCount:engine "+ engineCount);
        Log.d(TAG, "GetCarModelCount: sedan"+ sedanCount);

    }
    @Override
    public void onItemClick(int position) {

    }
    public void getCancelledRequests(){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("Cancelled").child(userId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//              ;modelCount=0;
                cancelledCount=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cancelledCount= (int) dataSnapshot.getChildrenCount();
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
}