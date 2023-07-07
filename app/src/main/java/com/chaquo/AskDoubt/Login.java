package com.chaquo.AskDoubt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView question;
    EditText email,pass;
    Button loginButton;
    private FirebaseAuth auth;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        question=findViewById(R.id.loginPageQuestion);
        email=findViewById(R.id.loginusername);
        pass=findViewById(R.id.loginpwd);
        loginButton=findViewById(R.id.LoginButton);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        question.setOnClickListener(view -> startActivity(new Intent(Login.this,Registration.class)));

        loginButton.setOnClickListener(v -> {

            String mail=email.getText().toString();
            String pwd=pass.getText().toString();

            if(TextUtils.isEmpty(mail)){
                email.setError("Email is Required!!");
            } if(TextUtils.isEmpty(pwd)){
                pass.setError("password is Required!!");
            }
            else{

                pd.setMessage("Login in Progress");
                pd.show();

                auth.signInWithEmailAndPassword(mail,pwd).addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        Toast.makeText(Login.this, "Logged in as"+ auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this,HomeActivity.class));
                        finish();
                    }
                    else{

                        Toast.makeText(Login.this, "cant login!", Toast.LENGTH_SHORT).show();

                    }
                });


            }

        });
    }

    @Override
    public void onResume() {


        super.onResume();
if(auth.getCurrentUser()!=null){
    Toast.makeText(Login.this, "Logged in as"+ auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
    startActivity(new Intent(Login.this,HomeActivity.class));
    finish();
}

    }
}