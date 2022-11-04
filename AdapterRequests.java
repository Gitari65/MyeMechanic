package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<request_details> requestsArrayList;

    public AdapterRequests(Context context, ArrayList<request_details> requestsArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;

        this.requestsArrayList = requestsArrayList;
    }



    @NonNull
    @Override
    public AdapterRequests.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.request_layout,parent,false);
        return new MyViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRequests.MyViewHolder holder, int position) {

        request_details req=requestsArrayList.get(position);
        holder.textViewcarPart.setText(req.getCarPart());
        holder.textViewcarProblemDesc.setText(req.getCarProblemDescription());
        holder.textViewcarModel.setText(req.getCarModel());
        holder.textViewrequestDate.setText(req.getDate());
        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accepting request
String current_userId=req.getDriversId();
                FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                db3.collection("driverRequest").document(current_userId).update("status","accepted")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: spinner stored");

                                Toast.makeText(context.getApplicationContext(), "request accepted  successfully",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context.getApplicationContext(), MechanicSelectDriver.class);
                                intent.putExtra("driversId", req.getDriversId());
                                // intent.putExtra("carModel", requestsArrayList.get(position).getCarModel());
                                //  intent.putExtra("carProblemDescription", requestsArrayList.get(position).getCarProblemDescription());

                                context.startActivity(intent);
                            }
                        });
            }
        });
        holder.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelling request
                String current_userId=req.getDriversId();
                FirebaseFirestore db3 = FirebaseFirestore.getInstance();
                db3.collection("driverRequest").document(current_userId).
                        update("mechanicId", "","status","rejected")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: spinner stored");

                                Toast.makeText(context.getApplicationContext(), "request cancelled xx",Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });



    }

    @Override
    public int getItemCount() {
        return requestsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewcarPart,textViewcarModel,textViewcarProblemDesc,textViewrequestDate;
        Button buttonAccept,buttonCancel;

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

            textViewcarModel=itemView.findViewById(R.id.txtview_requestCarModel);
            textViewcarProblemDesc=itemView.findViewById(R.id.txtview_requestCarProblemDesc);
            textViewcarPart=itemView.findViewById(R.id.txtview_requestCarPart);
            textViewrequestDate=itemView.findViewById(R.id.txt_requestDate);
            buttonAccept=itemView.findViewById(R.id.btnacceptRequest);
            buttonCancel=itemView.findViewById(R.id.btncancelRequest);
        }
    }
}
