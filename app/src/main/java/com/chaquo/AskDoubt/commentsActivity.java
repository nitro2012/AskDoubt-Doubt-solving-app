package com.chaquo.AskDoubt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.AskDoubt.Adapters.CommentAdapter;
import com.chaquo.AskDoubt.Model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class commentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView textView;
    private EditText editText;
    private ImageView imageView;
private ProgressDialog pd;
    private String postId;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        toolbar=findViewById(R.id.commentToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Comments");

        postId=getIntent().getStringExtra("postId");
        recyclerView=findViewById(R.id.commentRec);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);


        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(commentsActivity.this,commentList,postId);

        recyclerView.setAdapter(commentAdapter);

        imageView=findViewById(R.id.profImage);
        textView=findViewById(R.id.comment_post);
        editText=findViewById(R.id.adding_comment);
pd=new ProgressDialog(this);
        textView.setOnClickListener(view -> {
            String commentText=editText.getText().toString();

            if(TextUtils.isEmpty(commentText))
                editText.setError("Please type something");
            else
                addComment();

        });

readComments();
    }

    private void readComments() {


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){


                    Comment comment=snapshot1.getValue(Comment.class);

                    commentList.add(comment);

                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(commentsActivity.this, "Error ", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addComment(){


        pd.setMessage("Adding a Comment");
        pd.setCanceledOnTouchOutside(false);

        pd.show();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("comments").child(postId);
String commentId=reference.push().getKey();

String date= DateFormat.getDateInstance().format(new Date());

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("comment",editText.getText().toString());
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("commentid",commentId);
        hashMap.put("postid",postId);
        hashMap.put("date",date);

        reference.child(commentId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(commentsActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                }
                else{

                    Toast.makeText(commentsActivity.this, "Error adding comment", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                }

                editText.setText("");
            }
        });


    }
}