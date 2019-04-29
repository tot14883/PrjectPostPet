package com.pet.numwan.numwan_pet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomePAGE extends AppCompatActivity {
    private FirebaseAuth mAuth;
    AlertDialog.Builder builder;
    AlertDialog.Builder build;
    private DatabaseReference mDatabase;
    private Query cAuth;
    FirebaseUser user;
    private RecyclerView mRecycle;
    FloatingActionButton floatingActionButton;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mRecycle = (RecyclerView) findViewById(R.id.recycle_view);
        mRecycle.setHasFixedSize(true);
        mRecycle.setLayoutManager(new LinearLayoutManager(this));

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent post = new Intent(getApplicationContext(),PostPage.class);
                startActivity(post);
            }
        });
        floatingActionButton.hide();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cAuth = FirebaseDatabase.getInstance().getReference("User").child(user.getUid()).child("status");
                cAuth.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue().toString().equals("admin")) {
                            floatingActionButton.show();

                        } else {
                            floatingActionButton.hide();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        },3000);
        mDatabase = FirebaseDatabase.getInstance().getReference("Post");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.action_sign_out:
             builder = new AlertDialog.Builder(HomePAGE.this);
             builder.setMessage("คุณต้องการออกจากระบบจริงหรือไม่ ?");
             builder.setPositiveButton("ใช้", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     mAuth.signOut();
                     Intent loginpage = new Intent(HomePAGE.this, MainActivity.class);
                     startActivity(loginpage);
                     finish();

                 }
             });
             builder.setNegativeButton("ไม่",null);
             builder.show();break;
            case R.id.action_admin:

                Intent loginpage = new Intent(HomePAGE.this, AdminPage.class);
                startActivity(loginpage);
                finish();


        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        UpdateUI(user);

        FirebaseRecyclerOptions<Card_Pet> options =
                new FirebaseRecyclerOptions.Builder<Card_Pet>()
                .setQuery(mDatabase, Card_Pet.class)
                .build();
        FirebaseRecyclerAdapter<Card_Pet,Card_View> adapter = new FirebaseRecyclerAdapter<Card_Pet, Card_View>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Card_View holder, int position, @NonNull Card_Pet model) {
               final String post_key = getRef(position).getKey().toString();
               holder.setTitle(model.getPet_name());
               holder.setimageView(getApplicationContext(),model.getImageURL());

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cAuth = FirebaseDatabase.getInstance().getReference("User").child(user.getUid()).child("status");
                        cAuth.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue().toString().equals("admin")) {
                                    holder.settingBtn.setVisibility(View.VISIBLE);

                                } else {
                                    floatingActionButton.hide();
                                    holder.settingBtn.setVisibility(View.INVISIBLE);
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                },1000);
               holder.settingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        build = new AlertDialog.Builder(HomePAGE.this);
                        build.setMessage("คุณต้องลบโพสนี้หรือไม่ ?");
                        build.setPositiveButton("ใช้", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDatabase.child(post_key).removeValue();

                            }
                        });
                        build.setNegativeButton("ไม่",null);
                        build.show();
                    }
                });
               holder.mView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent detailPage = new Intent(getApplicationContext(),DetailPage.class);
                       detailPage.putExtra("PostID",post_key);
                       startActivity(detailPage);
                   }
               });


            }

            @NonNull
            @Override
            public Card_View onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.card_pet,viewGroup,false);
                return new Card_View(view);
            }
        };
        mRecycle.setAdapter(adapter);
        adapter.startListening();

    }
    private void UpdateUI(FirebaseUser user) {
        if (user != null) {
            System.out.print("Exist");
        }
        else{
            Intent loginpage = new Intent(this, MainActivity.class);
            startActivity(loginpage);
            this.finish();
        }
    }
    public static class Card_View extends RecyclerView.ViewHolder{
        View mView;
        Button settingBtn;
        public Card_View(View itemView){
             super(itemView);
             mView = itemView;
             settingBtn = (Button) itemView.findViewById(R.id.overflow);
             settingBtn.setVisibility(View.INVISIBLE);
        }
        public void setTitle(String vtitle){
            TextView title = (TextView) mView.findViewById(R.id.vname);
            title.setText("ชื่อพันธุ์: "+vtitle);
        }
        public void setimageView(final Context ctx, final String image){
            final ImageView imgView = (ImageView) mView.findViewById(R.id.thumbnail);
            Picasso.with(ctx).load(image).into(imgView);
        }
    }
}

