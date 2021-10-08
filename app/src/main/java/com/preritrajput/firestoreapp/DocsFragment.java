package com.preritrajput.firestoreapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.preritrajput.firestoreapp.databinding.FragmentAddSubjectBinding;
import com.preritrajput.firestoreapp.databinding.FragmentDocsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocsFragment extends Fragment {

    private FragmentDocsBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ArrayList<ModelSubjects> subjectsList;
    AdapterSubjects adapterSubjects;
    String uid;

    public DocsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDocsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        uid=user.getUid();

        subjectsList= new ArrayList<>();

        LinearLayoutManager layoutManager1=new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,true);
        layoutManager1.setStackFromEnd(true);
        binding.subjectRecycler.setLayoutManager(layoutManager1);

        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreateActivity.class));
            }
        });

        binding.addSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreateActivity.class));
            }
        });

        binding.shimmerLayout4.startShimmer();

        loadSubjects();

        return view;
    }

    private void loadSubjects() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Subjects");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectsList.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    binding.subjectRecycler.setVisibility(View.VISIBLE);
                    ModelSubjects modelRecommended=ds.getValue(ModelSubjects.class);
                    if(modelRecommended.getUid().contains(uid))
                    {
                        subjectsList.add(modelRecommended);
                    }

                }
                adapterSubjects=new AdapterSubjects(subjectsList,getActivity());
                if(adapterSubjects.getItemCount()==0)
                {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.subjectRecycler.setVisibility(View.GONE);
                }
                else
                {

                    binding.empty.setVisibility(View.GONE);
                    binding.subjectRecycler.setVisibility(View.VISIBLE);

                }
                binding.subjectRecycler.setAdapter(adapterSubjects);
                binding.shimmerLayout4.stopShimmer();
                binding.shimmerLayout4.setVisibility(View.GONE);
                adapterSubjects.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}