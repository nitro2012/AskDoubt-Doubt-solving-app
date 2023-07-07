package com.chaquo.AskDoubt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chaquo.AskDoubt.Adapters.PostAdapter;
import com.chaquo.AskDoubt.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    private MaterialSearchBar searchBar;
    private List<String> lastSearches;
    private DatabaseReference TagsRef;
    private List<String > posts;
    private List<Post> postList;
    private Toolbar toolbar;
    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private LinearLayout notFound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        recyclerView=findViewById(R.id.postRecSearch);

        notFound=findViewById(R.id.notFound);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        lastSearches=new ArrayList<>();
        lastSearches.add("default");
        searchBar.setSpeechMode(false);

        toolbar=findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Answers");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FFC107"));


        getSupportActionBar().setBackgroundDrawable(colorDrawable);
posts=new ArrayList<>();
        TagsRef= FirebaseDatabase.getInstance().getReference().child("tags");
        searchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
        searchBar.setLastSuggestions(lastSearches);
        searchBar.setHint("Search");


        searchBar.setCardViewElevation(10);
        findViewById(R.id.askQuestionButton).setOnClickListener(view -> startActivity(new Intent(SearchActivity.this,askQuestionActivity.class)));
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(SearchActivity.this,postList);
        recyclerView.setAdapter(postAdapter);




    }
    private void readQuestionPosts() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("questions posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();


                for(String postId:posts){


                    HashMap hm= (HashMap) snapshot.child(postId).getValue();

                    Post post=new Post((String) hm.get("askedBy"),(String) hm.get("date"),(String) hm.get("postId"),(String) hm.get("publisher"),(String) hm.get("question"),(String) hm.get("questionImage"),(String) hm.get("topic"));





                    postList.add(post);
                }



                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Error!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

        posts.clear();
        String txt= text.toString();
        String [] tags=txt.split(" ");

        TagsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        Object t = dataSnapshot.getKey();
                        String tg = (String) t;

                        for (String tag : tags) {

                            if (tg.equals(tag)) {

                                Object obj = dataSnapshot.getValue();
                                HashMap hmap = (HashMap) obj;


                                for (Object s : hmap.keySet()) {
                                    String str = (String) s;

                                    if (!posts.contains(str))
                                        posts.add(str);

                                }


                            }

                        }


                    }

                    if(posts.isEmpty()){

                        notFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);


                    }
                    else{

                        notFound.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        readQuestionPosts();



                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}