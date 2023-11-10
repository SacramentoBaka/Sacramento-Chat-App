package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ReplyActivity extends AppCompatActivity {
    private String userID, question, postKey, key;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userReference, currentUserReference;
    private TextView nameTV, questionTV, replyTV;
    private RecyclerView recyclerView;
    private ImageView userImageView, imageViewQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        nameTV = findViewById(R.id.idReplyNameTextView);
        questionTV = findViewById(R.id.idReplyQuestionTextView);
        imageViewQue = findViewById(R.id.idReplyIMG);
        userImageView = findViewById(R.id.idReplyProfileIMG);
        replyTV = findViewById(R.id.idReplyAnswerTextView);

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
    }
}