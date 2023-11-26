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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView imageView;
    ImageButton sendBTN, cameraBTN, micBTN;
    TextView username;
    EditText messageET;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef1, rootRef2;
    MessageMember messageMember;
    String receiverName, receiverUID, senderUID, url, profession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
            receiverName = extras.getString("name");
            receiverUID = extras.getString("userid");
            profession = extras.getString("profession");

        } else {

        }

        messageMember = new MessageMember();
        recyclerView = findViewById(R.id.idMessageRecyclerview);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        imageView = findViewById(R.id.idMessageProfileIMG);
        messageET = findViewById(R.id.inputMessage);
        sendBTN = findViewById(R.id.idMessageSend);
        micBTN = findViewById(R.id.idMessageMic);
        cameraBTN = findViewById(R.id.idMessageCamera);
        username = findViewById(R.id.idMessageName);

        Picasso.get().load(url).into(imageView);
        username.setText(receiverName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        senderUID = user.getUid();

        rootRef1 = database.getReference("Message").child(senderUID);
        rootRef2 = database.getReference("Message").child(senderUID);

        sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MessageMember> options =
                new FirebaseRecyclerOptions.Builder<MessageMember>()
                        .setQuery(rootRef1, MessageMember.class)
                        .build();

        FirebaseRecyclerAdapter<MessageMember, MessageViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<MessageMember, MessageViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageMember model) {

                        holder.setMessage(getApplication(), model.getName(), model.getTime(), model.getDate(), model.getMessage(),
                                model.getType(), model.getSenderUID(), model.getReceiverUID());

                    }
                    @NonNull
                    @Override
                    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_layout, parent, false);

                        return new MessageViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
    private void sendMessage() {

        String message = messageET.getText().toString();

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveDate = currentDate.format(callForDate.getTime());
        // for Dat
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        final String saveTime = currentTime.format(callForTime.getTime());

        if(message.isEmpty()){
            Toast.makeText(this, "cannot send empty message", Toast.LENGTH_SHORT).show();
        }else {

            messageMember.setDate(saveDate);
            messageMember.setName(saveTime);
            messageMember.setMessage(message);
            messageMember.setReceiverUID(receiverUID);
            messageMember.setSenderUID(senderUID);
            messageMember.setType("text");

            String id = rootRef1.push().getKey();
            rootRef1.child(id).setValue(messageMember);

            String id1 = rootRef2.push().getKey();
            rootRef2.child(id1).setValue(messageMember);

            messageET.setText("");
        }
    }
}