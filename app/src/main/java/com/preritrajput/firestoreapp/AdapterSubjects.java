package com.preritrajput.firestoreapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;

public class AdapterSubjects extends RecyclerView.Adapter<AdapterSubjects.MyHolder>{

    public ArrayList<ModelSubjects> usersList;
    private Context context;
    private FirebaseUser user;

    public AdapterSubjects(ArrayList<ModelSubjects> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyHolder holder, int position) {
        String semesterNo = usersList.get(position).getSemester();
        String subj = usersList.get(position).getSubjectName();
        String courcode = usersList.get(position).getCourseCode();
        String fName = usersList.get(position).getFacultyName();
        String uid = usersList.get(position).getUid();
        String email = usersList.get(position).getEmail();
        String timeStamp = usersList.get(position).getTimeStamp();
        String name = usersList.get(position).getName();
        String phone = usersList.get(position).getPhone();


        holder.semester.setText("Semester : "+semesterNo);
        holder.subject.setText("Subject : "+subj);
        holder.code.setText("Code : "+courcode);
        holder.faculty.setText("Faculty : "+fName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ExperimentsActivity.class);
                intent.putExtra("Semester",semesterNo);
                intent.putExtra("Subject",subj);
                intent.putExtra("Code",courcode);
                intent.putExtra("Faculty",fName);
                intent.putExtra("uid",uid);
                intent.putExtra("email",email);
                intent.putExtra("timeStamp",timeStamp);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                context.startActivity(intent);
            }
        });

        holder.editSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,EditSubjectActivity.class);
                intent.putExtra("Semester",semesterNo);
                intent.putExtra("Subject",subj);
                intent.putExtra("Code",courcode);
                intent.putExtra("Faculty",fName);
                intent.putExtra("uid",uid);
                intent.putExtra("email",email);
                intent.putExtra("timeStamp",timeStamp);
                context.startActivity(intent);
            }
        });

        holder.deleteSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert= new AlertDialog.Builder(context,R.style.AlertDialogTheme);
                View viewm=LayoutInflater.from(context).inflate(R.layout.sub_delete_dialog,null);

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
                        removeItem(position);
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
            }
        });
    }

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
        private TextView semester, subject,code,faculty;
        private Button editSub,deleteSub;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            semester=itemView.findViewById(R.id.semno);
            subject=itemView.findViewById(R.id.sub);
            code=itemView.findViewById(R.id.courseCode);
            faculty=itemView.findViewById(R.id.fName);
            editSub=itemView.findViewById(R.id.edit_subject);
            deleteSub=itemView.findViewById(R.id.delete_subject);
        }
    }
}
