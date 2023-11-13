package com.example.socialmedia;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private Context myContext;
    private TextView createPost;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DocumentReference documentReference;
    private ImageView profileIMG;
    private DatabaseReference reference, likeRef, db1, db2, db3;
    private Boolean likeChecker = false;
    private FirebaseRecyclerAdapter<PostMember, PostViewHolder> adapter;
    private FirebaseRecyclerOptions<PostMember> options;
    private String currentUserID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        createPost = view.findViewById(R.id.idHomeFragCreateBTN);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = user.getUid();
        documentReference = db.collection("user").document(currentUserID);
        reference = database.getReference("All Posts");
        likeRef = database.getReference("Post Likes");
        db1 = database.getReference("All Images").child(currentUserID);
        db2 = database.getReference("All Videos").child(currentUserID);
        db3 = database.getReference("All Posts");
        recyclerView = view.findViewById(R.id.idPostRecyclerView);
        profileIMG = view.findViewById(R.id.idHomeFragProfileIMG);
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

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("url");
                            Picasso.get().load(url).into(profileIMG);
                        } else {
                            Toast.makeText(myContext, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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


                String postUrl = getItem(position).getPostUri();
                String name = getItem(position).getName();
                String url = getItem(position).getUrl();
                String time = getItem(position).getTime();
                String type = getItem(position).getType();
                String userID = getItem(position).getUserID();


                holder.menuOptionsBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(menuItem -> {
                            switch (menuItem.getItemId()) {

                                case R.id.popup_download:

                                    if(type.equals("iv")){

                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(postUrl));
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE);
                                        request.setTitle("Download");
                                        request.setDescription("Downloading image...");
                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name+System.currentTimeMillis()+
                                                ".jpg");
                                        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                        manager.enqueue(request);
                                        Toast.makeText(myContext, name + " your Post Deleted Successfully", Toast.LENGTH_SHORT).show();

                                    } else if (type.equals("vv")) {
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(postUrl));
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE);
                                        request.setTitle("Download");
                                        request.setDescription("Downloading video...");
                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name+System.currentTimeMillis()+
                                                ".mp4");
                                        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                        manager.enqueue(request);
                                        Toast.makeText(myContext, name + "Downloaded Successfully", Toast.LENGTH_SHORT).show();

                                    }

                                    Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.popup_share:

                                    String shareText = name + " from Chat App" + "\n" + "\n" + postUrl;
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_TEXT, shareText);
                                    intent.setType("text/plain");
                                    startActivity(intent.createChooser(intent, "Share via"));

                                    Toast.makeText(getActivity(), "Sharing", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.popup_delete:
                                    if(userID.equals(currentUserID)){
                                        Query query = db1.orderByChild("time").equalTo(time);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    dataSnapshot.getRef().removeValue();
                                                    Toast.makeText(myContext, name + " your post deleted successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        Query query2 = db2.orderByChild("time").equalTo(time);
                                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    dataSnapshot.getRef().removeValue();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        Query query3 = db3.orderByChild("time").equalTo(time);
                                        query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    dataSnapshot.getRef().removeValue();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        Toast.makeText(myContext, name + " your post deleted successfully", Toast.LENGTH_SHORT).show();
                                    }else {

                                        Toast.makeText(getActivity(), "Not allowed to delete this post", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.popup_copy_link:

                                    ClipboardManager clipManager = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("String", postUrl);
                                    clipManager.setPrimaryClip(clipData);
                                    clipData.getDescription();

                                    Toast.makeText(getActivity(), "Link Copied", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        });
                        popupMenu.show();
                    }
                });

                holder.likeChecker(postKey);

                holder.likeBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker = true;
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (likeChecker.equals(true)) {
                                    if (snapshot.child(postKey).hasChild(currentUserID)) {
                                        likeRef.child(postKey).child(currentUserID).removeValue();
//                                        delete(time);
                                        likeChecker = false;
                                    } else {
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