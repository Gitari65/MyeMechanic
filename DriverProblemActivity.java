package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DriverProblemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView textViewEn,textViewBr,textViewEl,textViewTy;
    Button button,button1;
    EditText editText;
    String[] problem = {"Engine", "Tyres", "Electrical", "Brakes", "Don't know"};
    String[] carModelP = {"Sedan", "Suzuki", "Mazda", "Toyota", "Don't know"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ActionBar actionBar = getSupportActionBar();
      //  assert actionBar != null;
      //  actionBar.hide();

        setContentView(R.layout.activity_driver_problem);

        button1=findViewById(R.id.problem_backbutton);
        button=findViewById(R.id.btnfindMechs);
        editText =findViewById(R.id.edttextCarProblemDesc);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,problem);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
String  carProblemDescription=editText.getText().toString();
        //spinner
        Spinner spin = (Spinner) findViewById(R.id.spinnerProblem);
        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        //Performing action onItemSelected and onNothing selected
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String carPart=spin.getSelectedItem().toString();
                Log.d(TAG, "onCreate: carPart"+ carPart);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editText.getText().toString().equals("")){
                            editText.setError("write something");
                            editText.requestFocus();
                            return;
                        }
                        //storing spinner
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String  carProblemDescription=editText.getText().toString();
                        FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                        Map<String, Object> userToy = new HashMap<>();
                        userToy.put("carPart",carPart);
                        userToy.put("carProblemDescription",carProblemDescription);
                        dbEn.collection("driverRequest").
                                document(user_id).set(userToy, SetOptions.merge()).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent1 = new Intent(getApplicationContext(), DriverViewMechanics.class);
                                            intent1.putExtra("carPart", carPart);
                                            intent1.putExtra("driverId", user_id);
                                           // intent.putExtra("carProblemDescription",carProblemDescription);
                                            //intent.putExtra("carModel", "Suzuki");
                                            startActivity(intent1);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"failed Try again",Toast.LENGTH_SHORT).show();

                                    }
                                });


                    }
                });


            }

                                           @Override
                                           public void onNothingSelected(AdapterView<?> parent) {

                                           }
                                       });

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,carModelP);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinner
        Spinner spin1 = (Spinner) findViewById(R.id.spinnerCarModel);
        spin1.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //Setting the ArrayAdapter data on the Spinner
        spin1.setAdapter(aa1);
        //Performing action onItemSelected and onNothing selected
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String carModel=spin1.getSelectedItem().toString();
                Log.d(TAG, "onCreate: carModel"+ carModel);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editText.getText().toString().equals("")){
                            editText.setError("write something");
                            editText.requestFocus();
                            return;
                        }
                        //storing spinner
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy @ HH:mm", Locale.US);
                        String date = dateFormat.format(new Date());
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore dbEn = FirebaseFirestore.getInstance();
                        Map<String, Object> userToy1 = new HashMap<>();
                        userToy1.put("carModel",carModel);
                        userToy1.put("driversId",user_id);
                        userToy1.put("mechanicId","");
                        userToy1.put("finalStatus","");
                        userToy1.put("status","");
                        userToy1.put("date",date);
                        dbEn.collection("driverRequest").
                                document(user_id).set(userToy1, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Intent intent1 = new Intent(getApplicationContext(), DriverViewMechanics.class);
                                            intent1.putExtra("carModel", carModel);
                                            startActivity(intent1);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"failed Try again",Toast.LENGTH_SHORT).show();

                                    }
                                });


                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(getApplicationContext(),DriversHomeActivity.class);
                startActivity(intent1);
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}