package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainReportFragment newInstance(String param1, String param2) {
        MainReportFragment fragment = new MainReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    DatePicker startDatePicker,endDatePicker;
    long startTimestamp,endTimestamp;
    int count1,count2,count3,count4;
     TextView selectedDateTextView,selectedDateTextView1;
     Button  button;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_main_report, container, false);
         startDatePicker = view.findViewById(R.id.start_date_picker);
         endDatePicker = view.findViewById(R.id.end_date_picker);
        DatePicker datePicker = view.findViewById(R.id.start_date_picker);
         selectedDateTextView = view.findViewById(R.id.txtview_startdate);
button=view.findViewById(R.id.btnDownloadReport);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    selectedDateTextView.setText(sdf.format(calendar.getTime()));
                }
            });
        }
        DatePicker datePicker1 = view.findViewById(R.id.end_date_picker);
        selectedDateTextView1 = view.findViewById(R.id.txtview_enddate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker1.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker1, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    selectedDateTextView1.setText(sdf.format(calendar.getTime()));
                }
            });
        }


        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth());
        startTimestamp = startCalendar.getTimeInMillis();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(endDatePicker.getYear(), endDatePicker.getMonth(), endDatePicker.getDayOfMonth());
       endTimestamp = endCalendar.getTimeInMillis();

        selectedDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(datePicker.getVisibility() == View.VISIBLE){
                    datePicker.setVisibility(View.GONE);
                } else {
                    datePicker.setVisibility(View.VISIBLE);
                }
            }
        });
        selectedDateTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(datePicker1.getVisibility() == View.VISIBLE){
                    datePicker1.setVisibility(View.GONE);
                } else {
                    datePicker1.setVisibility(View.VISIBLE);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateReport();
            }
        });

        return view;
    }
    public void CreateReport(){
        if(startTimestamp == 0 || endTimestamp == 0  ){
            Toast.makeText(getContext(), " enter start date ",Toast.LENGTH_SHORT).show();

        }
        else{




        String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();

// Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Work").child("mechanics").child(userID);
    Query query = ref.orderByChild("timestamp").startAt(startTimestamp).endAt(endTimestamp);


        query.addValueEventListener(new ValueEventListener() {
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
                    Chunk priceChunk1 = new Chunk("AMOUNT", nameFont);
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
                    dataPhrase1.add(" ");  // Add some space between the name and address
                    dataPhrase1.add(priceChunk1);
                    dataPhrase1.add("   ");  // Add some space between the name and address
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
                    count2=0;
                    count3=0;
                    count4=0;

// Create a CountDownLatch with the number of queries
                    CountDownLatch latch = new CountDownLatch(4);
// Query to find all children that contain the first value
                    Query query1 = ref.orderByChild("carPart").equalTo("Engine");
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotE) {
                            // Get the number of children that contain the first value
                            count1 = (int) dataSnapshotE.getChildrenCount();
                            Log.d(TAG, "onDataChange: count1"+ count1);
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
                            try {
                                doc.add(dataParagraph5);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }

                            // Decrement the latch
//                            latch.countDown();
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
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotT) {
                            // Get the number of children that contain the second value
                            count2 = (int) dataSnapshotT.getChildrenCount();
                            Log.d(TAG, "onDataChange: count2"+ count2);
                            // Decrement the latch
//                            latch.countDown();
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
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Get the number of children that contain the third value
                            count3 = (int) dataSnapshot.getChildrenCount();
                            // Decrement the latch
//                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Query cancelled", databaseError.toException());
                        }
                    });
                    // Query to find all children that contain the fourth value
                    Query query4= ref.orderByChild("paymentMethod").equalTo("Cash");
                    query4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Get the number of children that contain the third value
                            count4 = (int) dataSnapshot.getChildrenCount();
                            // Decrement the latch
//                            latch.countDown();
                            doc.close();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Query cancelled", databaseError.toException());
                        }
                    });
// Wait for all queries to complete
//                    latch.await();




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
}