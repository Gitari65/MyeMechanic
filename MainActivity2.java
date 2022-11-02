package com.example.myemechanic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity2 extends AppCompatActivity {
Animation animation1;
Button button,button1;
LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
       // ActionBar actionBar = getSupportActionBar();
     //   actionBar.hide();


        button=findViewById(R.id.btnMechanicType);
        button1=findViewById(R.id.btnDriverType);
        layout= (LinearLayout) findViewById(R.id.userTypeButtons);
        animation1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_animation);
        layout.setAnimation(animation1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),Mechanic_signup.class);
                startActivity(intent);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),DriverRegistration.class);
                startActivity(intent);
            }
        });
    }
}