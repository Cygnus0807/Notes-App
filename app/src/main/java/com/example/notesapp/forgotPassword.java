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
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {

    private EditText mforgotpassword;
    private Button mpasswordrecoverybutton;
    private TextView mgobacktologin;

//   taking instance of firebaseAuth if someone forgot there password
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        //        hiding action bar
        getSupportActionBar().hide();

//        assigning ids connecting between xml to java
        mforgotpassword=findViewById(R.id.forgotpassword);
        mpasswordrecoverybutton=findViewById(R.id.passwordrecoverbutton);
        mgobacktologin=findViewById(R.id.gobacktologin);

//        recovering password of current instance of the user
        firebaseAuth=FirebaseAuth.getInstance();

//        intent on clicking go back to login page
        mgobacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgotPassword.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //        after clicking passwword recover button send verification email
        mpasswordrecoverybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                storing mail written by user
                String mail = mforgotpassword.getText().toString().trim();
                if (mail.isEmpty()){
//                    mail place is empty show this line
                    Toast.makeText(getApplicationContext(),"Enter your mail first!!",Toast.LENGTH_SHORT).show();
                }
                else {
//                    if mail place is field sent recover password mail
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Mail Sent,you can recover your password through mail", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgotPassword.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Email is wrong or Account doesn't exists.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
}