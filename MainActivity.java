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
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_main);
drawerLayout=findViewById(R.id.drawer_layout);
navigationView=findViewById(R.id.navigation_view);
actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.menu_open,R.string.closed_menu);
drawerLayout.addDrawerListener(actionBarDrawerToggle);
actionBarDrawerToggle.syncState();



getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        replaceFragment(new MechanicHomeFragment());

navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                replaceFragment(new MechanicHomeFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_account:
                replaceFragment(new MechanicProfileFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_chats:
                replaceFragment(new ChatlistFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;



            case R.id.nav_requests:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent=new Intent(getApplicationContext(),TechnicianHomeActivity.class);
                break;
            case R.id.nav_logout:

                drawerLayout.closeDrawer(GravityCompat.START);
                FirebaseAuth.getInstance().signOut();
                Intent intent1 =new Intent(getApplicationContext(),Mechanic_login.class);
                startActivity(intent1);
                break;
        }
        return true;
    }
});





    }
    public void replaceFragment(Fragment fragment){
     ;
        androidx.fragment.app.FragmentManager fragmentManager=getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment,fragment);
        fragmentTransaction.addToBackStack(null);           //Optional
        fragmentTransaction.commit();



    }
}