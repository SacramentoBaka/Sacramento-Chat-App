package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AnswerViewHolder extends RecyclerView.ViewHolder {

    ImageView profileIMG;
    TextView nameTV, timeTV, answerTV, agreeTV, agreeCountTV;
    int numberOfAgrees;
    DatabaseReference reference;
    FirebaseDatabase database;

    public AnswerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setAnswer(Application application, String name, String answer, String userID, String time, String url){

        profileIMG = itemView.findViewById(R.id.idAnswerItemIMG);
        nameTV = itemView.findViewById(R.id.idAnswerItemName);
        timeTV = itemView.findViewById(R.id.idAnswerItemTime);
        answerTV = itemView.findViewById(R.id.idAnswer);

        nameTV.setText(name);
        timeTV.setText(time);
        answerTV.setText(answer);
        Picasso.get().load(url).into(profileIMG);

    }
    public void agreeCountChecker(String postKey){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Agrees");
        agreeTV = itemView.findViewById(R.id.idAnswerItemAgree);
        agreeCountTV = itemView.findViewById(R.id.idAnswerItemCount);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String cUserID = user.getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(cUserID)){
                    agreeTV.setText("Agreed");
                    numberOfAgrees = (int)snapshot.child(postKey).getChildrenCount();
                    agreeCountTV.setText(Integer.toString(numberOfAgrees) + " Agreed");
                }else {
                    agreeTV.setText("Agree");
                    numberOfAgrees = (int)snapshot.child(postKey).getChildrenCount();
                    agreeCountTV.setText(Integer.toString(numberOfAgrees) + " Agreed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
