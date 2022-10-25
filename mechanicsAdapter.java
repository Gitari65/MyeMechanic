package com.example.myemechanic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class mechanicsAdapter extends RecyclerView.Adapter<mechanicsAdapter.ViewHolder> {
    // creating variables for our ArrayList and context


    private ArrayList<mechanic_details> mechanicsArrayList;
    private Context context;

    // creating constructor for our adapter class
    public mechanicsAdapter(ArrayList<mechanic_details> mechanicsArrayList, Context context) {
        this.mechanicsArrayList = mechanicsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public mechanicsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.mechanic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mechanicsAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return mechanicsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.

        }
    }
}

