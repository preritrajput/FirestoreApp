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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_change_password);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();

        name=findViewById(R.id.signup_email);
        phone=findViewById(R.id.change_phone);
        change=findViewById(R.id.relativeLayout2);
        pd=findViewById(R.id.progressBar);
        textView=findViewById(R.id.text);
        error=findViewById(R.id.error);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.GONE);
                String pass=name.getText().toString().trim();
                String newpass= phone.getText().toString().trim();

                if (TextUtils.isEmpty(pass)||TextUtils.isEmpty(newpass)) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Please provide password details");
                }
                else
                {
                    pd.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    AuthCredential authCredential= EmailAuthProvider.getCredential(user.getEmail(),pass);
                    user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            user.updatePassword(newpass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.setVisibility(View.INVISIBLE);
                                    textView.setVisibility(View.VISIBLE);
                                    name.setText("");
                                    phone.setText("");
                                    Toast.makeText(ChangePasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.setVisibility(View.INVISIBLE);
                                    textView.setVisibility(View.VISIBLE);
                                    name.setText("");
                                    phone.setText("");
                                    error.setVisibility(View.VISIBLE);
                                    error.setText(e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            name.setText("");
                            phone.setText("");
                            error.setVisibility(View.VISIBLE);
                            error.setText(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void back(View view) {
        ChangePasswordActivity.super.onBackPressed();
    }
}