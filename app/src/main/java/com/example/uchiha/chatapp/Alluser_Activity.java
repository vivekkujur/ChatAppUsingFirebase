package com.example.uchiha.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Alluser_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView all_users_recycler;
    private DatabaseReference mref;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluser_);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

        mref=FirebaseDatabase.getInstance().getReference().child("User");

        toolbar=findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        all_users_recycler=findViewById(R.id.users_recycler);
        all_users_recycler.setHasFixedSize(true);
        all_users_recycler.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(mref,users.class)
                        .build();

        FirebaseRecyclerAdapter<users,userViewholder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<users, userViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull userViewholder holder, int position, @NonNull users model) {
                holder.setDisplayname(model.getName());
                holder.setStatus(model.getStatus());
                holder.setThumb_image(model.getThumb_image());
                final String Uid= firebaseUser.getUid();


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile_intent=new Intent(Alluser_Activity.this,Profile_Activity.class);
                        profile_intent.putExtra("display_name",Uid);
                        startActivity(profile_intent);
                    }
                });

            }

            @NonNull
            @Override
            public userViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_recycler,parent,false);
                return new userViewholder(view);

            }
        };

        //       recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        all_users_recycler.setAdapter(firebaseRecyclerAdapter);
    }

    public class userViewholder extends RecyclerView.ViewHolder {

        View mView;
        TextView displayname,status;
        CircleImageView thumb_image;


        public userViewholder(View itemView) {
            super(itemView);
            mView=itemView;
            displayname=mView.findViewById(R.id.user_disname);
            status=mView.findViewById(R.id.user_status);
            thumb_image=mView.findViewById(R.id.user_thumbimage);

        }

        public void setDisplayname(String name){
            displayname.setText(name);
        }

        public  void setStatus (String stat){
            status.setText(stat);
        }
        public void setThumb_image(String thumbnailimage){
            Picasso.get().load(thumbnailimage).placeholder(R.drawable.user).into(thumb_image);
        }


    }
}
