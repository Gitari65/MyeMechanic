package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {
    DrawerLayout drawerLayout ;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)
        ) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        drawerLayout=findViewById(R.id.drawerlayout_admin);
        navigationView=findViewById(R.id.navigation_admin_view);
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.menu_open,R.string.closed_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        replaceFragment(new AdminHomeFragment());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_admin_home:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        replaceFragment(new AdminHomeFragment());
                        break;
                    case R.id.nav_admin_verified:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        replaceFragment(new VerifiedMechanicsFragment());

                        break;
                    case R.id.nav_admin_pending:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        replaceFragment(new PendingMechanicsFragments());
                        break;
                    case R.id.nav_admin_help:
                        Log.i(TAG, "onNavigationItemSelected: earnings");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_admin_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(getApplicationContext(),AdminLogin.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }


        });



    }
    public void replaceFragment(Fragment fragment){

        androidx.fragment.app.FragmentManager fragmentManager=getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_viewA,fragment);
        fragmentTransaction.addToBackStack(null);           //Optional
        fragmentTransaction.commit();



    }


}