package com.preritrajput.firestoreapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterExperiments extends RecyclerView.Adapter<AdapterExperiments.MyHolder>{

    public ArrayList<ModelExperiments> usersList;
    private Context context;
    private FirebaseUser user;

    public AdapterExperiments(ArrayList<ModelExperiments> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exp, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterExperiments.MyHolder holder, int position) {
        String semesterNo = usersList.get(position).getSemester();
        String subj = usersList.get(position).getSubjectName();
        String courcode = usersList.get(position).getCourseCode();
        String fName = usersList.get(position).getFacultyName();
        String uid = usersList.get(position).getUid();
        String email = usersList.get(position).getEmail();
        String timeStamp = usersList.get(position).getTimeStamp();
        String expNo = usersList.get(position).getExpNo();
        String aim = usersList.get(position).getAim();
        String theory = usersList.get(position).getTheory();
        String code = usersList.get(position).getOutputCode();
        String outputPic = usersList.get(position).getOutputPic();
        String subTime = usersList.get(position).getSubTime();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        final String pTime= DateFormat.format("dd-MM-yyy", calendar).toString();

        holder.time.setText(pTime);
        holder.expNo.setText("#"+(position+1)+"  ->  Exp No. "+expNo);
        holder.aim.setText("Aim : "+aim);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,EditExperimentActivity.class);
                intent.putExtra("semesterNo",semesterNo);
                intent.putExtra("subj",subj);
                intent.putExtra("courcode",courcode);
                intent.putExtra("fName",fName);
                intent.putExtra("uid",uid);
                intent.putExtra("email",email);
                intent.putExtra("timeStamp",timeStamp);
                intent.putExtra("expNo",expNo);
                intent.putExtra("aim",aim);
                intent.putExtra("theory",theory);
                intent.putExtra("code",code);
                intent.putExtra("outputPic",outputPic);
                intent.putExtra("subTime",subTime);
                context.startActivity(intent);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,EditExperimentActivity.class);
                intent.putExtra("semesterNo",semesterNo);
                intent.putExtra("subj",subj);
                intent.putExtra("courcode",courcode);
                intent.putExtra("fName",fName);
                intent.putExtra("uid",uid);
                intent.putExtra("email",email);
                intent.putExtra("timeStamp",timeStamp);
                intent.putExtra("expNo",expNo);
                intent.putExtra("aim",aim);
                intent.putExtra("theory",theory);
                intent.putExtra("code",code);
                intent.putExtra("outputPic",outputPic);
                intent.putExtra("subTime",subTime);
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert= new AlertDialog.Builder(context,R.style.AlertDialogTheme);
                View viewm=LayoutInflater.from(context).inflate(R.layout.delete_dialog,null);

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
                        removeItem(position);
                        deleteWithImage(timeStamp,outputPic,alertDialog);
                    }
                });
                alertDialog.show();
                alertDialog.getWindow().setLayout(700,700);
            }
        });

    }

    private void deleteWithImage(String timeStamp, String outputPic, AlertDialog alertDialog) {
        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(outputPic);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query fquery= FirebaseDatabase.getInstance().getReference("Experiments").orderByChild("timeStamp").equalTo(timeStamp);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            ds.getRef().removeValue();
                        }
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }


    public void removeItem(int position) {
        usersList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        //UI Views
        private TextView expNo, aim,time;
        private RelativeLayout edit, delete;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            expNo=itemView.findViewById(R.id.expNo);
            aim=itemView.findViewById(R.id.aim);
            time=itemView.findViewById(R.id.time);
            edit=itemView.findViewById(R.id.edit);
            delete=itemView.findViewById(R.id.delete);

        }
    }
}
