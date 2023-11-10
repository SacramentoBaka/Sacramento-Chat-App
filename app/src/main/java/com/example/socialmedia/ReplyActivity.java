package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ReplyActivity extends AppCompatActivity {
    private String userID, question, postKey, key;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userReference, currentUserReference;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference AgreeReference, AllQuestionRef;
    private TextView nameTV, questionTV, replyTV;
    private RecyclerView recyclerView;
    private ImageView userImageView, imageViewQue;

    private FirebaseRecyclerAdapter<AnswerMember, AnswerViewHolder> adapter;
    private FirebaseRecyclerOptions<AnswerMember> options;
    private Boolean agreeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        nameTV = findViewById(R.id.idReplyNameTextView);
        questionTV = findViewById(R.id.idReplyQuestionTextView);
        imageViewQue = findViewById(R.id.idReplyIMG);
        userImageView = findViewById(R.id.idReplyProfileIMG);
        replyTV = findViewById(R.id.idReplyAnswerTextView);
        recyclerView = findViewById(R.id.idReplyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
//            intent.putExtra("userID", userID);
//            intent.putExtra("question", question);
//            intent.putExtra("postKey", postKey);
//            intent.putExtra("key", privacy);
            userID = extra.getString("userID");
            question = extra.getString("question");
            postKey = extra.getString("postKey");
            key = extra.getString("key");


        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = user.getUid();

        AgreeReference = database.getReference("Agrees");
        AllQuestionRef =  database.getReference("All Questions").child(postKey).child("Answer");

        userReference = db.collection("user").document(userID);
        currentUserReference = db.collection("user").document(currentUID);

        replyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
                intent.putExtra("u", userID);
                intent.putExtra("p", postKey);
//                        intent.putExtra("key", privacy);
                startActivity(intent);

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

        // Question User Reference
        userReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("url");
                            String name = task.getResult().getString("name");
                            nameTV.setText(name);
                            questionTV.setText(question);
                            Picasso.get().load(url).into(userImageView);
                        } else {
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // Reference Current User
        currentUserReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("url");
                            Picasso.get().load(url).into(imageViewQue);
                        } else {
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        options = new FirebaseRecyclerOptions.Builder<AnswerMember>().setQuery(AllQuestionRef, AnswerMember.class).build();
        adapter = new FirebaseRecyclerAdapter<AnswerMember, AnswerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AnswerViewHolder holder, int position, @NonNull AnswerMember model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();

                final String posKey = getRef(position).getKey();

                holder.setAnswer(getApplication(), model.getName(), model.getAnswer(), model.getUserID(),
                        model.getTime(), model.getUrl());
                holder.agreeCountChecker(posKey);
                holder.agreeTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        agreeChecker = true;
                        AgreeReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(agreeChecker.equals(true)){
                                    if(snapshot.child(posKey).hasChild(currentUserID)){
                                        AgreeReference.child(posKey).child(currentUserID).removeValue();
                                        agreeChecker = false;
                                    }else {
                                        AgreeReference.child(posKey).child(currentUserID).setValue(true);
                                        agreeChecker = false;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
            @NonNull
            @Override
            public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.answer_item, parent, false);
                return new AnswerViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }
}