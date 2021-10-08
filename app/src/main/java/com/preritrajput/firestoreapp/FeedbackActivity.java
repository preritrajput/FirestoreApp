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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.preritrajput.firestoreapp.databinding.ActivityExperimentsBinding;
import com.preritrajput.firestoreapp.databinding.ActivityFeedbackBinding;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    private ActivityFeedbackBinding binding;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        binding.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.error.setVisibility(View.INVISIBLE);
                String semester = binding.semesterNo.getText().toString().trim();

                if(TextUtils.isEmpty(semester))
                {
                    binding.error.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.relativeLayout2.setBackgroundResource(R.color.grey);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.text.setVisibility(View.INVISIBLE);
                    binding.relativeLayout2.setClickable(false);

                    final String timeStamp = String.valueOf(System.currentTimeMillis());
                    HashMap<Object,String> hashMap= new HashMap<>();
                    hashMap.put("feedback",semester);
                    hashMap.put("uid",user.getUid());
                    hashMap.put("email",user.getEmail());
                    hashMap.put("timeStamp",timeStamp);

                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("Feedbacks");
                    db.child(user.getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.relativeLayout2.setBackgroundResource(R.color.discordblue);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.text.setVisibility(View.VISIBLE);
                            binding.success.setVisibility(View.VISIBLE);
                            binding.success.startAnimation(animShow);
                            binding.semesterNo.setText("");
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
                FeedbackActivity.super.onBackPressed();
            }
        });
    }

    public void back(View view) {
        FeedbackActivity.super.onBackPressed();
    }

    public void food(View view) {
    }
}