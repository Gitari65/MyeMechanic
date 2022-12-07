package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.Rating;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterTechnicians extends RecyclerView.Adapter<AdapterTechnicians.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<Technician> technicianArrayList;
    String current_userId;
    double Ratings;
    String rating;


    public AdapterTechnicians(Context context, ArrayList<Technician> technicianArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;

        this.technicianArrayList = technicianArrayList;
    }

    @NonNull
    @Override
    public AdapterTechnicians.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.mechanic_item,parent,false);
        return new MyViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTechnicians.MyViewHolder holder, int position) {

        Technician tech=technicianArrayList.get(position);
        holder.textViewfname.setText(tech.getFirstNamee());
        holder.textViewsname.setText(tech.getSecondNamee());
        current_userId=tech.getUserid();





    }

    @Override
    public int getItemCount() {
        return technicianArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewfname,textViewsname;
        RatingBar ratingBar;
        ImageView imageViewmechprofile;
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

            ratingBar=itemView.findViewById(R.id.total_star_ratingMecahnic);
            textViewfname=itemView.findViewById(R.id.txtview_mechanicfirstname);
            textViewfname=itemView.findViewById(R.id.txtview_mechanicfirstname);
            textViewsname=itemView.findViewById(R.id.txtview_mechanicsecondname);
            imageViewmechprofile=itemView.findViewById(R.id.imgviewMech_profilepic);


        }
    }


}
