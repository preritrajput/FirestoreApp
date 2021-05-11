package com.preritrajput.firestoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    String personName;

    TextInputEditText name,email,password,phone;
    RelativeLayout register,google;
    ProgressBar pd;
    TextView textView,error;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black_trans80));
        setContentView(R.layout.activity_sign_up);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        mAuth=FirebaseAuth.getInstance();

        name=findViewById(R.id.user_name);
        email=findViewById(R.id.signup_email);
        password=findViewById(R.id.signup_pass);
        phone=findViewById(R.id.signup_phone);
        register=findViewById(R.id.relativeLayout2);
        pd=findViewById(R.id.progressBar);
        textView=findViewById(R.id.text);
        error=findViewById(R.id.error);
        google=findViewById(R.id.relativeLayout3);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.GONE);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.GONE);
                String uemail = email.getText().toString().trim();
                String upassword = password.getText().toString().trim();
                String uname= name.getText().toString().trim();
                String uphone= phone.getText().toString().trim();

                if(TextUtils.isEmpty(uemail) || TextUtils.isEmpty(upassword) || TextUtils.isEmpty(uname) || TextUtils.isEmpty(uphone))
                {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Please provide all the details.");
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches())
                {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Invalid email.");
                }
                else
                {
                    pd.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    register.setClickable(false);
                    mAuth.createUserWithEmailAndPassword(uemail, upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        String email = user.getEmail();
                                        String  uid= user.getUid();

                                        HashMap<Object,String> hashMap= new HashMap<>();
                                        hashMap.put("email",email);
                                        hashMap.put("uid",uid);
                                        hashMap.put("name",uname);
                                        hashMap.put("phone",uphone);
                                        hashMap.put("image","");
                                        hashMap.put("cover","");

                                        DatabaseReference db=FirebaseDatabase.getInstance().getReference("Users");
                                        db.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                pd.setVisibility(View.INVISIBLE);
                                                textView.setVisibility(View.VISIBLE);
                                                register.setClickable(true);
                                                Intent intent = new Intent(SignUpActivity.this,DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.setVisibility(View.INVISIBLE);
                                                textView.setVisibility(View.VISIBLE);
                                                register.setClickable(true);
                                                error.setVisibility(View.VISIBLE);
                                                error.setText(e.getMessage());
                                            }
                                        });

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
                                        register.setClickable(true);
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
                                    register.setClickable(true);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        pd.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
        google.setClickable(false);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    if (acct != null) {
                        personName = acct.getDisplayName();
                    }
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                String email = user.getEmail();
                                String  uid= user.getUid();

                                Map<Object,String> hashMap= new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name",personName);
                                hashMap.put("phone","");
                                hashMap.put("image","");
                                hashMap.put("cover","");

                                DatabaseReference db=FirebaseDatabase.getInstance().getReference("Users");
                                db.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.setVisibility(View.INVISIBLE);
                                        textView.setVisibility(View.VISIBLE);
                                        google.setClickable(true);
                                        Intent intent = new Intent(SignUpActivity.this,DashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.setVisibility(View.INVISIBLE);
                                        textView.setVisibility(View.VISIBLE);
                                        google.setClickable(true);
                                        error.setVisibility(View.VISIBLE);
                                        error.setText(e.getMessage());
                                    }
                                });
                            }
                            Intent intent = new Intent(SignUpActivity.this,DashboardActivity.class);
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
                            google.setClickable(true);
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
                        google.setClickable(true);
                        error.setVisibility(View.VISIBLE);
                        error.setText(e.getMessage());
                    }
                },4000);
            }
        });
    }

    public void login(View view) {
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }

    public void back(View view) {
        final AlertDialog.Builder alert= new AlertDialog.Builder(SignUpActivity.this,R.style.AlertDialogTheme);
        View viewm=getLayoutInflater().inflate(R.layout.back_dialog,null);

        Button cancel=(Button)viewm.findViewById(R.id.cancel_button);
        Button logout=(Button)viewm.findViewById(R.id.logout_btn);

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
                alertDialog.dismiss();
                SignUpActivity.super.onBackPressed();
                finish();
            }
        });
        alertDialog.show();
        alertDialog.getWindow().setLayout(700,700);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alert= new AlertDialog.Builder(SignUpActivity.this,R.style.AlertDialogTheme);
        View viewm=getLayoutInflater().inflate(R.layout.back_dialog,null);

        Button cancel=(Button)viewm.findViewById(R.id.cancel_button);
        Button logout=(Button)viewm.findViewById(R.id.logout_btn);

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
                alertDialog.dismiss();
                SignUpActivity.super.onBackPressed();
                finish();
            }
        });
        alertDialog.show();
        alertDialog.getWindow().setLayout(700,700);
    }
}