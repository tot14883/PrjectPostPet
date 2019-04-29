package com.pet.numwan.numwan_pet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText email_input;
    private EditText pass_input;
    private Button btn_signin;
    private Button btn_signup;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//ทำการควบคุมการทำงานทั้งหมดใน 1 layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//ทำการเชื่อมต่อเข้ากับ xml layout ชื่อ activity_main
        getSupportActionBar().hide();

        email_input = (EditText) findViewById(R.id.email_login); //ทำการประกาศหาค่า id ของ element ที่สร้างไว้ในไฟล์ xml
        pass_input = (EditText) findViewById(R.id.pass_login);
        btn_signin = (Button) findViewById(R.id.sign_in_btn);
        btn_signup = (Button) findViewById(R.id.sign_up_btn);

        mAuth = FirebaseAuth.getInstance();// เรียกใช้ Firebase Authentication



        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //คำสั่งเมื่อ button กด
                String email = email_input.getText().toString(); //รับค่ามาจาก Edittext email
               String pass = pass_input.getText().toString();
               if(email.equals(null) || pass.equals(null)){
                   Toast.makeText(getApplication(), "Authentication failed.",
                           Toast.LENGTH_SHORT).show();
               }
               else {
                   mAuth.signInWithEmailAndPassword(email, pass) //ทำกรนำค่าที่ได้มาตรวจสอบสถานะล๊อกอิน
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful()) {//ถ้ามีรหัสนี้อยู่จะเด้งไปอีกหน้านึง
                                       FirebaseUser user = mAuth.getCurrentUser();
                                       UpdateUI(user);
                                   } else {
                                       Log.w("Error", "signInWithEmail:failure", task.getException());
                                       Toast.makeText(getApplication(), "Authentication failed.",
                                               Toast.LENGTH_SHORT).show();
                                       UpdateUI(null);
                                   }
                               }
                           });
               }

            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent register = new Intent(getApplicationContext(),RegisterPage.class);
               startActivity(register);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();//ตรวจสอบว่า๊อคอินอยู่รึป่าว
        UpdateUI(user);

    }
    private void UpdateUI(FirebaseUser user) { // คำสั่งตรวจสอบเมื่อเด้งไปอีกหน้า
        if (user != null) {
            Intent homepage = new Intent(this, HomePAGE.class);
            startActivity(homepage);
            this.finish();
        }
        else{
            System.out.print("Not Login");
        }
    }
}
