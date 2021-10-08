package com.preritrajput.firestoreapp;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.preritrajput.firestoreapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    ShimmerFrameLayout shimmerFrameLayout;
    ArrayList<ModelUsers> usersList;
    AdapterUsers adapterUsers;
    RecyclerView usersRecycler;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference collectionReference;

    FirebaseDatabase db;

    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        /*shimmerFrameLayout=view.findViewById(R.id.shimmerLayout1);
        usersRecycler=view.findViewById(R.id.chatsRecycler);

        usersList = new ArrayList<>();

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        layoutManager1.setStackFromEnd(true);
        usersRecycler.setLayoutManager(layoutManager1);*/

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        db=FirebaseDatabase.getInstance();
        collectionReference=db.getReference("Users");

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);


        binding.createCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreateActivity.class));
            }
        });

        binding.showCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomNavigationView.setSelectedItemId(R.id.doc_nav);
            }
        });

        binding.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationView.setSelectedItemId(R.id.profile_nav);
            }
        });

        Query query = collectionReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {

                    String dp1=""+ds.child("image").getValue();
                    String name=""+ds.child("name").getValue();

                    binding.textView3.setText(name);

                    try {
                        Glide.with(view.getContext()).load(dp1).centerCrop().into(binding.dp);
                        if (dp1.equals(""))
                        {
                            Glide.with(view.getContext()).load(R.drawable.ic_user).centerCrop().into(binding.dp);
                        }
                    }catch (Exception e)
                    {
                        Glide.with(view.getContext()).load(R.drawable.ic_user).centerCrop().into(binding.dp);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  view;
    }

    private void loadUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    usersRecycler.setVisibility(View.VISIBLE);
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                    usersList.add(modelUsers);
                }
                adapterUsers = new AdapterUsers(usersList, getActivity());
                usersRecycler.setAdapter(adapterUsers);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                adapterUsers.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}