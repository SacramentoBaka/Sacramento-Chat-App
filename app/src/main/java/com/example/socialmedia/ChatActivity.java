package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText searchET;
    ImageView backPress;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUID;

    {
        assert user != null;
        currentUID = user.getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        searchET = findViewById(R.id.idChatSearchUser);
        recyclerView = findViewById(R.id.idChatRecyclerView);
        backPress = findViewById(R.id.idChatBack);
        backPress.setOnClickListener(view -> onBackPressed());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String query = searchET.getText().toString();

                FirebaseRecyclerOptions<All_UserMember> options =
                        new FirebaseRecyclerOptions.Builder<All_UserMember>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("followers")
                                        .child(currentUID)
                                        .orderByChild("name")
                                        .startAt(query).endAt(query + "~"), All_UserMember.class)
                                .build();

                FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                                holder.setProfileInChat(getApplication(), model.getName(), model.getUserID(), model.getProfession(), model.getUrl());

                                String name = getItem(position).getName();
                                String url = getItem(position).getUrl();
                                String uid = getItem(position).getUserID();
                                String profession = getItem(position).getProfession();

                                holder.chatSendMessage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                                        intent.putExtra("name", name);
                                        intent.putExtra("url", url);
                                        intent.putExtra("userid", uid);
                                        intent.putExtra("profession", profession);
                                        startActivity(intent);
                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.chat_profile_item, parent, false);

                                return new ProfileViewHolder(view);
                            }
                        };
                firebaseRecyclerAdapter.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<All_UserMember> options1 =
                new FirebaseRecyclerOptions.Builder<All_UserMember>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("followers")
                                .child(currentUID), All_UserMember.class)
                        .build();

        FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                        holder.setProfileInChat(getApplication(), model.getName(), model.getUserID(), model.getProfession(), model.getUrl());

                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUserID();
                        String profession = getItem(position).getProfession();

                        holder.chatSendMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("url", url);
                                intent.putExtra("userid", uid);
                                intent.putExtra("profession", profession);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_profile_item, parent, false);

                        return new ProfileViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}