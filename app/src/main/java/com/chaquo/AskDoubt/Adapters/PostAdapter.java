package com.chaquo.AskDoubt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chaquo.AskDoubt.Model.Post;
import com.chaquo.AskDoubt.Model.User;
import com.chaquo.AskDoubt.R;
import com.chaquo.AskDoubt.ViewPostsActivity;
import com.chaquo.AskDoubt.commentsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{
public Context context;
public List<Post> postList;
private FirebaseUser  firebaseUser;
    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.questions_retrieved_layout,parent,false);


        return new PostAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final Post post=postList.get(position);

        if(post.getQuestionImage()==null)
            holder.questionImage.setVisibility(View.GONE);

        else

            holder.questionImage.setVisibility(View.VISIBLE);
        Glide.with(context).load(post.getQuestionImage()).into(holder.questionImage);
        holder.expandableTextView.setText(post.getQuestion());

        holder.askedOnTextView.setText(post.getDate());


        publisherInfo(holder.publisher_profile_image,holder.askedBy,post.getPublisher());

        isLiked(post.getPostId(),holder.like);

        isDisLiked(post.getPostId(), holder.dislike);

        getLikes(holder.likes,post.getPostId());
        getDisLikes(holder.dislikes,post.getPostId());

        holder.like.setOnClickListener(v -> {
            if (holder.like.getTag().equals("like") && holder.dislike.getTag().equals("dislike")){
                FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);

            }
            else if (holder.like.getTag().equals("like") && holder.dislike.getTag().equals("disliked")){
                FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);


            }else {
                FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.dislike.setOnClickListener(v -> {
            if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("like")){
                FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
            }else if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("liked")){
                FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
            }else {
                FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
            }
        });


        holder.comment.setOnClickListener(view -> {


            Intent intent=new Intent(context, commentsActivity.class);

            intent.putExtra("postId",post.getPostId());
            intent.putExtra("publisher",post.getPublisher());
            context.startActivity(intent);
        });


        holder.answers.setOnClickListener(view -> {

            Intent intent=new Intent(context, ViewPostsActivity.class);

            intent.putExtra("postId",post.getPostId());

            String pimage=null;

            if(post.getQuestionImage()!=null)
                pimage= post.getQuestionImage();

            intent.putExtra("postImage",pimage);

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

public ImageView publisher_profile_image,more,questionImage,like,dislike,comment,answers;
public TextView askedBy,askedOnTextView,likes,dislikes,comments;
public TextView expandableTextView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            publisher_profile_image=itemView.findViewById(R.id.publisherProfile);
            askedBy=itemView.findViewById(R.id.asked_by);
            likes=itemView.findViewById(R.id.likes);
            dislikes=itemView.findViewById(R.id.dislikes);
            comments=itemView.findViewById(R.id.comments);
            more=itemView.findViewById(R.id.more);
            questionImage=itemView.findViewById(R.id.questionImageCard);
            like=itemView.findViewById(R.id.like);
            dislike=itemView.findViewById(R.id.dislike);
            comment=itemView.findViewById(R.id.comment);
            answers=itemView.findViewById(R.id.answers);

            askedOnTextView=itemView.findViewById(R.id.askedOnTextView);
            expandableTextView=itemView.findViewById(R.id.expandable_text);

        }
    }
private void publisherInfo(ImageView pImage,TextView askedBy,String userId){

    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(userId);

    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            User user=snapshot.getValue(User.class);

            askedBy.setText(user.getFullname());


        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            Toast.makeText(context, "Error fetching publisher ", Toast.LENGTH_SHORT).show();
        }
    });

}

private void isLiked(String postid,ImageView imageView){

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){

                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }

                else{
                    imageView.setImageResource(R.drawable.ic_thumb);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}
private void isDisLiked(String postid,ImageView imageView){

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("dislikes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){

                    imageView.setImageResource(R.drawable.ic_disliked);
                    imageView.setTag("disliked");
                }

                else{
                    imageView.setImageResource(R.drawable.ic_thumb_down);
                    imageView.setTag("dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}

private void getLikes(TextView likes,String postid){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                long numberOfLikes=snapshot.getChildrenCount();

                int NOL=(int) numberOfLikes;

                if(NOL>1)
                    likes.setText(snapshot.getChildrenCount()+"likes");
                else if(NOL==0)
                    likes.setText("0 likes");

                else
                    likes.setText(snapshot.getChildrenCount()+"like");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });


}
private void getDisLikes(TextView likes,String postid){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("dislikes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                long numberOfDislikes=snapshot.getChildrenCount();

                int NOD=(int) numberOfDislikes;

                if(NOD>1)
                    likes.setText(snapshot.getChildrenCount()+"dislikes");
                else if(NOD==0)
                    likes.setText("0 dislikes");

                else
                    likes.setText(snapshot.getChildrenCount()+"dislike");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });


}
}
