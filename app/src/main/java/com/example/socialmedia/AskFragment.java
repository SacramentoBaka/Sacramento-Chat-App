package com.example.socialmedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class AskFragment extends Fragment {

    private FloatingActionButton floatingButton;
    private Context myContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference reference;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference, favouriteReference, favouriteListRef;
    private Boolean favouriteChecker = false;
    private  QuestionMember member;
    String currentUserID;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<QuestionMember, QuestionsViewHolder> adapter;
    private FirebaseRecyclerOptions<QuestionMember> options;

    private ImageView profileIMG;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = user.getUid();
        reference = db.collection("user").document(currentUserID);
        databaseReference = database.getReference("All Questions");
        floatingButton = view.findViewById(R.id.idAskFragFloatingButton);
        profileIMG = view.findViewById(R.id.idAskFragProfileIMG);
        recyclerView = view.findViewById(R.id.idAskFragRecyclerView);
        member = new QuestionMember();
        favouriteReference = database.getReference("Favourites");
        favouriteListRef = database.getReference("Favourite List").child(currentUserID);

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myContext, AskActivity.class);
                startActivity(intent);
            }
        });
        profileIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetAskFragment bottomSheetAskFragment = new BottomSheetAskFragment();
                bottomSheetAskFragment.show(getFragmentManager(), "bottom");
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<QuestionMember>().setQuery(databaseReference, QuestionMember.class).build();
        adapter = new FirebaseRecyclerAdapter<QuestionMember, QuestionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position, @NonNull QuestionMember model) {
                // pattern : name, url, userID, key, question, privacy, time;
                holder.setItem(getActivity(), model.getName(),
                        model.getUrl(), model.getUserID(), model.getKey(),
                        model.getQuestion(), model.getPrivacy(), model.getTime());
                //For Favourite Button
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();

                String question = getItem(position).getQuestion();
                String name = getItem(position).getName();
                String url = getItem(position).getUrl();
                String time = getItem(position).getTime();
                String privacy = getItem(position).getPrivacy();
                String userID = getItem(position).getUserID();

                holder.replyBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ReplyActivity.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("question", question);
                        intent.putExtra("postKey", postKey);
//                        intent.putExtra("key", privacy);
                        startActivity(intent);
                    }
                });

                holder.favouriteChecker(postKey);
                holder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favouriteChecker = true;
                        favouriteReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(favouriteChecker.equals(true)){
                                    if(snapshot.child(postKey).hasChild(currentUserID)){
                                        favouriteReference.child(postKey).child(currentUserID).removeValue();
                                        delete(time);
                                        favouriteChecker = false;
                                    }else {
                                        favouriteReference.child(postKey).child(currentUserID).setValue(true);
                                        member.setName(name);
                                        member.setTime(time);
                                        member.setPrivacy(privacy);
                                        member.setUserID(userID);
                                        member.setUrl(url);
                                        member.setQuestion(question);

//                                        String id = favouriteListRef.push().getKey();
                                        favouriteListRef.child(postKey).setValue(member);
                                        favouriteChecker = false;
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
            public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(myContext).inflate(R.layout.question_item, parent, false);
                return new QuestionsViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        recyclerView.setAdapter(adapter);

        reference.get()
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
    }

    private void delete(String time) {
        Query query = favouriteListRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(myContext, "Question Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}