package com.example.socialmedia;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VideoTabFragment extends Fragment {


    private Context myContext;
    private FirebaseRecyclerAdapter<PostMember, VideosFragmentHolder> adapter;
    private FirebaseRecyclerOptions<PostMember> options;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_tab, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        recyclerView = view.findViewById(R.id.id_video_recyclerview_tab);
        reference = database.getReference("All Videos").child(userID);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<PostMember>().setQuery(reference, PostMember.class).build();
        adapter = new FirebaseRecyclerAdapter<PostMember, VideosFragmentHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideosFragmentHolder holder, int position, @NonNull PostMember model) {
                // pattern : name, url, userID, key, question, privacy, time;

                holder.setVideo(getActivity(), model.getName(), model.getUrl(), model.getPostUri(),
                        model.getTime(), model.getUserID(), model.getType(), model.getDescription());
                //For Favourite Button
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();


            }

            @NonNull
            @Override
            public VideosFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(myContext).inflate(R.layout.post_videos, parent, false);
                return new VideosFragmentHolder(view);
            }
        };

        GridLayoutManager layoutManager = new GridLayoutManager(myContext, 2, GridLayoutManager.VERTICAL,false);


        adapter.startListening();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }

}