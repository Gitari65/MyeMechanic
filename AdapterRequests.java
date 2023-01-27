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

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
     String mechanicFirstName,mechanicPhoneNumber;
        String driverFirstName;
        String driverSecondName;
        String driverEmail;
        String driverPhoneNumber,workChildKey;
        String carModel,carPart,carProblemDescription,date,responseDate,status;
    final long  timestamp = System.currentTimeMillis();

    @Override
    public void onBindViewHolder(@NonNull AdapterRequests.MyViewHolder holder, int position) {

        request_details req=requestsArrayList.get(position);
        carProblemDescription=req.getCarProblemDescription();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        responseDate = dateFormat.format(new Date());
        carModel=req.getCarModel();
        carPart=req.getCarPart();
        status=req.getStatus();

        current_userId=req.getDriversId();

            FirebaseAuth mAuth= FirebaseAuth.getInstance();
            FirebaseUser userID  = mAuth.getCurrentUser();


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (current_userId!=null){
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



        date= req.getDate();
        holder.textViewcarPart.setText(req.getCarPart());
        holder.textViewrequeststatus.setText(req.getStatus());
        holder.textViewcarProblemDesc.setText(req.getCarProblemDescription());
        holder.textViewcarModel.setText(req.getCarModel());
        holder.textViewrequestDate.setText(req.getDate());
        if (Objects.equals(status, "accepted")){
            holder.buttonAccept.setVisibility(View.GONE);
          //  holder.textViewrequeststatus.setTextColor();
            holder.buttonView.setVisibility(View.VISIBLE);
        }
        if (Objects.equals(status, "rejected")){
            holder.buttonCancel.setVisibility(View.GONE);
        }
        if (Objects.equals(status, "not sent")){
            holder.buttonCancel.setVisibility(View.GONE);
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonView.setVisibility(View.GONE);
        }



        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accepting request
                String current_userId=req.getDriversId();
                String date=req.getDate();
                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                String status=req.getStatus();
                String CarPart=req.getCarPart();
                String CarModel=req.getCarModel();

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("Request");
                Query userQuery = additionalUserInfoRef.orderByChild("date").equalTo(date);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("status", "accepted");
                            map.put("mechanicId", userId);
                            map.put("timestamp",timestamp);
                            ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    SaveWorkHistory();
                                    storeReport();

                                    Toast.makeText(context, " accepted request successfully ",Toast.LENGTH_SHORT).show();

                                    Intent intent=new Intent(context.getApplicationContext(),MechanicSelectDriver.class);
                                    intent.putExtra("driversId", current_userId);
                                    intent.putExtra("date", date);
                                    intent.putExtra("timestamp",  timestamp);

                                    intent.putExtra("CarPart", CarPart);
                                    intent.putExtra("CarModel", CarModel);
                                    intent.putExtra("workChildKey", workChildKey);
                                    context.startActivity(intent);

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    }
                };
                userQuery.addListenerForSingleValueEvent(valueEventListener);




            }
        });
        holder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CarPart=req.getCarPart();
                String CarModel=req.getCarModel();
                Intent intent =new Intent(context.getApplicationContext(),MechanicSelectDriver.class);
                intent.putExtra("driversId",current_userId);
                intent.putExtra("date",  req.getDate());
                intent.putExtra("CarPart", CarPart);
                intent.putExtra("CarModel", CarModel);
                intent.putExtra("timestamp",  req.getTimestamp());
                context.startActivity(intent);
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
                String status= "cancelled";

                //save to cancelled transanction



                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("Request");
                Query userQuery = additionalUserInfoRef.orderByChild("date").equalTo(date);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("status", "rejected");
                            map.put("mechanicId", "");
                            ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                   String status="rejected";
                                    SaveWorkHistory();
                                    Toast.makeText(context, " You have turned down the request ",Toast.LENGTH_SHORT).show();




                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    }
                };
                userQuery.addValueEventListener(valueEventListener);

            }
        });

    }
    public void SaveWorkHistory(){
      String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = String.valueOf(System.currentTimeMillis());

        Map<String, Object> userToy1 = new HashMap<>();
        userToy1.put("carModel", carModel);
        userToy1.put("driversId", current_userId);
        userToy1.put("mechanicId", userId);
        userToy1.put("carPart", carPart);
        userToy1.put("carProblemDescription", carProblemDescription);
        userToy1.put("driverFirstName", driverFirstName);
        userToy1.put("timestamp", timestamp);
        userToy1.put("driverPhoneNumber", driverPhoneNumber);
        userToy1.put("finalStatus", "");
        userToy1.put("responseDate", responseDate);
        userToy1.put("status", status);
        userToy1.put("date", date);
        DatabaseReference myRef1=FirebaseDatabase.getInstance().getReference().child("DriverRequest").
                child("MechanicWork").push();
        myRef1.setValue(userToy1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                workChildKey=myRef1.getKey();
                Toast.makeText(context, "Saved Request ",Toast.LENGTH_SHORT).show();

            }
        });
    }
public  void getMechanicDetails(){
    String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
    if (userId!=null){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("mechanics").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {

                                mechanicFirstName= documentSnapshot.getString("firstName");

                                mechanicPhoneNumber= documentSnapshot.getString("phoneNumber");

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
    public void storeSimpleRequests(){
        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> user3 = new HashMap<>();
        user3.put("workExpense","");
        user3.put("workPrice","");
        user3.put("carPart","");
        user3.put("workProblem",carProblemDescription);

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("mechanics").child(mechId);
        dbRef.push().setValue(user3);
    }
    public void storeReport(){
        //get string
        getMechanicDetails();




        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> user3 = new HashMap<>();
        user3.put("workExpense","");
        user3.put("workPrice","");
        user3.put("paymentMethod","");
        user3.put("workProblem",carProblemDescription);
        user3.put("timeTaken","");
        user3.put("timestamp",timestamp);
        user3.put("carpart",carPart);
        user3.put("driversId",current_userId);
        user3.put("driverFirstName",driverFirstName);
        user3.put("driverPhoneNumber",driverPhoneNumber);
        user3.put("paymentStatus","not paid");
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("mechanics").child(mechId);
        dbRef.push().setValue(user3);
        Map<String, Object> user4 = new HashMap<>();
        user4.put("workExpense","");
        user4.put("workPrice","");
        user4.put("paymentMethod","");
        user4.put("workProblem",carProblemDescription);
        user4.put("timeTaken","");
        user4.put("carpart",carPart);
        user4.put("carModel",carModel);
        user4.put("timestamp",timestamp);
        user4.put("mechanicId",mechId);
        user4.put("driverFirstName",driverFirstName);
        user4.put("driverPhoneNumber",driverPhoneNumber);
        user4.put("paymentStatus","not paid");
        DatabaseReference dbRef1=FirebaseDatabase.getInstance().getReference("Work").child("drivers").child(current_userId);
        dbRef1.push().setValue(user4);

    }


    @Override
    public int getItemCount() {
        return requestsArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewcarPart,textViewcarModel,textViewcarProblemDesc,textViewrequeststatus,textViewrequestDate;
        Button buttonAccept,buttonCancel,buttonView;

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
            textViewrequeststatus=itemView.findViewById(R.id.txt_requestStatus);
            buttonAccept=itemView.findViewById(R.id.btnacceptRequest);
            buttonView=itemView.findViewById(R.id.btnmechViewAcceptedRequest);
            buttonCancel=itemView.findViewById(R.id.btncancelRequest);
        }
    }

}
