package com.chaquo.AskDoubt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.AskDoubt.Adapters.PostAdapter;
import com.chaquo.AskDoubt.Adapters.QuestionAdapter;
import com.chaquo.AskDoubt.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class answerActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private QuestionAdapter qAdapter;
    private List<Post> postList;
    private Toolbar toolbar;
    private ProgressBar pb;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        pb=findViewById(R.id.answer_progress);
        recycler=findViewById(R.id.QArecycler);
        userRef= FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        toolbar=findViewById(R.id.ans_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Answer");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FFC107"));


        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(linearLayoutManager);
        postList=new ArrayList<>();
        qAdapter=new QuestionAdapter(answerActivity.this,postList);
        recycler.setAdapter(qAdapter);

        readQuestionPosts();

    }

    private void readQuestionPosts() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("questions posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Post post=dataSnapshot.getValue(Post.class);
                    postList.add(post);

                }

                qAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(answerActivity.this, "Error!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}