package com.chaquo.AskDoubt;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registration extends AppCompatActivity {
private Button register;
private EditText userName,fullName,email,password;
private FirebaseAuth auth;
private DatabaseReference reference;
private ProgressDialog pd;
private String onlineUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        userName=findViewById(R.id.userName);
        fullName=findViewById(R.id.fname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.pwd);

        register=findViewById(R.id.registerButton);




findViewById(R.id.regPageQuestion).setOnClickListener(v -> startActivity(new Intent(Registration.this,Login.class)));
pd=new ProgressDialog(this);
        register.setOnClickListener(v -> {

            String user=userName.getText().toString();
            String name=fullName.getText().toString();
            String mail=email.getText().toString();
            String pass=password.getText().toString();

            if(TextUtils.isEmpty(user)){
                userName.setError("Username is Required!!");
            } if(TextUtils.isEmpty(name)){
                fullName.setError("name is Required!!");
            } if(TextUtils.isEmpty(mail)){
                email.setError("email is Required!!");
            } if(TextUtils.isEmpty(pass)){
                password.setError("password is Required!!");
            }
else{

pd.setMessage("Registration in Progress");
pd.show();
auth=FirebaseAuth.getInstance();
auth.createUserWithEmailAndPassword(mail,pass).addOnSuccessListener(task -> {
   /* if(!task.isSuccessful()){
        Toast.makeText(Registration.this, "Registration unsuccessful!", Toast.LENGTH_SHORT).show();
    }*/
   // else {

    try {
        sleep(2000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    if(auth.getCurrentUser()!=null)
        onlineUserId = auth.getCurrentUser().getUid();

    else {

        auth.signInWithEmailAndPassword(mail,pass);
        onlineUserId = auth.getCurrentUser().getUid();
    }

        while(onlineUserId.isEmpty()){}
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserId);
        Map hashMap = new HashMap();
        hashMap.put("username", user);
        hashMap.put("fullname", name);
        hashMap.put("id", mail);
        hashMap.put("password", pass);

        reference.updateChildren(hashMap).addOnCompleteListener(task1 -> {

            if (task1.isSuccessful()) {


                Toast.makeText(Registration.this, "Details Set successfully!!", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(Registration.this, "Details Set failed!!", Toast.LENGTH_SHORT).show();

            }
            pd.dismiss();
            startActivity(new Intent(Registration.this, HomeActivity.class));
            finish();




        });



   // }
});



            }
        });

    }
}