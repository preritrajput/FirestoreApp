package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String mUID;

    FrameLayout main_container;
    BottomNavigationView bottomNavigationView;
    FirebaseUser user;

    Menu a;
    MenuItem b;

    final Fragment fragment1=new HomeFragment();
    final Fragment fragment5=new ProfileFragment();
    final FragmentManager fm=getSupportFragmentManager();
    Fragment active=fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        setContentView(R.layout.activity_dashboard);
        firebaseAuth=FirebaseAuth.getInstance();

        main_container=findViewById(R.id.main_container);
        firebaseAuth=FirebaseAuth.getInstance();

        user=firebaseAuth.getCurrentUser();

        fm.beginTransaction().add(R.id.main_container,fragment5,"5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1,"1").commit();

        bottomNavigationView =findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
        bottomNavigationView.setItemIconTintList(null);

        a=bottomNavigationView.getMenu();
        b=a.findItem(R.id.profile_nav);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            {
                switch (item.getItemId())
                {
                    case R.id.home_nav:
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active=fragment1;
                        return true;

                    case R.id.profile_nav:
                        fm.beginTransaction().hide(active).show(fragment5).commit();
                        active=fragment5;
                        return true;
                }
                return false;
            }
        }
    };

    public void logout(View view) {
        firebaseAuth.signOut();
        checkUserStatus();
    }

    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID=user.getUid();
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
        }
        else
        {
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }
}