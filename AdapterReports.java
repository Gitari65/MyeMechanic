package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AdapterReports extends RecyclerView.Adapter<AdapterReports.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    DocumentSnapshot documentSnapshot;
    ArrayList<request_details> requestsArrayList;
    String childKey;

    public AdapterReports(Context context, ArrayList<request_details> requestsArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;

        this.requestsArrayList = requestsArrayList;
    }



    @NonNull
    @Override
    public AdapterReports.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.item_layout_reports,parent,false);
        return new MyViewHolder(v,recyclerViewInterface);
    }
    String current_userId;

    String driverFirstName;
    String driverSecondName;
    String driverEmail;
    String driverPhoneNumber,workChildKey,paymentStatus;
    String carModel,carPart,carProblemDescription,date,responseDate,status;

    @Override
    public void onBindViewHolder(@NonNull AdapterReports.MyViewHolder holder, int position) {

        request_details req=requestsArrayList.get(position);
        carProblemDescription=req.getCarProblemDescription();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        responseDate = dateFormat.format(new Date());
        carModel=req.getCarModel();
        carPart=req.getCarPart();
        status=req.getStatus();
        current_userId=req.getDriversId();
        date= req.getDate();
        holder.textViewProblem.setText(req.getCarPart());
        holder.textViewPaymentstatus.setText(req.getPaymentStatus());
        holder.textViewUser.setText(req.getDriverFirstName());
        holder.textViewAmount.setText(req.getWorkPrice());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(req.getTimestamp());

        String formattedDate = dateFormat.format(cal.getTime());
        holder.textViewDate.setText(formattedDate);








    }



    @Override
    public int getItemCount() {
        return requestsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewProblem,textViewAmount,textViewDate,textViewUser,textViewPaymentstatus;
        Button buttonAccept,buttonCancel,buttonView;

        public MyViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface!=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

            textViewAmount=itemView.findViewById(R.id.txtReportAmount);
            textViewDate=itemView.findViewById(R.id.txtReportDate);
            textViewUser=itemView.findViewById(R.id.txtReportUserName);
            textViewProblem=itemView.findViewById(R.id.txtReportProblem);
            textViewPaymentstatus=itemView.findViewById(R.id.txtReportPaymentstatus);
            //buttonAccept=itemView.findViewById(R.id.btnacceptRequest);

        }
    }

}
