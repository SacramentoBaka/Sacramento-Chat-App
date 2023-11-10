package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

    private String userID, question, postKey, name, url, time;
    private EditText editText;
    private Button button;
    private AnswerMember member;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference AllQuestions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        member = new AnswerMember();
        editText = findViewById(R.id.idAnswerQuestion);
        button = findViewById(R.id.idAnswerSubmitButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            userID = bundle.getString("u");
            postKey = bundle.getString("p");


        } else {
            Toast.makeText(this, "Something went wrong with Bundle", Toast.LENGTH_SHORT).show();
        }
        AllQuestions =  database.getReference("All Questions").child(postKey).child("Answer");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAnswer();
            }
        });

    }
    private void saveAnswer() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String cUserID = user.getUid();

        String answer = editText.getText().toString();
        if(answer != null){

            // For Date
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveDate = currentDate.format(callForDate.getTime());
            // for Dat
            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            final String saveTime = currentTime.format(callForTime.getTime());

            time = saveDate + ":" + saveTime;

            member.setAnswer(answer);
            member.setTime(time);
            member.setName(name);
            member.setUserID(cUserID);
            member.setUrl(url);

            String id = AllQuestions.push().getKey();
            AllQuestions.child(id).setValue(member);
            Toast.makeText(this, "Answer Submitted", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Please write answer", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userReference;
        userReference = db.collection("user").document(userID);

        userReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            url = task.getResult().getString("url");
                            name = task.getResult().getString("name");

                        } else {
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}