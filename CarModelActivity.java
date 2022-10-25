package com.example.myemechanic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CarModelActivity extends AppCompatActivity {
ImageView imageViewToy,imageViewMaz,imageViewSuz,imageViewNis;
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_car_model);
        imageViewMaz=findViewById(R.id.imgview_modelMazda);
        imageViewNis=findViewById(R.id.imgview_modelNissan);
        imageViewToy=findViewById(R.id.imgview_modelToyota);
        imageViewSuz=findViewById(R.id.imgview_modelSuzuki);
        button=findViewById(R.id.carmodel_backbutton);


    String carPart=getIntent().getStringExtra("carPart");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(getApplicationContext(),DriverProblemActivity.class);
                startActivity(intent1);
            }
        });
        imageViewSuz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), DriverViewMechanics.class);
                intent.putExtra("carPart", carPart);
                intent.putExtra("carModel", "Suzuki");
                startActivity(intent);
            }
        });
        imageViewToy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriverViewMechanics.class);
                intent.putExtra("carPart", carPart);
                intent.putExtra("carModel", "Toyota");
                startActivity(intent);
            }
        });
        imageViewMaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carPart=getIntent().getStringExtra("carPart");
                Intent intent = new Intent(getApplicationContext(), DriverViewMechanics.class);
                intent.putExtra("carPart", carPart);
                intent.putExtra("carModel", "Mazda");
                startActivity(intent);
            }
        });
        imageViewNis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriverViewMechanics.class);
                intent.putExtra("carPart", carPart);
                intent.putExtra("carModel", "Nissan");
                startActivity(intent);
            }
        });

    }
}