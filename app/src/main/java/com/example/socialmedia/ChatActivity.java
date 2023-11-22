package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference profileRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    EditText searchET;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUID = user.getUid();
    ImageView backPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        profileRef = database.getReference("All Users");

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

                FirebaseRecyclerOptions<All_UserMember> options1 =
                        new FirebaseRecyclerOptions.Builder<All_UserMember>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("All Users")
                                        .orderByChild("name")
                                        .startAt(query).endAt(query+"~"), All_UserMember.class)
                                .build();

                FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter1 =
                        new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options1) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                                final String postkey = getRef(position).getKey();

                                holder.setProfileInChat(getApplication(), model.getName(), model.getUserID(), model.getProfession(), model.getUrl());

                                String name = getItem(position).getName();
                                String url = getItem(position).getUrl();
                                String uid = getItem(position).getUserID();
                                String profession = getItem(position).getProfession();

                                holder.chatToProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (currentUID.equals(uid)) {
                                            Snackbar.make(view, "Your are currently the User, goto your profile...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();

                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), ShowUser.class);
                                            intent.putExtra("n", name);
                                            intent.putExtra("u", url);
                                            intent.putExtra("uid", uid);
                                            intent.putExtra("profession", profession);
                                            startActivity(intent);
                                        }
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
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<All_UserMember> options1 =
                new FirebaseRecyclerOptions.Builder<All_UserMember>()
                        .setQuery(profileRef, All_UserMember.class)
                        .build();

        FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                        final String postkey = getRef(position).getKey();

                        holder.setProfileInChat(getApplication(), model.getName(), model.getUserID(), model.getProfession(), model.getUrl());

                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUserID();
                        String profession = getItem(position).getProfession();

                        holder.chatToProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (currentUID.equals(uid)) {
                                    Snackbar.make(view, "Your are currently the User, goto your profile...", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                } else {
                                    Intent intent = new Intent(getApplicationContext(), ShowUser.class);
                                    intent.putExtra("n", name);
                                    intent.putExtra("u", url);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("profession", profession);
                                    startActivity(intent);
                                }
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