package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.Myholder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;
    public List<Technician> technicianList;
    private HashMap<String, String> lastMessageMap;
    public AdapterChatList(Context context, List<Technician> technicianList) {
        this.context = context;
        this.technicianList = technicianList;
        lastMessageMap = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
    }



    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chatlist, parent, false);
        List<Technician> technicianList=new ArrayList<>();
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, final int position) {

        final String hisuid = technicianList.get(position).getUserid();
       // String userimage = technicianList.get(position).getProfilePhotoUrl();
        String username = technicianList.get(position).getFirstNamee();
        String lastmess = lastMessageMap.get(hisuid);
        holder.name.setText(username);
        holder.block.setImageResource(R.drawable.ic_unblock);
      //  Picasso.get().load(userImage).placeholder(R.drawable.ic_default_img).into(myHolder.mAvatarIv);

        // if no last message then Hide the layout
        if (lastmess == null || lastmess.equals("default")) {
            holder.lastmessage.setVisibility(View.GONE);
        } else {
            holder.lastmessage.setVisibility(View.VISIBLE);
            holder.lastmessage.setText(lastmess);
        }
        try {
            // loading profile pic of user
           // Glide.with(context).load(userimage).into(holder.profile);
        } catch (Exception e) {

        }

        // redirecting to chat activity on item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainChatActivity.class);

                // putting uid of user in extras
                intent.putExtra("uid", hisuid);
                context.startActivity(intent);
            }
        });

    }

    // setting last message sent by users.
    public void setlastMessageMap(String userId, String lastmessage) {
        lastMessageMap.put(userId, lastmessage);
    }

    @Override
    public int getItemCount() {
      // ArrayList <Technician> technicianList=new ArrayList<Technician>();
        //Log.d(TAG, "getItemCount: items..."+technicianList.get(0));
            return technicianList.size();
    }

    class Myholder extends RecyclerView.ViewHolder {
        ImageView profile, status, block, seen;
        TextView name, lastmessage;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profileimage);
            status = itemView.findViewById(R.id.onlinestatus);
            name = itemView.findViewById(R.id.nameonline);
            lastmessage = itemView.findViewById(R.id.lastmessge);
            block = itemView.findViewById(R.id.blocking);
            seen = itemView.findViewById(R.id.seen);
        }
    }
}