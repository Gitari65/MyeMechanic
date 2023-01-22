package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifiedMechanicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends Fragment implements RecyclerViewInterface {

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
    private AdapterReports myRAdapter,adapter;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;
    Button button,button1;
    Bitmap bmp,scaledbmp;
    EditText editText;
    String searchdate;
    int pageWidth=1700;
    String printDate,Amount,Problem,date,carModel,user,phone,total,Payment,Expense,paymentStatus;
    ProgressBar loadingPB;
    // Variables to store the counts
    int count1 ;
    int count2 ;
    int count3 ;
    int count4 ;

    public ReportsFragment() {
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
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();
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

        View view=inflater.inflate(R.layout.fragment_reports, container, false);
        recyclerView=view.findViewById(R.id.myrecylerviewReports);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        button=view.findViewById(R.id.btnPrintButton);
        button1=view.findViewById(R.id.btnFullReportButton);

                editText=view.findViewById(R.id.edtSearchReport);
                searchdate=editText.getText().toString();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchdate=editText.getText().toString();

                        if (Objects.equals(searchdate, "")){
                            editText.setError("input date");
                            editText.requestFocus();
                            return;
                        }

                    getReportDetails();
                    }
                });
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreateReport();
                    }
                });


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        printDate = dateFormat.format(new Date());
        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.mechanic_icon1);
        scaledbmp=Bitmap.createScaledBitmap(bmp,1200,510,false);


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

    @Override
    public void onStart() {
        super.onStart();
        myRAdapter.notifyDataSetChanged();
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    private void MyEventChangeListener(){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference;
        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance()
                .getReference().child("DriverRequest").child("MechanicWork")
        ;

        databaseReference.orderByChild("mechanicId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void onResume() {
        super.onResume();
        myRAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myRAdapter.notifyDataSetChanged();
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
    //pdf create
    public void  createPDF(){
        PdfDocument myPdfDocument=new PdfDocument();

        Paint titlePaint=new Paint();
        Paint myPaint=new Paint();


        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1700,2010,1).create();
        PdfDocument.Page myPage1=myPdfDocument.startPage(myPageInfo1);
        Canvas canvas=myPage1.getCanvas();
        canvas.drawBitmap(scaledbmp,150,50,myPaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(70);
        canvas.drawText("e-Mechanic Services",pageWidth/2,600,titlePaint);

        myPaint.setColor(Color.rgb(0,113,188));
        myPaint.setTextSize(30f);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("call: 0748344757",1600,40,myPaint);
        canvas.drawText("email: agitari65@gmail.com",1600,60,myPaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        canvas.drawText("REPORT",pageWidth/2,700,titlePaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("CustomerName: ",20,700,myPaint);

        myPaint.setTextAlign(Paint.Align.RIGHT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Invoice no: ",pageWidth-20,700,myPaint);
        canvas.drawText("Date: "+printDate,pageWidth-20,740,myPaint);
        canvas.drawText("Margins Kshs/=: "+total,pageWidth-20,1100,myPaint);


        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20,780,pageWidth-20,860,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("Date  Time ",40,830,myPaint);
        canvas.drawText("CarProblem",320,830,myPaint);
        canvas.drawText("CarModel ",525,830,myPaint);
        canvas.drawText("Customer ",700,830,myPaint);
        canvas.drawText("Amount ",890,830,myPaint);
        canvas.drawText("Payment ",1055,830,myPaint);
        canvas.drawText("Expense Kshs/=",1230,830,myPaint);

        canvas.drawText("Status",1505,830,myPaint);


        canvas.drawLine(300,790,300,840,myPaint);
        canvas.drawLine(515,790,515,840,myPaint);
        canvas.drawLine(690,790,690,840,myPaint);
        canvas.drawLine(880,790,880,840,myPaint);
        canvas.drawLine(1050,790,1050,840,myPaint);
        canvas.drawLine(1220,790,1220,840,myPaint);
        canvas.drawLine(1500,790,1500,840,myPaint);

        canvas.drawText("| "+date ,0,930,myPaint);
        canvas.drawText("| "+Problem ,350,930,myPaint);
        canvas.drawText("| "+carModel ,540,930,myPaint);
        canvas.drawText("| "+user ,700,930,myPaint);
        canvas.drawText("| "+phone ,700,970,myPaint);
        canvas.drawText("| "+ Amount ,890,930,myPaint);
        canvas.drawText("| "+ Payment ,1050,930,myPaint);
        canvas.drawText("| "+ Expense ,1230,930,myPaint);
        canvas.drawText("| "+ paymentStatus ,1505,930,myPaint);





        myPdfDocument.finishPage(myPage1);
        File file=new File(requireActivity().getExternalFilesDir("/"),"/myReport.pdf");
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(getActivity(), " Downloading... ",Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), " Downloaded",Toast.LENGTH_SHORT).show();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();
    }
    public  void  getReportDetails(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("MechanicWork");
        Query userQuery = additionalUserInfoRef.orderByChild("responseDate").equalTo(searchdate);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Problem= (String) snapshot.child("workProblem").getValue();
                            Amount= (String) snapshot.child("workPrice").getValue();
                            carModel= (String) snapshot.child("carModel").getValue();
                            user= (String) snapshot.child("driverFirstName").getValue();
                            phone = (String) snapshot.child("driverPhoneNumber").getValue();

                            date= (String) snapshot.child("date").getValue();
                            Payment= (String) snapshot.child("paymentMethod").getValue();
                            Expense= (String) snapshot.child("workExpense").getValue();
                            paymentStatus=(String) snapshot.child("paymentStatus").getValue();

                            if(Amount==null& Expense==null){
                                int payment=0;
                                int expense=0;
                                total="0";
                            }
                            if(Amount==null){
                                int payment=0;
                            }

                            if(Amount!=null& Expense!=null){
                                int payment= Integer.parseInt(Amount);
                                int expense= Integer.parseInt(Expense);
                                int margin=payment-expense;
                                total= String.valueOf(margin);
                            }



                            createPDF();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery.addListenerForSingleValueEvent(valueEventListener);



    }
    public void CreateReport(){

String userID=FirebaseAuth.getInstance().getCurrentUser().getUid();

// Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("DriverRequest").child("MechanicWork");

// Retrieve data from the database
        ref.orderByChild("mechanicId").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    // Reference to the specific parent node in the database
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverRequest").child("MechanicWork");
                      count1=0;

// Query to find all children that contain the first value
                    Query query1 = ref.orderByChild("carPart").equalTo("Engine");
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the number of children that contain the first value
                            count1 = (int) dataSnapshot.getChildrenCount();
                            Chunk engine = new Chunk("Engine", nameFont);
                            Chunk engineDb = new Chunk((char) count1);

                            Phrase dataPhrase5 = new Phrase();
                            dataPhrase5.add(engine);
                            dataPhrase5.add(":");
                            dataPhrase5.add(engineDb);

                            Paragraph dataParagraph5 = new Paragraph();
                            dataParagraph5.add(dataPhrase5);

// Add the paragraph to the PDF
                            try {
                                doc.add(dataParagraph5);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Query cancelled", databaseError.toException());
                        }
                    });

// Query to find all children that contain the second value
                    Query query2 = ref.orderByChild("carPart").equalTo("Tyres");
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the number of children that contain the second value
                            count2 = (int) dataSnapshot.getChildrenCount();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Query cancelled", databaseError.toException());
                        }
                    });

// Query to find all children that contain the third value
                    Query query3 = ref.orderByChild("paymentMethod").equalTo("Mpesa");
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the number of children that contain the third value
                            count3 = (int) dataSnapshot.getChildrenCount();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Query cancelled", databaseError.toException());
                        }
                    });
                    // Query to find all children that contain the fourth value
                    Query query4 = ref.orderByChild("paymentMethod").equalTo("Cash");
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the number of children that contain the third value
                            count4 = (int) dataSnapshot.getChildrenCount();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Query cancelled", databaseError.toException());
                        }
                    });

// Print the count1
                    Chunk engine = new Chunk("Engine", nameFont);
                    Chunk engineDb = new Chunk((char) count1);
                    Phrase dataPhrase5 = new Phrase();
                    dataPhrase5.add(engine);
                    dataPhrase5.add(":");
                    dataPhrase5.add(engineDb);

                    Paragraph dataParagraph5 = new Paragraph();
                    dataParagraph5.add(dataPhrase5);

// Add the paragraph to the PDF
                        doc.add(dataParagraph5);


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

}