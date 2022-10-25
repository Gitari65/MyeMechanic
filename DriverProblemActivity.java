package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverProblemActivity extends AppCompatActivity {
    TextView textViewEn,textViewBr,textViewEl,textViewTy;
    Button button;
    String[] problem = {"Engine", "Tyres", "Electrical", "Brakes", "Don't know"};
    String[] carModel = {"Sedan", "Suzuki", "Mazda", "Toyota", "Don't know"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_driver_problem);

        button=findViewById(R.id.problem_backbutton);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,problem);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
                //storing spinner

            }

                                           @Override
                                           public void onNothingSelected(AdapterView<?> parent) {

                                           }
                                       });

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,carModel);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinner
        Spinner spin1 = (Spinner) findViewById(R.id.spinnerCarModel);
        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa1);
        //Performing action onItemSelected and onNothing selected
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String carModel=spin.getSelectedItem().toString();
                //storing spinner

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
}