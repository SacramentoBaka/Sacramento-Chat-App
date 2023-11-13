package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class QuestionsViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    ImageButton imageButton;
    TextView timeResult, nameResult, questionResult, userDelete, replyBTN, reply, viewReply;
    DatabaseReference favouriteDatabaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public QuestionsViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public void setItem(FragmentActivity activity, String name, String url, String userID, String key, String question,
                   String privacy, String time){

        imageView = itemView.findViewById(R.id.idQuestionItemIMG);
        timeResult = itemView.findViewById(R.id.idQuestionItemTime);
        nameResult = itemView.findViewById(R.id.idQuestionItemName);
        questionResult = itemView.findViewById(R.id.idQuestionItemQuestion);
        imageButton = itemView.findViewById(R.id.idQuestionItemIMGBTN);
        replyBTN = itemView.findViewById(R.id.idQuestionItemReply);

        Picasso.get().load(url).into(imageView);
        timeResult.setText(time);
        nameResult.setText(name);
        questionResult.setText(question);

    }

    public void favouriteChecker(String postKey) {

        favouriteDatabaseReference = database.getReference("Favourites");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        favouriteDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postKey).hasChild(userID)){
                    imageButton.setImageResource(R.drawable.baseline_turned_in_24);
                }else {
                    imageButton.setImageResource(R.drawable.baseline_turned_in_not_24);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setItemUserQ(Application activity, String name, String url, String userID, String key, String question,
                               String privacy, String time){

        ImageView userImage = itemView.findViewById(R.id.idUserQItemIMG);
        TextView userTimeTV = itemView.findViewById(R.id.idUserQItemTime);
        TextView userNameTV = itemView.findViewById(R.id.idUserQItemName);
        TextView userQuestionTV = itemView.findViewById(R.id.idUserQItemQuestion);
        userDelete = itemView.findViewById(R.id.idUserQItemDelete);
        viewReply = itemView.findViewById(R.id.idUserQItemView);

        Picasso.get().load(url).into(userImage);
        userTimeTV.setText(time);
        userNameTV.setText(name);
        userQuestionTV.setText(question);

    }

    public void setItemRelated(Application activity, String name, String url, String userID, String key, String question,
                               String privacy, String time){

        ImageView image = itemView.findViewById(R.id.idRelatedItemIMG);
        TextView timeTV = itemView.findViewById(R.id.idRelatedItemTime);
        TextView nameTV = itemView.findViewById(R.id.idRelatedItemName);
        TextView questionTV = itemView.findViewById(R.id.idRelatedItemQuestion);
        reply = itemView.findViewById(R.id.idRelatedItemReply);

        Picasso.get().load(url).into(image);
        timeTV.setText(time);
        nameTV.setText(name);
        questionTV.setText(question);

    }
}
