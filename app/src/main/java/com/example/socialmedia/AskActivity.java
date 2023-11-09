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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference AllQuestion, UserQuestion;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private QuestionMember member;
    private String name, url, privacy, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = user.getUid();

        editText = findViewById(R.id.idAskQuestion);
        button = findViewById(R.id.idAskSubmitButton);

        documentReference = db.collection("user").document(currentUserID);
        AllQuestion = database.getReference("All Questions");
        UserQuestion = database.getReference("User Questions").child(currentUserID);

        member = new QuestionMember();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editText.getText().toString();
                // For Date
                Calendar callForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String saveDate = currentDate.format(callForDate.getTime());
                // for Dat
                Calendar callForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                final String saveTime = currentTime.format(callForTime.getTime());

                String time = saveDate + ":" + saveTime;

                if(question != null){
                    member.setQuestion(question);
                    member.setTime(time);
                    member.setName(name);
                    member.setUrl(url);
                    member.setPrivacy(privacy);
                    member.setUserID(userID);
                    String id = UserQuestion.push().getKey();
                    assert id != null;
                    UserQuestion.child(id).setValue(member);

                    String child = AllQuestion.push().getKey();
                    member.setKey(id);
                    assert child != null;
                    AllQuestion.child(child).setValue(member);

                    Toast.makeText(AskActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AskActivity.this, "Please ask a Question", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {
                    name = task.getResult().getString("name");
                    url = task.getResult().getString("url");
                    privacy = task.getResult().getString("privacy");
                    userID = task.getResult().getString("userID");

                } else {
                    Toast.makeText(AskActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}