package com.preritrajput.firestoreapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    public ArrayList<ModelUsers> usersList;
    private Context context;
    private FirebaseUser user;

    public AdapterUsers(ArrayList<ModelUsers> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row_cards, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String image = usersList.get(position).getImage();
        String name = usersList.get(position).getName();
        String email = usersList.get(position).getEmail();
        String uid = usersList.get(position).getUid();

        holder.nameTv.setText(name);
        holder.emailTv.setText(email);

        Shimmer shimmer = new Shimmer.ColorHighlightBuilder().setBaseColor(Color.parseColor("#888888")).setBaseAlpha(1).setHighlightColor(Color.parseColor("#E7E7E7")).setHighlightAlpha(1).setDropoff(50).build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ChatActivity.class);
                intent.putExtra("uid",uid);
                context.startActivity(intent);
            }
        });


        try {
            Glide.with(context).load(image).centerCrop().placeholder(shimmerDrawable).into(holder.dpIv);
            if (image.equals(""))
            {
                Glide.with(context).load(R.drawable.ic_user).centerCrop().placeholder(shimmerDrawable).into(holder.dpIv);
            }
        }catch (Exception e)
        {
            Glide.with(context).load(R.drawable.ic_user).centerCrop().placeholder(shimmerDrawable).into(holder.dpIv);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        //UI Views
        private ImageView dpIv;
        private TextView nameTv, emailTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            dpIv=itemView.findViewById(R.id.circleImageView2);
            nameTv=itemView.findViewById(R.id.textView16);
            emailTv=itemView.findViewById(R.id.message);
        }
    }
}
