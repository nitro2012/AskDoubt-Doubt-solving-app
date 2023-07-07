package com.chaquo.AskDoubt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaquo.AskDoubt.Adapters.AnswerAdapter;
import com.chaquo.AskDoubt.Adapters.PostAdapter;
import com.chaquo.AskDoubt.Model.Answer;
import com.chaquo.AskDoubt.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewPostsActivity extends AppCompatActivity {
private String imageUrl,postId;

    private RecyclerView recycler;
    private AnswerAdapter adapter;
private TextView questionTextView;
    private Toolbar toolbar;
private List<Answer> ansList;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);



        questionTextView=findViewById(R.id.question_text);
        toolbar=findViewById(R.id.posts_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Answers");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FFC107"));


        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        postId=  getIntent().getStringExtra("postId");


        recycler=findViewById(R.id.viewPostRec);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(linearLayoutManager);
        ansList=new ArrayList<>();
        adapter=new AnswerAdapter(ViewPostsActivity.this,ansList);
        recycler.setAdapter(adapter);

        ref=FirebaseDatabase.getInstance().getReference("questions posts").child(postId);


        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




                    Post post=snapshot.getValue(Post.class);
                    questionTextView.setText(post.getQuestion());







            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPostsActivity.this, "Error!!", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference("answers").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                ansList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Answer ans=dataSnapshot.getValue(Answer.class);
                    ansList.add(ans);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}