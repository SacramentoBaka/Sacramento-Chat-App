package com.example.socialmedia;

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
    TextView timeResult, nameResult, questionResult;
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
}