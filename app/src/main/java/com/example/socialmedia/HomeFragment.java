package com.example.socialmedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private Context myContext;
    private Button createPost;
    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference, likeRef;
    private Boolean likeChecker = false;
    private FirebaseRecyclerAdapter<PostMember, PostViewHolder> adapter;
    private FirebaseRecyclerOptions<PostMember> options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        createPost = view.findViewById(R.id.idHomeFragCreateBTN);
        reference = database.getReference("All Posts");
        likeRef = database.getReference("Post Likes");
        recyclerView = view.findViewById(R.id.idPostRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PostActivity.class));
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();


        options = new FirebaseRecyclerOptions.Builder<PostMember>().setQuery(reference, PostMember.class).build();
        adapter = new FirebaseRecyclerAdapter<PostMember, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostMember model) {
                // pattern : name, url, userID, key, question, privacy, time;

                holder.setPost(getActivity(), model.getName(), model.getUrl(), model.getPostUri(),
                        model.getTime(), model.getUserID(), model.getType(), model.getDescription());
                //For Favourite Button
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();

//                String question = getItem(position).getQuestion();
//                String name = getItem(position).getName();
//                String url = getItem(position).getUrl();
//                String time = getItem(position).getTime();
//                String privacy = getItem(position).getPrivacy();
//                String userID = getItem(position).getUserID();


                holder.likeChecker(postKey);
                holder.likeBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker = true;
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(likeChecker.equals(true)){
                                    if(snapshot.child(postKey).hasChild(currentUserID)){
                                        likeRef.child(postKey).child(currentUserID).removeValue();
//                                        delete(time);
                                        likeChecker = false;
                                    }else {
                                        likeRef.child(postKey).child(currentUserID).setValue(true);

                                        likeChecker = false;
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
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(myContext).inflate(R.layout.post_layout, parent, false);
                return new PostViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}