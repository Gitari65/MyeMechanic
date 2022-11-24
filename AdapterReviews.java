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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterReviews extends RecyclerView.Adapter<AdapterReviews.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    DocumentSnapshot documentSnapshot;
    ArrayList<reviews_details> reviewsArrayList;

    public AdapterReviews(Context context, ArrayList<reviews_details> reviewsArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;

        this.reviewsArrayList = reviewsArrayList;
    }



    @NonNull
    @Override
    public AdapterReviews.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.item_view_reviews,parent,false);
        return new MyViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReviews.MyViewHolder holder, int position) {

        reviews_details req=reviewsArrayList.get(position);
       holder.ratingBar.setRating(req.getRating());
        holder.textViewreview.setText(req.getReview());
        holder.textViewdate.setText(req.getDate());
        holder.textViewname.setText(req.getName());




    }

    @Override
    public int getItemCount() {
        return reviewsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewreview,textViewdate,textViewname;
        RatingBar ratingBar;


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

            textViewdate=itemView.findViewById(R.id.tv_view_date);
            textViewname=itemView.findViewById(R.id.tv_driverName);
            textViewreview=itemView.findViewById(R.id.tv_view_descReview);
            ratingBar=itemView.findViewById(R.id.tv_view_Mrating);

        }
    }

}
