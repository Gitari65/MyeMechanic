package com.example.myemechanic;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsMainActivity extends AppCompatActivity {
    Bitmap bmp,scaledbmp;
    int pageWidth=1200;
    String printDate;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_main);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        printDate = dateFormat.format(new Date());
        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.mechanic_icon1);
        scaledbmp=Bitmap.createScaledBitmap(bmp,1200,510,false);

    }
    //pdf create
    public void  createPDF(){
        PdfDocument myPdfDocument=new PdfDocument();

        Paint titlePaint=new Paint();
        Paint myPaint=new Paint();


        PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page myPage1=myPdfDocument.startPage(myPageInfo1);
        Canvas canvas=myPage1.getCanvas();
        canvas.drawBitmap(scaledbmp,0,0,myPaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(70);
        canvas.drawText("e-Mechanic Services",pageWidth/2,270,titlePaint);

        myPaint.setColor(Color.rgb(0,113,188));
        myPaint.setTextSize(30f);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("call: 0748344757",1160,40,myPaint);
        canvas.drawText("email: agitari65@gmail.com",1160,60,myPaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        canvas.drawText("REPORT",pageWidth/2,500,titlePaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("CustomerName: ",20,590,myPaint);

        myPaint.setTextAlign(Paint.Align.RIGHT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Invoice no: ",pageWidth-20,590,myPaint);
        canvas.drawText("Date: "+printDate,pageWidth-20,640,myPaint);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20,780,pageWidth-20,860,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("S1. No: ",40,830,myPaint);
        canvas.drawText("Date: ",120,830,myPaint);
        canvas.drawText("CarProblem: ",200,830,myPaint);
        canvas.drawText("CarModel ",280,830,myPaint);
        canvas.drawText("Mechanic ",360,830,myPaint);
        canvas.drawText("Amount ",400,830,myPaint);

        canvas.drawLine(180,790,180,840,myPaint);
        canvas.drawLine(680,790,680,840,myPaint);
        canvas.drawLine(880,790,880,840,myPaint);


        myPdfDocument.finishPage(myPage1);
        File file=new File(Environment.getExternalStorageDirectory(),"/myReport.pdf");
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        tableLayout = (TableLayout) findViewById(R.id.tableLayoutrequest);
    }

    private void loadData() {
        List<request_details> requests = new ArrayList<request_details>();
        requests.add(new request_details("", "","", "Description for request 1"));
        requests.add(new request_details("", "", "", "Description for request 2"));
        requests.add(new request_details("p03", "Name 3","", "Description for request 3"));

        requests.add(new request_details("", "", "", ""));
        requests.add(new request_details("", "", "", ""));

        //requests.add(new request_details("p04", "Name 4", 11, "Description for request 4", R.drawable.thumb1));
       // requests.add(new request("p05", "Name 5", 5, "Description for request 5", R.drawable.thumb2));
     //   requests.add(new request("p06", "Name 6", 21, "Description for request 6", R.drawable.thumb3));
       // requests.add(new request("p07", "Name 7", 15, "Description for request 7", R.drawable.thumb1));
     //   requests.add(new request("p08", "Name 8", 8, "Description for request 8", R.drawable.thumb2));
     //   requests.add(new request("p09", "Name 9", 32, "Description for request 9", R.drawable.thumb3));

        createColumns();

        fillData(requests);

    }

    private void createColumns() {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Id Column
        TextView textViewId = new TextView(this);
        textViewId.setText("date");
        textViewId.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewId.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewId);

        // Name Column
        TextView textViewName = new TextView(this);
        textViewName.setText("carpart");
        textViewName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewName.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewName);

        // Price Column
        TextView textViewPrice = new TextView(this);
        textViewPrice.setText("carmodel");
        textViewPrice.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewPrice.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewPrice);

        // Photo Column
        TextView textViewPhoto = new TextView(this);
        textViewPhoto.setText("mechanic");
        textViewPhoto.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewPhoto.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewPhoto);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Add Divider
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Id Column
        textViewId = new TextView(this);
        textViewId.setText("-----------");
        textViewId.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewId.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewId);

        // Name Column
        textViewName = new TextView(this);
        textViewName.setText("-----------");

        textViewName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewName.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewName);

        // Price Column
        textViewPrice = new TextView(this);
        textViewPrice.setText("-----------");
        textViewPrice.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewPrice.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewPrice);

        // Photo Column
        textViewPhoto = new TextView(this);
        textViewPhoto.setText("-------------------------");
        textViewPhoto.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewPhoto.setPadding(5, 5, 5, 0);
        tableRow.addView(textViewPhoto);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

    }

    private void fillData(List<request_details> request_detailsList) {
        for (request_details request : request_detailsList) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TableRow currentRow = (TableRow) view;
                    TextView textViewId = (TextView) currentRow.getChildAt(0);
                    String id = textViewId.getText().toString();
                    Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                }
            });

            // Id Column
            TextView textViewId = new TextView(this);
            textViewId.setText(request.getDate());
            textViewId.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewId.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewId);

            // Name Column
            TextView textViewName = new TextView(this);
            textViewName.setText(request.getCarPart());
            textViewName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewName.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewName);

            // Price Column
            TextView textViewPrice = new TextView(this);
            textViewPrice.setText( String.valueOf(request.getCarProblemDescription()));
            textViewPrice.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewPrice.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewPrice);



            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }


}