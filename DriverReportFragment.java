package com.example.myemechanic;

import static android.content.ContentValues.TAG;

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
import android.widget.Button;
import android.widget.Toast;

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
import java.util.ArrayList;

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
        recyclerView=view.findViewById(R.id.myrecylerviewReportsD);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        button1=view.findViewById(R.id.btnFullReportButtonD);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateReport();
            }
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsArrayList=new ArrayList<request_details>();
        myRAdapter = new AdapterReports(getContext(),requestsArrayList,  this);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();
        MyEventChangeListener();

        return view;
    }
    private void MyEventChangeListener(){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference;
        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance()
                .getReference().child("DriverRequest").child("MechanicWork")
        ;

        databaseReference.orderByChild("driversId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void CreateReport(){

        String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();

// Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("DriverRequest").child("MechanicWork");

// Retrieve data from the database
        ref.orderByChild("driversId").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                // Create a new PDF document
                Document doc = new Document(new Rectangle(PageSize.A4.getWidth() + 200, PageSize.A4.getHeight()));
                try {

                    // Create a file in the internal storage directory
                    File file=new File(requireActivity().getExternalFilesDir("/"),"/output.pdf");

                    try {
                        PdfWriter.getInstance(doc, new FileOutputStream(file));
                        doc.open();

                        Toast.makeText(getContext(), " Downloading... ",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), " Downloaded",Toast.LENGTH_SHORT).show();



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // Create a new PdfWriter for the file
                    // Create a new PdfWriter for the file

                    Font nameFont3 = new Font();
                    nameFont3.setSize(19);
                    nameFont3.setStyle(Font.BOLD);
                    Phrase dataPhrase3 = new Phrase();
                    Paragraph dataParagraph3 = new Paragraph();
                    Chunk titleChunk3 = new Chunk("My-Mechanic Service Reports", nameFont3);
                    dataParagraph3.setSpacingAfter(10);
                    dataPhrase3.add(titleChunk3);
                    dataParagraph3.add(dataPhrase3);

// Add the paragraph to the PDF
                    doc.add(dataParagraph3);
                    // Create a new font for the name
                    Font nameFont = new Font();
                    nameFont.setSize(12);
                    nameFont.setStyle(Font.BOLD);

                    Chunk dateChunk1 = new Chunk("DATE", nameFont);
                    Chunk timeChunk1 = new Chunk("TIME", nameFont);

                    Chunk modelChunk1 = new Chunk("MODEL", nameFont);
                    Chunk partChunk1 = new Chunk("PART", nameFont);
                    Chunk problemChunk1 = new Chunk("PROBLEM", nameFont);
                    Chunk priceChunk1 = new Chunk("PRICE", nameFont);
                    Chunk phoneChunk1 = new Chunk("DRIVER", nameFont);
                    Chunk methodChunk1 = new Chunk("PAYMENT", nameFont);
                    Chunk statusChunk1 = new Chunk("STATUS", nameFont);

                    Phrase dataPhrase1 = new Phrase();
                    dataPhrase1.add(dateChunk1);
                    dataPhrase1.add("         ");  // Add some space between the name and address
                    dataPhrase1.add(timeChunk1);
                    dataPhrase1.add("        ");
                    dataPhrase1.add(modelChunk1);
                    dataPhrase1.add("         ");  // Add some space between the name and address
                    dataPhrase1.add(partChunk1);
                    dataPhrase1.add("          ");  // Add some space between the name and address
                    dataPhrase1.add(problemChunk1);
                    dataPhrase1.add("     ");  // Add some space between the name and address
                    dataPhrase1.add(priceChunk1);
                    dataPhrase1.add("       ");  // Add some space between the name and address
                    dataPhrase1.add(phoneChunk1);
                    dataPhrase1.add("        ");  // Add some space between the name and address
                    dataPhrase1.add(methodChunk1);
                    dataPhrase1.add("      ");  // Add some space between the name and address
                    dataPhrase1.add(statusChunk1);
                    // Create a new paragraph and add the phrase to it
                    Paragraph dataParagraph1 = new Paragraph();
                    dataParagraph1.add(dataPhrase1);
                    doc.add(dataParagraph1);
                    if (snapshot.hasChildren()) {

                        for (DataSnapshot child : snapshot.getChildren()) {
                            // Create a new phrase and add the chunks to it
                            Phrase dataPhrase = new Phrase();

                            String date = child.child("date").exists() ? child.child("date").getValue(String.class) : "      N/A       ";
                            String problem = child.child("workProblem").exists() ? child.child("workProblem").getValue(String.class) : "  N/A   ";
                            String price = child.child("workPrice").exists() ? child.child("workPrice").getValue(String.class) : "N/A";
                            String phone = child.child("driverPhoneNumber").exists() ? child.child("driverPhoneNumber").getValue(String.class) : "   N/A  ";
                            String paymentMethod = child.child("paymentMethod").exists() ? child.child("paymentMethod").getValue(String.class) : " N/A ";
                            String paymentStatus = child.child("paymentStatus").exists() ? child.child("paymentStatus").getValue(String.class) : " N/A ";
                            String address = child.child("carPart").exists() ? child.child("carPart").getValue(String.class) : "  N/A  ";

                            String name = child.child("carModel").exists() ? child.child("carModel").getValue(String.class) : "  N/A  ";
//                        String name = child.child("carModel").getValue(String.class);





                            // Create a new chunk with the name
                            Chunk nameChunk = new Chunk(name);
                            Chunk dateChunk = new Chunk(date);

                            Chunk problemChunk = new Chunk(problem);

                            Chunk priceChunk = new Chunk(price);
                            Chunk phoneChunk = new Chunk(phone);
                            Chunk methodChunk = new Chunk(paymentMethod);
                            Chunk statusChunk = new Chunk(paymentStatus);
// Create a new chunk with the address
                            Chunk addressChunk = new Chunk(address);

// Create a new phrase and add the chunks to it
//    Phrase dataPhrase = new Phrase();

                            dataPhrase.add(dateChunk);
                            dataPhrase.add("      ");  // Add some space between the name and address
                            dataPhrase.add(nameChunk);
                            dataPhrase.add("        ");  // Add some space between the name and address
                            dataPhrase.add(addressChunk);
                            dataPhrase.add("        ");  // Add some space between the name and address
                            dataPhrase.add(problemChunk);
                            dataPhrase.add("           ");  // Add some space between the name and address
                            dataPhrase.add(priceChunk);
                            dataPhrase.add("           ");  // Add some space between the name and address
                            dataPhrase.add(phoneChunk);
                            dataPhrase.add("           ");  // Add some space between the name and address
                            dataPhrase.add(methodChunk);
                            dataPhrase.add("          ");  // Add some space between the name and address
                            dataPhrase.add(statusChunk);

// Create a new paragraph and add the phrase to it
                            Paragraph dataParagraph = new Paragraph();
                            dataParagraph.add(dataPhrase);

// Add the paragraph to the PDF
                            doc.add(dataParagraph);





                        }}else {
                        // If the snapshot has no children, add a message to the PDF
                        doc.add(new Paragraph("No data available"));
                    }
                    doc.close();
                    // Add data to the PDF




                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onDataChange: error pdf ");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                Log.d(TAG, "onDatabaseError: error pdf ");

            }
        });

    }

    @Override
    public void onItemClick(int position) {

    }
}