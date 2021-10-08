package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.preritrajput.firestoreapp.databinding.ActivityCreateBinding;
import com.preritrajput.firestoreapp.databinding.ActivityEditSubjectBinding;

import java.util.HashMap;

public class EditSubjectActivity extends AppCompatActivity {

    private ActivityEditSubjectBinding binding;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    private Animation animShow, animHide;

    String sem,cc,fn,subjn,uid,email,timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        binding = ActivityEditSubjectBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sem=getIntent().getStringExtra("Semester");
        cc=getIntent().getStringExtra("Code");
        fn=getIntent().getStringExtra("Faculty");
        subjn=getIntent().getStringExtra("Subject");
        uid=getIntent().getStringExtra("uid");
        email=getIntent().getStringExtra("email");
        timeStamp=getIntent().getStringExtra("timeStamp");

        binding.semesterNo.setText(sem);
        binding.facultyName.setText(fn);
        binding.subjectName.setText(subjn);
        binding.courseCode.setText(cc);

        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        binding.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.error.setVisibility(View.INVISIBLE);
                String semester = binding.semesterNo.getText().toString().trim();
                String faculty = binding.facultyName.getText().toString().trim();
                String subject= binding.subjectName.getText().toString().trim();
                String course= binding.courseCode.getText().toString().trim();

                if(TextUtils.isEmpty(semester) || TextUtils.isEmpty(faculty) || TextUtils.isEmpty(subject) || TextUtils.isEmpty(course))
                {
                    binding.error.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.relativeLayout2.setBackgroundResource(R.color.grey);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.text.setVisibility(View.INVISIBLE);
                    binding.relativeLayout2.setClickable(false);

                    HashMap<String,Object> hashMap= new HashMap<>();
                    hashMap.put("semester",semester);
                    hashMap.put("uid",uid);
                    hashMap.put("email",email);
                    hashMap.put("facultyName",faculty);
                    hashMap.put("subjectName",subject);
                    hashMap.put("courseCode",course);
                    hashMap.put("timeStamp",timeStamp);

                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("Subjects");
                    db.child(timeStamp).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.relativeLayout2.setBackgroundResource(R.color.discordblue);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.text.setVisibility(View.VISIBLE);
                            binding.success.setVisibility(View.VISIBLE);
                            binding.success.startAnimation(animShow);
                            binding.textSuccess.setText("Subject : '"+subject+"'\nCourse Code : '"+course+"'\nEdited successfully");
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
                EditSubjectActivity.super.onBackPressed();
            }
        });
    }

    public void back(View view) {
        EditSubjectActivity.super.onBackPressed();
    }

    public void food(View view) {
    }
}