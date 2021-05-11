package com.preritrajput.firestoreapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int  CAMERA_REQUEST_CODE=100;
    private static  final int STORAGE_REQUEST_CODE=200;
    private static  final int IMAGE_PICK_GALLERY_CODE=300;
    private static  final int IMAGE_PICK_CAMERA_CODE=400;

    ImageView dp,coverIv;
    TextView nameTv,emailTv,phoneTv;

    String[] cameraPermissions;
    String[] storagePermissions;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    String storagePath = "Users_Profile_Cover_Imgs/",cover,image;

    FirebaseAuth firebaseAuth;
    RelativeLayout edit;
    BottomSheetDialog bottomSheetDialog1,bottomSheetDialog2;
    ImageView settings;

    FirebaseUser user;
    DatabaseReference collectionReference;

    ProgressBar pd;
    TextView  text;

    Uri image_uri;
    String profileOrCoverPhoto;

    FirebaseDatabase db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        user=firebaseAuth.getCurrentUser();
        storageReference=firebaseStorage.getInstance().getReference();
        collectionReference=db.getReference("Users");

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};

        edit=view.findViewById(R.id.relativeLayout2);
        dp=view.findViewById(R.id.circleImageView);
        coverIv=view.findViewById(R.id.imageView2);
        nameTv=view.findViewById(R.id.textView9);
        emailTv=view.findViewById(R.id.textView11);
        phoneTv=view.findViewById(R.id.textView12);
        settings=view.findViewById(R.id.settings);
        pd=view.findViewById(R.id.progressBar);
        text=view.findViewById(R.id.text);

        Query query = collectionReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String name=""+ds.child("name").getValue();
                    String email=""+ds.child("email").getValue();
                    String phone=""+ds.child("phone").getValue();
                    String dp1=""+ds.child("image").getValue();
                    String cover1=""+ds.child("cover").getValue();

                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText("+91 "+phone);

                    try {
                        Glide.with(view.getContext()).load(dp1).centerCrop().into(dp);
                        if (dp1.equals(""))
                        {
                            Glide.with(view.getContext()).load(R.drawable.ic_user).centerCrop().into(dp);
                        }
                    }catch (Exception e)
                    {
                        Glide.with(view.getContext()).load(R.drawable.ic_user).centerCrop().into(dp);
                    }

                    try {
                        Glide.with(view.getContext()).load(cover1).centerCrop().into(coverIv);
                        if (cover1.equals(""))
                        {
                            Glide.with(view.getContext()).load(R.drawable.main1).centerCrop().into(coverIv);
                        }
                    }catch (Exception e)
                    {
                        Glide.with(view.getContext()).load(R.drawable.main1).centerCrop().into(coverIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog1=new BottomSheetDialog(getActivity(),R.style.BottomSheetTheme);
                View sheetView=LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_edit,(ViewGroup)view.findViewById(R.id.bottom_sheet));
                sheetView.findViewById(R.id.change_dp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog1.dismiss();
                        profileOrCoverPhoto="image";
                        bottomSheetDialog2=new BottomSheetDialog(getActivity(),R.style.BottomSheetTheme);
                        View sheetView1=LayoutInflater.from(getContext()).inflate(R.layout.bottom_camera,(ViewGroup)view.findViewById(R.id.bottom_sheet_camera_1));
                        sheetView1.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
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
                                bottomSheetDialog2.dismiss();
                            }
                        });
                        sheetView1.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
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
                                bottomSheetDialog2.dismiss();
                            }
                        });
                        bottomSheetDialog2.setContentView(sheetView1);
                        bottomSheetDialog2.show();
                    }
                });
                sheetView.findViewById(R.id.change_cover).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog1.dismiss();
                        profileOrCoverPhoto="cover";
                        bottomSheetDialog2=new BottomSheetDialog(getActivity(),R.style.BottomSheetTheme);
                        View sheetView2=LayoutInflater.from(getContext()).inflate(R.layout.bottom_camera,(ViewGroup)view.findViewById(R.id.bottom_sheet_camera_1));
                        sheetView2.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
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
                                bottomSheetDialog2.dismiss();
                            }
                        });
                        sheetView2.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
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
                                bottomSheetDialog2.dismiss();
                            }
                        });
                        bottomSheetDialog2.setContentView(sheetView2);
                        bottomSheetDialog2.show();
                    }
                });
                sheetView.findViewById(R.id.change_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog1.dismiss();
                        startActivity(new Intent(getActivity(),EditProfileActivity.class));
                    }
                });
                bottomSheetDialog1.setContentView(sheetView);
                bottomSheetDialog1.show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog1=new BottomSheetDialog(getActivity(),R.style.BottomSheetTheme);
                View sheetView=LayoutInflater.from(getContext()).inflate(R.layout.bottom_settings,(ViewGroup)view.findViewById(R.id.bottom_sheet));
                sheetView.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog1.dismiss();
                        final AlertDialog.Builder alert= new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                        View viewm=getLayoutInflater().inflate(R.layout.logout_dialog,null);

                        Button cancel=(Button)viewm.findViewById(R.id.cancel_button);
                        Button logout=(Button)viewm.findViewById(R.id.logout_btn);
                        ProgressBar logou=(ProgressBar) viewm.findViewById(R.id.logou);

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
                                logou.setVisibility(View.VISIBLE);
                                logout.setVisibility(View.INVISIBLE);
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        logou.setVisibility(View.GONE);
                                        logout.setVisibility(View.VISIBLE);
                                        firebaseAuth.signOut();
                                        checkUserStatus();
                                        alertDialog.dismiss();
                                    }
                                },4000);
                            }
                        });
                        alertDialog.show();
                        alertDialog.getWindow().setLayout(700,700);
                    }
                });
                sheetView.findViewById(R.id.changePass).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog1.dismiss();
                        startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
                    }
                });
                bottomSheetDialog1.setContentView(sheetView);
                bottomSheetDialog1.show();
            }
        });

        return view;
    }


    private boolean checkStoragePermission()
    {
        boolean result= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return  result;
    }

    private void requestStoragePermission()
    {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission()
    {
        boolean result= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return  result && result1;
    }

    private void requestCameraPermission()
    {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
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
                        Toast.makeText(getActivity(), "Please Enable Permission", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Please Enable Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private void uploadProfileCoverPhoto(Uri image_uri) {
        pd.setVisibility(View.VISIBLE);
        text.setVisibility(View.INVISIBLE);
        String filePathAndName= storagePath + "" + profileOrCoverPhoto + "_" + user.getUid();
        StorageReference storageReference2nd= storageReference.child(filePathAndName);
        storageReference2nd.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri= uriTask.getResult();

                if(uriTask.isSuccessful())
                {
                    Map<String, Object> results= new HashMap<>();

                    results.put(profileOrCoverPhoto,downloadUri.toString());

                    db.getReference("Users").child(user.getUid()).updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.setVisibility(View.INVISIBLE);
                            text.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Your Profile/Cover image is updated.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.setVisibility(View.INVISIBLE);
                            text.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    pd.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.setVisibility(View.INVISIBLE);
                text.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK)
        {
            if(requestCode==IMAGE_PICK_GALLERY_CODE)
            {
                image_uri= data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE)
            {
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pickFromGallery() {
        Intent galleryIntent= new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic" );
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description" );

        image_uri= getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }


    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user != null) {
        }
        else
        {
            startActivity(new Intent(getContext(),MainActivity.class));
            getActivity().finish();
        }
    }
}