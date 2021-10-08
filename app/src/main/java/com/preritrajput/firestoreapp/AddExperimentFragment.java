package com.preritrajput.firestoreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.preritrajput.firestoreapp.databinding.FragmentAddExperimentBinding;
import com.preritrajput.firestoreapp.databinding.FragmentAddSubjectBinding;

public class AddExperimentFragment extends Fragment {

    private FragmentAddExperimentBinding binding;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    public AddExperimentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddExperimentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        binding.relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.error.setVisibility(View.GONE);
                if(TextUtils.isEmpty(binding.selSem.getText().toString()) || TextUtils.isEmpty(binding.selSem.getText().toString()))
                {
                    binding.error.setVisibility(View.VISIBLE);
                    binding.error.setText("Please select Semester & Subject");
                }
                else
                {

                }
            }
        });

        return  view;
    }
}