package com.preritrajput.firestoreapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.subjectRecycler);

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

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull  RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder, @NonNull  RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull  RecyclerView.ViewHolder viewHolder, int direction) {

            int position=viewHolder.getAdapterPosition();
            String timeStamp = subjectsList.get(position).getTimeStamp();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    adapterSubjects.notifyItemChanged(position);
                    final AlertDialog.Builder alert= new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                    View viewm=LayoutInflater.from(getActivity()).inflate(R.layout.sub_delete_dialog,null);

                    RelativeLayout cancel=(RelativeLayout)viewm.findViewById(R.id.cancel_button);
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
                            subjectsList.remove(position);
                            adapterSubjects.notifyItemRemoved(position);
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    deleteWithImage(timeStamp);
                                    alertDialog.dismiss();
                                }
                            },2000);
                        }
                    });
                    alertDialog.show();
                    break;
                case ItemTouchHelper.RIGHT:
                    adapterSubjects.notifyItemChanged(position);
                    Intent intent=new Intent(getActivity(),EditSubjectActivity.class);
                    intent.putExtra("Semester",subjectsList.get(position).semester);
                    intent.putExtra("Subject",subjectsList.get(position).subjectName);
                    intent.putExtra("Code",subjectsList.get(position).courseCode);
                    intent.putExtra("Faculty",subjectsList.get(position).facultyName);
                    intent.putExtra("uid",subjectsList.get(position).uid);
                    intent.putExtra("email",subjectsList.get(position).email);
                    intent.putExtra("timeStamp",subjectsList.get(position).timeStamp);
                    startActivity(intent);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull  Canvas c, @NonNull  RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24_white)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(),R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    private void deleteWithImage(String timeStamp) {
        Query fquery= FirebaseDatabase.getInstance().getReference("Subjects").orderByChild("timeStamp").equalTo(timeStamp);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}