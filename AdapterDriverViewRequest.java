package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterDriverViewRequest extends RecyclerView.Adapter<AdapterDriverViewRequest.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    DocumentSnapshot documentSnapshot;
    ArrayList<request_details> requestsArrayList;
    String childKey;

    public AdapterDriverViewRequest(Context context, ArrayList<request_details> requestsArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;

        this.requestsArrayList = requestsArrayList;
    }



    @NonNull
    @Override
    public AdapterDriverViewRequest.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.driver_view_requests,parent,false);
        return new MyViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDriverViewRequest.MyViewHolder holder, int position) {

        request_details req=requestsArrayList.get(position);

       holder.textViewStatus.setText(req.getStatus());
       holder.textViewcarPart.setText(req.getCarPart());
       holder.textViewCarModel.setText(req.getCarModel());
       holder.textViewCarDesc.setText(req.getCarProblemDescription());
       holder.textViewdate.setText(req.getDate());
    String status=req.getStatus();
    if(Objects.equals(status, "sent")){
        holder.buttonView.setVisibility(View.VISIBLE);
    }
        if(Objects.equals(status, "accepted")){
            holder.buttonView.setVisibility(View.VISIBLE);
        }
        if(Objects.equals(status, "cancelled")){
            holder.buttonSend.setVisibility(View.VISIBLE);
            holder.buttonView.setVisibility(View.GONE);

        }
        if(Objects.equals(status, "rejected")){
            holder.buttonSend.setVisibility(View.VISIBLE);
            holder.buttonView.setVisibility(View.GONE);

        }
        if(Objects.equals(status, "finished")){
            holder.buttonView.setVisibility(View.VISIBLE);
        }
        if(Objects.equals(status, "not sent")){
            holder.buttonSend.setVisibility(View.VISIBLE);
            holder.buttonView.setVisibility(View.GONE);

        }
        holder.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), MapsActivity.class);

                intent.putExtra("carPart", req.getCarPart());
                intent.putExtra("carModel", req.getCarModel());
                intent.putExtra("date", req.getDate());
                intent.putExtra("currentuserid", req.getMechanicId());


                context.startActivity(intent);
            }
        });
        holder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), DriverSelectMechanic.class);

                intent.putExtra("carPart", req.getCarPart());
                intent.putExtra("carModel", req.getCarModel());
                intent.putExtra("currentuserid", req.getMechanicId());
                intent.putExtra("date", req.getDate());


                context.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return requestsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewCarModel,textViewdate,textViewCarDesc,textViewStatus,textViewcarPart;
        Button buttonSend,buttonView;



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

            textViewdate=itemView.findViewById(R.id.txtview_DVrequestDate);
            textViewCarDesc=itemView.findViewById(R.id.txtview_DVrequestDescription);
            textViewCarModel=itemView.findViewById(R.id.txtview_DVrequestCarModel);
            textViewcarPart=itemView.findViewById(R.id.txtview_DVrequestCarPart);
            textViewStatus=itemView.findViewById(R.id.txtview_DVrequestStatus);
           buttonSend=itemView.findViewById(R.id.btnViewSendRequest);
            buttonView=itemView.findViewById(R.id.btnViewDetailRequest);



        }
    }

}
