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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.MyViewHolder> {
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    DocumentSnapshot documentSnapshot;
    ArrayList<request_details> requestsArrayList;
    String childKey;

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
    String current_userId;

        String driverFirstName;
        String driverSecondName;
        String driverEmail;
        String driverPhoneNumber;
        String carModel,carPart,carProblemDescription,date,responseDate,status;

    @Override
    public void onBindViewHolder(@NonNull AdapterRequests.MyViewHolder holder, int position) {

        request_details req=requestsArrayList.get(position);
        carProblemDescription=req.getCarProblemDescription();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        responseDate = dateFormat.format(new Date());
        carModel=req.getCarModel();
        carPart=req.getCarPart();
        date= req.getDate();
        holder.textViewcarPart.setText(req.getCarPart());
        holder.textViewcarProblemDesc.setText(req.getCarProblemDescription());
        holder.textViewcarModel.setText(req.getCarModel());
        holder.textViewrequestDate.setText(req.getDate());

        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accepting request
                String current_userId=req.getDriversId();
                String date=req.getDate();
                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference db;
                String status="accepted";
                db = FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Request");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            childKey = dataSnapshot1.getKey();
                        }

                        assert childKey != null;
                        db.child(childKey).child("status").setValue("accepted").
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Request Accepted ",Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(context.getApplicationContext(),MechanicSelectDriver.class);
                                intent.putExtra("driversId",current_userId);
                                context.startActivity(intent);
                                SaveWorkHistory();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        holder.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelling request
                String current_userId=req.getDriversId();
                String date= req.getDate();
                String description=req.getCarProblemDescription();
                String model=req.getCarModel();
                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                String status="accepted";

                //save to cancelled transanction




                DatabaseReference db;
                db = FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Request");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            childKey = dataSnapshot1.getKey();
                        }


                        db.child(childKey).child("status").setValue("rejected");
                        db.child(childKey).child("mechanicId").setValue("").
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, " turned down the request",Toast.LENGTH_SHORT).show();

                                        SaveWorkHistory();

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }
    public void SaveWorkHistory(){
      String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> userToy1 = new HashMap<>();
        userToy1.put("carModel", carModel);
        userToy1.put("driversId", current_userId);
        userToy1.put("mechanicId", userId);
        userToy1.put("carPart", carPart);
        userToy1.put("carProblemDescription", carProblemDescription);


        userToy1.put("finalStatus", "");
        userToy1.put("responseDate", responseDate);
        userToy1.put("status", status);
        userToy1.put("date", date);
        DatabaseReference myRef1=FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("MechanicWork").push();


        myRef1.setValue(userToy1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Saved Request ",Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void getDriverDetails(){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser userID  = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Drivers").document(current_userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                 driverFirstName= documentSnapshot.getString("driverFirstName");
                                 driverSecondName= documentSnapshot.getString("driverSecondName");
                                 driverEmail= documentSnapshot.getString("driverEmail");
                                 driverPhoneNumber= documentSnapshot.getString("driverPhoneNumber");

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
