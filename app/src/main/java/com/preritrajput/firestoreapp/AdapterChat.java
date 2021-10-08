package com.preritrajput.firestoreapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    public ArrayList<ModelChat> chatsList;
    private Context context;
    private FirebaseUser user;
    String imageUrl;

    public AdapterChat(ArrayList<ModelChat> chatsList, Context context, String imageUrl) {
        this.chatsList = chatsList;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_right, parent, false);
            context = parent.getContext();
            return new MyHolder(view);
        }else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_left, parent, false);
            context = parent.getContext();
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message=chatsList.get(position).getMessage();
        String timeStamp=chatsList.get(position).getTimeStamp();

        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy (hh:mm aa)",calendar).toString();

        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);

        Shimmer shimmer = new Shimmer.ColorHighlightBuilder().setBaseColor(Color.parseColor("#888888")).setBaseAlpha(1).setHighlightColor(Color.parseColor("#E7E7E7")).setHighlightAlpha(1).setDropoff(50).build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);


        try {
            Glide.with(context).load(imageUrl).centerCrop().placeholder(shimmerDrawable).into(holder.dpIv);
            if (imageUrl.equals(""))
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
        return chatsList.size();
    }


    @Override
    public int getItemViewType(int position) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if(chatsList.get(position).getSender().equals(user.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        //UI Views
        private ImageView dpIv;
        private TextView messageTv, timeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            dpIv=itemView.findViewById(R.id.circleImageView3);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);
        }
    }
}
