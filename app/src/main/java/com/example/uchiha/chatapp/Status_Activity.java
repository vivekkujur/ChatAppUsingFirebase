package com.example.uchiha.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Status_Activity extends AppCompatActivity {

    private TextInputLayout textInputLayout;
    private Button button;
    private DatabaseReference databaseReference;
    private FirebaseUser User;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_);

        User= FirebaseAuth.getInstance().getCurrentUser();
        String Uid=User.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User").child(Uid);

        textInputLayout=findViewById(R.id.status_textchange);
        button=findViewById(R.id.statuschange_btn);

        String mStatus=getIntent().getStringExtra("status_value");
        textInputLayout.getEditText().setText(mStatus);

        toolbar=findViewById(R.id.status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Status");



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status=textInputLayout.getEditText().getText().toString();
                databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(Status_Activity.this, "Status Change Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });




    }
}
