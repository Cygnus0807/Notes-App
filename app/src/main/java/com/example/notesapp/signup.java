package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    private EditText msignupemail, msignuppassword;
    private Button msignup;
    private TextView mgotologin;


    ///~~~~~~~Adding user for test ~~~~~~~~~~~~~~~~~~~~
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //        hiding action bar
        getSupportActionBar().hide();
//        xml id to java ide
        msignupemail= findViewById(R.id.signupemail);
        msignuppassword= findViewById(R.id.signuppassword);
        msignup= findViewById(R.id.signup);
        mgotologin = findViewById(R.id.gotologin);

        ///adding fake user
        firebaseAuth = FirebaseAuth.getInstance();

        mgotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this,MainActivity.class);
                startActivity(intent);
            }
        });

//        after clicking on signup registering user om firebase

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                chcking user insert details in editext or not
//                storing input of editext in 2variables 1.email 2.password
                String mail=msignupemail.getText().toString().trim();
                String password =msignuppassword.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"all fields are required",Toast.LENGTH_SHORT).show();
                }
//                password is greater than 7 letters
                else if (password.length()<7) {
                    Toast.makeText(getApplicationContext(),"password should atleast be 8 digits",Toast.LENGTH_SHORT).show();
                }
                else {

                    ////////Register the user to fire base

                    ///registering user
                    firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){
                              Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
//                              email enter by user is correct or not
//                              sending verification email to user
                              sendEmailVerification();
                          }
                          else {
//                             if any error occurred
                              Toast.makeText(getApplicationContext(), "Failed to Register", Toast.LENGTH_SHORT).show();
                          }
                        }
                    });

                }
            }
        });

    }

//    send verification email
    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification Email is sent,Verify it and Login Again", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
//                    again login for user
                    startActivity(new Intent(signup.this, MainActivity.class));

                }
            });
        }
        else {
//            if any error occurred
            Toast.makeText(getApplicationContext(), "Failed to Send Verification Email", Toast.LENGTH_SHORT).show();
        }
    }
}