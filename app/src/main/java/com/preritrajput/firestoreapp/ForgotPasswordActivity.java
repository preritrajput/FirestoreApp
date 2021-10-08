package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText email;
    RelativeLayout register;
    ProgressBar pd;
    TextView textView,error;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.transparent));
        setContentView(R.layout.activity_forgot_password);

        email=findViewById(R.id.signup_email);
        register=findViewById(R.id.relativeLayout2);
        pd=findViewById(R.id.progressBar);
        textView=findViewById(R.id.text);
        error=findViewById(R.id.error);
        firebaseAuth=FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.INVISIBLE);
                String text=email.getText().toString().trim();
                if(TextUtils.isEmpty(text))
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Please provide an email", Toast.LENGTH_SHORT).show();
                    error.setText("Please provide an email.");
                    error.setVisibility(View.VISIBLE);
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches())
                {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Invalid email.");
                }
                else
                {
                    pd.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    firebaseAuth.sendPasswordResetEmail(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.setVisibility(View.INVISIBLE);
                                        textView.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(ForgotPasswordActivity.this,ForgotConfirmationActivity.class);
                                        Toast.makeText(ForgotPasswordActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                                        intent.putExtra("text",text);
                                        startActivity(intent);
                                        finish();
                                    }
                                },4000);
                            }
                            else
                            {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.setVisibility(View.INVISIBLE);
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                },4000);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.setVisibility(View.INVISIBLE);
                                    textView.setVisibility(View.VISIBLE);
                                    error.setVisibility(View.VISIBLE);
                                    error.setText(e.getMessage());
                                }
                            },4000);
                        }
                    });
                }
            }
        });
    }

    public void back(View view) {
        ForgotPasswordActivity.super.onBackPressed();
    }

}