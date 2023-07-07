package com.chaquo.AskDoubt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.AskDoubt.Adapters.PostAdapter;
import com.chaquo.AskDoubt.Model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {
private DrawerLayout drawerLayout;
private Toolbar toolbar;
private FloatingActionButton fab;

private RecyclerView recycler;
private PostAdapter postAdapter;
private List<Post> postList;
private ImageView navHeaderImage;
private TextView navHeaderEmail,navHeaderName;
private ProgressBar pb;
private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_home);

        fab=findViewById(R.id.fab);
        toolbar=findViewById(R.id.home_toolbar);

        pb=findViewById(R.id.progressCircular);
recycler=findViewById(R.id.recycler);
userRef=FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AskDoubt");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FFC107"));


        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        drawerLayout=findViewById(R.id.drawer_layou);

        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);

        drawerLayout.addDrawerListener(toggle );

        toggle.syncState();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(linearLayoutManager);




fab.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this,askQuestionActivity.class)));

navHeaderEmail=navigationView.getHeaderView(0).findViewById(R.id.emailDrawer);
navHeaderName=navigationView.getHeaderView(0).findViewById(R.id.usernameDrawer);
navHeaderImage=navigationView.getHeaderView(0).findViewById(R.id.userImageDrawer);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                navHeaderName.setText(snapshot.child("username").getValue().toString());
                navHeaderEmail.setText(snapshot.child("id").getValue().toString());

                //navHeaderImage.setImageURI(snapshot.child());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(HomeActivity.this, "Error!!", Toast.LENGTH_SHORT).show();
            }
        });

postList=new ArrayList<>();
postAdapter=new PostAdapter(HomeActivity.this,postList);
recycler.setAdapter(postAdapter);

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

                postAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed(){


if(drawerLayout.isDrawerOpen(GravityCompat.START)){
super.onBackPressed();
    drawerLayout.closeDrawer(GravityCompat.START);

}
else
    super.onBackPressed();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_search:
                startActivity(new Intent(HomeActivity.this,SearchActivity.class));


                break;
                case R.id.nav_answer:
                    startActivity(new Intent(HomeActivity.this,answerActivity.class));
                break;
            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this,Login.class));
                    finish();
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}