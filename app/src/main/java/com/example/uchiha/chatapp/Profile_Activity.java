package com.example.uchiha.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Profile_Activity extends AppCompatActivity {
    private TextView user_id;
    private Toolbar pToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        pToolbar=findViewById(R.id.profile_toolbar);
        setSupportActionBar(pToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        user_id=findViewById(R.id.profile_disname);

        String displayname= getIntent().getStringExtra("display_name");
        user_id.setText(displayname);


    }
}
