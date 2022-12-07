package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
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

import java.util.ArrayList;

public class ChooseMechanicAdapter extends RecyclerView.Adapter<ChooseMechanicAdapter.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<mechanic_details> mechanicsArrayList;
    String current_userId;
    String rating;
    double Ratings;


    public ChooseMechanicAdapter(Context context, ArrayList<mechanic_details> mechanicsArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;

        this.mechanicsArrayList = mechanicsArrayList;
    }

    @NonNull
    @Override
    public ChooseMechanicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.mechanic_item,parent,false);
        return new MyViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseMechanicAdapter.MyViewHolder holder, int position) {

        mechanic_details mech=mechanicsArrayList.get(position);
        holder.textViewfname.setText(mech.getFirstName());
        holder.textViewsname.setText(mech.getSecondName());
        current_userId=mech.getCurrent_userId();
        if(mech.getMechanicRating()!=null)
        {

            Ratings=mech.getMechanicRating();
            // holder.ratingBar.setRating((float) rating);

            holder.ratingBar.setRating((float) Ratings);


        }



    }

    @Override
    public int getItemCount() {
        return mechanicsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewfname,textViewsname,textViewemail;
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
            // imageViewmechprofile=itemView.findViewById(R.id.imgview_mechanicprofilephoto);
        }
    }


}

