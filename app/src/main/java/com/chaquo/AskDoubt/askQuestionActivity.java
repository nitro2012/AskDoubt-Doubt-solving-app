package com.chaquo.AskDoubt;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class askQuestionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText question;

    private ImageView imageView;
    private Button cancel,post;

    private ProgressDialog pd;

    private String askedBy,  myURL;

    private DatabaseReference askedByRef;

    private StorageReference storageReference;
    private StorageTask uploadTask;

    private Uri imageUri;
    private FirebaseAuth auth;
    private String onlineUser;
    private ActivityResultLauncher<String> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {

                    imageView.setImageURI(uri);
                    imageUri=uri;
                }
                );

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_ask_question);


        toolbar=findViewById(R.id.question_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ask a Question");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FFC107"));


        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        imageView=findViewById(R.id.questionImage);

question=findViewById(R.id.questionText);

cancel=findViewById(R.id.cancel);
post=findViewById(R.id.PostQuestion);

pd=new ProgressDialog(this);

auth= FirebaseAuth.getInstance();
onlineUser=auth.getCurrentUser().getUid();

askedByRef= FirebaseDatabase.getInstance().getReference("users").child(onlineUser);

askedByRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        askedBy=snapshot.child("fullname").getValue(String.class);

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

storageReference= FirebaseStorage.getInstance().getReference("questions");





        imageView.setOnClickListener(view -> activityResultLauncher.launch("image/*"));

        cancel.setOnClickListener(view -> finish());

        post.setOnClickListener(view -> performValidations());
    }

    String date= DateFormat.getDateInstance().format(new Date());
    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("questions posts");



    private void performValidations() {

        String ques=question.getText().toString().trim();
        if(ques.isEmpty()){

            question.setError("Question Required ");


        }



        if(!ques.isEmpty()) {

            if(imageUri==null)
            uploadDataNoImage();

            else
                uploadData();
        }



    }

    private void uploadData() {

        startProgress();
        final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

        uploadTask=fileReference.putFile(imageUri);

        uploadTask.continueWithTask(task -> {

            if(!task.isComplete()){

                throw task.getException();

            }
            return fileReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                Uri downloadUri= (Uri) task.getResult();
                myURL=downloadUri.toString();
                String postid=ref.push().getKey();

                HashMap<String,Object> hashMap=new HashMap<>();

                hashMap.put("postId",postid);
                hashMap.put("question",question.getText().toString().trim());
                hashMap.put("publisher",onlineUser);
                hashMap.put("topic","");
                hashMap.put("askedBy",askedBy);
                hashMap.put("questionImage",myURL);
                hashMap.put("date",date);
                assert postid != null;
                ref.child(postid).setValue(hashMap).addOnCompleteListener(task1 -> {

                    if(task1.isSuccessful()){

                        Toast.makeText(askQuestionActivity.this, "Question Posted Successfully", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        startActivity(new Intent(askQuestionActivity.this,HomeActivity.class));
                        finish();

                    }

                    else{

                        Toast.makeText(askQuestionActivity.this, "could not upload", Toast.LENGTH_SHORT).show();

                        pd.dismiss();
                    }

                });
            }

        }).addOnFailureListener(e -> Toast.makeText(askQuestionActivity.this, "Failed to upload question", Toast.LENGTH_SHORT).show());


    }

    private void uploadDataNoImage() {

        startProgress();
        String postid=ref.push().getKey();

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("postId",postid);
        hashMap.put("question",question.getText().toString().trim());
        hashMap.put("publisher",onlineUser);
        hashMap.put("topic", "");
        hashMap.put("askedby",askedBy);
        hashMap.put("date",date);

        assert postid != null;
        ref.child(postid).setValue(hashMap).addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                Toast.makeText(askQuestionActivity.this, "Question Posted Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                startActivity(new Intent(askQuestionActivity.this,HomeActivity.class));
                finish();

            }

            else{

                Toast.makeText(askQuestionActivity.this, "could not upload", Toast.LENGTH_SHORT).show();

                pd.dismiss();
            }

        });

    }

    public void startProgress(){

        pd.setMessage("posting your question");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

}