package com.example.uchiha.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout displayname;
    private TextInputLayout email;
    private  TextInputLayout password;
    private Button mButton;
    private android.support.v7.widget.Toolbar toolbar;
    private ProgressDialog mProgress;
    private   DatabaseReference database;

    //firebase Auth
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();

        //toolbar
        toolbar=findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress=new ProgressDialog(this);

        displayname=(TextInputLayout) findViewById(R.id.regdisplayname);
        email=(TextInputLayout) findViewById(R.id.regemail);
        password=(TextInputLayout) findViewById(R.id.regpassword);
        mButton=findViewById(R.id.regbutton);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setTitle("Registering user");
                mProgress.setMessage(" Please wait while we create your account");
                mProgress.setCanceledOnTouchOutside(false);

                String disname=displayname.getEditText().getText().toString();
                String Email=email.getEditText().getText().toString();
                String Password=password.getEditText().getText().toString();
                if(!TextUtils.isEmpty(disname) || !TextUtils.isEmpty(Email) || !TextUtils.isEmpty(Password)){
                    mProgress.show();
                    register_user(disname,Email,Password);

                }
            }
        });
    }

    private void register_user(final String disname, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid=user.getUid();
                            // Write a message to the database
                            database = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                            HashMap<String,String> map=new HashMap<>();
                            map.put("name",disname);
                            map.put("status","Hi there, I am using chat app");
                            map.put("image","default");
                            map.put("thumb_image","default");

                            database.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        mProgress.dismiss();
                                        Intent mainintent=new Intent(RegisterActivity.this,MainActivity.class);
                                        mainintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainintent);
                                        finish();

                                    }
                                }
                            });



                        } else {
                            mProgress.hide();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "cannot signing in please check the form and try again",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
