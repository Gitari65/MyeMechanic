package com.example.myemechanic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    Animation myanimation,myanimation_1;
    TextView text_1,text_2;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        text_1=findViewById(R.id.text_1);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isConnectedToInternet()) {
                    Intent intent= new Intent(getApplicationContext(),MainActivity2.class);
                    startActivity(intent);

                }else {
                    Intent intent= new Intent(getApplicationContext(),No_internetActivity.class);
                    startActivity(intent);
                }

            }
        },7000);
        init();


    }
    private void init(){

        imageView=(android.widget.ImageView)findViewById(R.id.image_mechanic);
        myanimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        myanimation_1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_animation);
       imageView.setAnimation(myanimation);
       text_1.setAnimation(myanimation_1);



    }
    boolean isConnectedToInternet() {

        ConnectivityManager cm =
                (ConnectivityManager)SplashActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        return isConnected;

    };}