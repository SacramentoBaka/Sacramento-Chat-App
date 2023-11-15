package com.example.socialmedia;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageTabFragment extends Fragment {

    private Context myContext;
    private FirebaseRecyclerAdapter<PostMember, ImagesFragmentHolder> adapter;
    private FirebaseRecyclerOptions<PostMember> options;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_tab, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        recyclerView = view.findViewById(R.id.id_image_recyclerview_tab);
        reference = database.getReference("All Images").child(userID);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<PostMember>().setQuery(reference, PostMember.class).build();
        adapter = new FirebaseRecyclerAdapter<PostMember, ImagesFragmentHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ImagesFragmentHolder holder, int position, @NonNull PostMember model) {
                // pattern : name, url, userID, key, question, privacy, time;

                holder.setImage(getActivity(), model.getName(), model.getUrl(), model.getPostUri(),
                        model.getTime(), model.getUserID(), model.getType(), model.getDescription());
                //For Favourite Button
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();


            }

            @NonNull
            @Override
            public ImagesFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(myContext).inflate(R.layout.post_images, parent, false);
                return new ImagesFragmentHolder(view);
            }
        };

        GridLayoutManager layoutManager = new GridLayoutManager(myContext, 3, GridLayoutManager.VERTICAL,false);


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