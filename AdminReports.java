package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
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
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminReports extends AppCompatActivity implements RecyclerViewInterface {
    private BarChart barChart;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private RecyclerView recyclerView;
    private ArrayList<request_details> requestsArrayList;
    private mechanicsAdapter mechanicsAdapter2;
    private AdapterReports myRAdapter,adapter;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;

    TextView textviewpaid,textviewUnpaid,textviewCancelled,textviewrequest,textviewModel,textviewPart,textviewComplete,textViewIncomplete;
    int engineCount,tyresCount,electricalCount,brakesCount;
    int paidCount;
    int unpaidCount;
   int cancelledCount;
    int requestCount;
    long startTimestamp,endTimestamp;
    int completeCount;
    int transactinCount;
    TextView startingdatetextview,enddatetextview,textViewTransaction;
    DatePicker startDatePicker,endDatePicker;
    int modelCount;
    int partCount;
    Button button,buttonDetails;

    int incompleteCount;
    int suzukiCount,toyotaCount,sedanCount,mazdaCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);
        textviewpaid=findViewById(R.id.txtView_paidCountAdmin);
        textviewUnpaid=findViewById(R.id.txtView_unpaidCountAdmin);
        textviewCancelled=findViewById(R.id.txtView_cancelledCountAdmin);
        textViewIncomplete=findViewById(R.id.txtView_incompleteCountAdmin);
        textviewPart=findViewById(R.id.txtView_partCountAdmin);
        textviewModel=findViewById(R.id.txtView_modelCountAdmin);
        textviewrequest=findViewById(R.id.txtView_requestCountAdmin);
        textviewComplete=findViewById(R.id.txtView_finishedCountAdmin);
        startDatePicker = findViewById(R.id.start_date_pickerAdmin);
        endDatePicker = findViewById(R.id.end_date_pickerAdmin);
        startingdatetextview=findViewById(R.id.start_date_inputAdmin);
        enddatetextview=findViewById(R.id.end_date_inputAdmin);
        button=findViewById(R.id.btnFullReportButtonAdmin);
        buttonDetails=findViewById(R.id.btnViewReportButtonAdmin);

        // Create a new line chart

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AdminReports.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    savePdf();
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

                    Toast.makeText(AdminReports.this, "Enter the start date ", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (startTimestamp>endTimestamp ){
                    Toast.makeText(AdminReports.this, "start date cannot be past end date ", Toast.LENGTH_SHORT).show();
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
        barChart = (BarChart) findViewById(R.id.bar_chartAdmin);
        recyclerView=findViewById(R.id.myrecylerviewTransactionsAdmin);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminReports.this));
        requestsArrayList=new ArrayList<request_details>();
        myRAdapter = new AdapterReports(AdminReports.this,requestsArrayList,  this);

        progressDialog=new ProgressDialog(AdminReports.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data....");
        progressDialog.show();
        MyEventChangeListener();
        GetCarPartCount();
        getRequests();
        getCancelledRequests();

    }
    public void GetCarPartCount() {
        String userId= getIntent().getStringExtra("currentuserid");




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Work").child("mechanics").child(userId);
        reference.orderByChild("timestamp").startAt(startTimestamp).endAt(endTimestamp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                engineCount=0;tyresCount=0;electricalCount=0;brakesCount=0;
                suzukiCount=0;toyotaCount=0;sedanCount=0;mazdaCount=0;
                paidCount=0;unpaidCount=0;cancelledCount=0;modelCount=0;completeCount=0;incompleteCount=0;
                transactinCount=0;
                transactinCount= (int) dataSnapshot.getChildrenCount();
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
                int largest = Math.max(Math.max(mazdaCount, toyotaCount), Math.max(sedanCount, suzukiCount));
if (transactinCount==0){
    Toast.makeText(AdminReports.this, "No records ", Toast.LENGTH_SHORT).show();


}
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

        String mechId =  getIntent().getStringExtra("currentuserid");
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("Rejected").child(mechId);
        dbRef.orderByChild("timestamp").startAt(startTimestamp).endAt(endTimestamp).addValueEventListener(new ValueEventListener() {
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
    public void getRequests(){

        String mechId =  getIntent().getStringExtra("currentuserid");
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("DriverRequest").child("Request");
        dbRef.orderByChild("mechanicId").equalTo(mechId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//              ;modelCount=0;
                requestCount=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    requestCount= (int) dataSnapshot.getChildrenCount();
                    textviewrequest.setText(String.valueOf(requestCount) );





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
    private void savePdf() {
        // create a new document
        Document document = new Document();
        try {
            String pdfName = "AdminDriverReport.pdf";
            String path = Environment.getExternalStorageDirectory() + "/PDF/" + pdfName;
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            View rootView = getWindow().getDecorView().getRootView();
//            rootView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = rootView.getDrawingCache();
//            // create a bitmap of the view
////            Bitmap bitmap = Bitmap.createBitmap(view.getWidth()+200, view.getHeight()+200, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            rootView.draw(canvas);
            ScrollView sv = rootView.findViewById(R.id.scroll_view);
            sv.measure(View.MeasureSpec.makeMeasureSpec(sv.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            sv.layout(0, 0, sv.getMeasuredWidth(), sv.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(sv.getWidth(), sv.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            sv.draw(c);

            // add the bitmap to the document
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            addImage(document, byteArray);

            document.close();
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "download error", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImage(Document document, byte[] byteArray) {
        Image image = null;
        try {
            image = Image.getInstance(byteArray);
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.getWidth()) * 100;
            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(image);
        } catch (BadElementException | IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void MyEventChangeListener(){
        String userId= getIntent().getStringExtra("currentuserid");
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
}