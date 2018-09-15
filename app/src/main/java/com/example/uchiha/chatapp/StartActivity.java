package com.example.uchiha.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Button regButton;
    private Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

          regButton=findViewById(R.id.startRegbtn);
          regButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent regintent= new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(regintent);

            }
        });

          loginbtn=findViewById(R.id.start_login);
          loginbtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent loginintent= new Intent(StartActivity.this,LoginActivity.class);
                  startActivity(loginintent);
              }
          });
    }


}
