package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RelatedQuestions extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter<QuestionMember, QuestionsViewHolder> adapter;
    private FirebaseRecyclerOptions<QuestionMember> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_questions);
        recyclerView = findViewById(R.id.idRelatedRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = user.getUid();

        reference = database.getReference("Favourite List").child(currentUserID);
        options = new FirebaseRecyclerOptions.Builder<QuestionMember>().setQuery(reference, QuestionMember.class).build();
        adapter = new FirebaseRecyclerAdapter<QuestionMember, QuestionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position, @NonNull QuestionMember model) {
                // pattern : name, url, userID, key, question, privacy, time;
                holder.setItemRelated(getApplication(), model.getName(),
                        model.getUrl(), model.getUserID(), model.getKey(),
                        model.getQuestion(), model.getPrivacy(), model.getTime());
                //For Favourite Button
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();
//
//                String question = getItem(position).getQuestion();
//                String name = getItem(position).getName();
//                String url = getItem(position).getUrl();
//                String time = getItem(position).getTime();
//                String privacy = getItem(position).getPrivacy();
//                String userID = getItem(position).getUserID();

            }
            @NonNull
            @Override
            public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.related_item, parent, false);
                return new QuestionsViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }
}