package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.preritrajput.firestoreapp.databinding.ActivityAddExperimentBinding;
import com.preritrajput.firestoreapp.databinding.ActivityCreateBinding;

import java.util.HashMap;

public class AddExperimentActivity extends AppCompatActivity {

    private ActivityAddExperimentBinding binding;

    private static final int  CAMERA_REQUEST_CODE=100;
    private static  final int STORAGE_REQUEST_CODE=101;
    private static  final int IMAGE_PICK_GALLERY_CODE=100;
    private static  final int IMAGE_PICK_CAMERA_CODE=400;

    String sem,cc,fn,subjn,uid,email,time;

    private Animation animShow, animHide;

    String[] cameraPermissions;
    String[] storagePermissions;
    Uri image_uri;

    private BottomSheetDialog bottomSheetDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        binding = ActivityAddExperimentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        sem=getIntent().getStringExtra("Semester");
        cc=getIntent().getStringExtra("Code");
        fn=getIntent().getStringExtra("Faculty");
        subjn=getIntent().getStringExtra("Subject");
        uid=getIntent().getStringExtra("uid");
        email=getIntent().getStringExtra("email");
        time=getIntent().getStringExtra("timeStamp");

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog1=new BottomSheetDialog(AddExperimentActivity.this,R.style.BottomSheetTheme);
                View sheetview1= LayoutInflater.from(AddExperimentActivity.this).inflate(R.layout.bottom_camera,(ViewGroup) view.findViewById(R.id.bottom_sheet_camera_1));
                sheetview1.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!checkStoragePermission())
                        {
                            requestStoragePermission();
                        }
                        else
                        {
                            pickFromGallery();
                        }
                        bottomSheetDialog1.dismiss();
                    }
                });
                sheetview1.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!checkCameraPermission())
                        {
                            requestCameraPermission();
                        }
                        else
                        {
                            pickFromCamera();
                        }
                        bottomSheetDialog1.dismiss();
                    }
                });
                bottomSheetDialog1.setContentView(sheetview1);
                bottomSheetDialog1.show();
            }
        });

        binding.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.error.setVisibility(View.GONE);
                String ExpNo=binding.semesterNo.getText().toString().trim();
                String Aim=binding.facultyName.getText().toString().trim();
                String Theory=binding.subjectName.getText().toString().trim();
                String OutputCode=binding.courseCode.getText().toString().trim();

                if(TextUtils.isEmpty(ExpNo) || TextUtils.isEmpty(Aim) || TextUtils.isEmpty(Theory) || TextUtils.isEmpty(OutputCode))
                {
                    binding.error.setVisibility(View.VISIBLE);
                    Toast.makeText(AddExperimentActivity.this, "Please provide all details correctly", Toast.LENGTH_SHORT).show();
                }
                else if(image_uri==null)
                {
                    binding.error.setVisibility(View.VISIBLE);
                    Toast.makeText(AddExperimentActivity.this, "Please provide all details correctly", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    binding.relativeLayout2.setBackgroundResource(R.color.grey);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.text.setVisibility(View.INVISIBLE);
                    binding.relativeLayout2.setClickable(false);

                    final String timeStamp= String.valueOf(System.currentTimeMillis());

                    String filePathAndName="Experiments/" + "experiment_" + timeStamp;

                    StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathAndName);

                    ref.putFile(Uri.parse(String.valueOf(image_uri))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());

                            String downloadUri= uriTask.getResult().toString();

                            if(uriTask.isSuccessful())
                            {
                                HashMap<Object,String> hashMap= new HashMap<>();
                                hashMap.put("semester",sem);
                                hashMap.put("uid",uid);
                                hashMap.put("email",email);
                                hashMap.put("facultyName",fn);
                                hashMap.put("subjectName",subjn);
                                hashMap.put("courseCode",cc);
                                hashMap.put("expNo",ExpNo);
                                hashMap.put("aim",Aim);
                                hashMap.put("theory",Theory);
                                hashMap.put("outputCode",OutputCode);
                                hashMap.put("subTime",time);
                                hashMap.put("outputPic",downloadUri);
                                hashMap.put("timeStamp",timeStamp);

                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Experiments");
                                ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        binding.relativeLayout2.setBackgroundResource(R.color.discordblue);
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        binding.text.setVisibility(View.VISIBLE);
                                        binding.success.setVisibility(View.VISIBLE);
                                        binding.success.startAnimation(animShow);
                                        binding.textSuccess.setText("Exp No. : '"+ExpNo+"'\nAdded successfully");
                                        binding.semesterNo.setText("");
                                        binding.facultyName.setText("");
                                        binding.subjectName.setText("");
                                        binding.courseCode.setText("");
                                        binding.imageIv.setImageURI(null);
                                        image_uri=null;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.relativeLayout2.setBackgroundResource(R.color.discordblue);
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        binding.text.setVisibility(View.VISIBLE);
                                        binding.relativeLayout2.setClickable(true);
                                        binding.error.setVisibility(View.VISIBLE);
                                        binding.error.setText(e.getMessage());
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.relativeLayout2.setBackgroundResource(R.color.discordblue);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.text.setVisibility(View.VISIBLE);
                            binding.relativeLayout2.setClickable(true);
                            binding.error.setVisibility(View.VISIBLE);
                            binding.error.setText(e.getMessage());
                        }
                    });
                }
            }
        });

        binding.addAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.success.startAnimation(animHide);
                binding.success.setVisibility(View.INVISIBLE);
                binding.relativeLayout2.setClickable(true);
            }
        });

    }


    private boolean checkStoragePermission()
    {
        boolean result= ContextCompat.checkSelfPermission(AddExperimentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return  result;
    }

    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission()
    {
        boolean result= ContextCompat.checkSelfPermission(AddExperimentActivity.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(AddExperimentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return  result && result1;
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length >0)
                {
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted)
                    {
                        pickFromCamera();
                    }
                    else
                    {
                        Toast.makeText(this, "Please enable permission.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length >0)
                {
                    boolean writeStorageAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted)
                    {
                        pickFromGallery();
                    }
                    else
                    {
                        Toast.makeText(this, "Please enable permission.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private void pickFromCamera() {
        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                image_uri= data.getData();

                binding.imageIv.setImageURI(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                binding.imageIv.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void back(View view) {
        if(binding.success.getVisibility()==View.VISIBLE)
        {
            AddExperimentActivity.super.onBackPressed();
        }
        else
        {
            final AlertDialog.Builder alert= new AlertDialog.Builder(AddExperimentActivity.this,R.style.AlertDialogTheme);
            View viewm=getLayoutInflater().inflate(R.layout.changes_discarded_dialog,null);

            Button cancel=(Button)viewm.findViewById(R.id.cancel_button);
            Button logout=(Button)viewm.findViewById(R.id.logout_btn);

            alert.setView(viewm);

            AlertDialog alertDialog=alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    AddExperimentActivity.super.onBackPressed();
                }
            });
            alertDialog.show();
            alertDialog.getWindow().setLayout(700,700);
        }
    }

    public void food(View view) {
    }

    @Override
    public void onBackPressed() {
        if(binding.success.getVisibility()==View.VISIBLE)
        {
            AddExperimentActivity.super.onBackPressed();
        }
        else
        {
            final AlertDialog.Builder alert= new AlertDialog.Builder(AddExperimentActivity.this,R.style.AlertDialogTheme);
            View viewm=getLayoutInflater().inflate(R.layout.changes_discarded_dialog,null);

            Button cancel=(Button)viewm.findViewById(R.id.cancel_button);
            Button logout=(Button)viewm.findViewById(R.id.logout_btn);

            alert.setView(viewm);

            AlertDialog alertDialog=alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    AddExperimentActivity.super.onBackPressed();
                }
            });
            alertDialog.show();
            alertDialog.getWindow().setLayout(700,700);
        }
    }
}