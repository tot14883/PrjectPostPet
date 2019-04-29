package com.pet.numwan.numwan_pet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterPage extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText pass;
    private EditText repass;
    private EditText phone;
    private Button submit;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.name_regis);
        email = (EditText) findViewById(R.id.email_regis);
        pass = (EditText) findViewById(R.id.pass_regis);
        repass = (EditText) findViewById(R.id.re_pass_regis);
        phone = (EditText) findViewById(R.id.phone_regis);
        submit = (Button) findViewById(R.id.submit_btn);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();//ทำการสร้าง realtime database
        ref = database.getReference("User");//ทำการสร้าง table User

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name_in = name.getText().toString();
                final String email_in = email.getText().toString();
                final String pass_in = pass.getText().toString();
                String repass_in = repass.getText().toString();
                final String phone_in = phone.getText().toString();
                if(!pass_in.equals(repass_in)){
                    Toast.makeText(getApplication(), "Password not match!! failed.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                   mAuth.createUserWithEmailAndPassword(email_in,pass_in)
                   .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            ref.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   ref.child(user.getUid()).child("email").setValue(email_in);
                                   ref.child(user.getUid()).child("name").setValue(name_in);
                                   ref.child(user.getUid()).child("phone").setValue(phone_in);
                                   ref.child(user.getUid()).child("status").setValue("people");
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
                           Toast.makeText(getApplication(), "Success !!!",
                                   Toast.LENGTH_SHORT).show();
                           Intent main = new Intent(getApplicationContext(),MainActivity.class);
                           startActivity(main);
                           finish();
                       }
                   }) ;
                }
            }
        });
    }
}
