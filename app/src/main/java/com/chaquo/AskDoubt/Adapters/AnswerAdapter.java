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
import com.chaquo.AskDoubt.FullPage;
import com.chaquo.AskDoubt.Model.Answer;
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

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.viewHolder>{
public Context context;
public List<Answer> answerList;
private FirebaseUser  firebaseUser;
    public AnswerAdapter(Context context, List<Answer> postList) {
        this.context = context;
        this.answerList = postList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.answers_retrieved_layout,parent,false);


        return new AnswerAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final Answer ans= answerList.get(position);


        if(ans.getAnswerImage()==null)
            holder.questionImage.setVisibility(View.GONE);

        else

        { holder.questionImage.setVisibility(View.VISIBLE);

            holder.questionImage.setOnClickListener(view -> context.startActivity(new Intent(context, FullPage.class).putExtra("image",ans.getAnswerImage())));

        }
        Glide.with(context).load(ans.getAnswerImage()).into(holder.questionImage);
        holder.expandableTextView.setText(ans.getAnswer());

        holder.askedOnTextView.setText(ans.getDate());


        publisherInfo(holder.publisher_profile_image,holder.answeredBy,ans.getPublisher());







    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

public ImageView publisher_profile_image,more,questionImage;
public TextView answeredBy,askedOnTextView;
public TextView expandableTextView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            publisher_profile_image=itemView.findViewById(R.id.publisherProfile);
            answeredBy=itemView.findViewById(R.id.answered_by);

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
