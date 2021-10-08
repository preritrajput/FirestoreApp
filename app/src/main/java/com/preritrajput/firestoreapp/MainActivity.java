package com.preritrajput.firestoreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.transparent));
        setContentView(R.layout.activity_main);
    }

    public void register(View view) {
        startActivity(new Intent(MainActivity.this,SignUpActivity.class));
        finish();
    }

    public void login(View view) {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }
}