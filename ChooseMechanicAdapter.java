package com.example.myemechanic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChooseMechanicAdapter extends RecyclerView.Adapter<ChooseMechanicAdapter.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<mechanic_details> mechanicsArrayList;

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
        holder.textViewemail.setText(mech.getMechanicEmail());

    }

    @Override
    public int getItemCount() {
        return mechanicsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewfname,textViewsname,textViewemail;
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

            textViewemail=itemView.findViewById(R.id.txtview_mechanicemail);
            textViewfname=itemView.findViewById(R.id.txtview_mechanicfirstname);
            textViewfname=itemView.findViewById(R.id.txtview_mechanicfirstname);
            textViewsname=itemView.findViewById(R.id.txtview_mechanicsecondname);
            // imageViewmechprofile=itemView.findViewById(R.id.imgview_mechanicprofilephoto);
        }
    }
}
