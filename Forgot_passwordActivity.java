package com.example.myemechanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;



public class Forgot_passwordActivity extends AppCompatActivity {
EditText etEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        etEmail=findViewById(R.id.email_forgot);
        boolean validateInput;
OnClickAction performCodeVerify;


    }

            // Checking if the input in form is valid


        }