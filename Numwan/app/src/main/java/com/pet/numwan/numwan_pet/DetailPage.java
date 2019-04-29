package com.pet.numwan.numwan_pet;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailPage extends AppCompatActivity {
    private TextView pet_name;
    private TextView pet_des;
    private TextView pet_uni;
    private TextView pet_care;
    private TextView pet_feel;
    private TextView pet_history;
    private Button btn_edit;
    private ImageView imgView;
    private String post_key;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Query cAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        post_key = getIntent().getExtras().getString("PostID");

        btn_edit = (Button) findViewById(R.id.edit_btn);
        pet_name =(TextView) findViewById(R.id.name_pet);
        pet_des = (TextView) findViewById(R.id.des_pet);
        pet_uni = (TextView) findViewById(R.id.uni_pet);
        pet_care = (TextView) findViewById(R.id.care_pet);
        pet_feel = (TextView) findViewById(R.id.feel_pet);
        pet_history = (TextView) findViewById(R.id.history_pet);
        imgView = (ImageView) findViewById(R.id.image_pet);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        cAuth = FirebaseDatabase.getInstance().getReference("User").child(user.getUid()).child("status");
        cAuth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("admin")) {
                    btn_edit.setVisibility(View.VISIBLE);

                } else {
                    btn_edit.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EditPage.class);
                intent.putExtra("PostID",post_key);
                startActivity(intent);
            }
        });
        mDatabase =  FirebaseDatabase.getInstance().getReference("Post").child(post_key);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("pet_name").getValue().toString();
                String des = dataSnapshot.child("pet_des").getValue().toString();
                String uni = dataSnapshot.child("pet_uni").getValue().toString();
                String care = dataSnapshot.child("pet_care").getValue().toString();
                String feel = dataSnapshot.child("pet_feel").getValue().toString();
                String history = dataSnapshot.child("pet_history").getValue().toString();
                String pic = dataSnapshot.child("imageURL").getValue().toString();
                pet_name.setText("ชือพันธุ์ \n"+name);
                pet_des.setText("รายละเอียด \n"+des);
                pet_uni.setText("เอกลักษณ์ \n"+uni);
                pet_care.setText("วิธีดูแล \n"+care);
                pet_feel.setText("นิสัย \n"+feel);
                pet_history.setText("ประวัติ \n"+history);
                Picasso.with(getApplicationContext()).load(pic).into(imgView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
