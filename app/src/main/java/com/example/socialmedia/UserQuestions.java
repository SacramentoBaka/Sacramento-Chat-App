package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserQuestions extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference AllQuestion, UserQuestion, FavouriteList;
    private FirebaseRecyclerAdapter<QuestionMember, QuestionsViewHolder> adapter;
    private FirebaseRecyclerOptions<QuestionMember> options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_questions);
        RecyclerView recyclerView = findViewById(R.id.idUserQRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = user.getUid();

        AllQuestion = database.getReference("All Questions");
        UserQuestion = database.getReference("User Questions").child(currentUserID);
        FavouriteList = database.getReference("Favourite List").child(currentUserID);

        options = new FirebaseRecyclerOptions.Builder<QuestionMember>().setQuery(UserQuestion, QuestionMember.class).build();
        adapter = new FirebaseRecyclerAdapter<QuestionMember, QuestionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position, @NonNull QuestionMember model) {
                // pattern : name, url, userID, key, question, privacy, time;
                holder.setItemUserQ(getApplication(), model.getName(),
                        model.getUrl(), model.getUserID(), model.getKey(),
                        model.getQuestion(), model.getPrivacy(), model.getTime());
                //For Favourite Button
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();
                String question = getItem(position).getQuestion();
//                String name = getItem(position).getName();
//                String url = getItem(position).getUrl();
//                String time = getItem(position).getTime();
//                String privacy = getItem(position).getPrivacy();
                String userID = getItem(position).getUserID();

                final String time = getItem(position).getTime();
                holder.userDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete(time);
                    }
                });
                holder.viewReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ReplyActivity.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("question", question);
                        intent.putExtra("postKey", postKey);
                        startActivity(intent);
                    }
                });

            }
            @NonNull
            @Override
            public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.user_question_item, parent, false);
                return new QuestionsViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }
    private void delete(String time) {
        Query query = UserQuestion.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(UserQuestions.this, "Your Question Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query query1 = AllQuestion.orderByChild("time").equalTo(time);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(UserQuestions.this, "Your Question Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query query2 = FavouriteList.orderByChild("time").equalTo(time);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(UserQuestions.this, "Your Question Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}