package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText name,phone;
    RelativeLayout change;
    ProgressBar pd;
    TextView textView,error;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();

        name=findViewById(R.id.signup_email);
        phone=findViewById(R.id.change_phone);
        change=findViewById(R.id.relativeLayout2);
        pd=findViewById(R.id.progressBar);
        textView=findViewById(R.id.text);
        error=findViewById(R.id.error);

        db.collection("Users").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc:task.getResult())
                {
                    String name1=""+doc.getString("name");
                    String phone1=""+doc.getString("phone");

                    name.setText(name1);
                    phone.setText(phone1);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.GONE);
                String name1=name.getText().toString().trim();
                String phone1=phone.getText().toString().trim();

                if(!TextUtils.isEmpty(name1) && !TextUtils.isEmpty(phone1))
                {
                    pd.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    HashMap<String, Object> result= new HashMap<>();
                    result.put("name", name1);
                    result.put("phone", phone1);
                    db.collection("Users").document(user.getUid()).update(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            Toast.makeText(EditProfileActivity.this, "Details successfully changed", Toast.LENGTH_SHORT).show();
                            EditProfileActivity.super.onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            error.setVisibility(View.VISIBLE);
                            error.setText(e.getMessage());
                        }
                    });
                }
                else
                {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Please enter all details.");
                }
            }
        });

    }

    public void back(View view) {
        EditProfileActivity.super.onBackPressed();
    }
}