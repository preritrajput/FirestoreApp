package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hpsf.Section;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    String hisUid;
    ImageView dpIv;
    TextView nameTv;
    EditText message;
    ImageView send;

    ArrayList<ModelChat> chatsList;
    AdapterChat adapterChat;
    RecyclerView chatsRecycler;

    DatabaseReference collectionReference;
    FirebaseDatabase db;
    FirebaseAuth firebaseAuth;

    LinearLayout linearLayout;

    String dp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        setContentView(R.layout.activity_chat);

        firebaseAuth= FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        collectionReference=db.getReference("Users");

        hisUid=getIntent().getStringExtra("uid");

        dpIv=findViewById(R.id.dp);
        nameTv=findViewById(R.id.name);
        message=findViewById(R.id.message);
        send=findViewById(R.id.send);
        chatsRecycler=findViewById(R.id.chatRecycler);
        linearLayout=findViewById(R.id.linear);

        chatsList = new ArrayList<>();

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager1.setStackFromEnd(true);
        chatsRecycler.setLayoutManager(layoutManager1);

        Query query = collectionReference.orderByChild("uid").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String name=""+ds.child("name").getValue();
                    dp1=""+ds.child("image").getValue();

                    nameTv.setText(name);

                    Shimmer shimmer = new Shimmer.ColorHighlightBuilder().setBaseColor(Color.parseColor("#888888")).setBaseAlpha(1).setHighlightColor(Color.parseColor("#E7E7E7")).setHighlightAlpha(1).setDropoff(50).build();
                    ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
                    shimmerDrawable.setShimmer(shimmer);

                    try {
                        Glide.with(getApplicationContext()).load(dp1).centerCrop().placeholder(shimmerDrawable).into(dpIv);
                        if (dp1.equals(""))
                        {
                            Glide.with(getApplicationContext()).load(R.drawable.ic_user).centerCrop().placeholder(shimmerDrawable).into(dpIv);
                        }
                    }catch (Exception e)
                    {
                        Glide.with(getApplicationContext()).load(R.drawable.ic_user).centerCrop().placeholder(shimmerDrawable).into(dpIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=message.getText().toString().trim();
                if(TextUtils.isEmpty(msg))
                {
                    Toast.makeText(ChatActivity.this, "Please first type your message", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final String timeStamp = String.valueOf(System.currentTimeMillis());
                    HashMap<Object,String> hashMap=new HashMap<>();
                    hashMap.put("message",msg);
                    hashMap.put("sender",firebaseAuth.getCurrentUser().getUid());
                    hashMap.put("receiver",hisUid);
                    hashMap.put("timeStamp",timeStamp);

                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");
                    reference.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            message.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        
        loadMessages();

    }

    private void loadMessages() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();

                for (DataSnapshot ds:snapshot.getChildren())
                {
                    chatsRecycler.setVisibility(View.VISIBLE);
                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    if(modelChat.getReceiver().equals(firebaseAuth.getCurrentUser().getUid())&& modelChat.getSender().equals(hisUid) || modelChat.getReceiver().equals(hisUid) && modelChat.getSender().equals(firebaseAuth.getCurrentUser().getUid()))
                    {
                        chatsList.add(modelChat);
                    }
                }

                adapterChat = new AdapterChat(chatsList, getApplicationContext(),dp1);
                if(adapterChat.getItemCount()==0)
                {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    linearLayout.setVisibility(View.GONE);
                }
                adapterChat.notifyDataSetChanged();
                chatsRecycler.setAdapter(adapterChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back(View view) {
        ChatActivity.super.onBackPressed();
    }
}