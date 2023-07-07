package com.chaquo.AskDoubt.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.AskDoubt.Model.Comment;
import com.chaquo.AskDoubt.Model.User;
import com.chaquo.AskDoubt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context context;
    private List<Comment> commentList;
    String postid;

    FirebaseUser firebaseUser;
    public CommentAdapter(Context context, List<Comment> commentList, String postid) {
        this.context = context;
        this.commentList = commentList;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comments_layout,parent,false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Comment comment=commentList.get(position);

        holder.comment.setText(comment.getComment());
        holder.commentDate.setText("commented on :"+comment.getDate());

        getUserInformation(holder.profileImage,holder.commentorUsername,comment.getPublisher());


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

    public ImageView profileImage;
    public TextView commentorUsername,comment,commentDate;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage=itemView.findViewById(R.id.comment_profImage);
        commentorUsername=itemView.findViewById(R.id.commentorUsername);
        comment=itemView.findViewById(R.id.commentorComment);
        commentDate=itemView.findViewById(R.id.commentDate);


    }
}

private  void getUserInformation(ImageView profileImage,TextView username,String publisherid){

    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(publisherid);

    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            username.setText(snapshot.getValue(User.class).getUsername());


        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

        }
    });


}
}
