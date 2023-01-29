package com.example.myemechanic;

import androidx.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String printDate,Amount,Problem,date,carModel,user;
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
    public  void  getReportDetails(){

        //database
        String  userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Work").child("mechanics");
        reference.orderByChild("mechanicId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Problem= (String) snapshot.child("carPart").getValue();
                Amount= (String) snapshot.child("workPrice").getValue();
                carModel= (String) snapshot.child("carModel").getValue();
                user= (String) snapshot.child("driverFirstName").getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        canvas.drawText("User ",360,830,myPaint);
        canvas.drawText("Amount ",400,830,myPaint);

        canvas.drawLine(180,790,180,840,myPaint);
        canvas.drawLine(680,790,680,840,myPaint);
        canvas.drawLine(880,790,880,840,myPaint);

        canvas.drawText(date ,120,880,myPaint);
        canvas.drawText(Problem ,200,930,myPaint);
        canvas.drawText(carModel ,280,980,myPaint);
        canvas.drawText(user ,360,1030,myPaint);
        canvas.drawText(Amount ,400,1080,myPaint);









        myPdfDocument.finishPage(myPage1);
        File file=new File(ReportsMainActivity.this.getExternalFilesDir("/"),"/myReport.pdf");
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();
    }


}