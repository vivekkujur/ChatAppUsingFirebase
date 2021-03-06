package com.example.uchiha.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Setting_Activity extends AppCompatActivity {

    private DatabaseReference mdatabase;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private Button imagechangeBtn, statuschangeBtn;
    private TextView displayname, status;
    private CircleImageView circleImageView;
    String Status;
    private static int gallery_pick=1;
    private  Bitmap thumb_bitmapimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("User").child(uid);
        storageReference=FirebaseStorage.getInstance().getReference().child("profile_image");



        displayname=findViewById(R.id.setting_displayname);
        status=findViewById(R.id.setting_status);
        imagechangeBtn=findViewById(R.id.setting_changeimage);
        statuschangeBtn= findViewById(R.id.setting_changestatus);
        circleImageView=findViewById(R.id.setting_image);

        imagechangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryintnt= new Intent();
                galleryintnt.setType("image/*");
                galleryintnt.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintnt,"SELECT IMAGE"),gallery_pick);
            }
        });




        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //  Toast.makeText(Setting_Activity.this,dataSnapshot.toString(),Toast.LENGTH_LONG).show();

                String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                Status= dataSnapshot.child("status").getValue().toString();
                String thumbimage=dataSnapshot.child("thumb_image").getValue().toString();

                displayname.setText(name);
                status.setText(Status);
                if(!thumbimage.equals("default"))
                Picasso.get().load(thumbimage).placeholder(R.drawable.user).into(circleImageView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        statuschangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusintent= new Intent(Setting_Activity.this,Status_Activity.class);
                statusintent.putExtra("status_value",Status);
                startActivity(statusintent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallery_pick && resultCode==RESULT_OK){

            Uri imageuri=data.getData();
            CropImage.activity(imageuri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File thumb_file=new File(resultUri.getPath());

                try {
                     thumb_bitmapimage = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_file);


                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmapimage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                String filename=randomq();
                StorageReference filepath = storageReference.child(filename+".jpg");
                final StorageReference thumb_filestorage = storageReference.child(filename+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filestorage.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadurl=thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful()){

                                        Map<String, Object> map=new HashMap<>();
                                        map.put("image",downloadUrl);
                                        map.put("thumb_image",thumb_downloadurl);

                                       mdatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(Setting_Activity.this, "upload successfull", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else{

                                        Toast.makeText(Setting_Activity.this, "error in uploading thumbnail", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });


                        }
                        else{

                            Toast.makeText(Setting_Activity.this, "error in uploading", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                /*filepath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
//                              Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                mdatabase.child("image").setValue(downloadUrl);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });*/
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private  static String randomq()
    {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            if(randomLength == 0) randomLength = 10 ;
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
