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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<QuestionMember, QuestionsAdapter> adapter;
    FirebaseRecyclerOptions<QuestionMember> options;

    private ImageView profileIMG;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = user.getUid();
        floatingButton = view.findViewById(R.id.idAskFragFloatingButton);
        profileIMG = view.findViewById(R.id.idAskFragProfileIMG);
        reference = db.collection("user").document(currentUserID);
        databaseReference = database.getReference("All Questions");
        recyclerView = view.findViewById(R.id.idAskFragRecyclerView);

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
                Intent intent = new Intent(myContext, ImageActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<QuestionMember>().setQuery(databaseReference, QuestionMember.class).build();
        adapter = new FirebaseRecyclerAdapter<QuestionMember, QuestionsAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull QuestionsAdapter holder, int position, @NonNull QuestionMember model) {
                // pattern : name, url, userID, key, question, privacy, time;
                holder.setItem(getActivity(), model.getName(),
                        model.getUrl(), model.getUserID(), model.getKey(),
                        model.getQuestion(), model.getPrivacy(), model.getTime());

            }

            @NonNull
            @Override
            public QuestionsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(myContext).inflate(R.layout.question_item, parent, false);
                return new QuestionsAdapter(view);
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