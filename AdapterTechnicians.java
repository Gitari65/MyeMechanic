package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.media.Rating;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

public class AdapterTechnicians extends RecyclerView.Adapter<AdapterTechnicians.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<Technician> technicianArrayList;
    String current_userId,mechanicLat,mechanicLong;
    String Ratings;
    String rating,imageUrl;

    Double driverLat,driverLong;
    Float results;


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
        tech.getCurrentLatitude();
        tech.getCurrentLongitude();


        Picasso.get().load(tech.getProfilePhotoUrl()).placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageViewmechprofile);
        if(tech.getMechanicRating()!=null)
        {

            Ratings=tech.getMechanicRating();
           // Double totals=Double.parseDouble(Ratings);
            // holder.ratingBar.setRating((float) rating);

            holder.ratingBar.setRating(Float.parseFloat(Ratings));

        }


            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference additionalUserInfoRef = rootRef.child("Technicians");
            Query userQuery1 = additionalUserInfoRef.orderByChild("userid").equalTo(userId);
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                        ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                //Timber.tag(TAG).d("onDataChange: driver long%s", dlongitude);
                                //    Timber.tag(TAG).d("onDataChange: driver lat%s", dlatitude);
                                if ( snapshot.child("driverLatitude").getValue()!=null)
                                {
                                    driverLat=Double.parseDouble((String) snapshot.child("driverLatitude").getValue());

                                    driverLong=Double.parseDouble((String) snapshot.child("driverLatitude").getValue());
                                    if (tech.getCurrentLatitude()!=null){
                                        getKmFromLatLong(  driverLat.floatValue(),
                                                driverLong.floatValue(),
                                                Float.parseFloat(tech.getCurrentLatitude()),
                                                Float.parseFloat(tech.getCurrentLongitude()));
                                        holder.textViewDistance.setText(String.valueOf(results));
                                        Timber.tag(TAG).d(": OnBindView :driver lat%s", driverLat);
                                    }

                                }

//                                if( driverLat !=null && driverLong!=null){
//                                    distanceInMiles(  driverLat,
//                                            driverLong,
//                                            Double.parseDouble(tech.getCurrentLatitude()),
//                                            Double.parseDouble(tech.getCurrentLongitude()));
//                                    holder.textViewDistance.setText(String.valueOf(results));
//                                    Timber.tag(TAG).d(": OnBindView :driver lat%s", driverLat);
//                                }





                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userQuery1.addListenerForSingleValueEvent(valueEventListener1);












    }

    @Override
    public int getItemCount() {
        return technicianArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewfname,textViewsname,textViewDistance;
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
            textViewDistance=itemView.findViewById(R.id.textViewDistance);


        }
    }
    public void getDriverLocation(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference additionalUserInfoRef = rootRef.child("Technicians");
        Query userQuery1 = additionalUserInfoRef.orderByChild("userid").equalTo(userId);
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //Timber.tag(TAG).d("onDataChange: driver long%s", dlongitude);
                            //    Timber.tag(TAG).d("onDataChange: driver lat%s", dlatitude);

                                driverLat=Double.parseDouble((String) Objects.requireNonNull(snapshot.child("currentLatitude").getValue()));

                                driverLong=Double.parseDouble((String) Objects.requireNonNull(snapshot.child("currentLatitude").getValue()));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userQuery1.addListenerForSingleValueEvent(valueEventListener1);

    }
//    public void distance (float lat_a, float lng_a, float lat_b, float lng_b )
//    {
//
//
//        double earthRadius = 3958.75;
//        double latDiff = Math.toRadians(lat_b-lat_a);
//        double lngDiff = Math.toRadians(lng_b-lng_a);
//        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
//                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
//                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        double distance = earthRadius * c;
//
//        int meterConversion = 1609;
//         results= (float) (distance * meterConversion);
//
//
//    }
//   public void meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
//        float pk = (float) (180.f/Math.PI);
//
//        float a1 = lat_a / pk;
//        float a2 = lng_a / pk;
//        float b1 = lat_b / pk;
//        float b2 = lng_b / pk;
//
//        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
//        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
//        double t3 = Math.sin(a1) * Math.sin(b1);
//        double tt = Math.acos(t1 + t2 + t3);
//
//        results= (float) (6366000 * tt);
//    }
//    public void distances(float lat1, float lng1, float lat2, float lng2) {
//        double earthRadius = 6371000; //meters
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLng = Math.toRadians(lng2 - lng1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        float dist = (float) (earthRadius * c);
//
//        results= dist;
//    }
private void distanceInMiles(double lat1, double lon1, double lat2, double lon2) {
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1))
            * Math.sin(deg2rad(lat2))
            + Math.cos(deg2rad(lat1))
            * Math.cos(deg2rad(lat2))
            * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    results= (float)dist;
}

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    public void getKmFromLatLong(float lat1, float lng1, float lat2, float lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        float distanceInMeters = loc1.distanceTo(loc2);
        results=(float) distanceInMeters/1000;
    }


}
