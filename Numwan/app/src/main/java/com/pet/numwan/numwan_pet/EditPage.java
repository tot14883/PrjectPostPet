package com.pet.numwan.numwan_pet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class EditPage extends AppCompatActivity {
    ImageButton btnOpenGalery;
    public static int RESULT_LOAD_IMAGE = 1;
    Uri SelectedImage = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference refer;
    private EditText name_pet;
    private EditText descrip_pet;
    private EditText Uni_pet;
    private EditText feel_pet;
    private EditText care_pet;
    private EditText History_pet;
    private Button submit_pet;
    FirebaseAuth mAuth;
    String post_key;
    Bitmap bitmap;
    BitmapDrawable ob;
    ImageButton imageview;
    int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);
        post_key = getIntent().getExtras().getString("PostID");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        name_pet = (EditText) findViewById(R.id.Pet_name);
        descrip_pet = (EditText) findViewById(R.id.Pet_description);
        Uni_pet = (EditText) findViewById(R.id.Pet_Uni);
        feel_pet = (EditText) findViewById(R.id.Pet_feel);
        care_pet = (EditText) findViewById(R.id.Pet_care);
        History_pet = (EditText) findViewById(R.id.Pet_history);
        btnOpenGalery = (ImageButton) findViewById(R.id.pet_image);
        imageview = (ImageButton) findViewById(R.id.image_overlap);
        imageview.setVisibility(View.INVISIBLE);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();//เปิด แกลอรี่
                intent.setType("image/*");//เปิดรูปทุกชนิด
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_IMAGE);//ทำการเปิด
            }
        });
        btnOpenGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();//เปิด แกลอรี่
                intent.setType("image/*");//เปิดรูปทุกชนิด
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_IMAGE);//ทำการเปิด
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();//ทำการสร้าง realtime database
        refer = database.getReference("Post").child(post_key);//ทำการเรียก table User และเพิ่ม push เพิ่มสร้าง primary key
        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("pet_name").getValue().toString();
                String des = dataSnapshot.child("pet_des").getValue().toString();
                String uni = dataSnapshot.child("pet_uni").getValue().toString();
                String care = dataSnapshot.child("pet_care").getValue().toString();
                String feel = dataSnapshot.child("pet_feel").getValue().toString();
                String history = dataSnapshot.child("pet_history").getValue().toString();
                String pic = dataSnapshot.child("imageURL").getValue().toString();

                name_pet.setText(name);
                descrip_pet.setText(des);
                Uni_pet.setText(uni);
                feel_pet.setText(feel);
                care_pet.setText(care);
                History_pet.setText(history);


                Picasso.with(getApplicationContext()).load(pic).into(btnOpenGalery);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        submit_pet = (Button) findViewById(R.id.Pet_Submit);
        submit_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            btnOpenGalery.setVisibility(View.INVISIBLE);
            imageview.setVisibility(View.VISIBLE);
            check += 1;
            SelectedImage = data.getData();//เรียกข้อมูลรูปภาพ

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), SelectedImage);//ทำการแปลงข้อมูล
                ob = new BitmapDrawable(getResources(), bitmap);//ทำการแปลงข้อมูลรอบ 2 เพื่อนำไป setBackground
                imageview.setBackgroundDrawable(ob);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void UploadImage(){
        if(SelectedImage != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading....");
            progressDialog.show();
            if (check > 0) {
                final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                ref.putFile(SelectedImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        final String downloadUrl = uri.toString();
                                        refer.child("imageURL").setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        final String name = name_pet.getText().toString();
                                                        final String des = descrip_pet.getText().toString();
                                                        final String uni = Uni_pet.getText().toString();
                                                        final String feel = feel_pet.getText().toString();
                                                        final String care = care_pet.getText().toString();
                                                        final String History = History_pet.getText().toString();
                                                        refer.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                refer.child("pet_name").setValue(name);
                                                                refer.child("pet_des").setValue(des);
                                                                refer.child("pet_uni").setValue(uni);
                                                                refer.child("pet_feel").setValue(feel);
                                                                refer.child("pet_care").setValue(care);
                                                                refer.child("pet_history").setValue(History);

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                });
                                progressDialog.dismiss();
                                Toast.makeText(EditPage.this, "Upload finish ", Toast.LENGTH_SHORT).show();
                                Intent homepage = new Intent(getApplicationContext(), HomePAGE.class);
                                startActivity(homepage);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(EditPage.this, "Upload Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Upload" + (int) progress + "%");
                    }
                });
            }
        }else if(check == 0){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading....");
            progressDialog.show();
               refer.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       final String name = name_pet.getText().toString();
                       final String des = descrip_pet.getText().toString();
                       final String uni = Uni_pet.getText().toString();
                       final String feel = feel_pet.getText().toString();
                       final String care = care_pet.getText().toString();
                       final String History = History_pet.getText().toString();

                       refer.child("pet_name").setValue(name);
                       refer.child("pet_des").setValue(des);
                       refer.child("pet_uni").setValue(uni);
                       refer.child("pet_feel").setValue(feel);
                       refer.child("pet_care").setValue(care);
                       refer.child("pet_history").setValue(History);

                       progressDialog.dismiss();
                       Toast.makeText(EditPage.this, "Upload Success", Toast.LENGTH_SHORT).show();

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {
                       progressDialog.dismiss();
                       Toast.makeText(EditPage.this, "Upload Failed" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                   }

               });
               Intent homepage = new Intent(getApplicationContext(), HomePAGE.class);
               startActivity(homepage);
               finish();
        }
    }
}
