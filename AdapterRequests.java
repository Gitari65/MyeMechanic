package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    Long timestamp;

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
//    final long  timestamp = System.currentTimeMillis();

    @Override
    public void onBindViewHolder(@NonNull AdapterRequests.MyViewHolder holder, int position) {

        request_details req=requestsArrayList.get(position);
        carProblemDescription=req.getCarProblemDescription();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
        responseDate = dateFormat.format(new Date());
        carModel=req.getCarModel();
        carPart=req.getCarPart();
        status=req.getStatus();
        timestamp=req.getTimestamp();
        current_userId=req.getDriversId();

            FirebaseAuth mAuth= FirebaseAuth.getInstance();
            FirebaseUser userID  = mAuth.getCurrentUser();
MechanicDetails();

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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(req.getTimestamp());

        String formattedDate = dateFormat.format(cal.getTime());
        holder.textViewrequestDate.setText(formattedDate);
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
               Long timestamp=req.getTimestamp();
                DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference();
                String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();

//        DatabaseReference myRef2=FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Driver").child(user_id).push();
                DatabaseReference additionalUserInfoRef2 = rootRef2.child("DriverRequest").child("Driver").child(user_id);
                Query userQuery2 = additionalUserInfoRef2.orderByChild("timestamp").equalTo(timestamp);
                ValueEventListener valueEventListener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("status", "accepted");
                            map.put("mechanicId", userId);

                            ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    }
                };
                userQuery2.addListenerForSingleValueEvent(valueEventListener2);



                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("Request");
                Query userQuery = additionalUserInfoRef.orderByChild("timestamp").equalTo(timestamp);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("status", "accepted");
                            map.put("mechanicId", userId);

                            ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    String current_userId=req.getDriversId();
                                    String date=req.getDate();
                                    String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    String status=req.getStatus();
                                    String CarPart=req.getCarPart();
                                    String CarModel=req.getCarModel();
                                    Long timestamp=req.getTimestamp();


                                    storeReport();
                                    SaveWorkHistory();
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
                String current_userId=req.getDriversId();
                String date=req.getDate();
                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                String status=req.getStatus();

                Long timestamp=req.getTimestamp();
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
                Long timestamp=req.getTimestamp();
                //save to cancelled transanction

                DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference();
                String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference myRef2=FirebaseDatabase.getInstance().getReference().child("DriverRequest").child("Driver").child(user_id).push();
                DatabaseReference additionalUserInfoRef2 = rootRef2.child("DriverRequest").child("Driver").child(user_id);
                Query userQuery2 = additionalUserInfoRef2.orderByChild("timestamp").equalTo(timestamp);
                ValueEventListener valueEventListener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("status", "rejected");
                            map.put("mechanicId", "");
                            ds.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                    }
                };
                userQuery2.addListenerForSingleValueEvent(valueEventListener2);



                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference additionalUserInfoRef = rootRef.child("DriverRequest").child("Request");
                Query userQuery = additionalUserInfoRef.orderByChild("timestamp").equalTo(timestamp);
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
                                    String current_userId=req.getDriversId();
                                    String date=req.getDate();
                                    String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    String CarPart=req.getCarPart();
                                    String CarModel=req.getCarModel();
                                    Long timestamp=req.getTimestamp();
                                   String status="rejected";

                                    storeReport();
                                    SaveWorkHistory();
                                    StoreRejectedrequests();
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


        Map<String, Object> userToy1 = new HashMap<>();
        userToy1.put("carModel", carModel);
        userToy1.put("driversId", current_userId);
        userToy1.put("mechanicId", userId);
        userToy1.put("carPart", carPart);
        userToy1.put("carProblemDescription", carProblemDescription);
        userToy1.put("driverFirstName", driverFirstName);

        userToy1.put("driverPhoneNumber", driverPhoneNumber);
        userToy1.put("finalStatus", "");
        userToy1.put("responseDate", responseDate);
        userToy1.put("timestamp", timestamp);
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
    public void StoreRejectedrequests(){
        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> user3 = new HashMap<>();
        user3.put("workExpense","");
        user3.put("workPrice","");
        user3.put("carPart",carPart);
        user3.put("carModel",carModel);
        user3.put("driversId",current_userId);
        user3.put("workProblem",carProblemDescription);
        user3.put("timestamp",timestamp);

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("Rejected").child(mechId);
        dbRef.setValue(user3);
    }

    public void StoreCancelledrequests(){
        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> user3 = new HashMap<>();

        user3.put("carPart",carPart);
        user3.put("driversId",current_userId);
        user3.put("carModel",carModel);
        user3.put("workProblem",carProblemDescription);
        user3.put("timestamp",timestamp);

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("Cancelled").child(mechId);
        dbRef.push().setValue(user3);
    }


    public void storeReport(){
        //get string
//        getMechanicDetails();



        String mechId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> user3= new HashMap<>();
        user3.put("workExpense","");
        user3.put("workPrice","");
        user3.put("paymentMethod","");
        user3.put("status", status);

        user3.put("workProblem",carProblemDescription);
        user3.put("timeTaken","");
        user3.put("timestamp",timestamp);
        user3.put("carPart",carPart);
        user3.put("carModel",carModel);
        user3.put("driversId",current_userId);
        user3.put("driverFirstName",driverFirstName);
        user3.put("driverPhoneNumber",driverPhoneNumber);
        user3.put("paymentStatus","not paid");
        String key = FirebaseDatabase.getInstance().getReference("Work").child("mechanics").child(mechId).push().getKey();
        // Add the new data with the unique key
        FirebaseDatabase.getInstance().getReference("Work").child("mechanics").child(mechId).child(key).setValue(user3);
//        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Work").child("mechanics").child(mechId);
//        dbRef.setValue(user3);

        Map<String, Object> user4 = new HashMap<>();
        user4.put("workExpense","");
        user4.put("workPrice","");
        user4.put("paymentMethod","");
        user4.put("workProblem",carProblemDescription);
        user4.put("timeTaken","");
        user4.put("carPart",carPart);
        user4.put("carModel",carModel);
        user4.put("status", status);
        user4.put("timestamp",timestamp);
        user4.put("mechanicId",mechId);
        user4.put("mechanicFirstName",mechanicFirstName);
        user4.put("mechanicPhoneNumber",mechanicPhoneNumber);
        user4.put("paymentStatus","not paid");
//        DatabaseReference dbRef1=FirebaseDatabase.getInstance().getReference("Work").child("drivers").child(current_userId);
//        dbRef1.setValue(user4);
        // Generate a unique key
        String key1 = FirebaseDatabase.getInstance().getReference("Work").child("drivers").child(current_userId).push().getKey();
        // Add the new data with the unique key
        FirebaseDatabase.getInstance().getReference("Work").child("drivers").child(current_userId).child(key1).setValue(user4);


    }
   public void MechanicDetails(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference additionalUserInfoRef = rootRef.child("Technicians");
        Query userQuery = additionalUserInfoRef.orderByChild("userid").equalTo(userId);

        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           mechanicFirstName= (String) snapshot.child("firstNamee").getValue();
                            mechanicPhoneNumber= (String) snapshot.child("mobilee").getValue();

//kahuko.alex19@students.dkut.ac.ke



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
        userQuery.addValueEventListener(valueEventListener1);


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
