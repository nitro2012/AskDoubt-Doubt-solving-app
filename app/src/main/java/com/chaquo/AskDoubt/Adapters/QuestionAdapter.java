package com.chaquo.AskDoubt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chaquo.AskDoubt.Model.Post;
import com.chaquo.AskDoubt.Model.User;
import com.chaquo.AskDoubt.R;
import com.chaquo.AskDoubt.WriteAnswerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class QuestionAdapter  extends RecyclerView.Adapter<QuestionAdapter.viewHolder> {

    public Context context;
    public List<Post> postList;
    private FirebaseUser firebaseUser;

    public QuestionAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.answer_questions_layout,parent,false);


        return new QuestionAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Post post=postList.get(position);

        if(post.getQuestionImage()==null)
            holder.questionImage.setVisibility(View.GONE);

        else

            holder.questionImage.setVisibility(View.VISIBLE);
        Glide.with(context).load(post.getQuestionImage()).into(holder.questionImage);
        holder.expandableTextView.setText(post.getQuestion());

        holder.askedOnTextView.setText(post.getDate());


        publisherInfo(holder.publisher_profile_image,holder.askedBy,post.getPublisher());

        holder.answerButton.setOnClickListener(view -> context.startActivity(new Intent(context, WriteAnswerActivity.class).putExtra("postId",post.getPostId())));


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public ImageView publisher_profile_image,more,questionImage;
        public TextView askedBy,askedOnTextView;
        public TextView expandableTextView;
        public Button answerButton;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            publisher_profile_image=itemView.findViewById(R.id.publisherProfile);
            askedBy=itemView.findViewById(R.id.asked_by);
            answerButton=itemView.findViewById(R.id.answerButton);

            more=itemView.findViewById(R.id.more);
            questionImage=itemView.findViewById(R.id.questionImageCard);


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

}
