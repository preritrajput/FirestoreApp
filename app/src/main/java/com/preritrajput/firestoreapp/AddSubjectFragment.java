package com.preritrajput.firestoreapp;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.preritrajput.firestoreapp.databinding.FragmentAddSubjectBinding;
import com.preritrajput.firestoreapp.databinding.FragmentHomeBinding;

import java.util.HashMap;


public class AddSubjectFragment extends Fragment {

    private FragmentAddSubjectBinding binding;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    public AddSubjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddSubjectBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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

                    HashMap<Object,String> hashMap= new HashMap<>();
                    hashMap.put("semester",semester);
                    hashMap.put("uid",user.getUid());
                    hashMap.put("email",user.getEmail());
                    hashMap.put("facultyName",faculty);
                    hashMap.put("subjectName",subject);
                    hashMap.put("courseCode",course);

                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("Subjects");
                    db.child(user.getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Subject Added Successfully", Toast.LENGTH_SHORT).show();
                            binding.relativeLayout2.setBackgroundResource(R.color.discordblue);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.text.setVisibility(View.VISIBLE);
                            successAnim();
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

        return view;
    }

    private void successAnim() {
        binding.success.setVisibility(View.VISIBLE);
        binding.semesterNo.setText("");
        binding.facultyName.setText("");
        binding.subjectName.setText("");
        binding.courseCode.setText("");
        binding.anim.playAnimation();
        binding.anim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                binding.relativeLayout2.setClickable(true);
                binding.success.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}