package com.preritrajput.firestoreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.preritrajput.firestoreapp.databinding.ActivityExperimentsBinding;
import com.preritrajput.firestoreapp.databinding.ActivitySuccessBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.AUTHORITY;

public class SuccessActivity extends AppCompatActivity {

    private ActivitySuccessBinding binding;
    String cc,subjn,fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.transparent));
        binding = ActivitySuccessBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        cc=getIntent().getStringExtra("course");
        subjn=getIntent().getStringExtra("subject");
        fileName=getIntent().getStringExtra("filename");

        binding.p.setText("Subject : "+subjn+"\nCourse : "+cc);

        binding.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File fileLocation = new File(getExternalFilesDir(null), fileName);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(SuccessActivity.this, BuildConfig.APPLICATION_ID + ".provider",fileLocation);
                    intent.setDataAndType(uri,
                            "application/msword");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }catch (Exception e)
                {

                }
            }
        });

        binding.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File fileLocation = new File(getExternalFilesDir(null), fileName);
                    Uri uri = FileProvider.getUriForFile(SuccessActivity.this, BuildConfig.APPLICATION_ID + ".provider",fileLocation);

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("application/msword");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(sharingIntent,"Share Via"));
                }catch (Exception e)
                {

                }
            }
        });
    }

    public void back(View view) {
        SuccessActivity.super.onBackPressed();
    }
}